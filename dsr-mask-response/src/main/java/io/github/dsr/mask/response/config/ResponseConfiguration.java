package io.github.dsr.mask.response.config;

import io.github.dsr.mask.core.process.ResponseProcess;
import io.github.dsr.mask.response.process.DefaultResponseProcess;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResponseConfiguration {


    @Bean
    @ConditionalOnMissingBean(ResponseProcess.class)
    public ResponseProcess defaultResponseProcess() {
        return new DefaultResponseProcess();
    }

}
