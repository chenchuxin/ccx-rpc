package com.ccx.rpc.demo.client;

import com.ccx.rpc.core.annotation.RpcReference;
import com.ccx.rpc.demo.service.api.UserService;
import com.ccx.rpc.demo.service.bean.UserInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenchuxin
 * @date 2021/8/3
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @RpcReference
    private UserService userService;

    @GetMapping("/{uid}")
    public UserInfo getUser(@PathVariable("uid") long uid) {
        return userService.getUser(uid);
    }
}
