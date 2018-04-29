package com.poseidon.spamevader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by udit on 10/11/17.
 */

public class Adaptor extends ArrayAdapter<String> {

    private Activity activity;
    private final String TAG = Adaptor.class.getName();

    public Adaptor(@NonNull Context context, Activity activity) {
        super(context, 0, new ArrayList<String>());
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String spamNumber = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_layout, parent, false);
            convertView.setTag(new ViewHolder(convertView, position));
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.userSubmittedTextView.setText(spamNumber);
        viewHolder.position = position;

        return convertView;
    }

    private class ViewHolder {
        TextView userSubmittedTextView;
        FrameLayout deleteOneBtnLayout;
        int position;

        ViewHolder(View itemView, int position) {
            this.position = position;
            userSubmittedTextView = (TextView) itemView.findViewById(R.id.spam_number_tv);
            deleteOneBtnLayout = (FrameLayout) itemView.findViewById(R.id.delete_one_btn_layout);

            deleteOneBtnLayout.setOnClickListener(deleteBtnOnClickListener);
        }

        private final View.OnClickListener deleteBtnOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Delete Pressed At position : " + position);
                try {
                    userConfirmationForDeleteDialogBox(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void userConfirmationForDeleteDialogBox(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String item = getItem(position);
                        new DatabaseHelper(activity).deleteSingle(item);
                        remove(item);
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
