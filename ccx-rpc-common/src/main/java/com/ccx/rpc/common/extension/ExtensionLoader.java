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
    private final Map<Class<?>, Map<String, Class<?>>> extensionClassesCache = new ConcurrentHashMap<>();

    /**
     * 扩展类加载器的类型
     */
    private final Class<T> type;

    /**
     * 扩展类存放的目录地址
     */
    private static final String EXTENSION_PATH = "META-INF/ccx-rpc/";

    /**
     * @param type 扩展类加载器的类型
     */
    private ExtensionLoader(Class<T> type) {
        this.type = type;
    }

    /**
     * 获取对应类型的扩展加载器实例
     *
     * @param type 扩展类加载器的类型
     * @return 扩展类加载器实例
     */
    public static ExtensionLoader<?> getExtensionLoader(Class<?> type) {
        // 扩展类型必须是接口
        if (!type.isInterface()) {
            throw new IllegalStateException(type.getName() + " is not interface");
        }
        ExtensionLoader<?> extensionLoader = extensionLoaderCache.get(type);
        if (extensionLoader != null) {
            return extensionLoader;
        }
        // TODO 线程安全
        extensionLoader = new ExtensionLoader<>(type);
        extensionLoaderCache.putIfAbsent(type, extensionLoader);
        return extensionLoader;
    }

    /**
     * 获取默认的扩展类实例，会自动加载 @SPI 注解中的 value 指定的类实例
     *
     * @return 返回该类的注解 @SPI.value 指定的类实例
     */
    public T getDefaultExtension() {
        SPI annotation = type.getAnnotation(SPI.class);
        if (annotation == null) {
            throw new IllegalStateException(type.getName() + " has not @SPI annotation.");
        }
        return getExtension(annotation.value());
    }

    /**
     * 根据名字获取扩展类实例
     *
     * @param name 扩展类在配置文件中配置的名字
     * @return 扩展类实例，如果找不到，则抛出异常
     */
    public T getExtension(String name) {
        // 从缓存中获取
        T extension = extensionsCache.get(name);
        if (extension != null) {
            return extension;
        }
        extension = createExtension(name);
        extensionsCache.putIfAbsent(name, extension);
        return extension;
    }

    /**
     * 创建对应名字的扩展类实例
     * TODO 添加缓存
     *
     * @param name 扩展名
     * @return 扩展类实例
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
     * @return {name: clazz}
     */
    private Map<String, Class<?>> getAllExtensionClasses() {
        // TODO 线程安全
        Map<String, Class<?>> extensionClasses = extensionClassesCache.get(type);
        if (extensionClasses != null) {
            return extensionClasses;
        }
        extensionClasses = loadClassesFromResources();
        extensionClassesCache.putIfAbsent(type, extensionClasses);
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
            throw new IllegalStateException("Parse file fail." + e.getMessage());
        }
        return extensionClasses;
    }

    /**
     * 解析行，并且把解析到的类，放到 extensionClasses 中
     *
     * @param line
     * @param extensionClasses
     * @throws ClassNotFoundException
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
