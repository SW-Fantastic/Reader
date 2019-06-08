package org.swdc.reader.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Aspect注解，注解后方法会被ViewMethodAspect所接管
 * void类型的方法会被包裹在Platform.runLater中
 *
 * 不要对Controller使用这个注解，会导致FXML注解失效的
 *
 * an aspect annotation, a void return type method with this
 * annotation will be run in a runnable interface by Platform.runLater
 * method
 *
 * do not use it in fxml controllers classes, because annotation 'fxml'
 * in aspect is disabled.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UIMethod {
}
