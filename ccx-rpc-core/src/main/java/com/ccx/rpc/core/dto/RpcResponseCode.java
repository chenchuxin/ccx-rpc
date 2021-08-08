package com.ccx.rpc.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回码
 *
 * @author chenchuxin
 * @date 2021/7/26
 */
@Getter
@AllArgsConstructor
public enum RpcResponseCode {
    SUCCESS(200, "success"),
    FAIL(500, "fail");

    private final int code;
    private final String message;
}
