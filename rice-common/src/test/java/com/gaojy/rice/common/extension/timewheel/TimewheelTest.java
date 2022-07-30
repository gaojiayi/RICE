package com.gaojy.rice.common.extension.timewheel;

import com.gaojy.rice.common.timewheel.HashedWheelTimer;
import com.gaojy.rice.common.timewheel.Timeout;
import com.gaojy.rice.common.timewheel.Timer;
import com.gaojy.rice.common.timewheel.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author gaojy
 * @ClassName TimewheelTest.java
 * @Description 
 * @createTime 2022/02/10 22:51:00
 */
@RunWith(JUnit4.class)
public class TimewheelTest {
    @Test
    public void test() throws InterruptedException {
        final Timer timer = new HashedWheelTimer(Executors.defaultThreadFactory(),5,TimeUnit.SECONDS,2);
        TimerTask task = new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                System.out.println("task=========>>>>>>>>");
                timer.newTimeout(this,5,TimeUnit.SECONDS);
            }
        };

        timer.newTimeout(task,5,TimeUnit.SECONDS);

        new CountDownLatch(1).await();
    }


}
