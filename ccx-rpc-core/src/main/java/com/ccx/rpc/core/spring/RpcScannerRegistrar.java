package com.ccx.rpc.core.spring;

import com.ccx.rpc.common.annotation.RpcScan;
import com.ccx.rpc.common.annotation.RpcService;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author chenchuxin
 * @date 2021/7/30
 */
public class RpcScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //扫描注解
        Map<String, Object> annotationAttributes = importingClassMetadata
                .getAnnotationAttributes(RpcScan.class.getName());
        String[] basePackages = (String[]) annotationAttributes.get("basePackages");
        RpcScanner serviceScanner = new RpcScanner(registry, RpcService.class);
        // 消费者关注多个注解
        RpcScanner clientScanner = new RpcScanner(registry, Component.class, Service.class, Resource.class);
        if (resourceLoader != null) {
            serviceScanner.setResourceLoader(resourceLoader);
            clientScanner.setResourceLoader(resourceLoader);
        }
        serviceScanner.scan(basePackages);
        clientScanner.scan(basePackages);
    }
}
