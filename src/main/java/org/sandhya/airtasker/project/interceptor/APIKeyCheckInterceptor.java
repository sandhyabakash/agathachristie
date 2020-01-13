package org.sandhya.airtasker.project.interceptor;

import org.sandhya.airtasker.project.provider.RateLimitProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class APIKeyCheckInterceptor implements HandlerInterceptor {

    Logger logger = LoggerFactory.getLogger(APIKeyCheckInterceptor.class);

    @Value("${api_key:testKey}")
    public String API_KEY;

    @Value("${api_key.header:x-api-key}")
    public String API_KEY_HEADER;

    @Override
    public boolean preHandle( HttpServletRequest request, HttpServletResponse response,
                              Object handler) throws Exception {
        logger.debug("Header Name :" + API_KEY_HEADER);
        String apiKey = request.getHeader(API_KEY_HEADER);
        //for simplicity checking for a pre-configured value in the properties file.
        if ( apiKey == null || apiKey.isEmpty() || !apiKey.equals(API_KEY) ) {
            logger.info("API key is either null or incorrect");
            response.getWriter().write("Incorrect API Key");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return false;
        } else {
            logger.info("API key check succeeded");
            return true;
        }
    }
        @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {}

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) throws Exception {}
}

