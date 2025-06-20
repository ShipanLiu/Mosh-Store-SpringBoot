package com.codewithmosh.store.controller;

import com.codewithmosh.store.entity.User;
import com.codewithmosh.store.service.notification.NotificationService;
import com.codewithmosh.store.service.order.OrderService;
import com.codewithmosh.store.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class HomeController {

    @Value("${spring.application.name}")
    private String appName;

    private OrderService orderService;
    private NotificationService notificationManagerService;
    private UserService userService;

    public HomeController(OrderService orderService, NotificationService notificationManagerService, UserService userService) {
        this.orderService = orderService;
        this.notificationManagerService = notificationManagerService;
        this.userService = userService;
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

    // for testing the register user + get notifications
    @GetMapping("/register")
    public ResponseEntity<String> registerUser() {
        try {
            // Create user without ID (auto-generated) and with proper field names
            User newUser = new User("shipanliu", "shipan@example.com", "Shipan", "Liu", "hashedPassword123");
            userService.register(newUser);
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error registering user: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/user-info")
    @ResponseBody
    public User getUserInfo() {
        // This method demonstrates the use of @ResponseBody
        // The User object will be automatically converted to JSON
        // Create user without ID (auto-generated) and with proper field names
        return new User("example_user", "user@example.com", "John", "Doe", "hashedPassword123");
    }
}
