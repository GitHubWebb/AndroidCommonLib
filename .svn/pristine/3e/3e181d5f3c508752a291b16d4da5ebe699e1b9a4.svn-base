package com.framelibrary.util.clickutil;

/**
 * 存储点击事件，可以定制存储的数量超出了可以自动删除之前的。
 * @author wangwx
 */
public class AntiShake {

    private static LimitQueue<OneClick> queue = new LimitQueue<>(20);

    public static boolean check(Object o) {
        String flag;
        if(o == null) {
            flag = Thread.currentThread().getStackTrace()[2].getMethodName();
        } else {
            flag = o.toString();
        }
        for (OneClick clickUtil : queue.getArrayList()) {
            if (clickUtil.getMethodName().equals(flag)) {
                return clickUtil.check();
            }
        }
        OneClick clickUtil = new OneClick(flag);
        queue.offer(clickUtil);
        return clickUtil.check();
    }
}
