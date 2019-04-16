package com.css.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.css.addbase.token.TokenConfig;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
@Configuration
public class WebConfig {

	/**
	 * 验证码
	 * @return
	 */
    @Bean(name = "producer")  
    public DefaultKaptcha getLifecycleBeanPostProcessor() {  
    	Properties properties=new Properties();
    	properties.setProperty("kaptcha.border", "no");
    	properties.setProperty("kaptcha.textproducer.font.color", "black");
    	properties.setProperty("kaptcha.textproducer.char.space", "5");
    	Config config=new Config(properties);
    	DefaultKaptcha kaptcha= new DefaultKaptcha();
    	kaptcha.setConfig(config);
        return kaptcha;  
    }
    
    /**
     * token配置类初始化参数
     * @param env
     * @return
     */
   @Bean
	public TokenConfig tokenConfig(Environment env) {
   	TokenConfig tokenConfig = new TokenConfig(env);
		return tokenConfig;
	}
}
