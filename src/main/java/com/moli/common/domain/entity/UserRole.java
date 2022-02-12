package com.moli.common.domain.entity;

import com.moli.common.core.BaseEntity;
import lombok.Data;

@Data
public class UserRole {

    private Long id;

    private Long userId;

    private Long roleId;
}
