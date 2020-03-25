package com.lsd.fun.config.properties;

import lombok.Data;

/**
 * Created by lsd
 * 2019-11-15 14:08
 */
@Data
public class RedisProperties {

    @Data
    public static class KeyPrefix {
        // 数据字典的Key
        private String dictionary = "";
        // 购物车的Key
        private String cart = "";
    }

    private KeyPrefix keyPrefix = new KeyPrefix();

}
