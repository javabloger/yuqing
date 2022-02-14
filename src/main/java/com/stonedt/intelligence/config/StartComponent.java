package com.stonedt.intelligence.config;

import com.stonedt.intelligence.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 1)
public class StartComponent implements ApplicationRunner{
	
	@Autowired
    UserService userService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		userService.setAlloffline();
	}
}
