package io.github.dsr.mask.core.util;

import io.github.dsr.mask.core.DsrMaskStrategyRegistry;
import io.github.dsr.mask.core.registry.MaskStrategyRegistry;
import io.github.dsr.mask.core.strategy.MaskStrategy;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * 脱敏策略门面类 - 改进版
 * 自动检测Spring环境并使用Spring容器中的注册表
 *
 * @author haiji
 */
public class MaskStrategies {

    /**
     * 全局注册表实例
     */
    private static volatile MaskStrategyRegistry registry;
    /**
     * 初始化锁
     */
    private static final Object initLock = new Object();

    /**
     * 初始化状态
     */
    private static volatile boolean initialized = false;

    /**
     * 获取全局注册表实例（单例）
     */
    public static MaskStrategyRegistry getRegistry() {
        if (!initialized) {
            synchronized (initLock) {
                if (!initialized) {
                    initialize();
                    initialized = true;
                }
            }
        }
        return registry;
    }

    /**
     * 设置注册表实例（主要用于测试或特殊场景）
     */
    public static void setRegistry(MaskStrategyRegistry newRegistry) {
        if (newRegistry == null) {
            throw new IllegalArgumentException("Registry cannot be null");
        }

        synchronized (initLock) {
            MaskStrategyRegistry old = registry;
            if (old != null && old != newRegistry) {
                // 可以在这里迁移策略（如果需要）
                System.out.println("Info: Overriding existing MaskStrategyRegistry instance");
            }
            registry = newRegistry;
            initialized = true;
        }
    }

    /**
     * 清除注册表实例（主要用于测试）
     */
    public static void clearRegistry() {
        synchronized (initLock) {
            registry = null;
            initialized = false;
        }
    }

    private static void initialize() {
        // 1. 首先检查是否已经通过setRegistry设置了实例
        if (registry != null) {
            return;
        }

        // 2. 检查Spring环境中是否有用户自定义的MaskStrategyRegistry
        MaskStrategyRegistry springRegistry = findSpringRegistry();
        if (springRegistry != null) {
            registry = springRegistry;
            System.out.println("Using Spring container MaskStrategyRegistry: " +
                    springRegistry.getClass().getName());
        }else {
            // 3. 使用默认实现
            registry = new DsrMaskStrategyRegistry();
            System.out.println("Using default MaskStrategyRegistry");
        }

    }

    private static MaskStrategyRegistry findSpringRegistry() {
        // 检查Spring容器中是否有MaskStrategyRegistry类型的Bean
        Map<String, MaskStrategyRegistry> registries = SpringContextDetector.getBeansOfType(MaskStrategyRegistry.class);

        if (registries == null || registries.isEmpty()) {
            return null;
        }

        // 如果有多个，按优先级选择
        // 1. 首先查找名称为"maskStrategyRegistry"的
        MaskStrategyRegistry namedRegistry = registries.get("maskStrategyRegistry");
        if (namedRegistry != null) {
            return namedRegistry;
        }

        // 2. 查找是否有@Primary标记的
        for (Map.Entry<String, MaskStrategyRegistry> entry : registries.entrySet()) {
            MaskStrategyRegistry reg = entry.getValue();
            if (isPrimaryBean(reg)) {
                return reg;
            }
        }

        // 3. 返回第一个
        return registries.values().iterator().next();
    }

    private static boolean isPrimaryBean(Object bean) {
        try {
            Class<?> beanClass = bean.getClass();
            // 检查类上的@Primary注解
            if (beanClass.isAnnotationPresent((Class<? extends Annotation>) getPrimaryAnnotationClass())) {
                return true;
            }

            // 如果是代理类，检查原始类
            if (beanClass.getName().contains("$$")) {
                // 尝试获取原始类
                Class<?> targetClass = bean.getClass().getSuperclass();
                if (targetClass != null && targetClass.isAnnotationPresent((Class<? extends Annotation>) getPrimaryAnnotationClass())) {
                    return true;
                }
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return false;
    }

    private static Class<?> getPrimaryAnnotationClass() throws ClassNotFoundException {
        return Class.forName("org.springframework.context.annotation.Primary");
    }

    /**
     * 注册脱敏策略
     */
    public static void register(MaskStrategy strategy) {
        getRegistry().register(strategy);
    }


    // 私有构造函数，防止实例化
    private MaskStrategies() {
    }
}