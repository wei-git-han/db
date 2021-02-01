/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.css.config;

import java.util.List;

import javax.servlet.MultipartConfigElement;

import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.css.base.filter.CurrentFilter;
import com.css.base.filter.SSOAuthFilter;

/**
 * @author 中软信息系统工程有限公司
 * @since 2015-12-19 16:16
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	/**
	 * 注册静态目录
	 * @param registry
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/","classpath:/META-INF/resources/");
	}
	
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	/**
	 * 配置FastJson为默认的Object转json工具类
	 * @param converters
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		FastJsonHttpMessageConverter fastJsonHttpMessageConverter=new FastJsonHttpMessageConverter();
		converters.add(fastJsonHttpMessageConverter);
		super.configureMessageConverters(converters);
	}
	
	@Bean
	public MultipartConfigElement multipartConfigElement() {
		 MultipartConfigFactory factory = new MultipartConfigFactory();
		 factory.setMaxFileSize("100000000000000KB");
		 factory.setMaxRequestSize("100000000000000KB");
		 return factory.createMultipartConfig();
	}
	
	@Bean  
    public EmbeddedServletContainerCustomizer containerCustomizer() throws Exception {  
        return (ConfigurableEmbeddedServletContainer container) -> {  
            if (container instanceof TomcatEmbeddedServletContainerFactory) {  
                TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;  
                tomcat.addConnectorCustomizers((connector) -> {  
                    connector.setMaxPostSize(256000000); // 10 MB  
                });
            }  
        };
	}

	/**
	 * 配置文件上传大小限制
	 * @return
	 */
	/*@Bean(name = "multipartResolver")
	public CommonsMultipartResolver getMultipartResolver() {
		CommonsMultipartResolver cmr = new CommonsMultipartResolver();
		cmr.setMaxUploadSize(1000000000);
		return cmr;
	}*/
	
	/**
	 * 配置异常Handler
	 * @return
	 */
//	@Bean(name = "handlerExceptionResolver")
//	public ExceptionHandler getExceptionHandler() {
//		ExceptionHandler rh = new ExceptionHandler();
//		return rh;
//	}

	/**
	 * 配置druid监控过滤器
	 * @return
	 */
	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean druidFilter= new FilterRegistrationBean(new WebStatFilter());
		druidFilter.addUrlPatterns("/*");
		druidFilter.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,*.noauth,/druid/*");
		return druidFilter;
	}

	/**
	 * 配置druid监控界面servlet
	 * @return
	 */
	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		return new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
	}
	
	@Bean
	public DruidStatInterceptor getDruidStatInterceptor() {
		return new DruidStatInterceptor();
	}
	
	/**
	 * 配置druid对java代码性能监控
	 * @return
	 */
	@Bean
	public JdkRegexpMethodPointcut getJdkRegexpMethodPointcut(){
		JdkRegexpMethodPointcut jrmp=new JdkRegexpMethodPointcut();
		jrmp.setPatterns("com.css.*");
		jrmp.setExcludedPattern("com.css.websocket.*");
		return jrmp;
	}
	@Bean
	public DefaultPointcutAdvisor getDefaultPointcutAdvisor(){
		DefaultPointcutAdvisor dpa=new DefaultPointcutAdvisor(getJdkRegexpMethodPointcut(), getDruidStatInterceptor());
		return dpa;
	}
	
	@Bean
	public FilterRegistrationBean corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
		bean.setOrder(0);
		return bean;
	}
	@Bean
	public FilterRegistrationBean currentFilter() {
		FilterRegistrationBean bean = new FilterRegistrationBean(new CurrentFilter());
		bean.setOrder(1);
		bean.addUrlPatterns("*");
		return bean;
	}
	@Bean
	public FilterRegistrationBean sSOAuthFilter(Environment env) {
		SSOAuthFilter sSOAuthFilter = new SSOAuthFilter();
		sSOAuthFilter.initFilterBean(env);
		FilterRegistrationBean bean = new FilterRegistrationBean(sSOAuthFilter);
		bean.setOrder(2);
		bean.addUrlPatterns("*");
		return bean;
	}
}
