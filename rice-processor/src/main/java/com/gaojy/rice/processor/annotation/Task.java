package com.gaojy.rice.processor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gaojy
 * @ClassName Task.java
 * @Description TODO
 * @createTime 2022/01/02 15:17:00
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Task {

    String taskCode() default "";
}
