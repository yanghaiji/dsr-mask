package io.github.dsr.mask.core.process;

/**
 * 响应处理接口
 * <p>
 * @author haiji
 */
public interface ResponseProcess {

    /**
     * String 类型特殊处理（JSON / 非 JSON）
     * @param body 响应体
     * @return 处理后的响应体
     */
    Object processStringBody(String body);

    /**
     * 响应体统一入口
     * @param body 响应体
     * @return 处理后的响应体
     */
    Object processResponseBody(Object body);
}
