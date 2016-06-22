package com.ares.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
public class VersionController
{
	private static final String ENV_PROPERTY_KEY="env";

	@RequestMapping("/version")
	@ResponseBody
	public String getVersion() {
		return  "1";
	}

	@RequestMapping("/env")
	@ResponseBody
	public String getEnvironment() {
		return System.getProperty( ENV_PROPERTY_KEY );
	}
}
