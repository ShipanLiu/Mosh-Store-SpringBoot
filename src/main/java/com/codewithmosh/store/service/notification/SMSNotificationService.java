package com.codewithmosh.store.service.notification;

import org.springframework.stereotype.Service;

@Service("sms")
public class SMSNotificationService implements NotificationServiceAPI {
    @Override
    public void send(String notification) {
        System.out.println("Sending SMS notification with content: " + notification);
    }
}
