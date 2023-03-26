package com.moli.common.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 自动生成ID类
 */
@Component
public class IdGenerator {
    @Value("${id.snowflake.worker_id:0}")
    private long workerId;
    @Value("${id.snowflake.data_center_id:0}")
    private long dataCenterId;

    private static SnowflakeIdWorker idWorker;

    public static String getStringId() {
        return getId() + "";
    }

    public static Long getId() {
        return idWorker.nextId();
    }

    @PostConstruct
    public void postConstruct() {
        idWorker = new SnowflakeIdWorker(workerId, dataCenterId);
    }

}
