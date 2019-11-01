package oditek.com.hlw;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity {
    RelativeLayout rl1, rl2, rl3, rl4, rl5, rl6;
    TextView tvRl2, tvTop, tvMobile;
    Button btnMobile, btnEmail, btnSubmit;
    LinearLayout MobileLayout, EmailLayout;
    EditText etNumber;
    AVLoadingIndicatorView progressDialog;
    String con_code_ = "", mobile_ = "";
    private int i = 0;
    oditek.com.hlw.NetworkConnection nw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");

        nw = new oditek.com.hlw.NetworkConnection(oditek.com.hlw.ForgotPassword.this);
        rl1 = findViewById(R.id.rl1);
        rl2 = findViewById(R.id.rl2);
        rl3 = findViewById(R.id.rl3);
        rl4 = findViewById(R.id.rl4);
        rl5 = findViewById(R.id.rl5);
        rl6 = findViewById(R.id.rl6);
        tvRl2 = findViewById(R.id.tvRl2);
        progressDialog = findViewById(R.id.progressDialog);

        rl1.setVisibility(View.GONE);
        rl2.setVisibility(View.VISIBLE);
        rl3.setVisibility(View.GONE);
        rl4.setVisibility(View.GONE);
        rl5.setVisibility(View.GONE);
        rl6.setVisibility(View.GONE);
        tvRl2.setText("Forgot Password");
        tvRl2.setTypeface(typeFaceLight);
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //Toolbar End

        btnMobile = findViewById(R.id.btnMobile);
        btnMobile.setTypeface(typeFaceBold);
        btnEmail = findViewById(R.id.btnEmail);
        btnEmail.setTypeface(typeFaceBold);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setTypeface(typeFaceBold);

        tvTop = findViewById(R.id.tvTop);
        tvTop.setTypeface(typeFaceLight);

        tvMobile = findViewById(R.id.tvMobile);
        tvMobile.setTypeface(typeFaceBold);

        etNumber = findViewById(R.id.etNumber);
        etNumber.setTypeface(typeFaceBold);

        MobileLayout = findViewById(R.id.MobileLayout);
        EmailLayout = findViewById(R.id.EmailLayout);

        btnMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMobile.setBackgroundResource(R.drawable.capsule_white);
                btnEmail.setBackground(null);
                btnMobile.setTextColor(ContextCompat.getColor(oditek.com.hlw.ForgotPassword.this, R.color.colorPrimaryDark));
                btnEmail.setTextColor(ContextCompat.getColor(oditek.com.hlw.ForgotPassword.this, R.color.white));
                EmailLayout.setVisibility(View.GONE);
                MobileLayout.setVisibility(View.VISIBLE);
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMobile.setBackground(null);
                btnEmail.setBackgroundResource(R.drawable.capsule_white);
                btnMobile.setTextColor(ContextCompat.getColor(oditek.com.hlw.ForgotPassword.this, R.color.white));
                btnEmail.setTextColor(ContextCompat.getColor(oditek.com.hlw.ForgotPassword.this, R.color.colorPrimaryDark));
                EmailLayout.setVisibility(View.VISIBLE);
                MobileLayout.setVisibility(View.GONE);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String mobile_ = etNumber.getText().toString().trim();

                if (mobile_.length() == 0) {
                    ErrorDialog("Invalid Mobile Number", "Please enter a valid mobile number");
                } else if (!isValidPhoneNumber(mobile_)) {
                    ErrorDialog("Invalid Mobile Number", "Please enter a valid mobile number");

                } else if (nw.isConnectingToInternet()) {
                    startLoading();
                    con_code_ = oditek.com.hlw.webservices.ApiClient.getDataFromKey(oditek.com.hlw.ForgotPassword.this, "CountryCode");

                    syncSendOTPForForgetPassword(con_code_, mobile_);

                } else {
                    ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                }
            }
        });
    }

    private void syncSendOTPForForgetPassword(final String con_code_, final String mobile_) {
        startLoading();
        String tag_json_req = "sync_SendOTPForForgetPassword";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.sendOTPForgetPassword_url),
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

                            if ("0".equalsIgnoreCase(status_)) {
                                ErrorDialog(msg_, "");
                            } else {
                                String for_forgotpass = "1";
                                String mobile = mobile_;
                                Toast.makeText(oditek.com.hlw.ForgotPassword.this, msg_, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(oditek.com.hlw.ForgotPassword.this, oditek.com.hlw.ChangePhoneOTP.class);
                                intent.putExtra("forgot_password", for_forgotpass);
                                intent.putExtra("mobile_", mobile);
                                startActivity(intent);
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
                        syncSendOTPForForgetPassword(con_code_, mobile_);
                    } else {
                        Toast.makeText(oditek.com.hlw.ForgotPassword.this, "Please try after sometime...",
                                Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                } else
                    Toast.makeText(oditek.com.hlw.ForgotPassword.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("con_code", con_code_);
                params.put("mobile", mobile_);
                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        oditek.com.hlw.webservices.AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }

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
        new iOSDialogBuilder(oditek.com.hlw.ForgotPassword.this)
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
