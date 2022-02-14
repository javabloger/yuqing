package com.stonedt.intelligence.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @date  2019年11月27日 上午11:44:43
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})//作用在参数和方法上
@Retention(RetentionPolicy.RUNTIME)//运行时注解
@Documented//表明这个注解应该被 javadoc工具记录
public @interface SystemControllerLog {
    String operation() default "";
    String type() default "";
    String remark() default "";
    String module() default "";
    String submodule() default "";
    
    
    
    
    String a = "";
    
    
    
    
}

