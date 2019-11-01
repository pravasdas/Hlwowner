package com.oditek.hlw_owner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.oditek.hlw_owner.webservices.AppController;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText newPass, confirmPass;
    private Button updatePass;
    ProgressDialog progressDialog;
    private int i = 0;
    NetworkConnection nw;
    String mobileNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        nw = new NetworkConnection(this);
        newPass = findViewById(R.id.etNewPass);
        confirmPass = findViewById(R.id.etConPass);
        updatePass = findViewById(R.id.btnUpdatePassword);
        Bundle extras = getIntent().getExtras();
        mobileNo = extras.getString("MobileNumber");
        updatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPassTxt = newPass.getText().toString().trim();
                String confirmPassTxt = confirmPass.getText().toString().trim();

                if (newPassTxt.length() == 0) {
                    ErrorDialog("Error", "Please enter the new password");
                } else if (confirmPassTxt.length() == 0) {
                    ErrorDialog("Error", "Please enter confirm password");
                } else if(!newPassTxt.equalsIgnoreCase(confirmPassTxt)){
                    ErrorDialog("Error", "New Password & Confirm Password are not matching");
                } else{
                    if (nw.isConnectingToInternet()) {
                        syncUpdatePasswordAPI();
                    }else{
                        ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                    }
                }
            }
        });
    }
    //==============================================================================================
    private void syncUpdatePasswordAPI(){
        String tag_json_req = "update_password";
        startLoading();
        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.updatePassword_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            stopLoading();
                            Log.d("forgot response is ", response);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                                SuccessDialog("Success", jsonObject.getString("msg"));
                            }else
                                ErrorDialog("Error", jsonObject.getString("msg"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() == null) {
                    stopLoading();
                    if (i < 3) {
                        Log.e("Retry due to error ", "for time : " + i);
                        i++;
                        syncUpdatePasswordAPI();
                    } else  {
                        ErrorDialog("Error", "Something went wrong...Please try again after some time");
                        //progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(ChangePasswordActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

           /* @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("API_KEY", api);
                return headers;
            }*/

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("mobile", mobileNo);
                params.put("password", newPass.getText().toString().trim());

                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }
    //===============================================================================================
    public void ErrorDialog(String title, String subtitle) {
        new iOSDialogBuilder(this)
                .setTitle(title)
                .setSubtitle(subtitle)
                .setBoldPositiveLabel(true)
                .setCancelable(false)
                .setPositiveListener("Ok", new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .build().show();
    }
    void startLoading() {
        progressDialog = CustomProgressDialog.custom(this);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    void stopLoading() {
        if ((progressDialog != null) && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    public void SuccessDialog(String title, String subtitle) {
        new iOSDialogBuilder(this)
                .setTitle(title)
                .setSubtitle(subtitle)
                .setBoldPositiveLabel(true)
                .setCancelable(false)
                .setPositiveListener("Ok", new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        Intent in = new Intent(ChangePasswordActivity.this,LoginMainActivity.class);
                        startActivity(in);
                        dialog.dismiss();
                    }
                })
                .build().show();
    }
}
