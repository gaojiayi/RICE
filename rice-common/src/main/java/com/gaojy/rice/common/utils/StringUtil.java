package com.gaojy.rice.common.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;

/**
 * @author gaojy
 * @ClassName StringUtil.java
 * @Description TODO
 * @createTime 2022/01/03 00:23:00
 */
public class StringUtil {
    public static final String EMPTY = "";

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String join(String[] array) {
        if (ArrayUtils.isEmpty(array)) {
            return EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * "file:/home/whf/cn/fh" -> "/home/whf/cn/fh"
     * "jar:file:/home/whf/foo.jar!cn/fh" -> "/home/whf/foo.jar"
     */
    public static String getRootPath(URL url) {
        String fileUrl = url.getFile();
        int pos = fileUrl.indexOf('!');

        if (-1 == pos) {
            return fileUrl;
        }

        return fileUrl.substring(5, pos);
    }

    /**
     * "cn.fh.lightning" -> "cn/fh/lightning"
     * @param name
     * @return
     */
    public static String dotToSplash(String name) {
        return name.replaceAll("\\.", "/");
    }

    /**
     * "Apple.class" -> "Apple"
     */
    public static String trimExtension(String name) {
        int pos = name.indexOf('.');
        if (-1 != pos) {
            return name.substring(0, pos);
        }

        return name;
    }

    /**
     * /application/home -> /home
     * @param uri
     * @return
     */
    public static String trimURI(String uri) {
        String trimmed = uri.substring(1);
        int splashIndex = trimmed.indexOf('/');

        return trimmed.substring(splashIndex);
    }

    public static String toString(Throwable e) {
        UnsafeStringWriter w = new UnsafeStringWriter();
        PrintWriter p = new PrintWriter(w);
        p.print(e.getClass().getName());
        if (e.getMessage() != null) {
            p.print(": " + e.getMessage());
        }
        p.println();
        try {
            e.printStackTrace(p);
            return w.toString();
        } finally {
            p.close();
        }
    }

    public static class UnsafeStringWriter extends Writer {
        private StringBuilder mBuffer;

        public UnsafeStringWriter() {
            lock = mBuffer = new StringBuilder();
        }

        public UnsafeStringWriter(int size) {
            if (size < 0) {
                throw new IllegalArgumentException("Negative buffer size");
            }

            lock = mBuffer = new StringBuilder();
        }

        @Override
        public void write(int c) {
            mBuffer.append((char) c);
        }

        @Override
        public void write(char[] cs) throws IOException {
            mBuffer.append(cs, 0, cs.length);
        }

        @Override
        public void write(char[] cs, int off, int len) throws IOException {
            if ((off < 0) || (off > cs.length) || (len < 0) ||
                ((off + len) > cs.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            }

            if (len > 0) {
                mBuffer.append(cs, off, len);
            }
        }

        @Override
        public void write(String str) {
            mBuffer.append(str);
        }

        @Override
        public void write(String str, int off, int len) {
            mBuffer.append(str.substring(off, off + len));
        }

        @Override
        public Writer append(CharSequence csq) {
            if (csq == null) {
                write("null");
            } else {
                write(csq.toString());
            }
            return this;
        }

        @Override
        public Writer append(CharSequence csq, int start, int end) {
            CharSequence cs = (csq == null ? "null" : csq);
            write(cs.subSequence(start, end).toString());
            return this;
        }

        @Override
        public Writer append(char c) {
            mBuffer.append(c);
            return this;
        }

        @Override
        public void close() {
        }

        @Override
        public void flush() {
        }

        @Override
        public String toString() {
            return mBuffer.toString();
        }
    }
}
