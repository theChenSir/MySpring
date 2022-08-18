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

public class BeanFactory {     //�����ĸ��ӿ�

    //�˴�Ĭ�϶���ʵ��
    private static BeanFactory instance = new BeanFactory();
    //���Ըĳ�ͬ�������Ƶı���ģʽ���߾�̬�ڲ�������ö��ʵ��
    public static Map<String, Object> map = new HashMap<String, Object>();
    //�������������


    private Class configClass;    //ע�⿪���Ļ���Ҫ����һ��������
    //�������ṩһ���вι���


    public BeanFactory(Class configClass) {
        try{
            this.configClass = configClass;
            if(configClass.isAnnotationPresent(ComponentScan.class)){
                ComponentScan cs = (ComponentScan)configClass.getAnnotation(ComponentScan.class);
                String path = cs.value();
                path = path.replace(".","/");
                //��·����ʽ�滻�ɿɲ���ʽ
                ClassLoader cl = BeanFactory.class.getClassLoader();    //��ȡAppClassLoader
                //��ȡ���������Ϊ�˸���·����������ҵ��������ļ�
                //��Ϊ��Ҫ�õ����ļ��Ǳ�����class�ļ���������.javaԴ�ļ�
                URL url = cl.getResource(path);
                File file = new File(url.getFile());   //�ȿ��Խ����ļ���Ҳ���Խ����ļ�
                System.out.print(file);   //����Ǳ����ľ���·��������õ�������ļ�
                if(file.isDirectory()){   //������ļ��оͻ�ȡ���е������ļ�
                    File[] fileArr = file.listFiles();
                    for(File f : fileArr){
                        if(f.getAbsolutePath().endsWith(".class")){
                            //�����ж�.class�ļ����Ƿ���һ��Bean
                            String fileName = f.getAbsolutePath();
                            String className = fileName.substring(fileName.indexOf("com"),fileName.indexOf(".class"));
                            fileName = fileName.replace("\\",".");
                            //Ŀ����Ϊ�˻�ȡ�����ȫ�޶�������Ϊ�������ֻ�ܼ���ȫ�޶�����ʽ
                            Class<?> clazz = null; //Class.forName(className)
                            clazz = cl.loadClass(className);
                            if(clazz.isAnnotationPresent(Component.class)){
                                String beanName = "";
                                String componentValue = clazz.getAnnotation(Component.class).value();
                                //�������Ǹ�Bean�࣬�̶���Bean���и��ֲ���
                                if(clazz.isAnnotationPresent(Scope.class)){
                                    //�ж��Ƿ�����ͨ����ȡ@Scopeע����ʶ�𣬲�дĬ�ϵ���
                                    Scope annotation = clazz.getAnnotation(Scope.class);
                                    String value = annotation.value();
                                    if("prototype".equals(value)){
                                        //�ǵ���
                                    }
                                    else if("singleton".equals(value)){
                                        //����
                                        if(clazz.isAnnotationPresent(Lazy.class)){
                                            Lazy annotation1 = clazz.getAnnotation(Lazy.class);
                                            boolean value1 = annotation1.value();
                                            if(value1){
                                                //������
                                            }else{
                                                //��������
                                                if("".equals(componentValue)){
                                                    beanName = clazz.getSimpleName().substring(0,1).toLowerCase()+clazz.getSimpleName().substring(1);
                                                }
                                                else beanName = componentValue;
                                                Object instance = createBean(beanName,clazz);
                                                map.put(beanName,instance);       //�������������ڵ�bean���뵥����
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



    //�Է������صĵ���beanͳһ����
    private Object createBean(String beanName, Class clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object instance = clazz.getConstructor().newInstance();
        //����ע��
        for(Field f:clazz.getDeclaredFields()){
            //ֻ�����е�@Autowired���Խ����Զ�ע��
            if(f.isAnnotationPresent(Autowired.class)){
                f.setAccessible(true);   //������˽�����Եĸ�ֵ
                f.set(instance,getBean(f.getName()));    //����ֵע��
            }
        }
        return instance;
    }

    public static void parseElement(Element ele) {
        try {
            Object beanObj = null;
            Class clazz = null;
            String id = ele.attributeValue("id");
            if (map.get(id) == null) {             //�������޴�idҲ����beanName�Ļ��ټ���������
                clazz = Class.forName(ele.attributeValue("class"));
                //class.forName�ķ�ʽ�Ļ��ײ�Ҳ���õ����������loadClass������ֻ�������ַ�ʽĬ�ϻ�ִ���侲̬����鲢�ҳ�ʼ����̬����
                beanObj = clazz.newInstance();    //ʵ����
                map.put(id, beanObj);       //���뵥����
            }

            //ele�Ƿ�����Ԫ��
            Object obj = null;
            String ref = "";
            List<Element> childElements = ele.elements();//�õ�ele����Ԫ�ؼ���
            for (Element childEle : childElements) {
                ref = childEle.attributeValue("ref");    //��ȡ����
                obj = map.get(ref);
                if (obj == null) {
                    for (Element el : list) {
                        String ids = el.attributeValue("id");
                        if (ids.equals(ref)) {
                            parseElement(el);// �ݹ鴦��  ��һ��ѭ��el��ʾpermissionDao
                            //���б�ʾ�ݹ�Ļ�ȡbeanDefinition�ж༶�����Խ��н���
                        }
                    }
                }
                obj = map.get(ref);
                //�����ٴλ�ȡ��ԭ���������һ�λ�ȡΪ������if�߼����������������ref��ʵ���ģ���Ҫ���»�ȡһ��ˢ��ֵ���������ʹ��
                if (clazz != null) {
                    Method methods[] = clazz.getDeclaredMethods();
                    for (Method m : methods) {
                        if (m.getName().startsWith("set") && m.getName().toLowerCase().contains(ref.toLowerCase())) {
                        /*
                        ����������setXXX����ʵ��bean���Զ�ע��
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

    //��̬�飺JVM������̬���ֱ��ִ��
    static {
        try {
            SAXReader reader = new SAXReader();
            InputStream in = Class.forName("cn.edu.guet.ioc.BeanFactory")
                    .getResourceAsStream("/applicationContext.xml");
            Document doc = reader.read(in);
            // xPathExpression��xPath���ʽ
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

    //��չһ����Class���ͻ�ȡ�ķ�ʽ
    public Object getBean(Class clazz){
        List beans = new ArrayList();
        map.forEach((a,b)->{
            if(b.getClass().equals(clazz))
                beans.add(b);
        });
        return beans;
    }
}
