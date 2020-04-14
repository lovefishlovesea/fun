package com.lsd.fun.modules.spark.data_graphics_etl;

import com.lsd.fun.modules.spark.dto.ETLTaskResult;

/**
 * Created by lsd
 * 2020-04-14 11:55
 */
public interface ETLTask {

    ETLTaskResult cache();

}
