package com.framelibrary.util.clickutil;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 一定对象的链表，超出了定义的范围就删除第一个对象
 * @author wangwx
 * @param <E> 存储的对象
 */
public class LimitQueue<E>  {

    private int limitedSize;

    private LinkedList<E> linkedList = new LinkedList<>();

    public LimitQueue(int size) {
        this.limitedSize = size;
    }

    public void offer(E e) {
        if (linkedList.size() >= limitedSize) {
            linkedList.poll(); //poll()获取并移除此列表的头（第一个元素）  size 随之改变
        }
        linkedList.offer(e);
    }

    public E get(int position) {
        return linkedList.get(position);
    }

    public E getLast() {
        return linkedList.getLast();
    }

    public E getFirst() {
        return linkedList.getFirst();
    }

    public int getLimit() {
        return limitedSize;
    }

    public void setLimitedSize(int size) {
        this.limitedSize = size;
    }

    public int size() {
        return linkedList.size();
    }

    public ArrayList<E> getArrayList() {
        ArrayList<E> arrayList = new ArrayList<>();
        for (int i = 0; i < linkedList.size(); i ++) {
            arrayList.add(linkedList.get(i));
        }
        return arrayList;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < linkedList.size(); i++) {
            buffer.append(linkedList.get(i));
            buffer.append(" ");
        }
        return buffer.toString();
    }
}