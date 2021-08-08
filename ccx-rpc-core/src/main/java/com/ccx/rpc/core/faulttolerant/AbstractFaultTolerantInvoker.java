package com.ccx.rpc.core.faulttolerant;

import com.ccx.rpc.common.consts.RpcException;
import com.ccx.rpc.common.consts.URLKeyConst;
import com.ccx.rpc.common.extension.ExtensionLoader;
import com.ccx.rpc.common.url.URL;
import com.ccx.rpc.common.url.URLBuilder;
import com.ccx.rpc.core.config.ClusterConfig;
import com.ccx.rpc.core.config.ConfigManager;
import com.ccx.rpc.core.dto.RpcRequest;
import com.ccx.rpc.core.dto.RpcResult;
import com.ccx.rpc.core.invoke.Invoker;
import com.ccx.rpc.core.loadbalance.LoadBalance;
import com.ccx.rpc.core.registry.Registry;
import com.ccx.rpc.core.registry.RegistryFactory;

import java.util.List;
import java.util.Map;

/**
 * 容错执行者
 *
 * @author chenchuxin
 * @date 2021/8/8
 */
public abstract class AbstractFaultTolerantInvoker implements FaultTolerantInvoker {

    protected final ClusterConfig clusterConfig = ConfigManager.getInstant().getClusterConfig();

    private final RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getAdaptiveExtension();
    private final Registry registry = registryFactory.getRegistry(ConfigManager.getInstant().getRegistryConfig().toURL());
    private final LoadBalance loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class)
            .getExtension(ConfigManager.getInstant().getClusterConfig().getLoadBalance());
    private final Invoker invoker = ExtensionLoader.getExtensionLoader(Invoker.class).getDefaultExtension();

    @Override
    public RpcResult invoke(RpcRequest request) throws RpcException {
        Map<String, String> serviceParam = URLBuilder.getServiceParam(request.getInterfaceName(), request.getVersion());
        URL url = URL.builder().protocol(URLKeyConst.CCX_RPC_PROTOCOL).host(URLKeyConst.ANY_HOST).params(serviceParam).build();
        // 注册中心拿出所有服务的信息
        List<URL> urls = registry.lookup(url);
        return doInvoke(request, invoker, urls, loadBalance);
    }

    /**
     * 执行
     *
     * @param request       请求
     * @param invoker       具体执行者
     * @param candidateUrls 候选服务列表
     * @param loadBalance   负载
     * @return 结果
     * @throws RpcException 请求失败会抛出异常
     */
    protected abstract RpcResult doInvoke(RpcRequest request, Invoker invoker, List<URL> candidateUrls, LoadBalance loadBalance) throws RpcException;
}
