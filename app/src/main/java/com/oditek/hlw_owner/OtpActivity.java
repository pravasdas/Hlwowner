package com.oditek.hlw_owner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chaos.view.PinView;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.oditek.hlw_owner.webservices.AppController;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OtpActivity extends AppCompatActivity {
    TextView tvUserId,tv_Resendotp;
    ImageView ivEdit;
    // String otp = "";
    String mobileNo_ = "";
    private int i = 0;
    NetworkConnection nw;
    Button btnVerify;
    PinView pinView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        nw = new NetworkConnection(this);
        tv_Resendotp = findViewById(R.id.tv_Resendotp);
        btnVerify =  findViewById(R.id.btnVerify);
        pinView =  findViewById(R.id.forgot_otp);
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onBackPressed();
            }
        });

        ivEdit = findViewById(R.id.ivEdit);
        tvUserId = findViewById(R.id.tvUserId);
        Bundle extras = getIntent().getExtras();
        mobileNo_ = extras.getString("MobileNo");
        tvUserId.setText(mobileNo_);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otpTxt = pinView.getText().toString();
                if (otpTxt.length() == 0) {
                    ErrorDialog("Error", "Please enter OTP");
                }else {
                    if (nw.isConnectingToInternet()) {
                        syncVerifyOtpAPI(otpTxt);
                    }else{
                        ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                    }
                }
            }
        });
        tv_Resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nw.isConnectingToInternet()) {
                    syncForgotPasswordAPI();
                }else{
                    ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                }

            }

        });
    }
    private void syncVerifyOtpAPI(final String otpT){
        String tag_json_req = "verify_otp";
        startLoading();

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.verifyOTP_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();
                        try {
                            stopLoading();
                            Log.d("otp response is ", response);
                            JSONObject jsonObject = new JSONObject(response);
                            String statusOtp = jsonObject.getString("status");
                            if("1".equalsIgnoreCase(statusOtp)){
                                String msg_ = jsonObject.getString("msg");
                                Intent in = new Intent(OtpActivity.this, ChangePasswordActivity.class);
                                in.putExtra("MobileNumber", mobileNo_);
                                startActivity(in);
                            }else{
                                pinView.setText("");
                                pinView.requestFocus();
                                String msg_ = jsonObject.getString("msg");
                                ErrorDialog("Error",msg_);
                            }

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
                        syncVerifyOtpAPI(otpT);
                    } else  {
                        ErrorDialog("Error", "Something went wrong...Please try after some time");
                        //progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(OtpActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("mobile", mobileNo_);
                params.put("otp", otpT);
                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }
    //========================================================================
    private void syncForgotPasswordAPI(){
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
                               /* Toast.makeText(OtpActivity.this, jsonObject.getString("msg"),
                                        Toast.LENGTH_LONG).show();*/
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
                        syncForgotPasswordAPI();
                    } else  {
                        ErrorDialog("Error", "Something went wrong...Please try after some time");
                        //progressDialog.dismiss();
                    }
                } else
                    Toast.makeText(OtpActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("mobile",mobileNo_);
                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }
    //============================================================================================
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
  /*  public void SuccessDialog(String title, String subtitle) {
        new iOSDialogBuilder(this)
                .setTitle(title)
                .setSubtitle(subtitle)
                .setBoldPositiveLabel(true)
                .setCancelable(false)
                .setPositiveListener("Ok", new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        Intent in = new Intent(OtpActivity.this, ChangePasswordActivity.class);
                        in.putExtra("MobileNumber", mobileNo_);
                        startActivity(in);
                        dialog.dismiss();
                    }
                })
                .build().show();
    }*/
}
