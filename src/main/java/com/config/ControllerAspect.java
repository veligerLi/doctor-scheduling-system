package com.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class ControllerAspect {
    /**
     * 配置禁止操作的路径列表（支持Ant风格通配符）
     */
    private static final List<String> FORBIDDEN_PATHS = Arrays.asList(
            "/**/save",
            "/**/update",
            "/**/delete",
            "**/resetPass"
    );

    /**
     * 定义切入点：拦截含有 @Controller 注解的类中的所有方法
     */
    @Pointcut("@within(org.springframework.stereotype.Controller)")
    public void controllerPointcut() {
    }

    /**
     * 环绕通知：在方法执行前后插入逻辑
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("controllerPointcut()")
    public Object time(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前请求路径
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestURI = request.getRequestURI();

        // 检查路径是否在禁止列表中
        if (isForbiddenPath(requestURI)) {
            // 禁止操作，返回提示信息（示例直接抛出异常）
            throw new RuntimeException("演示模式，禁止操作！");
        }
        // 允许正常执行
        return joinPoint.proceed();

    }

    /**
     * 路径匹配检查
     *
     * @param requestURI
     * @return
     */
    private boolean isForbiddenPath(String requestURI) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return FORBIDDEN_PATHS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }
}
