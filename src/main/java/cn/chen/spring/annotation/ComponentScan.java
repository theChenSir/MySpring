package cn.chen.spring.annotation;

import java.lang.annotation.*;

@Target({       //表示可以标注在类上
        ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)     //运行时加载
@Documented
public @interface ComponentScan {
    String value();
}
