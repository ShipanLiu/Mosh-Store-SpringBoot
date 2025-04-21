package com.codewithmosh.store.controller;

import com.codewithmosh.store.service.notification.NotificationManagerService;
import com.codewithmosh.store.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @Value("${spring.application.name}")
    private String appName;

    private OrderService orderService;
    private NotificationManagerService notificationManagerService;

    @Autowired
    public HomeController(
            OrderService orderService,
            NotificationManagerService notificationManagerService) {
        this.orderService = orderService;
        this.notificationManagerService = notificationManagerService;
    }

    @GetMapping("/")
    public String index() {
        orderService.placeOrder(300);
        return "index.html";
    }

    @GetMapping("/notify")
    public String sendNotification() {
        notificationManagerService.sendNotification("hallo world, this is a notification");
        return "index.html";
    }
}
