package swingersparadise.app.solutions.novatech.pro.swingersparadise.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.pusher.pushnotifications.fcm.MessagingService;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.Login;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import com.google.firebase.messaging.FirebaseMessagingService;


public class MyFirebaseMessagingService  extends FirebaseMessagingService {
    int  notificationId = 10;
    String channelId  = "swingers_notifications";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

/*      //  Log.i("NotificationsService", "Got a remote message ");
        //remoteMessage.getNotification().getBody()
        /*PendingIntent pIntent = PendingIntent.getActivity(this,
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
        */
        try{
            String notification_title= remoteMessage.getNotification().getTitle();
            String notification_message=remoteMessage.getNotification().getBody();
            String click_action=remoteMessage.getNotification().getClickAction();

            String from_user_id=remoteMessage.getData().get("from_user_id");
            //Log.e("from_user_id in FMS is:",from_user_id);

            //----BUILDING NOTIFICATION LAYOUT----
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(notification_title)
                    .setContentText(notification_message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            //--CLICK ACTION IS PROVIDED---
            Intent resultIntent = new Intent(click_action);
            resultIntent.putExtra("user_id",from_user_id);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);


            int mNotificationId=(int)System.currentTimeMillis();
            NotificationManager mNotifyManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            mNotifyManager.notify(mNotificationId,mBuilder.build());

            // Log.e("from_user_id 5 is:",from_user_id);
        }catch(Exception e){
            Log.e("Exception is ",e.toString());
        }
    }
}
