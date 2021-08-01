package com.ccx.rpc.demo.service.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息
 *
 * @author chenchuxin
 * @date 2021/8/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private Long userId;
    private String userName;
}
