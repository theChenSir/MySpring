package cn.edu.guet.config;

import cn.edu.guet.mvc.annotation.Controller;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * 扫描自定义注解，例如，我们自己写的Controller注解
 */
@Configuration
public class ControllerConfig implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ClassPathBeanDefinitionScanner scanner=new ClassPathBeanDefinitionScanner(registry);
        scanner.setBeanNameGenerator(new AnnotationBeanNameGenerator());// 生成beanName的规则
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
        scanner.scan("cn.edu.guet.controller");
        System.out.println("扫描自定义注解成功");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // nothing to do
    }
}
