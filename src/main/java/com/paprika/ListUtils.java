package com.paprika;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class ListUtils {

    public static int getIndex(ListRand list, ListNode node) {
        int i = 0;
        ListNode fCur = node;
        ListNode bCur = node;
        while (fCur.next != null && bCur.previous != null) {
            fCur = fCur.next;
            bCur = bCur.previous;
            i++;
        }
        return fCur.next == null ? list.count - i : i;
    }

    public static ListNode getNode(ListRand list, int index) {
        ListNode cur = list.head;
        for (int i = 0; i < index; i++) cur = cur.next;
        return cur;
    }

    public static ListRand from(String... args) {
        ListRand list = new ListRand();

        if (args.length > 0) for (String s : args) list.add(s);

        ListNode cur = list.head;
        while (cur != null) {
            int index = ThreadLocalRandom.current().nextInt(0, args.length);
            cur.rand = getNode(list, index);
            cur = cur.next;
        }

        return list;
    }

    public static boolean equals(ListRand listA, ListRand listB) {
        if (listA == listB) return true;
        if (listA == null || listB == null || listA.count != listB.count) return false;

        ListNode curA = listA.head;
        ListNode curB = listB.head;
        while (curA != null) {
            if (!Objects.equals(curA.value, curB.value)) return false;
            if (!Objects.equals(curA.next, curB.next)) return false;
            if (!Objects.equals(curA.previous, curB.previous)) return false;
            if (!Objects.equals(curA.rand, curB.rand)) return false;

            curA = curA.next;
            curB = curB.next;
        }
        return true;
    }
}
