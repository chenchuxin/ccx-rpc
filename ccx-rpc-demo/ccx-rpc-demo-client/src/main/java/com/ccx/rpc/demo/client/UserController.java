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

    @RpcReference(version = "v2")
    private UserService userService2;

    @GetMapping("/{uid}")
    public UserInfo getUser(@PathVariable("uid") long uid) {
        return userService.getUser(uid);
    }

    @GetMapping("/v2/{uid}")
    public UserInfo getUserV2(@PathVariable("uid") long uid) {
        return userService2.getUser(uid);
    }
}
