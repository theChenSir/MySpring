package cn.edu.guet.util;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

/**
 * 单例模式
 */
public class MyBatisSqlSessionFactory {

    /*
    1、私有的构造方法
    2、私有、静态的类的实例
    3、公共、静态返回类实例的方法
     */
    private SqlSessionFactory sqlSessionFactory;
    private static MyBatisSqlSessionFactory getInstance=new MyBatisSqlSessionFactory();// 饿汉模式

    private MyBatisSqlSessionFactory(){
        InputStream in = null;
        try {
            in = Class.forName("cn.edu.guet.service.impl.UserServiceImpl")
                    .getResourceAsStream("/mybatis-config.xml");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        sqlSessionFactory = builder.build(in);
    }
    public SqlSessionFactory getSqlSessionFactory(){
        return sqlSessionFactory;
    }
    public static MyBatisSqlSessionFactory getInstance(){
        return getInstance;
    }
}
