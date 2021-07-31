package com.ccx.rpc.core.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

/**
 * rpc 类扫描
 *
 * @author chenchuxin
 * @date 2021/7/30
 */
public class RpcScanner extends ClassPathBeanDefinitionScanner {

    public RpcScanner(BeanDefinitionRegistry registry, Class<? extends Annotation>... annotationTypes) {
        super(registry);
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            super.addIncludeFilter(new AnnotationTypeFilter(annotationType));
        }
    }
}
