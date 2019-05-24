package com.example.demo.controler;


import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.example.demo.exception.ServiceException;
import com.example.demo.exception.ServiceExceptionCode;

/**
 * <p>
 * 文件功能说明：
 * 
 * </p>
 * 
 * @Author linshiqin
 *         <p>
 *         <li>2019年5月14日-下午4:27:29</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>linshiqin：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
@Controller
@RequestMapping("/upload")
public class UploadService {

	// @Bean
	// public MultipartConfigElement multipartConfigElement() {
	// return new MultipartConfigElement("");
	// }


	/**
	 * @Author linshiqin
	 *         <p>
	 *         <li>2019年5月14日-下午4:31:03</li>
	 *         <li>功能说明：上传账单excel</li>
	 *         </p>
	 * @param file
	 * @param input
	 * @return
	 */
	@PostMapping("/uuuu")
	@ResponseBody
	public String upload(@RequestParam(value = "files", required = false) MultipartFile[] files,
			HttpServletRequest request) {

		if (files == null || files.length <= 0) {
			System.out.println("失败");
			throw new ServiceException(ServiceExceptionCode.FILE_ERROR_0001);
		}

		// printInfo(request);
		try {

			Map<String,Object> result = new HashMap<>();
			
			result.put("info", "上传成功");
			result.put("successCount", 1);
			result.put("failureCount", 1);
			result.put("result", 1);
			
			return JSON.toJSONString(result);

		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}

	}

	/**
	 * @Author linshiqin
	 *         <p>
	 *         <li>2019年5月15日-上午10:51:56</li>
	 *         <li>功能说明：使用list的循环</li>
	 *         </p>
	 * @param request
	 */
	private void printInfo(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();

			String[] paramValues = request.getParameterValues(paramName);
			if (paramValues.length > 0) {
				String paramValue = paramValues[0];
				if (paramValue.length() != 0) {
					map.put(paramName, paramValue);
				}
			}
		}

		Set<Map.Entry<String, Object>> set = map.entrySet();
		System.out.println("==============================================================");
		for (Map.Entry entry : set) {
			MultipartFile file;
			Object obj = entry.getValue();
			if (obj.getClass().getTypeName().equals("MultipartFile")) {
				file = (MultipartFile) obj;
				System.out.println("file:" + file.getOriginalFilename());
			}
			System.out.println(entry.getKey() + ":" + entry.getValue());

			System.out.println(entry.getValue().getClass().getTypeName());
		}
	}

	@PostMapping("/uuu11")
	@ResponseBody
	public String upload1() {

		return "上传失败！";
	}
}
