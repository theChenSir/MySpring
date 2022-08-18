package cn.edu.guet.BPP;

import org.springframework.beans.BeansException;

public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(Object bean, String beanName) ;
    Object postProcessAfterInitialization(Object bean, String beanName) ;
}
