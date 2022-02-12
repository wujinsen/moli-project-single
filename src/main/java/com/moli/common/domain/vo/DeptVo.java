package com.moli.common.domain.vo;

import com.moli.common.core.BaseEntity;
import lombok.Data;

import java.util.List;

@Data
public class DeptVo extends BaseEntity {

    private Long parentId;

    private String deptName;

    private Integer orderNum;

    private List<DeptVo> children;
    
}
