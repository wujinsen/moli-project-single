package com.moli.ai;

import com.moli.common.Messages;
import com.moli.common.core.MoliResult;
import com.moli.service.OpenAiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("chatgpt")
@Api(tags = "ChatGPT")
@Slf4j
public class ChatGPTController {

    @Resource
    private OpenAiService openAiService;

    @GetMapping("/v1/createCompletion")
    @ApiOperation(value = "chatgpt-文本问答", notes = "chatgpt-文本问答")
    public MoliResult<String> createCompletion(Messages messages) {
        log.info("content: {}", messages.getContent());
        Long startTime = System.currentTimeMillis();
        String result = openAiService.createCompletion(messages.getContent());
        Long endTime = System.currentTimeMillis();
        log.info("createCompletion result: {}, 耗时: {} 毫秒", result, (endTime-startTime));
        return MoliResult.success(result);
    }

    @GetMapping("/v1/createCompletionTurbo")
    @ApiOperation(value = "chatgpt-文本问答", notes = "chatgpt-文本问答")
    public MoliResult<String> createCompletionTurbo(Messages messages) {
        log.info("content: {}", messages.getContent());
        Long startTime = System.currentTimeMillis();
        String result = openAiService.createCompletionTurbo(messages.getContent());
        Long endTime = System.currentTimeMillis();
        log.info("createCompletionTurbo result: {}, 耗时: {} 毫秒", result, (endTime-startTime));
        return MoliResult.success(result);
    }

}
