package cn.chen.spring.ioc;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chen.spring.annotation.*;
import cn.edu.guet.annotation.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;

public class BeanFactory {     //容器的父接口

    //此处默认饿汉实现
    private static BeanFactory instance = new BeanFactory();
    //可以改成同步锁限制的饱汉模式或者静态内部类甚至枚举实现
    public static Map<String, Object> map = new HashMap<String, Object>();
    //这个当作单例池


    private Class configClass;    //注解开发的话需要传入一个配置类
    //我们先提供一个有参构造


    public BeanFactory(Class configClass) {
        try{
            this.configClass = configClass;
            if(configClass.isAnnotationPresent(ComponentScan.class)){
                ComponentScan cs = (ComponentScan)configClass.getAnnotation(ComponentScan.class);
                String path = cs.value();
                path = path.replace(".","/");
                //将路径格式替换成可查找式
                ClassLoader cl = BeanFactory.class.getClassLoader();    //获取AppClassLoader
                //获取类加载器是为了根据路径这个串来找到编译后的文件
                //因为需要拿到的文件是编译后的class文件，而不是.java源文件
                URL url = cl.getResource(path);
                File file = new File(url.getFile());   //既可以接收文件夹也可以接收文件
                System.out.print(file);   //结果是本机的绝对路径，借此拿到编译后文件
                if(file.isDirectory()){   //如果是文件夹就获取其中的所有文件
                    File[] fileArr = file.listFiles();
                    for(File f : fileArr){
                        if(f.getAbsolutePath().endsWith(".class")){
                            //继续判断.class文件上是否是一个Bean
                            String fileName = f.getAbsolutePath();
                            String className = fileName.substring(fileName.indexOf("com"),fileName.indexOf(".class"));
                            fileName = fileName.replace("\\",".");
                            //目的是为了获取到类的全限定名，因为类加载器只能加载全限定名格式
                            Class<?> clazz = null; //Class.forName(className)
                            clazz = cl.loadClass(className);
                            if(clazz.isAnnotationPresent(Component.class)){
                                String beanName = "";
                                String componentValue = clazz.getAnnotation(Component.class).value();
                                //表明这是个Bean类，继而对Bean进行各种操作
                                if(clazz.isAnnotationPresent(Scope.class)){
                                    //判断是否单例，通过读取@Scope注解来识别，不写默认单例
                                    Scope annotation = clazz.getAnnotation(Scope.class);
                                    String value = annotation.value();
                                    if("prototype".equals(value)){
                                        //非单例
                                    }
                                    else if("singleton".equals(value)){
                                        //单例
                                        if(clazz.isAnnotationPresent(Lazy.class)){
                                            Lazy annotation1 = clazz.getAnnotation(Lazy.class);
                                            boolean value1 = annotation1.value();
                                            if(value1){
                                                //懒加载
                                            }else{
                                                //非懒加载
                                                if("".equals(componentValue)){
                                                    beanName = clazz.getSimpleName().substring(0,1).toLowerCase()+clazz.getSimpleName().substring(1);
                                                }
                                                else beanName = componentValue;
                                                Object instance = createBean(beanName,clazz);
                                                map.put(beanName,instance);       //将完整生命周期的bean加入单例池
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //对非懒加载的单例bean统一创建
    private Object createBean(String beanName, Class clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object instance = clazz.getConstructor().newInstance();
        //依赖注入
        for(Field f:clazz.getDeclaredFields()){
            //只对类中的@Autowired属性进行自动注入
            if(f.isAnnotationPresent(Autowired.class)){
                f.setAccessible(true);   //开启对私有属性的赋值
                f.set(instance,getBean(f.getName()));    //属性值注入
            }
        }
        return instance;
    }

    public static void parseElement(Element ele) {
        try {
            Object beanObj = null;
            Class clazz = null;
            String id = ele.attributeValue("id");
            if (map.get(id) == null) {             //单例池无此id也就是beanName的话再加载它的类
                clazz = Class.forName(ele.attributeValue("class"));
                //class.forName的方式的话底层也是用的类加载器的loadClass方法，只不过这种方式默认会执行其静态代码块并且初始化静态属性
                beanObj = clazz.newInstance();    //实例化
                map.put(id, beanObj);       //存入单例池
            }

            //ele是否有子元素
            Object obj = null;
            String ref = "";
            List<Element> childElements = ele.elements();//得到ele的子元素集合
            for (Element childEle : childElements) {
                ref = childEle.attributeValue("ref");    //获取属性
                obj = map.get(ref);
                if (obj == null) {
                    for (Element el : list) {
                        String ids = el.attributeValue("id");
                        if (ids.equals(ref)) {
                            parseElement(el);// 递归处理  第一次循环el表示permissionDao
                            //这行表示递归的获取beanDefinition中多级的属性进行解析
                        }
                    }
                }
                obj = map.get(ref);
                //这里再次获取的原因是如果第一次获取为空走了if逻辑后单例池中是有这个ref的实例的，需要重新获取一次刷新值，方便后续使用
                if (clazz != null) {
                    Method methods[] = clazz.getDeclaredMethods();
                    for (Method m : methods) {
                        if (m.getName().startsWith("set") && m.getName().toLowerCase().contains(ref.toLowerCase())) {
                        /*
                        反射调用类的setXXX方法实现bean的自动注入
                         */
                            m.invoke(beanObj, obj);
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    static List<Element> list;

    //静态块：JVM遇到静态块会直接执行
    static {
        try {
            SAXReader reader = new SAXReader();
            InputStream in = Class.forName("cn.edu.guet.ioc.BeanFactory")
                    .getResourceAsStream("/applicationContext.xml");
            Document doc = reader.read(in);
            // xPathExpression：xPath表达式
            list = doc.selectNodes("/beans/bean");
            for (Element ele : list) {
                parseElement(ele);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private BeanFactory() {
    }

    public static BeanFactory getInstance() {
        return instance;
    }

    public Object getBean(String id) {
        return map.get(id);
    }

    //扩展一下用Class类型获取的方式
    public Object getBean(Class clazz){
        List beans = new ArrayList();
        map.forEach((a,b)->{
            if(b.getClass().equals(clazz))
                beans.add(b);
        });
        return beans;
    }
}
