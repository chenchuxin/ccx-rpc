package com.ccx.rpc.demo.client;

import com.ccx.rpc.core.annotation.RpcScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author chenchuxin
 * @date 2021/8/1
 */
@SpringBootApplication
@RpcScan(basePackages = {"com.ccx.rpc.demo.client"})
public class ClientBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(ClientBootstrap.class, args);
    }

}
