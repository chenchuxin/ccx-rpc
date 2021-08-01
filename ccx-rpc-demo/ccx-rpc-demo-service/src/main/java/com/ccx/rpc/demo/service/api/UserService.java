package com.ccx.rpc.demo.service.api;

import com.ccx.rpc.demo.service.bean.UserInfo;

/**
 * 用户服务
 *
 * @author chenchuxin
 * @date 2021/8/2
 */
public interface UserService {

    /**
     * 根据用户 id 获取用户信息
     *
     * @param id 用户 id
     * @return 用户信息，如果获取不到，返回 null
     */
    UserInfo getUser(Long id);
}
