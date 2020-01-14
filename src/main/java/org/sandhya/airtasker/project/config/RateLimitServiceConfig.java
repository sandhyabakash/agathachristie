package org.sandhya.airtasker.project.config;

import org.sandhya.airtasker.project.interceptor.APIKeyCheckInterceptor;
import org.sandhya.airtasker.project.interceptor.RateLimitInterceptor;
import org.sandhya.airtasker.project.provider.RateLimitProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@PropertySource("classpath:application.properties")
public class RateLimitServiceConfig implements WebMvcConfigurer {

    @Bean
    public RateLimitInterceptor rateLimitInterceptor() {
        return new RateLimitInterceptor();
    }

    @Bean
    public APIKeyCheckInterceptor apiKeyCheckInterceptor() {
        return new APIKeyCheckInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiKeyCheckInterceptor()).addPathPatterns("/hello");
        registry.addInterceptor(rateLimitInterceptor()).addPathPatterns("/hello");
    }

    @Bean
    public RateLimitProvider rateLimitProvider() {
        return new RateLimitProvider();
    }


}
