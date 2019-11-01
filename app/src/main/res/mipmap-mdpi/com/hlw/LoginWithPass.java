package oditek.com.hlw;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import oditek.com.hlw.webservices.AppController;

public class LoginWithPass extends AppCompatActivity {

    NetworkConnection nw;
    private int i = 0;
    AVLoadingIndicatorView progressDialog;
    RelativeLayout rl1, rl2, rl3, rl4, rl5, rl6;
    TextView tvRl2, tvNumber, tvForgotPass, tvLogin_OTP,tvTop,tvPass,tv1,tvTerms,tv2,tvPolicy,tv3;
    EditText etPassword;
    Button btnLoginPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_pass);

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
        tvRl2.setText("Login with Password");
        tvRl2.setTypeface(typeFaceLight);
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //Toolbar End


        nw = new NetworkConnection(oditek.com.hlw.LoginWithPass.this);
        progressDialog = findViewById(R.id.progressDialog);

        tvNumber = findViewById(R.id.tvNumber);
        tvNumber.setTypeface(typeFaceBold);
        tvForgotPass = findViewById(R.id.tvForgotPass);
        tvForgotPass.setTypeface(typeFaceBold);
        tvLogin_OTP = findViewById(R.id.tvLogin_OTP);
        tvLogin_OTP.setTypeface(typeFaceBold);
        btnLoginPass = findViewById(R.id.btnLoginPass);
        btnLoginPass.setTypeface(typeFaceBold);
        tvTop = findViewById(R.id.tvTop);
        tvTop.setTypeface(typeFaceBold);
        tvPass = findViewById(R.id.tvPass);
        tvPass.setTypeface(typeFaceBold);
        etPassword = findViewById(R.id.etPassword);
        etPassword.setTypeface(typeFaceLight);
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
        tvNumber.setText(number);

        tvLogin_OTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(oditek.com.hlw.LoginWithPass.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        btnLoginPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(LoginWithPass.this, SlideMenu.class);
//                startActivity(intent);
//                finish();

                final String password = etPassword.getText().toString().trim();

                if (password.length() == 0) {
                    ErrorDialog("Password is required!", "Field should mot remain blank");
                } else if (nw.isConnectingToInternet()) {
                    startLoading();
                    syncVerifyPassword(number, password);
                } else {
                    ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                }
            }
        });
    }

    private void syncVerifyPassword(final String Mobile, final String Password) {
        startLoading();
        String tag_json_req = "sync_VerifyPassword";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.verify_password_url),
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

                                Intent intent = new Intent(oditek.com.hlw.LoginWithPass.this, SlideMenu.class);
                                intent.putExtra("ClickedFrom","LoginWithPass");
                                startActivity(intent);
                                finish();
                            }

                            //store values in sharedpreferences
//                            ApiClient.saveDataWithKeyAndValue(LoginOtp.this, "name", name);
//                            ApiClient.saveDataWithKeyAndValue(LoginOtp.this, "user_id", user_id);
//                            ApiClient.saveDataWithKeyAndValue(LoginOtp.this, "access_token", access_token);
//                            ApiClient.saveDataWithKeyAndValue(LoginOtp.this, "mobile", mobile);
//                            ApiClient.saveDataWithKeyAndValue(LoginOtp.this, "email", email);

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
                        syncVerifyPassword(Mobile, Password);
                    } else {
                        Toast.makeText(oditek.com.hlw.LoginWithPass.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                } else
                    Toast.makeText(oditek.com.hlw.LoginWithPass.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("mobile", Mobile);
                params.put("password", Password);

                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }

    public void ErrorDialog(String title, String subtitle) {
        new iOSDialogBuilder(oditek.com.hlw.LoginWithPass.this)
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
