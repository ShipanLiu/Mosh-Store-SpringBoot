package com.codewithmosh.store.service.notification;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private NotificationServiceInterface notificationService;


    public NotificationService(@Qualifier("email") NotificationServiceInterface notificationService) {
        this.notificationService = notificationService;
    }

    //core business logic

    public void sendNotification(String msg) {
        notificationService.send(msg);
    }


    public NotificationServiceInterface getNotificationService() {
        return notificationService;
    }

    public void setNotificationService(NotificationServiceInterface notificationService) {
        this.notificationService = notificationService;
    }
}
