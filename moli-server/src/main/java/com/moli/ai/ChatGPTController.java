package com.moli.ai;

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

    @GetMapping("/v1/createCompletion/{content}")
    @ApiOperation(value = "chatgpt-文本问答", notes = "chatgpt-文本问答")
    public MoliResult<String> getInfo(@PathVariable String content) {
        log.info("content: {}", content);
        String result = openAiService.createCompletion(content);
        log.info("result: {}", result);
        return MoliResult.success(result);
    }

}
