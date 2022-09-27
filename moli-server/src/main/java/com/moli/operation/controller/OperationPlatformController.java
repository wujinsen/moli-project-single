package com.moli.operation.controller;

import com.moli.operation.mapper.OperationPlatformMapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/operation/platform")
@Api(tags = "运维平台管理")
@Slf4j
public class OperationPlatformController {

    @Resource
    private OperationPlatformMapper operationPlatformMapper;


}
