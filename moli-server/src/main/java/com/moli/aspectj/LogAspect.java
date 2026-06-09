package com.moli.aspectj;

import com.alibaba.fastjson.JSON;
import com.moli.common.core.IdGenerator;
import com.moli.common.domain.entity.SysOperationLog;
import com.moli.common.domain.entity.SysUser;
import com.moli.common.enums.BusinessTypeEnum;
import com.moli.common.enums.HttpMethodEnum;
import com.moli.common.log.MoliLog;
import com.moli.common.utils.IpUtils;
import com.moli.common.utils.ServletUtils;
import com.moli.common.utils.SpringUtil;
import com.moli.config.util.ShiroUtils;
import com.moli.system.mapper.SysOperationLogMapper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * 操作日志：自动拦截 system / operation 包下 Controller 的 POST、PUT、DELETE。
 * 标题优先 @MoliLog，其次 @ApiOperation，否则类名.方法名。
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Pointcut("(execution(* com.moli.system.controller..*.*(..)) "
            + "|| execution(* com.moli.operation.controller..*.*(..))) "
            + "&& (@annotation(org.springframework.web.bind.annotation.PostMapping) "
            + "|| @annotation(org.springframework.web.bind.annotation.PutMapping) "
            + "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public void writeOperationPointcut() {
    }

    @Pointcut("execution(* com.moli.system.controller.LoginController.*(..)) "
            + "|| execution(* com.moli.system.controller.LogController.*(..))")
    public void writeOperationExcludePointcut() {
    }

    @Pointcut("writeOperationPointcut() && !writeOperationExcludePointcut()")
    public void controllerWriteOps() {
    }

    @AfterReturning(pointcut = "controllerWriteOps()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, resolveMoliLog(joinPoint), null, jsonResult);
    }

    @AfterThrowing(pointcut = "controllerWriteOps()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, resolveMoliLog(joinPoint), e, null);
    }

    private MoliLog resolveMoliLog(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getAnnotation(MoliLog.class);
    }

    protected void handleLog(final JoinPoint joinPoint, MoliLog controllerLog, final Exception e, Object jsonResult) {
        try {
            SysUser user = ShiroUtils.getUserInfo();
            HttpServletRequest request = ServletUtils.getRequest();

            SysOperationLog sysOperationLog = new SysOperationLog();
            sysOperationLog.setId(IdGenerator.getId());
            sysOperationLog.setStatus(1);
            sysOperationLog.setRequestIp(IpUtils.getIpAddr(request));
            sysOperationLog.setRequestUrl(request.getRequestURI());
            if (user != null) {
                sysOperationLog.setUserName(user.getUserName());
            }
            if (e != null) {
                sysOperationLog.setStatus(0);
                sysOperationLog.setResponseResult(StringUtils.substring(e.getMessage(), 0, 2000));
            }

            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            sysOperationLog.setMethodName(className + "." + methodName + "()");
            sysOperationLog.setRequestMethod(request.getMethod());
            sysOperationLog.setCreateTime(new Date());

            getControllerMethodDescription(joinPoint, controllerLog, sysOperationLog, jsonResult);
            SpringUtil.getBean(SysOperationLogMapper.class).insert(sysOperationLog);
        } catch (Exception exp) {
            log.error("操作日志写入失败: {}.{}",
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    exp);
        }
    }

    public void getControllerMethodDescription(JoinPoint joinPoint, MoliLog moliLog,
                                                 SysOperationLog operLog, Object jsonResult) throws Exception {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        operLog.setBusinessType(resolveBusinessType(moliLog, operLog.getRequestMethod()));
        operLog.setTitle(resolveTitle(moliLog, method, joinPoint));

        boolean saveRequest = moliLog == null || moliLog.isSaveRequestData();
        boolean saveResponse = moliLog == null || moliLog.isSaveResponseData();

        if (saveRequest) {
            setRequestValue(joinPoint, operLog);
        }
        if (saveResponse && jsonResult != null && Integer.valueOf(1).equals(operLog.getStatus())) {
            operLog.setResponseResult(StringUtils.substring(JSON.toJSONString(jsonResult), 0, 2000));
        }
    }

    private int resolveBusinessType(MoliLog moliLog, String requestMethod) {
        if (moliLog != null && moliLog.businessType() != BusinessTypeEnum.OTHER) {
            return moliLog.businessType().ordinal();
        }
        if (HttpMethodEnum.POST.name().equals(requestMethod)) {
            return BusinessTypeEnum.INSERT.ordinal();
        }
        if (HttpMethodEnum.PUT.name().equals(requestMethod)) {
            return BusinessTypeEnum.UPDATE.ordinal();
        }
        if (HttpMethodEnum.DELETE.name().equals(requestMethod)) {
            return BusinessTypeEnum.DELETE.ordinal();
        }
        return BusinessTypeEnum.OTHER.ordinal();
    }

    private String resolveTitle(MoliLog moliLog, Method method, JoinPoint joinPoint) {
        if (moliLog != null && StringUtils.isNotBlank(moliLog.title())) {
            return moliLog.title();
        }
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        if (apiOperation != null && StringUtils.isNotBlank(apiOperation.value())) {
            return apiOperation.value();
        }
        return joinPoint.getTarget().getClass().getSimpleName() + "." + method.getName();
    }

    private void setRequestValue(JoinPoint joinPoint, SysOperationLog operLog) throws Exception {
        String requestMethod = operLog.getRequestMethod();
        if (HttpMethodEnum.PUT.name().equals(requestMethod) || HttpMethodEnum.POST.name().equals(requestMethod)) {
            String params = argsArrayToString(joinPoint.getArgs());
            operLog.setRequestParam(StringUtils.substring(params, 0, 2000));
        } else {
            Map<?, ?> paramsMap = (Map<?, ?>) ServletUtils.getRequest()
                    .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (paramsMap != null && !paramsMap.isEmpty()) {
                operLog.setRequestParam(StringUtils.substring(paramsMap.toString(), 0, 2000));
            }
        }
    }

    private String argsArrayToString(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null && paramsArray.length > 0) {
            for (Object o : paramsArray) {
                if (o != null && !isFilterObject(o)) {
                    try {
                        params.append(JSON.toJSON(o).toString()).append(' ');
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return params.toString().trim();
    }

    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }

}
