package com.codex.foodcaf.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.codex.foodcaf.R;
import com.codex.foodcaf.activity.MainActivity;

public class CartNotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "cart_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // අර යවපු සිග්නල් එකමද කියලා බලනවා
        if ("com.codex.foodcaf.CART_UPDATED".equals(intent.getAction())) {

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Android O (8.0) සහ ඊට ඉහළ නම් Channel එකක් අනිවාර්යයි
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Cart Notifications",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                notificationManager.createNotificationChannel(channel);
            }

            // Notification එක එබුවම ආයෙත් MainActivity එක ඕපන් වෙන්න
            Intent tapIntent = new Intent(context, MainActivity.class);
            tapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, tapIntent, PendingIntent.FLAG_IMMUTABLE);

            // Notification එකේ Design එක
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.shopping_cart_24px) // 🛒 Cart එකේ අයිකන් එක
                    .setContentTitle("Added to Cart! 🛒")
                    .setContentText("The product has been added to your cart. You can buy it now!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true); // එබුවම අයින් වෙලා යන්න

            // Notification එක පෙන්වනවා
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}
