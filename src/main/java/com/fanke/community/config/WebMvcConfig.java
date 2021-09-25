package com.fanke.community.config;

import com.fanke.community.controller.interceptor.AlphaInterceptor;
import com.fanke.community.controller.interceptor.LoginRequiredInterceptor;
import com.fanke.community.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName WebMvcConfig
 * @Author Fanke
 * @Created 2021/9/25 0:21
 */

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AlphaInterceptor alphaInterceptor;
    private final LoginTicketInterceptor loginTicketInterceptor;
    private final LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    public WebMvcConfig (LoginRequiredInterceptor loginRequiredInterceptor,
                             LoginTicketInterceptor loginTicketInterceptor,
                         AlphaInterceptor alphaInterceptor) {
        this.loginRequiredInterceptor = loginRequiredInterceptor;
        this.loginTicketInterceptor = loginTicketInterceptor;
        this.alphaInterceptor = alphaInterceptor;
    }

    /**
     * 注册拦截器
     * 拦截器的注册一般需要配置三个参数：拦截器对象、指定不拦截的路径（一般是静态资源）、指定拦截的路径（不配置则默认全局）
     * @param registry 注册拦截器的对象
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg")
                .addPathPatterns("/register", "/login");

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }

}
