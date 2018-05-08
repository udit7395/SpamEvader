package com.poseidon.spamevader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by udit on 10/11/17.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private Activity activity;
    private final String TAG = Adapter.class.getName();
    private ArrayList<String> listBlockNumbers;

    public Adapter(Activity activity, ArrayList<String> listBlockNumbers) {
        this.activity = activity;
        this.listBlockNumbers = new ArrayList<>();
        this.listBlockNumbers.clear();
        this.listBlockNumbers.addAll(listBlockNumbers);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.view_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (validateEntry(position)) {
            holder.userSubmittedTextView.setText(listBlockNumbers.get(position));
        }
    }

    private boolean validateEntry(int position) {
        return listBlockNumbers.get(position) != null && listBlockNumbers.get(position).trim().length() > 0;
    }

    @Override
    public int getItemCount() {
        return listBlockNumbers.size();
    }

    public void add(String userSpamNumberInput) {
        this.listBlockNumbers.add(userSpamNumberInput);
        notifyItemInserted(getItemCount());
    }

    public void clear() {
        listBlockNumbers.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView userSubmittedTextView;
        FrameLayout deleteOneBtnLayout;

        ViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            this.userSubmittedTextView = (TextView) itemView.findViewById(R.id.spam_number_tv);
            this.deleteOneBtnLayout = (FrameLayout) itemView.findViewById(R.id.delete_one_btn_layout);

            deleteOneBtnLayout.setOnClickListener(deleteBtnOnClickListener);
        }

        private final View.OnClickListener deleteBtnOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    userConfirmationForDeleteDialogBox(getAdapterPosition());
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
                        new DatabaseHelper(activity).deleteSingle(listBlockNumbers.get(position));
                        listBlockNumbers.remove(listBlockNumbers.get(position));
                        notifyItemRemoved(position);
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
