package cn.edu.guet.service;

import cn.edu.guet.bean.Permission;
import cn.edu.guet.bean.User;

import java.util.List;

public interface IUserService {
    User login(String username, String password);
    List<Permission> getMenuByUserId(String userId);
}
