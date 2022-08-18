package cn.edu.guet.BPP;

import cn.edu.guet.annotation.Component;
import cn.edu.guet.ioc.BeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Proxy;

@Component
public class BeanPostProcessorImpl implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)  {
        return bean;
    }
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName){
        if("userService".equals(beanName)){
                //如何判断Bean是否需要代理，
                    //数据库连接如何拿到？线程
            //创建JDK动态代理对象并返回，因为getBean("userService")是应当是拿到代理对象
            Object proxyInstance = Proxy.newProxyInstance(BeanFactory.class.getClassLoader(),bean.getClass().getInterfaces(), (o, method, objects) -> {
                //切面逻辑操作
                return method.invoke(bean,objects);
                //注意此处第一个参数别写成o了，o是代理对象，写o就递归了
            });
            return proxyInstance;   //如果是需要切面操作的类就返回代理对象，否则不改动bean直接返回
        }
        return bean;
    }
}
