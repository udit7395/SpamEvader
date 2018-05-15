package com.poseidon.spamevader;

import java.lang.reflect.Field;

public class TestUtils {
    public static void resetDatabaseInstance(Class singletonClass) {
        try {
            Field modifiersField = singletonClass.getDeclaredField("instance");
            modifiersField.setAccessible(true);
            modifiersField.set(DatabaseHelper.class, null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
