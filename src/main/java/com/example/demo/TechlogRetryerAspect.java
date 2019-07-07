package com.example.demo;

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryListener;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.google.common.base.Predicates;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static com.github.rholder.retry.WaitStrategies.fixedWait;

@Aspect
@Service
public class TechlogRetryerAspect {

    @Around(value = "@annotation(TechlogRetryer)")
    public Object monitorAround(ProceedingJoinPoint pjp) throws Throwable {
        //todo：插入重试job表
        Method method;
        if (pjp.getSignature() instanceof MethodSignature) {
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            method = signature.getMethod();
        } else {
            return null;
        }
        //获取注解对象
        TechlogRetryer retryerAnnotation = method.getDeclaredAnnotation(TechlogRetryer.class);
        if (retryerAnnotation.maxDelayMsec() <= 0 && retryerAnnotation.maxAttempt() <= 1) {
            return pjp.proceed();
        }
        //定义重试对象
        RetryerBuilder retryer = RetryerBuilder.newBuilder();
        //隔几秒重试
        if (retryerAnnotation.waitMsec() > 0) {
            retryer.withWaitStrategy(fixedWait(retryerAnnotation.waitMsec(), TimeUnit.MILLISECONDS));
        }
        //设定异常重试
        if (retryerAnnotation.retryThrowable().length > 0) {
            for (Class retryThrowable : retryerAnnotation.retryThrowable()) {
                if (retryThrowable != null && Throwable.class.isAssignableFrom(retryThrowable)) {
                    retryer.retryIfExceptionOfType(retryThrowable);
                }
            }
        }
        //最多几秒停止重试
        if (retryerAnnotation.maxDelayMsec() > 0) {
            retryer.withStopStrategy(StopStrategies.stopAfterDelay(retryerAnnotation.maxDelayMsec(), TimeUnit.MILLISECONDS));
        }
        //最多几次停止重试
        if (retryerAnnotation.maxAttempt() > 0) {
            retryer.withStopStrategy(StopStrategies.stopAfterAttempt(retryerAnnotation.maxAttempt()));
        }
        //返回状态值为false
        if(!retryerAnnotation.result()){
            retryer.retryIfResult(Predicates.equalTo(false));
        }
        //监听  todo:重试都失败了记录日志
        retryer.withRetryListener(new RetryListener() {
            @Override
            public <V> void onRetry(Attempt<V> attempt) {
                //todo:重试都失败了记录日志
            }

        });
        String retrylog = pjp.getTarget().getClass().getCanonicalName() + "." + method.getName();
        return retryer.build().call(() -> {
            try {
                System.out.println("[retry]->"+retrylog);
                return pjp.proceed();
            } catch (Throwable throwable) {
                if (throwable instanceof Exception) {
                    throw (Exception) throwable;
                } else {
                    throw new Exception(throwable);
                }
            }
        });
    }
}
