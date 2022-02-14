package com.stonedt.intelligence.aspect;

import java.lang.annotation.*;

/**
 * @date 2019年11月27日 上午11:44:43
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})//作用在参数和方法上
@Retention(RetentionPolicy.RUNTIME)//运行时注解
@Documented//表明这个注解应该被 javadoc工具记录
public @interface SystemControllerLog {
    String operation() default ""; // 请求名称

    String type() default ""; // 操作类型

    String remark() default "";  // 注释

    String module() default ""; // 模块名称

    String submodule() default ""; // 子模块名称

    String description() default ""; // 描述
}

