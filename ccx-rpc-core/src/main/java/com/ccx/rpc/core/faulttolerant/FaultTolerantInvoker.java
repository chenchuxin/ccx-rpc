package com.ccx.rpc.core.faulttolerant;

import com.ccx.rpc.common.extension.SPI;
import com.ccx.rpc.core.invoke.Invoker;

/**
 * 容错
 *
 * @author chenchuxin
 * @date 2021/8/8
 */
@SPI("fail-fast")
public interface FaultTolerantInvoker extends Invoker {
}
