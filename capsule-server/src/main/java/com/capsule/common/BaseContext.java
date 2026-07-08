package com.capsule.common;

public class BaseContext {
    private static final ThreadLocal<Long> THREAD_LOCAL_USER_ID = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        THREAD_LOCAL_USER_ID.set(id);
    }

    public static Long getCurrentId() {
        return THREAD_LOCAL_USER_ID.get();
    }

    public static void removeCurrentId() {
        THREAD_LOCAL_USER_ID.remove();
    }
}