package com.dataexo.zblog.aop;

import com.dataexo.zblog.mapper.LogMapper;
import com.dataexo.zblog.vo.LogInfo;
import com.dataexo.zblog.vo.User;
import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;

/**
 * This controller implement to leave log for debug.
 * When you are running on your server , this class will detect the errors while it is running.
 * You can see 'log' table in database.
 * This table is for detect exceptions.
 */
@Aspect
@Component
@Order(5)
public class LoggerAspect {
    @Autowired
    private LogMapper logMapper;
    private Logger logger = LoggerFactory.getLogger(LoggerAspect.class);
    @Pointcut("execution(* com.dataexo.zblog.controller.*.*(..))")
    public void exceptionLog(){}
    private Thread thread;
    private Throwable err;

    JoinPoint joinPoint;
    /**
     * This is funtion implements to detect the exception while it is running.
     * @param joinPoint
     * @param err
     */
    @AfterThrowing(pointcut = "exceptionLog()",throwing = "err")
    public void afterThrowing(JoinPoint joinPoint,Throwable err){
        LogInfo log = new LogInfo();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            User user = (User) request.getSession().getAttribute("user");
            if (user!=null) {
                log.setUserId(user.getUsername());
            }
        }
        HttpServletRequest request = requestAttributes.getRequest();
        log.setIp(request.getRemoteAddr());
        log.setMethod(request.getMethod());
        log.setUrl(request.getRequestURL().toString());
        log.setArgs(Arrays.toString(joinPoint.getArgs()));
        log.setClassMethod(joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName());
        log.setException(err.getMessage());
        log.setOperateTime(new Date());
        logger.info(log.toString());
        if(log.getUserId() == null){
            log.setUserId("");
        }
        if(log.getException() == null){
            log.setException("");
        }
        logMapper.save(log);

    }

    public void run() {

    }
}
