package oditek.com.hlw;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import oditek.com.hlw.webservices.AppController;

public class ChangePhoneOTP extends AppCompatActivity {
    RelativeLayout rl1, rl2, rl3, rl4, rl5, rl6;
    TextView tvRl2, tvEnter, tv1, tvTerms;
    NetworkConnection nw;
    Button btnSubmitChangePhone, btnSubmitForgotPassword;
    AVLoadingIndicatorView progressDialog;
    PinView change_mobile_otp;
    String user_id = "", access_token = "", con_code = "", mobile = "";
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone_otp);

        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");

        nw = new NetworkConnection(oditek.com.hlw.ChangePhoneOTP.this);
        rl1 = findViewById(R.id.rl1);
        rl2 = findViewById(R.id.rl2);
        rl3 = findViewById(R.id.rl3);
        rl4 = findViewById(R.id.rl4);
        rl5 = findViewById(R.id.rl5);
        rl6 = findViewById(R.id.rl6);
        tvRl2 = findViewById(R.id.tvRl2);

        rl1.setVisibility(View.GONE);
        rl2.setVisibility(View.VISIBLE);
        rl3.setVisibility(View.GONE);
        rl4.setVisibility(View.GONE);
        rl5.setVisibility(View.GONE);
        rl6.setVisibility(View.GONE);
        tvRl2.setTypeface(typeFaceLight);
        tvRl2.setText("OTP Verification");

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvEnter = findViewById(R.id.tvEnter);
        tvEnter.setTypeface(typeFaceBold);
        tv1 = findViewById(R.id.tv1);
        tv1.setTypeface(typeFaceLight);
        tvTerms = findViewById(R.id.tvTerms);
        tvTerms.setTypeface(typeFaceLight);
        progressDialog = findViewById(R.id.progressDialog);
        change_mobile_otp = findViewById(R.id.change_mobile_otp);

        btnSubmitChangePhone = findViewById(R.id.btnSubmitChangePhone);
        btnSubmitForgotPassword = findViewById(R.id.btnSubmitForgotPassword);
        btnSubmitChangePhone.setTypeface(typeFaceBold);
        btnSubmitForgotPassword.setTypeface(typeFaceBold);

        Bundle bundle = getIntent().getExtras();
        final String ForgotPassword = bundle.getString("forgot_password");
        final String ChangePhone = bundle.getString("change_phone");
        final String Mobile = bundle.getString("mobile_");

        user_id = oditek.com.hlw.webservices.ApiClient.getDataFromKey(oditek.com.hlw.ChangePhoneOTP.this, "user_id");
        access_token = oditek.com.hlw.webservices.ApiClient.getDataFromKey(oditek.com.hlw.ChangePhoneOTP.this, "access_token");
        con_code = oditek.com.hlw.webservices.ApiClient.getDataFromKey(oditek.com.hlw.ChangePhoneOTP.this, "CountryCode");
        mobile = oditek.com.hlw.webservices.ApiClient.getDataFromKey(oditek.com.hlw.ChangePhoneOTP.this, "mobile");


        if ("1".equalsIgnoreCase(ForgotPassword)) {
            btnSubmitChangePhone.setVisibility(View.GONE);
            btnSubmitForgotPassword.setVisibility(View.VISIBLE);

            btnSubmitForgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String otp = change_mobile_otp.getText().toString().trim();
                    //String mobile_=Mobile;

                    if (otp.length() == 0) {
                        ErrorDialog("Please enter OTP", "");
                    } else if (nw.isConnectingToInternet()) {
                        startLoading();
                        syncVerifyOtpForForgotPasswordData(Mobile, otp);
                    } else {
                        ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                    }
                }
            });
        } else {
            btnSubmitChangePhone.setVisibility(View.VISIBLE);
            btnSubmitForgotPassword.setVisibility(View.GONE);

            if (nw.isConnectingToInternet()) {
                startLoading();
                syncChangeMobileData(user_id, access_token, mobile, con_code);
            } else {
                ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
            }

            btnSubmitChangePhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String otp1 = change_mobile_otp.getText().toString().trim();

                    if (otp1.length() == 0) {
                        ErrorDialog("Please enter OTP", "");
                    } else if (nw.isConnectingToInternet()) {
                        startLoading();
                        syncVerifyOtpData(user_id, access_token, mobile, otp1);
                    } else {
                        ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                    }
                }
            });
        }

    }


    private void syncChangeMobileData(final String user_id, final String access_token,
                                      final String mobile, final String con_code) {
        startLoading();
        String tag_json_req = "sync_ChangeMobileNumber";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.change_mobile_number_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            stopLoading();
                            Log.d("response=========", response);
                            JSONObject jsonObject = new JSONObject(response);
                            System.out.println("json----" + jsonObject);

                            String status_ = jsonObject.getString("status");
                            String msg = jsonObject.getString("msg");

                            if ("0".equalsIgnoreCase(status_)) {
                                ErrorDialog(msg, "Please try again later");
                            } else {
                                Toast.makeText(oditek.com.hlw.ChangePhoneOTP.this, msg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() == null) {
                    if (i < 3) {
                        Log.e("Retry due to error ", "for time : " + i);
                        i++;
                        syncChangeMobileData(user_id, access_token, mobile, con_code);
                    } else {
                        Toast.makeText(oditek.com.hlw.ChangePhoneOTP.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                } else
                    Toast.makeText(oditek.com.hlw.ChangePhoneOTP.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("access_token", access_token);
                params.put("mobile", mobile);
                params.put("con_code", con_code);

                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }

    private void syncVerifyOtpData(final String user_id, final String access_token,
                                   final String mobile, final String otp) {
        startLoading();
        String tag_json_req = "sync_VerifyOtp";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.change_mobile_verify_otp_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            stopLoading();
                            Log.d("response=========", response);
                            JSONObject jsonObject = new JSONObject(response);
                            System.out.println("json----" + jsonObject);

                            String status_ = jsonObject.getString("status");
                            String msg = jsonObject.getString("msg");

                            if ("0".equalsIgnoreCase(status_)) {
                                ErrorDialog(msg, "");
                            } else {
                                Intent intent = new Intent(oditek.com.hlw.ChangePhoneOTP.this, oditek.com.hlw.ChangePhone.class);
                                startActivity(intent);
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() == null) {
                    if (i < 3) {
                        Log.e("Retry due to error ", "for time : " + i);
                        i++;
                        syncVerifyOtpData(user_id, access_token, mobile, otp);
                    } else {
                        Toast.makeText(oditek.com.hlw.ChangePhoneOTP.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                } else
                    Toast.makeText(oditek.com.hlw.ChangePhoneOTP.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("access_token", access_token);
                params.put("mobile", mobile);
                params.put("otp", otp);

                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }

    private void syncVerifyOtpForForgotPasswordData(final String Mobile, final String otp) {
        startLoading();
        String tag_json_req = "sync_VerifyOtpForForgotPassword";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.otp_VerificationForForgotPassword_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            stopLoading();
                            Log.d("response=========", response);
                            JSONObject jsonObject = new JSONObject(response);
                            System.out.println("json----" + jsonObject);

                            String status_ = jsonObject.getString("status");
                            String msg = jsonObject.getString("msg");

                            if ("0".equalsIgnoreCase(status_)) {
                                ErrorDialog(msg, "");
                            } else {
                                new iOSDialogBuilder(oditek.com.hlw.ChangePhoneOTP.this)
                                        .setTitle(msg)
                                        .setSubtitle("")
                                        .setBoldPositiveLabel(true)
                                        .setCancelable(false)
                                        .setPositiveListener("Ok", new iOSDialogClickListener() {
                                            @Override
                                            public void onClick(iOSDialog dialog) {
                                                String changePassword = "1";
                                                dialog.dismiss();
                                                Intent intent = new Intent(oditek.com.hlw.ChangePhoneOTP.this,ChangePassword.class);
                                                intent.putExtra("change_password", changePassword);
                                                intent.putExtra("mobile", Mobile);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .build().show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() == null) {
                    if (i < 3) {
                        Log.e("Retry due to error ", "for time : " + i);
                        i++;
                        syncVerifyOtpForForgotPasswordData(Mobile, otp);
                    } else {
                        Toast.makeText(oditek.com.hlw.ChangePhoneOTP.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                } else
                    Toast.makeText(oditek.com.hlw.ChangePhoneOTP.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("mobile", Mobile);
                params.put("otp", otp);

                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }

    public void ErrorDialog(String title, String subtitle) {
        new iOSDialogBuilder(oditek.com.hlw.ChangePhoneOTP.this)
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
        progressDialog.show();
        progressDialog.setVisibility(View.VISIBLE);
        progressDialog.setIndicator("Loading...");
    }

    void stopLoading() {
        progressDialog.hide();
        progressDialog.setVisibility(View.GONE);
    }
}
