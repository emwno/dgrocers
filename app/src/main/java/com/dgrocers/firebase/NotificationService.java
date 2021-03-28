package com.dgrocers.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.dgrocers.R;
import com.dgrocers.ui.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class NotificationService extends FirebaseMessagingService {

	@Override
	public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);

		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("notification_order_id", remoteMessage.getData().getOrDefault("orderId", ""));
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_UPDATE_CURRENT);

		String channelId = getString(R.string.app_name);
		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(this, channelId)
						.setSmallIcon(R.mipmap.ic_launcher)
						.setContentTitle(remoteMessage.getNotification().getTitle())
						.setContentText(remoteMessage.getNotification().getBody())
						.setAutoCancel(true)
						.setContentIntent(pendingIntent);

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(channelId, channelId, IMPORTANCE_DEFAULT);
			notificationManager.createNotificationChannel(channel);
		}

		notificationManager.notify(0, notificationBuilder.build());
	}


	@Override
	public void onNewToken(@NonNull String s) {
		super.onNewToken(s);
	}
}
