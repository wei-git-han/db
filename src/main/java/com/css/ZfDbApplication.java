package com.css;
import com.css.websocket.ProductWebSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.WebApplicationInitializer;
/**
 * war包和jar包通用配置
 * jar包使用bootRepackage命令：gradlew -b build.gradle bootRepackage
 * war包使用war命令：gradlew -b build-war.gradle war
 */
@SpringBootApplication
@ServletComponentScan
@EnableEurekaClient
@EnableScheduling
@EnableTransactionManagement
public class ZfDbApplication extends SpringBootServletInitializer implements WebApplicationInitializer {
	    @Override
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	        return application.sources(ZfDbApplication.class);
	    }
	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(ZfDbApplication.class, args);
		ProductWebSocket.setApplicationContext(run);
	}
}
