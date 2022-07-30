package com.gaojy.rice.common.balance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinBalance implements Balance {
    private final AtomicInteger count = new AtomicInteger(0);


    @Override
    public <V> V select(List<V> all) {
        int index = count.getAndIncrement() % all.size();
        return (V) all.get(index);
    }
}
