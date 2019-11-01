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
import com.hbb20.CountryCodePicker;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import oditek.com.hlw.webservices.AppController;

public class LoginOtp extends AppCompatActivity {
    RelativeLayout rl1, rl2, rl3, rl4, rl5, rl6;
    TextView tvRl2, tvNumber, tvLogin_Password, tvTop, tvResendOTP, tvOR, tv1, tvTerms, tv2, tvPolicy, tv3;
    Button btnOTPLogin;
    NetworkConnection nw;
    CountryCodePicker CountryCode;
    private int i = 0;
    AVLoadingIndicatorView progressDialog;
    PinView login_otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");

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
        tvRl2.setText("Login with OTP");
        tvRl2.setTypeface(typeFaceLight);
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //Toolbar End
        nw = new NetworkConnection(oditek.com.hlw.LoginOtp.this);
        progressDialog = findViewById(R.id.progressDialog);
        CountryCode = findViewById(R.id.CountryCode);

        login_otp = findViewById(R.id.login_otp);
        tvNumber = findViewById(R.id.tvNumber);
        tvNumber.setTypeface(typeFaceBold);
        tvTop = findViewById(R.id.tvTop);
        tvTop.setTypeface(typeFaceBold);
        tvLogin_Password = findViewById(R.id.tvLogin_Password);
        tvLogin_Password.setTypeface(typeFaceBold);
        tvResendOTP = findViewById(R.id.tvResendOTP);
        tvResendOTP.setTypeface(typeFaceBold);
        btnOTPLogin = findViewById(R.id.btnOTPLogin);
        btnOTPLogin.setTypeface(typeFaceBold);
        tvOR = findViewById(R.id.btnOTPLogin);
        tvOR.setTypeface(typeFaceBold);
        tv1 = findViewById(R.id.tv1);
        tv1.setTypeface(typeFaceLight);
        tvTerms = findViewById(R.id.tvTerms);
        tvTerms.setTypeface(typeFaceLight);
        tv2 = findViewById(R.id.tv2);
        tv2.setTypeface(typeFaceLight);
        tvPolicy = findViewById(R.id.tvPolicy);
        tvPolicy.setTypeface(typeFaceLight);
        tv3 = findViewById(R.id.tv3);
        tv3.setTypeface(typeFaceLight);

        Bundle bundle = getIntent().getExtras();
        final String number = bundle.getString("number");
        final String concode = bundle.getString("con_code");
        tvNumber.setText(number);


        btnOTPLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String otp = login_otp.getText().toString().trim();

                if (otp.length() == 0) {
                    ErrorDialog("Please enter OTP", "");
                } else if (nw.isConnectingToInternet()) {
                    startLoading();
                    syncVerifyOtpData(number, otp);
                } else {
                    ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                }
            }
        });

        tvLogin_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(oditek.com.hlw.LoginOtp.this, LoginWithPass.class);
                intent.putExtra("number", number);
                startActivity(intent);
            }
        });

        tvResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (nw.isConnectingToInternet()) {
                    syncResendOtp(concode, number);
                } else {
                    ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                }
            }
        });
    }

    private void syncResendOtp(final String con_code, final String mobile_) {
        startLoading();
        String tag_json_req = "sync_ResendOtp";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.resend_OTP_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            stopLoading();
                            Log.d("response=========", response);
                            JSONObject jsonObject = new JSONObject(response);
                            System.out.println("json----" + jsonObject);

                            String status_ = jsonObject.getString("status");
                            String msg_ = jsonObject.getString("msg");

                            if ("1".equalsIgnoreCase(status_)) {
                                ErrorDialog("OTP sent successfully to", mobile_);
                            } else {
                                ErrorDialog("Failed", "Please try after sometime");
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
                        syncResendOtp(con_code, mobile_);
                    } else {
                        Toast.makeText(oditek.com.hlw.LoginOtp.this, "Please try after sometime...",
                                Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                } else
                    Toast.makeText(oditek.com.hlw.LoginOtp.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("con_code", con_code);
                params.put("mobile", mobile_);
                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }

    private void syncVerifyOtpData(final String Mobile, final String OTP) {
        startLoading();
        String tag_json_req = "sync_VerifyOtp";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.verify_OTP_url),
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
                                String name = jsonObject.getString("name");
                                String user_id = jsonObject.getString("user_id");
                                String access_token = jsonObject.getString("access_token");
                                String mobile = jsonObject.getString("mobile");
                                String email = jsonObject.getString("email");

                                Intent intent = new Intent(oditek.com.hlw.LoginOtp.this, SlideMenu.class);
                                intent.putExtra("ClickedFrom","LoginOtp");
                                startActivity(intent);
                                finish();

                                //store values in sharedpreferences
                                oditek.com.hlw.webservices.ApiClient.saveDataWithKeyAndValue(oditek.com.hlw.LoginOtp.this, "name", name);
                                oditek.com.hlw.webservices.ApiClient.saveDataWithKeyAndValue(oditek.com.hlw.LoginOtp.this, "user_id", user_id);
                                oditek.com.hlw.webservices.ApiClient.saveDataWithKeyAndValue(oditek.com.hlw.LoginOtp.this, "access_token", access_token);
                                oditek.com.hlw.webservices.ApiClient.saveDataWithKeyAndValue(oditek.com.hlw.LoginOtp.this, "mobile", mobile);
                                oditek.com.hlw.webservices.ApiClient.saveDataWithKeyAndValue(oditek.com.hlw.LoginOtp.this, "email", email);

                                System.out.println(user_id+"===="+access_token);
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
                        syncVerifyOtpData(Mobile, OTP);
                    } else {
                        Toast.makeText(oditek.com.hlw.LoginOtp.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                } else
                    Toast.makeText(oditek.com.hlw.LoginOtp.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("mobile", Mobile);
                params.put("otp", OTP);

                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }

    public void ErrorDialog(String title, String subtitle) {
        new iOSDialogBuilder(oditek.com.hlw.LoginOtp.this)
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
