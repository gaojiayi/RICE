package com.gaojy.rice.processor.api.invoker;

import com.gaojy.rice.processor.api.RiceBasicProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

/**
 * @author gaojy
 * @ClassName TestInvoker.java
 * @Description TODO
 * @createTime 2022/01/05 23:15:00
 */
@RunWith(JUnit4.class)
public class TestInvoker {
    @Test
    public void testMain() throws Exception {
        TaskInvoker invoker = TaskInvoker.getInvoker(I1.class);
        String[] ns = invoker.getDeclaredMethodNames();
        assertEquals(ns.length, 5);
    }

    public static interface I0 extends RiceBasicProcessor {
        String getName();
    }

    public static interface I1 extends I0 {
        void setName(String name);

        void hello(String name);

        int showInt(int v);

        float getFloat();

        void setFloat(float f);
    }

}
