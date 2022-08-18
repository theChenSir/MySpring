package cn.chen.spring.aop;




import org.aopalliance.intercept.MethodInvocation;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CGLIBProxyBean implements MethodInterceptor {


    private Object target;

    public CGLIBProxyBean(Object target) {
        this.target = target;
    }


    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        long starTime = begin();
        Object result = method.invoke(this.target, objects);//@1
        long endTime = after();
        System.out.println(this.target.getClass() + ".m1()方法耗时(纳秒):" + (endTime - starTime));
        return result;
    }

    //切面前置方法
    public static long begin() {
        return System.nanoTime();
    }

    //切面后置方法
    public static long after() {
        return System.nanoTime();
    }


    //对外暴露接口
    public static <T> T createProxy(Object target) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());     //设置父类，也就是被代理类
        enhancer.setCallback(new CGLIBProxyBean(target));
        return (T) enhancer.create();
    }



}
