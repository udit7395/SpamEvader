package com.poseidon.spamevader;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)

public class UtilsTest {

    private Context context;
    private final String SPAM_NUMBER = "91140";

    @Before
    public void setup(){
        context = RuntimeEnvironment.application.getApplicationContext();

        TestUtils.resetDatabaseInstance(DatabaseHelper.class);
    }

    @Test
    public void inputEmptyString_shouldReturnInvalidInput(){
        Assert.assertFalse(Utils.validateUserInput(""));
    }

    @Test
    public void inputEmptySpacesString_shouldReturnInvalidInput(){
        Assert.assertFalse(Utils.validateUserInput("      "));
    }

    @Test
    public void inputNullString_shouldReturnInvalidInput(){
        Assert.assertFalse(Utils.validateUserInput(null));
    }

    @Test
    public void insertBlockedNumberInDB_shouldReturnTrue(){
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);

        databaseHelper.deleteAllSpamNumbers();
        databaseHelper.addSpamNumber(SPAM_NUMBER);

        Assert.assertTrue(Utils.isASpamCall(context, "+" + SPAM_NUMBER));
    }

    @Test
    public void insertNonBlockedNumber_shouldReturnFalse(){
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);

        databaseHelper.deleteAllSpamNumbers();
        databaseHelper.addSpamNumber(SPAM_NUMBER);

        Assert.assertFalse(Utils.isASpamCall(context, "+919004"));
    }
}
