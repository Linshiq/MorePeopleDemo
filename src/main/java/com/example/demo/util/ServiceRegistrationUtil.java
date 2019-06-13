package com.example.demo.util;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import com.example.demo.annotation.ServiceCode;

/**
 * <p>
 * 文件功能说明：
 *       			
 * </p>
 * 
 * @Author linshiqin
 *         <p>
 *         <li>2019年6月13日-下午3:11:44</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>linshiqin：服务注册类</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
@Component
public class ServiceRegistrationUtil extends ApplicationObjectSupport{
	
	// 登记所有的服务
	private final Map<String,Class<?>> serviceCodeMap = new HashMap<>();
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceRegistrationUtil.class);
	
	/**
	 * @Author linshiqin
	 *         <p>
	 *         <li>2019年6月13日-下午4:01:17</li>
	 *         <li>功能说明：获取对应服务class</li>
	 *         </p>
	 * @param serviceCode 服务码
	 * @return
	 */
	public Class<?> getServiceClass(String serviceCode){
		return serviceCodeMap.get(serviceCode);
	}
	
	/**
	 * @Author linshiqin
	 *         <p>
	 *         <li>2019年6月13日-下午4:01:38</li>
	 *         <li>功能说明：服务注册</li>
	 *         </p>
	 * @throws BeansException
	 */
	@PostConstruct
	private void serviceRegistration() throws BeansException{
		
		logger.info("服务注册开始");
		Map<String, Object> beans = getApplicationContext().getBeansWithAnnotation(ServiceCode.class);
		
		for (String key:beans.keySet()) {
			ServiceCode serviceCode = AnnotationUtils.findAnnotation(beans.get(key).getClass(), ServiceCode.class);
			
			String serviceCodeVal = serviceCode.value();
			
			if(serviceCodeMap.containsKey(serviceCodeVal)){
				throw new RuntimeException("存在相同的服务ID:"+serviceCodeVal);
			}
			
			Class<?> classV = getApplicationContext().getType(key);
			logger.info("服务"+serviceCodeVal+ "注册成功");
			logger.info("服务位于"+classV);
			serviceCodeMap.put(serviceCodeVal, classV);
		}
		logger.info("服务注册结束");
	}
}
