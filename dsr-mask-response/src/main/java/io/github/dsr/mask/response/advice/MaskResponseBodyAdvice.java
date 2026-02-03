package io.github.dsr.mask.response.advice;

import io.github.dsr.mask.response.annotation.MaskResponse;
import io.github.dsr.mask.response.process.ResponseProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一响应掩码处理Advice - 修复嵌套对象掩码问题
 * <p>
 * @author haiji
 */
@ControllerAdvice
public class MaskResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private static final Logger log = LoggerFactory.getLogger(MaskResponseBodyAdvice.class);

    private final ResponseProcess processor;

    @Autowired
    public MaskResponseBodyAdvice(ResponseProcess processor) {
        this.processor = processor;
    }

    /**
     * 响应结果是否需要处理
     */
    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {

        if (!MappingJackson2HttpMessageConverter.class
                .isAssignableFrom(converterType)) {
            return false;
        }

        MaskResponse maskResponse = AnnotatedElementUtils.findMergedAnnotation(
                returnType.getMethod(), MaskResponse.class
        );

        if (maskResponse != null) {
            return maskResponse.enabled();
        }

        maskResponse = AnnotatedElementUtils.findMergedAnnotation(
                returnType.getContainingClass(), MaskResponse.class
        );

        return maskResponse != null && maskResponse.enabled();
    }

    /**
     * 响应结果处理
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        if (body == null) {
            return null;
        }
        try {
            // String 特殊处理
            if (body instanceof String str) {
                return processor.processStringBody(str);
            }

            return processor.processResponseBody(body);

        } catch (Exception e) {
            log.error("response mask process error", e);
            return body;
        }
    }
}
