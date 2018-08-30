package swingersparadise.app.solutions.novatech.pro.swingersparadise.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import com.pusher.pushnotifications.fcm.MessagingService;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.Login;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;

public class MyFirebaseMessagingService  extends MessagingService {
    int  notificationId = 10;
    String channelId  = "swingers_notifications";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
      //  Log.i("NotificationsService", "Got a remote message 🎉");
        //remoteMessage.getNotification().getBody()
        PendingIntent pIntent = PendingIntent.getActivity(this,
                (int) System.currentTimeMillis(), new Intent(this, Login.class), 0);

        Notification n  = new Notification.Builder(this)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);


    }
}