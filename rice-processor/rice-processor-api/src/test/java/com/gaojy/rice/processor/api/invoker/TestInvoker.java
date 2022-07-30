package com.gaojy.rice.processor.api.invoker;

import com.gaojy.rice.processor.api.ProcessResult;
import com.gaojy.rice.processor.api.RiceBasicProcessor;
import com.gaojy.rice.processor.api.RiceMapProcessor;
import com.gaojy.rice.processor.api.TaskContext;
import com.gaojy.rice.processor.api.config.ProcessorConfig;
import java.lang.reflect.Method;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

/**
 * @author gaojy
 * @ClassName TestInvoker.java
 * @Description 
 * @createTime 2022/01/05 23:15:00
 */
@RunWith(JUnit4.class)
public class TestInvoker {
    @Test
    public void testInvoker() throws Exception {
        TaskInvoker invoker = TaskInvoker.getInvoker(I1.class);
        String[] ns = invoker.getDeclaredMethodNames();
        //assertEquals(ns.length, 5);
        Impl1 impl1 = new Impl1();
        invoker.invokeMethod(impl1, "hello", new Class<?>[] {String.class}, new Object[] {"gaojy"});
    }

    @Test
    public void testTaskInvoker() throws Exception {
        ProcessorConfig config = new ProcessorConfig();
        TaskInvoker.init(config);
        TaskInvoker invoker = TaskInvoker.getInvoker("demoTaskCode");
        invoker.invokeMethod(invoker.getInvokerInstance("demoTaskCode"), "process", new Class<?>[] {TaskContext.class}, new Object[] {new TaskContext()});
        invoker.invokeMethod(invoker.getInvokerInstance("demoTaskCode"), "map", new Class<?>[] {}, new Object[] {});

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

    public static class Impl1 implements I1 {

        @Override public ProcessResult process(TaskContext context) {
            return null;
        }

        @Override public String getName() {
            return null;
        }

        @Override public void setName(String name) {

        }

        @Override public void hello(String name) {
            System.out.println(name);

        }

        @Override public int showInt(int v) {
            return 0;
        }

        @Override public float getFloat() {
            return 0;
        }

        @Override public void setFloat(float f) {

        }
    }
}
