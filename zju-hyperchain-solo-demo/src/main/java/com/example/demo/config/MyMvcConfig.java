package com.example.demo.config;

import com.example.demo.component.LoginHandlerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginHandlerInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/","/loginPage", "/registerPage", "/forgetPage", "/forgetPwdPage")
                .excludePathPatterns("/login", "/register", "/forgetPwd")
                .excludePathPatterns("/static/**");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
    }
}
