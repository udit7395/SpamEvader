package com.poseidon.spamevader;

import android.content.Context;
import android.database.Cursor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class DatabaseHelperTest {

    private Context context;
    private final String SPAM_NUMBER = "+91140";

    @Before
    public void setup() {
        context = RuntimeEnvironment.application.getApplicationContext();

        TestUtils.resetDatabaseInstance(DatabaseHelper.class);
    }

    @Test
    public void getDatabaseHelperInstance_shouldCreateTable() {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        final Cursor cursor = databaseHelper.getReadableDatabase().query(DatabaseHelper.TableSpam.TABLE_NAME,
                null, null, null, null, null, null, null);

        Assert.assertNotNull(cursor);
    }

    @Test
    public void deleteAllData_shouldNotHaveAnyDataInTable() {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);

        databaseHelper.addSpamNumber(SPAM_NUMBER);
        databaseHelper.deleteAllSpamNumbers();

        Assert.assertTrue(databaseHelper.getAllSpamNumbers().size() == 0);
    }

    @Test
    public void insertData_dataEntryShouldExistInTable(){
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);

        databaseHelper.deleteAllSpamNumbers();
        databaseHelper.addSpamNumber(SPAM_NUMBER);

        Assert.assertTrue(databaseHelper.getAllSpamNumbers().size() > 0);
    }

    @Test
    public void insertDataAlreadyExistsInTable_shouldNotBeAbleToAddEntryInTable(){
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);

        databaseHelper.deleteAllSpamNumbers();

        databaseHelper.addSpamNumber(SPAM_NUMBER);

        if(!databaseHelper.doesUserInputExistsInDB(SPAM_NUMBER)){
            databaseHelper.addSpamNumber(SPAM_NUMBER);
        }

        Assert.assertTrue((databaseHelper.getAllSpamNumbers().size() == 1));
    }
}
