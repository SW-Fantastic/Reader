package org.swdc.reader.aspects;

import javafx.application.Platform;
import lombok.extern.apachecommons.CommonsLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * Created by lenovo on 2019/6/8.
 */
@Aspect
@Component
@CommonsLog
public class ViewMethodAspect {

    @Around("@annotation(org.swdc.reader.anno.UIMethod)")
    public Object withPlatformThread(ProceedingJoinPoint point) {
        MethodSignature methodSignature = (MethodSignature)point.getSignature();
        Class<?> returnType = methodSignature.getReturnType();
        if (returnType == void.class || returnType == Void.class) {
            Platform.runLater(() -> {
                try {
                    point.proceed();
                } catch (Throwable e) {
                    log.error(e);
                }
            });
            return null;
        } else {
            try {
                return point.proceed();
            } catch (Throwable throwable) {
                log.error(throwable);
                return null;
            }
        }
    }

}
