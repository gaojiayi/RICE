package com.gaojy.rice.processor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gaojy
 * @ClassName Executer.java
 * @Description TODO
 * @createTime 2022/01/04 19:06:00
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Executer {

    String taskCode() default "";

    String taskName() default "";

    String appId() default "";
}
