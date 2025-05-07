package com.codewithmosh.store;

import com.codewithmosh.store.service.order.OrderService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableConfigurationProperties
public class StoreApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(StoreApplication.class, args);
		System.out.println("Spring Boot Application Started");

//		OrderService orderServicebean = context.getBean(OrderService.class);
//
//		// Call placeOrder method directly, not
//		orderServicebean.placeOrder(100);
//
//		context.close();

	}

}
