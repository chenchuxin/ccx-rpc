package com.ccx.rpc.demo.service.api.impl;

import com.ccx.rpc.core.annotation.RpcService;
import com.ccx.rpc.demo.service.api.UserService;
import com.ccx.rpc.demo.service.bean.UserInfo;

/**
 * 用户服务
 *
 * @author chenchuxin
 * @date 2021/8/2
 */
@RpcService
public class UserServiceImpl implements UserService {

    @Override
    public UserInfo getUser(Long id) {
        return UserInfo.builder().userId(id).userName("user" + id).build();
    }
}
