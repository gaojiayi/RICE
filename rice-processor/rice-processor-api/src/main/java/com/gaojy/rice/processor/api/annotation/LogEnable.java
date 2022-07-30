package com.gaojy.rice.processor.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gaojy
 * @ClassName LogEnable.java
 * @Description
 * @createTime 2022/07/28 22:48:00
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LogEnable {
}
