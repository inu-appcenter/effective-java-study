package org.items.item6;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapExample {

    private static long sum() {
        Long sum = 0L;
        for(long i = 0; i<=Integer.MAX_VALUE; i++) {
            sum += i;
        }
        return sum;
    }

    void mapTest() {
        Map<String, Integer> map = new HashMap<>();
        map.put("1", 3);
        map.put("2", 1);

        Set<String> strings1 = map.keySet();
        Set<String> strings2 = map.keySet();
        System.out.println(strings1);
        System.out.println(strings2);
    }
}
