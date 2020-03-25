package com.lsd.fun.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 数据类型转换
 * @author: zjr
 * @create: 2020-01-14 14:12
 **/
public class TransformUtil {

    /**
     * List<Integer> TO List<Long>
     * @param list
     * @return
     */
    public static List<Long> TO(List<Integer> list){
        ArrayList<Long> longs = new ArrayList<>();
        for (Integer i:list){
            longs.add(i.longValue());
        }
        return longs;
    }
}
