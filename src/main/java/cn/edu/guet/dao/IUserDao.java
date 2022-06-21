package cn.edu.guet.dao;


import cn.edu.guet.bean.Permission;
import cn.edu.guet.bean.User;

import java.util.List;

public interface IUserDao {

    User login(String username, String password);
    List<Permission> getMenuByUserId(String userId);
}
