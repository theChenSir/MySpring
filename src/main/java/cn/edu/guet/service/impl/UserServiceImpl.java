package cn.edu.guet.service.impl;

import cn.edu.guet.bean.Permission;
import cn.edu.guet.bean.User;
import cn.edu.guet.mapper.UserMapper;
import cn.edu.guet.service.IUserService;
import cn.edu.guet.util.MyBatisSqlSessionFactory;
import cn.edu.guet.util.PasswordEncoder;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.List;

public class UserServiceImpl implements IUserService {

    SqlSessionFactory sqlSessionFactory;
    public UserServiceImpl(){
        sqlSessionFactory= MyBatisSqlSessionFactory.getInstance().getSqlSessionFactory();
    }
    @Override
    public User login(String username, String password) {
        SqlSession sqlSession = sqlSessionFactory.openSession();// 相当于以前的Connection
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);
        User user=userMapper.login(username,password);
        // UserMapper到底是什么？UserMapper是一个动态代理对象，而且是JDK的动态代理
        // 动态代理：JDK动态代理、CGLIB动态代理
        // MyBatis根据UserMapper接口，帮我们创建的一个对象，该对象和UserDaoImpl
        if(user!=null){ //说明用户名是对的
            String encPass = user.getPassword();
            String salt = user.getSalt();
            PasswordEncoder encoderMd5 = new PasswordEncoder(salt, "MD5");
            boolean result = encoderMd5.matches(encPass, password);
            // result如果是真，说明密码也正确
            if (result) {
                // 密码验证完成后，清空password和salt，不要返回到浏览器
                user.setPassword("");
                user.setSalt("");
                return user;
            }
        }
        sqlSession.close();
        return null;
    }

    @Override
    public List<Permission> getMenuByUserId(String userId) {
        SqlSession sqlSession=sqlSessionFactory.openSession();// 相当于之前的JDBC的Connection
        UserMapper userMapper=sqlSession.getMapper(UserMapper.class);// UserMapper相当于JDBC时期的UserDaoImpl类

        List<Permission> permissionList = userMapper.getMenuByUserId(userId);
        sqlSession.close();
        return permissionList;
    }
}
