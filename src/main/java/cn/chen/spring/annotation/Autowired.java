package cn.chen.spring.annotation;

import java.lang.annotation.*;

@Target({       //表示可以标注的地方
                ElementType.CONSTRUCTOR,
                ElementType.METHOD,
                ElementType.PARAMETER,
                ElementType.FIELD,
                ElementType.ANNOTATION_TYPE
        })
@Retention(RetentionPolicy.RUNTIME)     //运行时加载
@Documented
public @interface Autowired {
    boolean required() default true;
    String value();                    //value值如果又默认为beanName
}
