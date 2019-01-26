package com.poseidon.spamevader;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class IncomingCallReceiver extends BroadcastReceiver {

    private static final String TAG = IncomingCallReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TelephonyManager.EXTRA_STATE_RINGING.equalsIgnoreCase(intent.getStringExtra(TelephonyManager.EXTRA_STATE)) &&
                intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) {
            String incomingNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.d(TAG, "Incoming Number: " + incomingNumber);

            if (incomingNumber != null) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                if (Utils.isASpamCall(context, incomingNumber)) {
                    try {
                        Log.d(TAG, "BlockedCall from: " + incomingNumber);
                        //How to end call programmatically
                        //https://stackoverflow.com/questions/18065144/end-call-in-android-programmatically
                        if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                            Method method = telephonyManager.getClass().getMethod("endCall", null);
                            method.invoke(telephonyManager);
                        } else {
                            TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                            tm.endCall();
                        }
                        notifyUser(context, incomingNumber);
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private void notifyUser(Context context, String incomingNumber) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notificationCompat = new NotificationCompat.Builder(context, Constants.BLOCKED_NOTIFICATION_CHANNEL_ID)
                .setContentTitle("BlockedCall From")
                .setContentText(incomingNumber)
                .setSmallIcon(R.drawable.notify)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notify))
                .build();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(Constants.BLOCKED_NOTIFICATION_CHANNEL_ID,
                    Constants.BLOCKED_NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW);

            notificationChannel.enableVibration(false);
            notificationChannel.enableLights(false);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), notificationCompat);
        }
    }

}
