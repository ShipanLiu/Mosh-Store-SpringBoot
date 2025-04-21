package com.codewithmosh.store.service.notification;

import org.springframework.stereotype.Service;

@Service("email")
public class EmailNotificationService implements NotificationServiceAPI {
    @Override
    public void send(String notification) {
        System.out.println("Sending email notification with content: " + notification);
    }
}
