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

public class SignUpOtp extends AppCompatActivity {
    RelativeLayout rl1, rl2, rl3, rl4, rl5, rl6;
    TextView tvRl2, tvEnter, tv1;
    Button btnSubmit;
    private int i = 0;
    NetworkConnection nw;
    AVLoadingIndicatorView progressDialog;
    PinView signup_otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_otp);


        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");

        nw = new NetworkConnection(oditek.com.hlw.SignUpOtp.this);
        progressDialog = findViewById(R.id.progressDialog);

        rl1 = findViewById(R.id.rl1);
        rl2 = findViewById(R.id.rl2);
        rl3 = findViewById(R.id.rl3);
        rl4 = findViewById(R.id.rl4);
        rl5 = findViewById(R.id.rl5);
        rl6 = findViewById(R.id.rl6);
        tvRl2 = findViewById(R.id.tvRl2);
        tvRl2.setTypeface(typeFaceLight);
        rl1.setVisibility(View.GONE);
        rl2.setVisibility(View.VISIBLE);
        rl3.setVisibility(View.GONE);
        rl4.setVisibility(View.GONE);
        rl5.setVisibility(View.GONE);
        rl6.setVisibility(View.GONE);
        tvRl2.setText("OTP Verification");
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //Toolbar End

        tvEnter = findViewById(R.id.tvEnter);
        tvEnter.setTypeface(typeFaceBold);
        tv1 = findViewById(R.id.tv1);
        tv1.setTypeface(typeFaceLight);
        signup_otp = findViewById(R.id.signup_otp);

        Bundle bundle = getIntent().getExtras();
        final String number = bundle.getString("number");
        final String ConCode = bundle.getString("concode");

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setTypeface(typeFaceBold);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nw.isConnectingToInternet()) {
                    final String otp = signup_otp.getText().toString().trim();
                    startLoading();
                    syncVerifyOtpData(number, otp);
                } else {
                    ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                }

            }
        });
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
                            String name = jsonObject.getString("name");
                            String user_id = jsonObject.getString("user_id");
                            String access_token = jsonObject.getString("access_token");
                            String msg = jsonObject.getString("msg");
                            String mobile = jsonObject.getString("mobile");
                            String email = jsonObject.getString("email");

                            if ("0".equalsIgnoreCase(status_)) {
                                ErrorDialog(msg, "");
                            } else {
                                new iOSDialogBuilder(oditek.com.hlw.SignUpOtp.this)
                                        .setTitle(msg)
                                        .setSubtitle("")
                                        .setBoldPositiveLabel(true)
                                        .setCancelable(false)
                                        .setPositiveListener("Ok", new iOSDialogClickListener() {
                                            @Override
                                            public void onClick(iOSDialog dialog) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(oditek.com.hlw.SignUpOtp.this, SlideMenu.class);
                                                intent.putExtra("ClickedFrom","SignUpOtp");
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .build().show();
                            }

                            //store values in sharedpreferences
                            oditek.com.hlw.webservices.ApiClient.saveDataWithKeyAndValue(oditek.com.hlw.SignUpOtp.this, "name", name);
                            oditek.com.hlw.webservices.ApiClient.saveDataWithKeyAndValue(oditek.com.hlw.SignUpOtp.this, "user_id", user_id);
                            oditek.com.hlw.webservices.ApiClient.saveDataWithKeyAndValue(oditek.com.hlw.SignUpOtp.this, "access_token", access_token);
                            oditek.com.hlw.webservices.ApiClient.saveDataWithKeyAndValue(oditek.com.hlw.SignUpOtp.this, "mobile", mobile);
                            oditek.com.hlw.webservices.ApiClient.saveDataWithKeyAndValue(oditek.com.hlw.SignUpOtp.this, "email", email);

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
                        Toast.makeText(oditek.com.hlw.SignUpOtp.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                } else
                    Toast.makeText(oditek.com.hlw.SignUpOtp.this, error.getMessage(), Toast.LENGTH_LONG).show();
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
        new iOSDialogBuilder(oditek.com.hlw.SignUpOtp.this)
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
