package com.codewithmosh.store.service.notification;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class NotificationManagerService {
    private NotificationServiceAPI notificationService;


    public NotificationManagerService(@Qualifier("email") NotificationServiceAPI notificationService) {
        this.notificationService = notificationService;
    }

    //core business logic

    public void sendNotification(String msg) {
        notificationService.send(msg);
    }













    public NotificationServiceAPI getNotificationService() {
        return notificationService;
    }

    public void setNotificationService(NotificationServiceAPI notificationService) {
        this.notificationService = notificationService;
    }
}
