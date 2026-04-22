package com.easyplan._01_web.in.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexView {

	@GetMapping("/")
	public String indexView() {
		return "randing";
	}
	
	@GetMapping("/dev")
	public String devView() {
		return "dev";
	}
}
