package io.github.dsr.mask.core.util;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Spring上下文工具类
 * 用于检测Spring环境并获取Spring容器中的Bean
 */
public class SpringContextDetector {

    /**
     * Spring上下文类名
     * 用于检测Spring环境
     */
    private static final String APPLICATION_CONTEXT_CLASS = "org.springframework.context.ApplicationContext";
    private static final String BEAN_FACTORY_CLASS = "org.springframework.beans.factory.BeanFactory";
    private static final String WEB_APPLICATION_CONTEXT_CLASS = "org.springframework.web.context.WebApplicationContext";

    private static volatile Object applicationContext;
    private static volatile Class<?> contextLoaderClass;
    private static volatile Class<?> webApplicationContextUtilsClass;

    /**
     * 检测是否在Spring环境中
     */
    public static boolean isSpringEnvironment() {
        try {
            Class.forName(APPLICATION_CONTEXT_CLASS);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * 检测是否在Spring Web环境中
     */
    public static boolean isSpringWebEnvironment() {
        try {
            Class.forName(WEB_APPLICATION_CONTEXT_CLASS);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * 获取Spring ApplicationContext
     */
    public static Object getApplicationContext() {
        if (applicationContext != null) {
            return applicationContext;
        }

        synchronized (SpringContextDetector.class) {
            if (applicationContext != null) {
                return applicationContext;
            }

            try {
                applicationContext = findApplicationContext();
            } catch (Exception e) {
                // 忽略异常
            }

            return applicationContext;
        }
    }

    private static Object findApplicationContext() throws Exception {
        // 方法1: 尝试通过WebApplicationContextUtils获取（Web环境）
        if (isSpringWebEnvironment()) {
            try {
                if (webApplicationContextUtilsClass == null) {
                    webApplicationContextUtilsClass = Class.forName("org.springframework.web.context.support.WebApplicationContextUtils");
                }

                // 获取ServletContext
                Class<?> servletContextClass = Class.forName("jakarta.servlet.ServletContext");
                Method getServletContextMethod = null;

                // 尝试获取ServletContext
                Object servletContext = getServletContext();
                if (servletContext != null) {
                    Method getWebApplicationContextMethod = webApplicationContextUtilsClass.getMethod("getWebApplicationContext", servletContextClass);
                    return getWebApplicationContextMethod.invoke(null, servletContext);
                }
            } catch (Exception e) {
                // 忽略，尝试其他方法
            }
        }

        // 方法2: 尝试通过ContextLoader获取
        try {
            if (contextLoaderClass == null) {
                contextLoaderClass = Class.forName("org.springframework.web.context.ContextLoader");
            }

            Method getCurrentWebApplicationContextMethod = contextLoaderClass.getMethod("getCurrentWebApplicationContext");
            Object context = getCurrentWebApplicationContextMethod.invoke(null);
            if (context != null) {
                return context;
            }
        } catch (Exception e) {
            // 忽略，尝试其他方法
        }

        // 方法3: 如果有SpringContextHolder这样的工具类
        try {
            Class<?> springContextHolderClass = Class.forName("com.example.mask.spring.SpringContextHolder");
            Method getApplicationContextMethod = springContextHolderClass.getMethod("getApplicationContext");
            return getApplicationContextMethod.invoke(null);
        } catch (ClassNotFoundException e) {
            // 忽略，没有SpringContextHolder
        } catch (Exception e) {
            // 忽略异常
        }

        return null;
    }

    private static Object getServletContext() throws Exception {
        try {
            // 尝试通过RequestContextHolder获取
            Class<?> requestContextHolderClass = Class.forName("org.springframework.web.context.request.RequestContextHolder");
            Method getRequestAttributesMethod = requestContextHolderClass.getMethod("getRequestAttributes");
            Object requestAttributes = getRequestAttributesMethod.invoke(null);

            if (requestAttributes != null) {
                Class<?> servletRequestAttributesClass = Class.forName("org.springframework.web.context.request.ServletRequestAttributes");
                Method getRequestMethod = servletRequestAttributesClass.getMethod("getRequest");
                Object request = getRequestMethod.invoke(requestAttributes);

                Class<?> httpServletRequestClass = Class.forName("jakarta.servlet.http.HttpServletRequest");
                Method getServletContextMethod = httpServletRequestClass.getMethod("getServletContext");
                return getServletContextMethod.invoke(request);
            }
        } catch (Exception e) {
            // 忽略
        }
        return null;
    }

    /**
     * 从Spring容器中获取指定类型的Bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> beanType) {
        Object context = getApplicationContext();
        if (context == null) {
            return null;
        }

        try {
            Method getBeanMethod = context.getClass().getMethod("getBean", Class.class);
            return (T) getBeanMethod.invoke(context, beanType);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从Spring容器中获取所有指定类型的Bean
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> getBeansOfType(Class<T> beanType) {
        Object context = getApplicationContext();
        if (context == null) {
            return null;
        }

        try {
            Method getBeansOfTypeMethod = context.getClass().getMethod("getBeansOfType", Class.class);
            return (Map<String, T>) getBeansOfTypeMethod.invoke(context, beanType);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从Spring容器中获取指定名称的Bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName, Class<T> beanType) {
        Object context = getApplicationContext();
        if (context == null) {
            return null;
        }

        try {
            Method getBeanMethod = context.getClass().getMethod("getBean", String.class, Class.class);
            return (T) getBeanMethod.invoke(context, beanName, beanType);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查Spring容器中是否有指定类型的Bean
     */
    public static boolean containsBean(Class<?> beanType) {
        Map<String, ?> beans = getBeansOfType(beanType);
        return beans != null && !beans.isEmpty();
    }

    /**
     * 检查Spring容器中是否有指定名称的Bean
     */
    public static boolean containsBean(String beanName) {
        Object context = getApplicationContext();
        if (context == null) {
            return false;
        }

        try {
            Method containsBeanMethod = context.getClass().getMethod("containsBean", String.class);
            return (Boolean) containsBeanMethod.invoke(context, beanName);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 设置ApplicationContext（用于手动注入）
     */
    public static void setApplicationContext(Object context) {
        applicationContext = context;
    }
}