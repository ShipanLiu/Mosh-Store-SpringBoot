package com.codewithmosh.store.service.user;

import com.codewithmosh.store.entity.user.User;
import com.codewithmosh.store.repository.UserRepository;
import com.codewithmosh.store.service.notification.NotificationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public UserService(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    public User register(User user) {
        // Check if user already exists by email
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User already exists with email: " + user.getEmail());
        }
        
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + user.getUsername());
        }
        
        // Save user (ID will be auto-generated)
        User savedUser = userRepository.save(user);
        
        // Send welcome notification
        notificationService.sendNotification(
            "Welcome to our store, " + savedUser.getUsername() + 
            "! Your registered email: " + savedUser.getEmail()
        );
        
        return savedUser;
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
