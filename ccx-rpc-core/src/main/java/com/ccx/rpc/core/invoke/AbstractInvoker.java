package com.ccx.rpc.core.invoke;

import cn.hutool.core.collection.CollectionUtil;
import com.ccx.rpc.common.consts.RpcException;
import com.ccx.rpc.common.consts.URLKeyConst;
import com.ccx.rpc.common.extension.ExtensionLoader;
import com.ccx.rpc.common.url.URL;
import com.ccx.rpc.common.url.URLBuilder;
import com.ccx.rpc.core.config.ConfigManager;
import com.ccx.rpc.core.dto.RpcRequest;
import com.ccx.rpc.core.dto.RpcResult;
import com.ccx.rpc.core.loadbalance.LoadBalance;
import com.ccx.rpc.core.registry.Registry;
import com.ccx.rpc.core.registry.RegistryFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 抽象执行者
 *
 * @author chenchuxin
 * @date 2021/8/8
 */
public abstract class AbstractInvoker implements Invoker {

    private final RegistryFactory registryFactory = ExtensionLoader.getLoader(RegistryFactory.class).getAdaptiveExtension();
    private final Registry registry = registryFactory.getRegistry(ConfigManager.getInstant().getRegistryConfig().toURL());
    private final LoadBalance loadBalance = ExtensionLoader.getLoader(LoadBalance.class)
            .getExtension(ConfigManager.getInstant().getClusterConfig().getLoadBalance());

    @Override
    public RpcResult invoke(RpcRequest request) throws RpcException {
        Map<String, String> serviceParam = URLBuilder.getServiceParam(request.getInterfaceName(), request.getVersion());
        URL url = URL.builder().protocol(URLKeyConst.CCX_RPC_PROTOCOL).host(URLKeyConst.ANY_HOST).params(serviceParam).build();
        // 注册中心拿出所有服务的信息
        List<URL> urls = registry.lookup(url);
        if (CollectionUtil.isEmpty(urls)) {
            throw new RpcException("Not service Providers registered." + serviceParam);
        }
        URL selected = loadBalance.select(urls, request);
        return doInvoke(request, selected);
    }

    protected abstract RpcResult doInvoke(RpcRequest rpcRequest, URL selected) throws RpcException;
}
