package com.ccx.rpc.common.extension;

import cn.hutool.core.util.StrUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>扩展类加载器。</p>
 * <p>扩展类的配置写到 {@code META-INF/extensions/ccx-rpc} 目录下，文件名为接口全名。</p>
 * <p>文件格式为：扩展名=扩展类全名。例如：{@code zk=com.ccx.core.registry.ZkRegistry}</p>
 * <p>获取扩展类实例的代码示例如下：</p>
 * <pre>{@code
 *     ExtensionLoader loader = ExtensionLoader.getExtensionLoader(Registry.class)
 *     Registry registry = loader.getExtension("zk");
 * }</pre>
 *
 * @author chenchuxin
 * @date 2021/7/12
 */
public class ExtensionLoader<T> {

    /**
     * 扩展类实例缓存 {name: 扩展类实例}
     */
    private final Map<String, T> extensionsCache = new ConcurrentHashMap<>();

    /**
     * 扩展加载器实例缓存 {类型：加载器实例}
     */
    private static final Map<Class<?>, ExtensionLoader<?>> extensionLoaderCache = new ConcurrentHashMap<>();

    /**
     * 扩展类配置列表缓存 {type: {name, 扩展类}}
     */
    private final Holder<Map<String, Class<?>>> extensionClassesCache = new Holder<>();

    /**
     * 创建扩展实例类的锁缓存 {name: synchronized 持有的锁}
     */
    private final Map<String, Object> createExtensionLockMap = new ConcurrentHashMap<>();

    /**
     * 扩展类加载器的类型
     */
    private final Class<T> type;

    /**
     * 扩展类存放的目录地址
     */
    private static final String EXTENSION_PATH = "META-INF/ccx-rpc/";

    /**
     * 默认扩展名缓存
     */
    private final String defaultNameCache;

    /**
     * 构造函数
     *
     * @param type 扩展类加载器的类型
     */
    private ExtensionLoader(Class<T> type) {
        this.type = type;
        SPI annotation = type.getAnnotation(SPI.class);
        defaultNameCache = annotation.value();
    }

    /**
     * 获取对应类型的扩展加载器实例
     *
     * @param type 扩展类加载器的类型
     * @return 扩展类加载器实例
     */
    public static <S> ExtensionLoader<S> getLoader(Class<S> type) {
        // 扩展类型必须是接口
        if (!type.isInterface()) {
            throw new IllegalStateException(type.getName() + " is not interface");
        }
        SPI annotation = type.getAnnotation(SPI.class);
        if (annotation == null) {
            throw new IllegalStateException(type.getName() + " has not @SPI annotation.");
        }
        ExtensionLoader<?> extensionLoader = extensionLoaderCache.get(type);
        if (extensionLoader != null) {
            //noinspection unchecked
            return (ExtensionLoader<S>) extensionLoader;
        }
        extensionLoader = new ExtensionLoader<>(type);
        extensionLoaderCache.putIfAbsent(type, extensionLoader);
        //noinspection unchecked
        return (ExtensionLoader<S>) extensionLoader;
    }

    /**
     * 获取默认的扩展类实例，会自动加载 @SPI 注解中的 value 指定的类实例
     *
     * @return 返回该类的注解 @SPI.value 指定的类实例
     */
    public T getDefaultExtension() {
        return getExtension(defaultNameCache);
    }

    /**
     * 根据名字获取扩展类实例(单例)
     *
     * @param name 扩展类在配置文件中配置的名字. 如果名字是空的或者空白的，则返回默认扩展
     * @return 单例扩展类实例，如果找不到，则抛出异常
     */
    public T getExtension(String name) {
        if (StrUtil.isBlank(name)) {
            return getDefaultExtension();
        }
        // 从缓存中获取单例
        T extension = extensionsCache.get(name);
        if (extension == null) {
            Object lock = createExtensionLockMap.computeIfAbsent(name, k -> new Object());
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (lock) {
                extension = extensionsCache.get(name);
                if (extension == null) {
                    extension = createExtension(name);
                    extensionsCache.put(name, extension);
                }
            }
        }
        return extension;
    }

    /**
     * 获取自适应扩展类
     *
     * @return 动态代理自适应类
     */
    public T getAdaptiveExtension() {
        InvocationHandler handler = new AdaptiveInvocationHandler<>(type);
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(ExtensionLoader.class.getClassLoader(),
                new Class<?>[]{type}, handler);
    }

    /**
     * 创建对应名字的扩展类实例
     *
     * @param name 扩展名
     * @return 扩展类实例
     */
    private T createExtension(String name) {
        // 获取当前类型所有扩展类
        Map<String, Class<?>> extensionClasses = getAllExtensionClasses();
        // 再根据名字找到对应的扩展类
        Class<?> clazz = extensionClasses.get(name);
        if (clazz == null) {
            throw new IllegalStateException("Extension not found. name=" + name + ", type=" + type.getName());
        }
        try {
            //noinspection unchecked
            return (T) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Extension not found. name=" + name + ", type=" + type.getName() + ". " + e.toString());
        }
    }

    /**
     * 获取当前类型{@link #type}的所有扩展类
     *
     * @return {name: clazz}
     */
    private Map<String, Class<?>> getAllExtensionClasses() {
        Map<String, Class<?>> extensionClasses = extensionClassesCache.get();
        if (extensionClasses != null) {
            return extensionClasses;
        }
        synchronized (extensionClassesCache) {
            extensionClasses = extensionClassesCache.get();
            if (extensionClasses == null) {
                extensionClasses = loadClassesFromResources();
                extensionClassesCache.set(extensionClasses);
            }
        }
        return extensionClasses;
    }

    /**
     * 从资源文件中加载所有扩展类
     *
     * @return {name: 扩展类}
     */
    private Map<String, Class<?>> loadClassesFromResources() {
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
                        parseLine(line, extensionClasses);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Parse file fail. " + e.toString());
        }
        return extensionClasses;
    }

    /**
     * 解析行，并且把解析到的类，放到 extensionClasses 中
     *
     * @param line             行
     * @param extensionClasses 扩展类列表
     * @throws ClassNotFoundException 找不到类
     */
    private void parseLine(String line, Map<String, Class<?>> extensionClasses) throws ClassNotFoundException {
        line = line.trim();
        // 忽略#号开头的注释
        if (line.startsWith("#")) {
            return;
        }
        String[] kv = line.split("=");
        if (kv.length != 2 || kv[0].length() == 0 || kv[1].length() == 0) {
            throw new IllegalStateException("Extension file parsing error. Invalid format!");
        }
        if (extensionClasses.containsKey(kv[0])) {
            throw new IllegalStateException(kv[0] + " is already exists!");
        }
        Class<?> clazz = ExtensionLoader.class.getClassLoader().loadClass(kv[1]);
        extensionClasses.put(kv[0], clazz);
    }
}
