package com.poseidon.spamevader;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Button submitButton;
    private Button deleteAllButton;
    private EditText spamNumberEditText;
    private ListView mListView;
    private Adaptor mAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (didUserGivePermission()) {
            init();
        } else {
            requestUserForPermission();
        }
    }

    private void requestUserForPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.MODIFY_PHONE_STATE},
                Constants.REQUEST_CODE);
    }

    private boolean didUserGivePermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    private void init() {
        setContentView(R.layout.activity_main);
        submitButton = (Button) findViewById(R.id.submit_btn);
        deleteAllButton = (Button) findViewById(R.id.delete_all_btn);
        spamNumberEditText = (EditText) findViewById(R.id.edit_text_regex);
        mListView = (ListView) findViewById(R.id.stored_regex_list_view);

        submitButton.setOnClickListener(submitBtnPressedListener);
        deleteAllButton.setOnClickListener(deleteAllBtnPressedListener);
        mListView.setOnItemClickListener(onListViewItemClickListener);

        mAdaptor = new Adaptor(getApplicationContext(), MainActivity.this);
        mAdaptor.clear();
        mAdaptor.addAll(new DatabaseHelper(getApplicationContext()).getAllSpamNumbers());

        mListView.setAdapter(mAdaptor);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "Got RQT CODE : " + requestCode);
        if (requestCode == Constants.REQUEST_CODE) {
            if (didUserGivePermission()) {
                Log.d(TAG, "User Gave Permission");
                init();
            } else {
                Log.d(TAG, "Permission Denied");
                showPermissionDeniedDialogBox();
            }
        }
    }

    private void showPermissionDeniedDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Permission Denied")
                .setMessage("Unable to run application without Phone Permission.\nRestart the app and give permission.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setCancelable(true);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private View.OnClickListener submitBtnPressedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String userSpamNumberInput = spamNumberEditText.getText().toString();
            Log.d(TAG, "The spam number is " + userSpamNumberInput);
            if (!userSpamNumberInput.equalsIgnoreCase("")) {
                Log.d(TAG, "User Entered Spam Number Starts With " + userSpamNumberInput);
                new DatabaseHelper(getApplicationContext()).addSpamNumber(userSpamNumberInput);
                mAdaptor.add(userSpamNumberInput);
                spamNumberEditText.setText("");
                mAdaptor.notifyDataSetChanged();
             }
        }
    };

    private AdapterView.OnItemClickListener onListViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        }
    };

    private View.OnClickListener deleteAllBtnPressedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "Delete All Btn Pressed");
            userConfirmationForDeleteDialogBox();
        }
    };

    private void userConfirmationForDeleteDialogBox() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setMessage("Are you sure you want to delete everything?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new DatabaseHelper(getApplicationContext()).deleteAllSpamNumbers();
                        mAdaptor.clear();
                    }
                })
                .setCancelable(true)
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
