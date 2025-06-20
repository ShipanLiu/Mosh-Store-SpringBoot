package com.codewithmosh.store.service.user;

import com.codewithmosh.store.entity.User;
import com.codewithmosh.store.repository.InMemoryUserRepository;
import com.codewithmosh.store.repository.UserRepository;
import com.codewithmosh.store.service.notification.EmailNotificationService;
import com.codewithmosh.store.service.notification.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private NotificationService notificationService;
    private UserRepository userRepository;

    public UserService(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    public void register(User user) {

        if (userRepository.findByEmail((user.getEmail())) != null) {
            throw new IllegalArgumentException("User already exists: " + user.getEmail());
        }
        userRepository.save(user);
        notificationService.sendNotification("Welcome to our store, " + user.getUsername() + " your registered Email: " + user.getEmail());
    }
}
