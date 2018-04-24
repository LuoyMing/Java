package com.seud.fyb.article;


import javax.servlet.Filter;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.seud.fyb.framework.utils.DBHelper;
import com.wfw.common.WfwApplication;
import com.wfw.common.utils.ContextUtils;
import com.wfw.proxyhandler.annotation.EnableWfwConfig;


@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@SpringBootApplication
@EnableWfwConfig(basePackages="com.seud.fyb.feignclient")
@ComponentScan(basePackages={"com.wfw.common.spring","com.seud.fyb.workflow.business","com.seud.fyb.article","com.seud.fyb.feignclient", "com.seud.fyb.framework.cache.redis"})
public class ArticleApplication extends SpringBootServletInitializer{
	
	private static final Logger log = LoggerFactory.getLogger(ArticleApplication.class);
	
	@Override
    protected SpringApplicationBuilder  configure(SpringApplicationBuilder application) {
		return application.sources(ArticleApplication.class);
	}
	
	public static void main(String[] args) {
		WfwApplication.run(ArticleApplication.class, false, args);
		ApplicationContext applicationContext = SpringApplication.run(ArticleApplication.class, args);
//		ContextUtils.setApplicationContext(applicationContext);
		log.debug("ArticleApplication ApplicationContext registed");
	}
	
	@Autowired
    private Environment env;
	
	@Bean
    public String dialect() {
		return DBHelper.getDialect(env);
	}
	
	@Bean
    public DataSource dataSource() {
		return DBHelper.getDruidDataSource(env);
	}
	
	@Bean(name="defaultLobHandler")
	public DefaultLobHandler defaultLobHandler() {
		DefaultLobHandler defaultLobHandler = new DefaultLobHandler();
		return defaultLobHandler;
	}
	
	
	@Bean
	public Filter characterEncodingFilter() {
		  CharacterEncodingFilter characterEncodingFilter =new CharacterEncodingFilter();
		  characterEncodingFilter.setEncoding("UTF-8");
		  characterEncodingFilter.setForceEncoding(true);
		  return characterEncodingFilter;
	}
	
}
