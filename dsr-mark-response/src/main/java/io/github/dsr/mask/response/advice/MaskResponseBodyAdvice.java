package io.github.dsr.mask.response.advice;

import io.github.dsr.mask.response.ResponseProcess;
import io.github.dsr.mask.response.annotation.MaskResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

/**
 * 统一响应掩码处理Advice - 修复嵌套对象掩码问题
 */
@ControllerAdvice
public class MaskResponseBodyAdvice implements ResponseBodyAdvice<Object> {


    private final Logger log = LoggerFactory.getLogger(MaskResponseBodyAdvice.class);

    private final ThreadLocal<ResponseProcess> maskProcessLocal = ThreadLocal.withInitial(ResponseProcess::new);


    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 只处理 JSON 转换器
        if (!MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType)) {
            return false;
        }

        // 检查方法上是否有@MaskResponse注解
        MaskResponse maskResponse = AnnotatedElementUtils.findMergedAnnotation(
                Objects.requireNonNull(returnType.getMethod()), MaskResponse.class
        );

        if (maskResponse != null) {
            return maskResponse.enabled();
        }

        // 检查类上是否有@MaskResponse注解
        maskResponse = AnnotatedElementUtils.findMergedAnnotation(
                returnType.getContainingClass(), MaskResponse.class
        );

        return maskResponse != null && maskResponse.enabled();
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            return null;
        }

        try {
            // 关键修复1: 字符串类型特殊处理
            if (body instanceof String) {
                return maskProcessLocal.get().processStringBody((String) body);
            }
            // 获取方法上的@MaskResponse注解
            MaskResponse maskResponse = AnnotatedElementUtils.findMergedAnnotation(
                    returnType.getMethod(), MaskResponse.class
            );

            if (maskResponse == null) {
                // 尝试从类上获取
                maskResponse = AnnotatedElementUtils.findMergedAnnotation(
                        returnType.getContainingClass(), MaskResponse.class
                );
            }

            if (maskResponse == null) {
                return body;
            }

            // 深度处理响应体
            return maskProcessLocal.get().processResponseBody(body);

        } catch (Exception e) {
            log.error("process mask error: {}", e.getMessage(), e);
            return body;
        }finally {
            maskProcessLocal.remove();
        }
    }

}