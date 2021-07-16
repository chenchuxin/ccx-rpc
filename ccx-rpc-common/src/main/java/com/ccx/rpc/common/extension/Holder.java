package com.ccx.rpc.common.extension;

/**
 * 持有者。用于单个变量既可以做锁又可以做值
 *
 * @author chenchuxin
 * @date 2021/7/17
 */
public class Holder<T> {
    private T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
