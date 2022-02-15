package com.gaojy.rice.common.balance;

import java.util.Collection;
import java.util.List;

public interface Balance {

    public <V> V select(List<V> all);
}
