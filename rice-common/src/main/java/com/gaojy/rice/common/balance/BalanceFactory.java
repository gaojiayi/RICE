package com.gaojy.rice.common.balance;

import java.util.Locale;

/**
 * @author gaojy
 * @ClassName BalanceFactory.java
 * @Description
 * @createTime 2022/07/30 00:42:00
 */
public class BalanceFactory {

    public static Balance getBalance(String balancePolicy) {
        if (balancePolicy.equalsIgnoreCase(Policy.RANDOM.name())) {
            return new RandomBalance();
        }
        if (balancePolicy.equalsIgnoreCase(Policy.ROBIN.name())) {
            return new RoundRobinBalance();
        }

        throw new RuntimeException("Unknown load balancing policy");
    }

    enum Policy {
        ROBIN,
        RANDOM

    }
}
