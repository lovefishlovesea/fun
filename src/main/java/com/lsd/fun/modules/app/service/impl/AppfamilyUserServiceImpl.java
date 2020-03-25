package com.lsd.fun.modules.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsd.fun.modules.app.dao.AppfamilyUserDao;
import com.lsd.fun.modules.app.entity.AppfamilyUserEntity;
import com.lsd.fun.modules.app.service.AppfamilyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AppfamilyUserServiceImpl extends ServiceImpl<AppfamilyUserDao, AppfamilyUserEntity> implements AppfamilyUserService {

    @Autowired
    private AppfamilyUserDao appfamilyUserDao;



}
