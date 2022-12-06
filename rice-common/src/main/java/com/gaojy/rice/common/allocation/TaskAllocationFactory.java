package com.gaojy.rice.common.allocation;

import com.gaojy.rice.common.exception.DispatcherException;

import java.util.HashMap;
import java.util.Map;

/**
 * 分配策略工厂
 */
public class TaskAllocationFactory {

    private static final Map<String, Strategy> strategyMap = new HashMap<>();

    public static Strategy getStrategy(AllocationType allocationType) {
        if (!strategyMap.containsKey(allocationType.name())) {
            synchronized (strategyMap) {
                if (!strategyMap.containsKey(allocationType.name())) {
                    strategyMap.computeIfAbsent(allocationType.name(), key -> {
                        if (key.equals(AllocationType.CONSISTENTHASH.name())) {
                            return new ConsistentHashingStrategy();
                        }
//                        if(key.equals(AllocationType.CONSISTENTHASH.name()){
//
//                        }
                        throw new DispatcherException("Unsupported task assignment strategy");
                    });
                }
            }
        }
        return strategyMap.get(allocationType.name());

    }


}
