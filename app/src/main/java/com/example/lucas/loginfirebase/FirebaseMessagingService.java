package com.example.lucas.loginfirebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //attribute a random different notification ID
        String CHANNEL_ID = "my_channel_01";
        int mNotificationID = (int)System.currentTimeMillis();
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        ///creation d'un channel ID en raison du bug pour les versions android supérieur au SDK 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            /* Create or update. */
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        /// recupération des informations contenues dans la notification
        String click_Action = remoteMessage.getNotification().getClickAction();
        String message = remoteMessage.getData().get("message"); ///type de notification position ou lieu de rendez_vous
        String dataFrom = remoteMessage.getData().get("from_id"); ///personne qui envoie la notification
        String messageTitle = remoteMessage.getNotification().getTitle(); ///titre de la notification définit par firebase cloud function
        String messageBody = remoteMessage.getNotification().getBody(); /// contenu de la notification (envoyé par un user)

        /// creation de la notification avec l'icon, le text contenu et un channel
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageTitle)
                .setAutoCancel(true)
                .setContentText(messageBody)
                .setChannelId(CHANNEL_ID);

        /// lors du click sur la notification on ouvre l'activité adaptée
        Intent intent = new Intent(click_Action); // "com.google.firebase.NotificationTarget"
        /// conduit vers l'interface MapsActivity2
        /// intent pour récupérer les informations de la notif et les envoyé à l'activité MapsActivity2
        intent.putExtra("message",messageBody);
        intent.putExtra("from_user_id",dataFrom);
        intent.putExtra("title", messageTitle);
        intent.putExtra("type", message);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        manager.notify(mNotificationID,mBuilder.build());

    }
}
