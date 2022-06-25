package cn.edu.guet.service.impl;

import cn.edu.guet.bean.Permission;
import cn.edu.guet.mapper.IPermissionDao;
import cn.edu.guet.mapper.impl.PermissionDaoImpl;
import cn.edu.guet.service.IPermissionService;

import java.util.List;

public class PermissionServiceImpl implements IPermissionService {
    @Override
    public List<Permission> permissionList() {
        IPermissionDao permissionDao=new PermissionDaoImpl();

        return permissionDao.permissionList();
    }
}
