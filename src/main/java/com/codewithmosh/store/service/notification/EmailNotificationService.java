package com.codewithmosh.store.service.notification;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service("email")
@Primary
public class EmailNotificationService implements NotificationServiceInterface {
    @Override
    public void send(String notification) {
        System.out.println("Sending EMAIL notification with content: " + notification);
    }
}
