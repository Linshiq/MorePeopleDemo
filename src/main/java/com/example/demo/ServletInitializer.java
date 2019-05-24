package com.example.demo;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * <p>
 * 文件功能说明：
 *       			
 * </p>
 * 
 * @Author linshiqin
 *         <p>
 *         <li>2019年5月22日-下午5:39:08</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>linshiqin：外置服务调用使用该类 ,例如tomcat</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
@SpringBootApplication // 打包到服务器要记得把他补上
//@EnableAutoConfiguration // 本人表示，一定要有这个标签(不然，你会吃亏的)
public class ServletInitializer extends SpringBootServletInitializer {
	
	private static final Logger logger = LoggerFactory.getLogger(ServletInitializer.class);
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		
		SpringApplicationBuilder context = application.sources(ServletInitializer.class);
		logger.info("spring boot 外置服务器启动成功");
		logger.info("上下文加载:"+context.context());
		
		return context;
	}
	
	public static void main(String[] args) {
		 // 把Application的标签移动到这里,这样可以兼容内外置tomcat启动方式 ,也可以防止test类启动失败
		 ConfigurableApplicationContext context = SpringApplication.run(ServletInitializer.class, args);
		// Application.applicationContext = context;
		 logger.info("spring boot 内置服务器启动成功");
	}
}
