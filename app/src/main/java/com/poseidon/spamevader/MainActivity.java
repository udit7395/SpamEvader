package com.poseidon.spamevader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private LinearLayout layoutFabAdd;
    private LinearLayout layoutFabDeleteAll;
    private FloatingActionButton fabMenu;
    private FloatingActionButton fabDeleteAll;
    private FloatingActionButton fabAdd;
    private RecyclerView recyclerView;
    private TextView introTV;
    private Adapter mAdaptor;
    private DatabaseHelper databaseHelper;
    private Animation fabOpen, fabClose, fabClockWiseRotation, fabAntiClockWiseRotation;

    private boolean fabMenuExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.didUserGivePermission(MainActivity.this)) {
            initViews();
        } else {
            Utils.requestUserForPermission(MainActivity.this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about_us:
                showAboutUsDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void initViews() {
        setContentView(R.layout.activity_main);

        introTV = findViewById(R.id.intro_tv);
        recyclerView = (RecyclerView) findViewById(R.id.stored_regex_recycler_view);

        layoutFabDeleteAll = findViewById(R.id.layoutFabDeleteAll);
        layoutFabDeleteAll.setVisibility(View.GONE);
        layoutFabAdd = findViewById(R.id.layoutFabAdd);
        layoutFabAdd.setVisibility(View.GONE);

        fabAdd = findViewById(R.id.fab_add);
        fabDeleteAll = findViewById(R.id.fab_delete_all);
        fabMenu = findViewById(R.id.fab);

        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabClockWiseRotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clock_wise);
        fabAntiClockWiseRotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anti_clock_wise);

        fabMenu.setOnClickListener(fabMenuPressedListener);
        layoutFabAdd.setOnClickListener(fabAddPressedListener);
        layoutFabDeleteAll.setOnClickListener(fabDeleteAllPressedListener);

        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        mAdaptor = new Adapter(MainActivity.this, databaseHelper.getAllSpamNumbers());

        handleIntroTextViewVisibility();

        recyclerView.setAdapter(mAdaptor);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "Got RQT CODE : " + requestCode);
        if (requestCode == Constants.REQUEST_CODE) {
            if (Utils.didUserGivePermission(MainActivity.this)) {
                Log.d(TAG, "User Gave Permission");
                initViews();
            } else {
                Log.d(TAG, "Permission Denied");
                showPermissionDeniedDialogBox();
            }
        }
    }

    //DIALOGS
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

    private void userConfirmationForDeleteDialogBox() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setMessage("Are you sure you want to delete everything?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseHelper.deleteAllSpamNumbers();
                        mAdaptor.clear();
                        handleIntroTextViewVisibility();
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

    private void showAboutUsDialog() {
        final LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.view_about_us, null);

        TextView developerDetails = alertLayout.findViewById(R.id.developer_details);
        developerDetails.setText(Constants.DEVELOPER_DETAILS);

        TextView githubDetails = alertLayout.findViewById(R.id.github_details);
        githubDetails.setText(Constants.GITHUB_DEVELOPER_URL);

        Linkify.addLinks(githubDetails, Linkify.ALL);

        TextView flatIconCredits = alertLayout.findViewById(R.id.flaticon_credits);
        flatIconCredits.setText(Html.fromHtml(Constants.ICON_CREDIT_URL));
        flatIconCredits.setMovementMethod(LinkMovementMethod.getInstance());

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Developer Info");
        alert.setView(alertLayout);
        alert.setCancelable(true);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void showUserInputDialog() {
        final LayoutInflater inflater = getLayoutInflater();
        View userInputLayout = inflater.inflate(R.layout.view_user_input, null);

        final EditText userInputEditText = userInputLayout.findViewById(R.id.user_input);

        final AlertDialog.Builder inputDialog = new AlertDialog.Builder(this);
        inputDialog.setMessage("Add SpamNumber To Be Blocked");
        inputDialog.setView(userInputLayout);
        inputDialog.setCancelable(true);
        inputDialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (handleUserInput(userInputEditText.getText().toString())) {
                    handleIntroTextViewVisibility();
                    dialog.dismiss();
                }
            }
        });
        inputDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = inputDialog.create();
        dialog.show();
    }

    //LISTENERS
    private View.OnClickListener fabMenuPressedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: fabMenuPressedListener");
            if (fabMenuExpanded) {
                closeFabSubMenu();
            } else {
                openFabSubMenu();
            }
        }
    };

    private View.OnClickListener fabAddPressedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            closeFabSubMenu();
            showUserInputDialog();
        }
    };

    private View.OnClickListener fabDeleteAllPressedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "Delete All Btn Pressed");
            closeFabSubMenu();
            userConfirmationForDeleteDialogBox();
        }
    };


    private void openFabSubMenu() {
        fabMenuExpanded = true;
        layoutFabAdd.setVisibility(View.VISIBLE);
        layoutFabDeleteAll.setVisibility(View.VISIBLE);
        animateFabMenu();
    }

    private void closeFabSubMenu() {
        fabMenuExpanded = false;
        animateFabMenu();
        layoutFabAdd.setVisibility(View.INVISIBLE);
        layoutFabDeleteAll.setVisibility(View.INVISIBLE);
    }

    private void animateFabMenu() {
        if (fabMenuExpanded) {
            fabMenu.startAnimation(fabClockWiseRotation);
            layoutFabDeleteAll.startAnimation(fabOpen);
            layoutFabAdd.startAnimation(fabOpen);
        } else {
            layoutFabAdd.startAnimation(fabClose);
            layoutFabDeleteAll.startAnimation(fabClose);
            fabMenu.startAnimation(fabAntiClockWiseRotation);
        }
    }

    private boolean handleUserInput(String userInput) {
        boolean userInputAdded;
        Log.d(TAG, "The spam number is " + userInput);
        if (Utils.validateUserInput(userInput)
                && !databaseHelper.doesUserInputExistsInDB(userInput)) {
            Log.d(TAG, "User Entered Spam Number Starts With " + userInput);
            databaseHelper.addSpamNumber(userInput);
            mAdaptor.add(userInput);
            userInputAdded = true;
        } else {
            if (userInput.trim().isEmpty()) {
                Toast.makeText(MainActivity.this, "Enter Some Input", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Input Already Exists", Toast.LENGTH_LONG).show();
            }
            userInputAdded = false;
        }
        return userInputAdded;
    }

    private void handleIntroTextViewVisibility() {
        if (mAdaptor != null) {
            if (mAdaptor.getItemCount() > 0) {
                introTV.setVisibility(View.GONE);
            } else {
                introTV.setVisibility(View.VISIBLE);
            }
        }
    }
}