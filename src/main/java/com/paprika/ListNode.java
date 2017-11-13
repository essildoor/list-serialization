package com.paprika;

public class ListNode {
    public String value;
    public ListNode next;
    public ListNode previous;
    public ListNode rand;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListNode listNode = (ListNode) o;

        return value.equals(listNode.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
