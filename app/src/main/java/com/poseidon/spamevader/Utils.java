package com.poseidon.spamevader;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

public class Utils {

    public static boolean validateUserInput(String userInput) {
        return userInput != null && !userInput.trim().isEmpty();
    }

    public static boolean isASpamCall(Context context, String incomingNumber) {
        boolean isASpamCall = false;
        final ArrayList<String> allSpamNumbers = DatabaseHelper.getInstance(context).getAllSpamNumbers();
        for (int index = 0; index < allSpamNumbers.size(); index++) {
            if (incomingNumber.startsWith(allSpamNumbers.get(index))) {
                isASpamCall = true;
                break;
            }
        }
        return isASpamCall;
    }

    public static void requestUserForPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.MODIFY_PHONE_STATE,
                        Manifest.permission.READ_CALL_LOG},
                Constants.REQUEST_CODE);
    }

    public static boolean didUserGivePermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
    }
}
