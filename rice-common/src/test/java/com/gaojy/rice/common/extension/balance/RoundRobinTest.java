package com.gaojy.rice.common.extension.balance;

import com.gaojy.rice.common.balance.RoundRobinBalance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class RoundRobinTest {

    @Test
    public void roundRobin() {
        List<String> all = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            all.add(String.valueOf(i));
        }
        RoundRobinBalance balance = new RoundRobinBalance();
        String s1  = (String) balance.select(all);
        String s2  = (String) balance.select(all);
        String s3  = (String) balance.select(all);
    }
}
