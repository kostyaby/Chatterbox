package com.chatterbox.utils;

public class DBException  extends RuntimeException {
    public DBException(String s) { super(s); }
    public DBException(String s, Throwable throwable) { super(s, throwable); }
}
