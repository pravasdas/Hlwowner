package com.oditek.hlw_owner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class ForgotPasswordActivity extends AppCompatActivity {
    Button btnGenerateOTP;
    EditText etUserId;
    ProgressDialog progressDialog;
    private int i = 0;
    NetworkConnection nw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        nw = new NetworkConnection(this);
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        etUserId = findViewById(R.id.etUserId);
        btnGenerateOTP = findViewById(R.id.btnGenerateOTP);
        btnGenerateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile_ = etUserId.getText().toString().trim();
                if (mobile_.length() == 0) {
                    ErrorDialog("Error", "Please enter the credentials");
                } else if (!isValidPhoneNumber(mobile_)) {
                    ErrorDialog("Error", "Please enter valid number");
                }
                else if (nw.isConnectingToInternet()) {
                    syncForgotPasswordAPI(mobile_);
                } else {
                    ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                }
            }
        });

    }
    private void syncForgotPasswordAPI(final String Mobile){
        String tag_json_req = "forgot_password";
        startLoading();

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.forgot_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            stopLoading();
                            Log.d("forgot response is ", response);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                               /* Toast.makeText(ForgotPasswordActivity.this, jsonObject.getString("msg"),
                                        Toast.LENGTH_LONG).show();*/
                                String mobileNo = etUserId.getText().toString().trim();
                                Intent in = new Intent(ForgotPasswordActivity.this, OtpActivity.class);
                                in.putExtra("MobileNo", mobileNo);
                                startActivity(in);

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
                    //progerssDialog.hide();
                    if (i < 3) {
                        Log.e("Retry due to error ", "for time : " + i);
                        i++;
                        syncForgotPasswordAPI(Mobile);
                    } else  {
                        ErrorDialog("Error", "Something went wrong...Please try after some time");
                        //progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(ForgotPasswordActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("mobile",Mobile);
                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }
    //==============================================================================================
    public static boolean isValidPhoneNumber(String phone) {
        boolean check;
        if (phone.length() < 10 || phone.length() > 12) {
            check = false;
        } else {
            check = true;
        }
        return check;
    }

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
}
