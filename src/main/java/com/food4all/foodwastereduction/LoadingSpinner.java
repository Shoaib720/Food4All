package com.food4all.foodwastereduction;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class LoadingSpinner {

    private Activity mActivity;
    private AlertDialog mDialog;
    private String mLoadingMessage;
    private TextView tvLoadingMessage;

    public LoadingSpinner(Activity activity, String loadingMessage){
        this.mActivity = activity;
        this.mLoadingMessage = loadingMessage;
    }

    public void startLoadingSpinner(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_progress_bar, null);
        tvLoadingMessage = (TextView) view.findViewById(R.id.tv_loading_message);
        tvLoadingMessage.setText(mLoadingMessage);
        builder.setView(view);
        builder.setCancelable(false);
        mDialog = builder.create();
        mDialog.show();
    }

    public void stopLoadingSpinner(){
        mDialog.dismiss();
    }
}
