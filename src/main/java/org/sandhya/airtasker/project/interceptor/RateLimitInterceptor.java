package org.sandhya.airtasker.project.interceptor;

import org.sandhya.airtasker.project.provider.RateLimitProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RateLimitInterceptor implements HandlerInterceptor {
    Logger logger = LoggerFactory.getLogger(APIKeyCheckInterceptor.class);

    @Value("${api_key.header:x-api-key}")
    public String API_KEY_HEADER;

    @Autowired
    RateLimitProvider rateLimitProvider;

    @Override
    public boolean preHandle( HttpServletRequest request, HttpServletResponse response,
                              Object handler) throws Exception {
        String apiKey = request.getHeader(API_KEY_HEADER);
        long winStart = rateLimitProvider.isCallPermitted(apiKey);
        if (winStart == 0) {
            logger.info("Rate limit check passed");
            return true;
        } else {
            logger.info("Rate limit check failed");
            String errorMessage = String.format("Rate limit exceeded. Try again in %d seconds\n", (int)winStart);
            response.getWriter().write(errorMessage);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return false;
        }

    }
        @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {}

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) throws Exception {}
}

