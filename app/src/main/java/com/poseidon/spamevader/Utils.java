package com.poseidon.spamevader;

import android.content.Context;

import java.util.ArrayList;

public class Utils {

    public static boolean validateUserInput(String userInput){
        return userInput!= null && !userInput.isEmpty();
    }

    public static boolean isASpamCall(Context context, String incomingNumber) {
        boolean isASpamCall = false;
        final ArrayList<String> allSpamNumbers = DatabaseHelper.getInstance(context).getAllSpamNumbers();
        for (int index = 0; index < allSpamNumbers.size(); index++) {
            if (incomingNumber.startsWith("+" + allSpamNumbers.get(index))) {
                isASpamCall = true;
            }
        }
        return isASpamCall;
    }
}
