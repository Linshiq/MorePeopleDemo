package com.example.demo.controler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class TestController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public String test() throws Exception {
	
		return "测试";
	}
}
