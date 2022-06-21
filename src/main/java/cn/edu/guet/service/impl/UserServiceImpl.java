package cn.edu.guet.service.impl;

import cn.edu.guet.bean.Permission;
import cn.edu.guet.bean.User;
import cn.edu.guet.dao.IUserDao;
import cn.edu.guet.dao.impl.UserDaoImpl;
import cn.edu.guet.service.IUserService;

import java.util.List;

public class UserServiceImpl implements IUserService {
    @Override
    public User login(String username, String password) {
        IUserDao userDao = new UserDaoImpl();
        return userDao.login(username, password);
    }

    @Override
    public List<Permission> getMenuByUserId(String userId) {
        IUserDao userDao = new UserDaoImpl();
        return userDao.getMenuByUserId(userId);
    }
}
