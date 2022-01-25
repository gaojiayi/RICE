package com.gaojy.rice.common.balance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinBalance<T> implements Balance {
    private final List<T> all;
    private static final AtomicInteger count = new AtomicInteger(0);

    public RoundRobinBalance(List<T> all) {
        this.all = all;

    }

    @Override
    public <T> T select() {
        int index = count.getAndIncrement() % all.size();
        return (T) all.get(index);
    }
}
