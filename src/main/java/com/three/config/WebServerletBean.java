package com.three.config;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebServerletBean extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // login页面在 templates 文件夹下
        registry.addViewController("/").setViewName("index");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        super.addViewControllers(registry);
    }

    //单点退出，可选
    @Bean
    public ServletListenerRegistrationBean singleSignOutListener(){
        ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> listenerRegistrationBean
                =new ServletListenerRegistrationBean<>(new SingleSignOutHttpSessionListener());
        return listenerRegistrationBean;
    }

    //单点退出，可选
    @Bean
    public FilterRegistrationBean singleSignOutFilter(){
        FilterRegistrationBean filterRegistrationBean=new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new SingleSignOutFilter());
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
        return filterRegistrationBean;
    }

    //认证过滤器，必须
    @Bean
    public FilterRegistrationBean authenticationFilter(){
        FilterRegistrationBean filterRegistrationBean=new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new AuthenticationFilter());
        Map initParam=new HashMap();
        //服务端登录界面
        initParam.put("casServerLoginUrl","http://192.168.135.101:8080/cas/login");
        //当前项目根地址
        initParam.put("serverName","http://localhost:8080");
        filterRegistrationBean.setInitParameters(initParam);
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
        return filterRegistrationBean;
    }

    //Ticket的校验工作，必须
    @Bean
    public FilterRegistrationBean cas20ProxyFilter(){
        FilterRegistrationBean filterRegistrationBean=new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new Cas20ProxyReceivingTicketValidationFilter());
        Map initParam=new HashMap();
        //服务端地址
        initParam.put("casServerUrlPrefix","http://192.168.135.101:8080/cas");
        //当前项目根地址
        initParam.put("serverName","http://localhost:8080");
        filterRegistrationBean.setInitParameters(initParam);
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
        return filterRegistrationBean;
    }

    //通过HttpServletRequest的getRemoteUser()方法获得SSO登录用户的登录名，可选
    @Bean
    public FilterRegistrationBean wrapperFilter(){
        FilterRegistrationBean filterRegistrationBean=new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new HttpServletRequestWrapperFilter());
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
        return filterRegistrationBean;
    }
}
