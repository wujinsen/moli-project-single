package com.moli.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.moli.config.util.ShiroUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.util.Date;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        this.strictInsertFill(metaObject, "isDelete", () -> 0, Integer.class);
        this.strictInsertFill(metaObject, "createTime", () -> new Date(), Date.class);
        this.strictInsertFill(metaObject, "createUser", () -> ShiroUtils.getUserInfo().getId(), Long.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        this.strictUpdateFill(metaObject, "updateTime", () -> new Date(), Date.class);
        if (ShiroUtils.getUserInfo() != null) {
            this.strictInsertFill(metaObject, "updateUser", () -> ShiroUtils.getUserInfo().getId(), Long.class);
        }
    }

}
