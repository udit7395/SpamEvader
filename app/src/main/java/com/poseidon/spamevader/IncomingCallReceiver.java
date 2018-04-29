package com.poseidon.spamevader;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class IncomingCallReceiver extends BroadcastReceiver {

    private static final String TAG = IncomingCallReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        if (TelephonyManager.EXTRA_STATE_RINGING.equalsIgnoreCase(stateStr)) {
            Log.d(TAG, "Inside PhoneRinging State");

            String incomingNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.d(TAG, "THe incoming number is " + incomingNumber);

            if (incomingNumber != null) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                if (isASpamCall(context, incomingNumber)) {
                    try {
                        Log.d(TAG, "Blocked Spam Call from " + incomingNumber);
                        //How to end call programmatically
                        //https://stackoverflow.com/questions/18065144/end-call-in-android-programmatically
                        Method method = telephonyManager.getClass().getMethod("endCall", null);
                        method.invoke(telephonyManager);

                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                        Notification notificationCompat = new NotificationCompat.Builder(context, Constants.BLOCKED_NOTIFICATION_CHANNEL_ID)
                                .setContentTitle("Blocked Spam Call")
                                .setContentText(incomingNumber)
                                .setSmallIcon(R.drawable.announcement_black)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.announcement_black))
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
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private boolean isASpamCall(Context context, String incomingNumber) {
        boolean isASpamCall = false;
        for (String spamNumberStartsWith : new DatabaseHelper(context).getAllSpamNumbers()) {
            if (spamNumberStartsWith != null && incomingNumber.startsWith("+" + spamNumberStartsWith)) {
                isASpamCall = true;
            }
        }
        return isASpamCall;
    }
}
