package com.moli.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.moli.common.core.IdGenerator;
import org.springframework.stereotype.Component;

@Component
public class CustomIdGenerator implements IdentifierGenerator {
    @Override
    public Long nextId(Object entity) {
        return Long.valueOf(IdGenerator.getId());
    }
}
