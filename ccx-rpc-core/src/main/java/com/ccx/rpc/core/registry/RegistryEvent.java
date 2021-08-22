package com.ccx.rpc.core.registry;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 注册中心事件
 *
 * @author chenchuxin
 * @date 2021/8/22
 */
@Data
@AllArgsConstructor
public class RegistryEvent {

    /**
     * 事件类型
     */
    public enum Type {
        /**
         * 节点已创建
         */
        CREATED,
        /**
         * 节点已删除
         */
        DELETED,
        /**
         * 节点已更改
         */
        CHANGED
    }

    /**
     * 事件类型
     */
    private Type type;

    /**
     * 旧数据
     */
    private String oldData;

    /**
     * 当前数据
     */
    private String data;
}
