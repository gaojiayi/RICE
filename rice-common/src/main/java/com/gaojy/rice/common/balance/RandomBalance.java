package com.gaojy.rice.common.balance;

import java.util.List;
import java.util.Random;

/**
 * @author gaojy
 * @ClassName RandomBalance.java
 * @Description TODO
 * @createTime 2022/02/14 12:26:00
 */
public class RandomBalance implements Balance{

    Random random  = new Random();

    @Override
    public <V> V select(List<V> all) {
        int pos = random.nextInt(all.size());
        return all.get(pos);
    }
}
