package com.ccx.rpc.common.extension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扩展类加载器。
 * 扩展类的配置写到 META-INF/extensions/ccx-rpc 目录下，文件名为接口全名。
 * 文件格式为：扩展名=扩展类全名。例如：zk=com.ccx.core.registry.zkRegistry
 *
 * @author chenchuxin
 * @date 2021/7/12
 */
public class ExtensionLoader<T> {

    /**
     * 扩展缓存
     */
    private final Map<String, T> extensionsCache = new ConcurrentHashMap<>();

    /**
     * 扩展类加载器的类型
     */
    private Class<T> type;

    /**
     * 扩展类存放的目录地址
     */
    private static final String EXTENSION_PATH = "META-INF/extensions/ccx-rpc";

    /**
     * TODO 这里可以做个缓存，各个类型对应各自的加载器
     *
     * @param type 扩展类加载器的类型
     */
    public ExtensionLoader(Class<T> type) {
        this.type = type;
    }

    /**
     * 根据名字获取扩展类实例
     *
     * @param name
     * @return
     */
    public T getExtension(String name) {
        // 从缓存中获取
        T extension = extensionsCache.get(name);
        if (extension != null) {
            return extension;
        }
        return createExtension(name);
    }

    /**
     * 创建对应名字的扩展类实例
     * TODO 添加缓存
     *
     * @param name
     * @return
     */
    private T createExtension(String name) {
        // 获取当前类型所有扩展类
        Map<String, Class<?>> extensionClasses = getAllExtensionClasses();
        // 再根据名字找到对应的扩展类
        Class<?> clazz = extensionClasses.get(name);
        IllegalStateException notFoundEx = new IllegalStateException("Extension not found. name=" + name + ", type=" + type.getName());
        if (clazz == null) {
            throw notFoundEx;
        }
        try {
            //noinspection unchecked
            return (T) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw notFoundEx;
        }
    }

    /**
     * 获取当前类型{@link #type}的所有扩展类
     *
     * @return {name，clazz}
     */
    private Map<String, Class<?>> getAllExtensionClasses() {
        // TODO 加缓存
        Map<String, Class<?>> extensionClasses = new ConcurrentHashMap<>();
        // 扩展配置文件名
        String fileName = EXTENSION_PATH + type.getName();
        // 拿到资源文件夹
        ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
        try {
            Enumeration<URL> resources = classLoader.getResources(fileName);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                    // 开始读文件
                    while (true) {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        line = line.trim();
                        // TODO: #号开头的注释解析
                        String[] kv = line.split("=");
                        if (kv.length != 2 || kv[0].length() == 0 || kv[1].length() == 0) {
                            throw new IllegalStateException("Extension file parsing error. Invalid format!");
                        }
                        if (extensionClasses.containsKey(kv[0])) {
                            throw new IllegalStateException(kv[0] + " is already exists!");
                        }
                        Class<?> clazz = classLoader.loadClass(kv[1]);
                        extensionClasses.put(kv[0], clazz);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // TODO
            e.printStackTrace();
        }
        return extensionClasses;
    }
}
