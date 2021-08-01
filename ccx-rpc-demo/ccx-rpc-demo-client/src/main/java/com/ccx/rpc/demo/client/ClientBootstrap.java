package com.ccx.rpc.demo.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author chenchuxin
 * @date 2021/8/1
 */
@SpringBootApplication
public class ClientBootstrap {

    public static void main(String[] args) {
        System.setProperty("registry.address", "local://localhost:123");
        SpringApplication.run(ClientBootstrap.class, args);
    }

}
