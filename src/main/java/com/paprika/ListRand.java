package com.paprika;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class ListRand {
    public ListNode head;
    public ListNode tail;
    public int count;

    public void serialize(FileOutputStream os) throws IOException {
        // write header: 4 bytes with total number of nodes
        os.write(fromInt(count));

        if (head == null || tail == null) {
            // no data to serialize
            return;
        }

        // 1 dim = list elements; 2 dim - a byte array representation of each node
        byte[][] listData = new byte[count][];

        // first travers - store values + set indexes fro nodes
        HashMap<Integer, Integer> nodeIndexMap = new HashMap<>();
        int currentIndex = 0;
        ListNode currentNode = head;
        while (currentNode != null) {
            byte[] payloadBytes = currentNode.value == null ? new byte[0] : currentNode.value.getBytes();

            // 4 extra bytes is to store index of random element
            byte[] elementBytes = new byte[payloadBytes.length + 4];

            // copy payload bytes
            System.arraycopy(payloadBytes, 0, elementBytes, 4, payloadBytes.length);

            listData[currentIndex] = elementBytes;

            // put node index to map
            nodeIndexMap.put(currentNode.hashCode(), currentIndex);

            currentIndex++;
            currentNode = currentNode.next;
        }

        // second traverse: set node.random indexes from map
        // and write bytes to output stream
        currentIndex = 0;
        currentNode = head;
        while (currentNode != null) {
            byte[] nodeBytes = listData[currentIndex];

            Integer index = nodeIndexMap.get(currentNode.rand.hashCode());
            byte[] indexBytes = fromInt(index);
            System.arraycopy(indexBytes, 0, nodeBytes, 0, 4);

            byte[] offset = fromInt(nodeBytes.length); // 4 bytes storing element data length
            os.write(offset);
            os.write(nodeBytes);

            currentIndex++;
            currentNode = currentNode.next;
        }
    }

    public void deserialize(FileInputStream is) throws IOException {
        if (is.available() < 4) throw new IllegalArgumentException("input stream must contain at least 4 bytes");

        // read elements count
        int count = readInt(is);
        if (count == 0) return;

        // read nodes from input stream, fill node indexes map
        HashMap<Integer, ListNode> indexNodeMap = new HashMap<>();
        int[] randomLinks = new int[count];
        for (int i = 0; i < count; i++) {
            int valueBytesLength = readInt(is) - 4; // node offset - 4 bytes with random node index
            randomLinks[i] = readInt(is);
            String value = readString(is, valueBytesLength);
            ListNode node = add(value);
            indexNodeMap.put(i, node);
        }

        int currentIndex = 0;
        ListNode currentNode = head;
        while (currentNode != null) {
            currentNode.rand = indexNodeMap.get(randomLinks[currentIndex]);
            currentIndex++;
            currentNode = currentNode.next;
        }
    }

    public ListNode add(String s) {
        ListNode newNode = new ListNode();
        newNode.value = s;
        if (count == 0) {
            head = newNode;
        } else {
            ListNode cur = head;
            while (cur.next != null) cur = cur.next;
            cur.next = newNode;
            newNode.previous = cur;
        }
        tail = newNode;
        count++;
        return newNode;
    }

    @Override
    public String toString() {
        if (head == null) return "empty list";

        StringBuilder sb = new StringBuilder();
        ListNode cur = head;
        int i = 0;
        while (cur != null) {
            sb.append(String.format("[%d, \"%s\", %d]", i, cur.value, ListUtils.getIndex(this, cur.rand))).append(" -> ");
            cur = cur.next;
            i++;
        }
        sb.delete(sb.length() - 4, sb.length());
        return sb.toString();
    }

    private int readInt(InputStream is) throws IOException {
        byte[] bytes = new byte[4];
        is.read(bytes, 0, 4);
        return ByteBuffer.wrap(bytes).getInt();
    }

    private String readString(InputStream is, int length) throws IOException {
        byte[] bytes = new byte[length];
        is.read(bytes, 0, length);
        return new String(bytes);
    }

    private byte[] fromInt(int i) {
        return ByteBuffer.allocate(4).putInt(i).array();
    }
}
