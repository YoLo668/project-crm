package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public User login(String loginAct, String loginPwd, String ip) throws LoginException {
        Map<String,String> map = new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        User user =  userDao.login(map);
        if(user == null){
            throw new LoginException("账号密码错误");
        }
        //如果程序能执行到这里说明账号密码正确
        //需要继续向下验证其他三项信息
        //判断是否失效
        String expireTime = user.getExpireTime();
        String currentTime = DateTimeUtil.getSysTime();
        if (expireTime.compareTo(currentTime)<0){
            throw new LoginException("账号已失效");
        }
        //判断锁定状态
        String lockState = user.getLockState();
        if ("0".equals(lockState)){
            throw new LoginException("账号已锁定");
        }
        //判断ip地址
        String allowIps = user.getAllowIps();
        if (!allowIps.contains(ip)){
            throw new LoginException("Ip地址受限");
        }

        return user;
    }

    @Override
    public List<User> getUserList() {

       List<User> ulist =  userDao.getUserList();
        return ulist;
    }
}
