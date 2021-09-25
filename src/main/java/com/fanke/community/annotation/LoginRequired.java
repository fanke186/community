package com.fanke.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 一个自定义注解，只是起到一个标记
 * 起作用的方式：拦截器通过反射获取当前handler的controller的注解，
 *          如果被此注解修饰，则表示要求已登录状态
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {

}
