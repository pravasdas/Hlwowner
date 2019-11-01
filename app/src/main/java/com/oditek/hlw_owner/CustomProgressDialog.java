package com.oditek.hlw_owner;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

public class CustomProgressDialog extends ProgressDialog {
    Context context;
    boolean isShowing;

    public static ProgressDialog custom(Context context) {
        CustomProgressDialog dialog = new CustomProgressDialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }

    public CustomProgressDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_progress_dialog);
    }

    @Override
    public void show() {
        super.show();
        isShowing = true;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        isShowing = false;

    }
}
