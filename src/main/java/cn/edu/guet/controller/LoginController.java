package cn.edu.guet.controller;

import cn.edu.guet.bean.Permission;
import cn.edu.guet.bean.User;
import cn.edu.guet.mvc.annotation.Controller;
import cn.edu.guet.mvc.annotation.RequestMapping;
import cn.edu.guet.service.IUserService;
import cn.edu.guet.service.impl.UserServiceImpl;

import java.util.List;

@Controller
public class LoginController {

    @RequestMapping("/login")
    public User login(String username, String password) {

        IUserService userService=new UserServiceImpl();

        User user=userService.login(username,password);

        return user;// 返回的JSON数据
    }
    @RequestMapping("/getUserPermission")// 相当于之前在web.xml中的url-pattern的内容
    public List<Permission> getUserPermission(String userId){
        IUserService userService=new UserServiceImpl();
        return userService.getMenuByUserId(userId);
    }
}
