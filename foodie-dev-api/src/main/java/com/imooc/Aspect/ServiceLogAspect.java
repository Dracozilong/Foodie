package com.imooc.Aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ServiceLogAspect {

    //添加日志
    public final static Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Around("execution(* com.imooc.service.Impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {

        //需要监控的类和类中的方法名
        logger.info("==== 开始执行 {}.{} ==========",
                    joinPoint.getTarget().getClass(),joinPoint.getSignature().getName());

        //方法执行前的开始时间
        long begin = System.currentTimeMillis();

        //方法执行
        Object result = joinPoint.proceed();

        //方法执行完的结束时间
        long end = System.currentTimeMillis();

        //方法的执行时间
        long methodTime=begin-end;

        //对执行时间进行判断

        if (methodTime>3000){
            logger.error("======执行结束，耗时:{} 毫秒 ========",methodTime);
        }else if (methodTime>2000){
            logger.warn("=======执行结束，耗时:{} 毫秒 ======",methodTime);
        }else {
            logger.info("=======执行结束，耗时:{} 毫秒 =======",methodTime);
        }

        return result;
    }
}
