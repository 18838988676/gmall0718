package com.atguigu.gmall.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequire {
// 由于加上拦截器后 所有方法都被拦截 ，因此 Wie进一步完善功能  需要加注解区分
    boolean ifNeedSuccess() default true;
}
