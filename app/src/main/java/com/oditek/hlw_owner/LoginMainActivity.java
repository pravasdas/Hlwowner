package com.oditek.hlw_owner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.oditek.hlw_owner.webservices.ApiClient;
import com.oditek.hlw_owner.webservices.AppController;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginMainActivity extends AppCompatActivity {
    private EditText etUserId, etUserPwd;
    Button btnLogin;
    private int i = 0;
    ProgressDialog progressDialog;
    TextView tvForgotPass,tv_Version;
    public String version;
    NetworkConnection nw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        nw = new NetworkConnection(this);
        btnLogin=findViewById(R.id.btnLogin);
        etUserId=findViewById(R.id.etUserId);
        etUserPwd=findViewById(R.id.etUserPwd);
        tv_Version=findViewById(R.id.tv_Version);
        tvForgotPass=findViewById(R.id.tvForgotPass);
        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginMainActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mobile_ = etUserId.getText().toString().trim();
                final String password_ = etUserPwd.getText().toString().trim();
                if (mobile_.length() == 0) {
                    ErrorDialog("Error", "Please enter mobile number");
                } else if (!isValidPhoneNumber(mobile_)) {
                    ErrorDialog("Error", "Please enter valid mobile number");
                }else if(password_.length()==0){
                    ErrorDialog("Error", "Please enter password");
                }else if (nw.isConnectingToInternet()) {
                    syncLogin(mobile_, password_);
                } else {
                    ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                }
            }
        });
        version = getCurrentAppVersion();
        tv_Version.setText("App Version: "+version);
    }
    //====================================API Call=================================================
    private void syncLogin(final String Mobile,final String Password) {
       // startLoading();
        String tag_json_req = "sync_Login";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.signin_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //stopLoading();
                            Log.d("response=========", response);
                            JSONObject jsonObject = new JSONObject(response);
                            System.out.println("json----" + jsonObject);

                            String status_ = jsonObject.getString("status");
                            String msg_ = jsonObject.getString("msg");

                            if ("0".equalsIgnoreCase(status_)) {
                                ErrorDialog("",msg_);
                            } else if("1".equalsIgnoreCase(status_)){
                                //SuccessDialog(msg_,"");
                                String loginStatus = jsonObject.getString("status");
                                String ownerName = jsonObject.getString("owner_name");
                                String owner_Email = jsonObject.getString("owner_email");
                                String owner_id = jsonObject.getString("owner_id");
                                String owner_Code = jsonObject.getString("owner_code");
                                String access_token = jsonObject.getString("access_token");
                                String ownerPhoto = jsonObject.getString("image");
                                String ownerMobile = jsonObject.getString("mobile");
                                String vehicleCount = jsonObject.getString("no_of_vehicle");
                                String driverCount = jsonObject.getString("no_of_driver");
                                System.out.println("Owner Photo----" + ownerPhoto);

                                //store values in sharedpreferences
                                ApiClient.saveDataWithKeyAndValue(LoginMainActivity.this, "status", loginStatus);
                                ApiClient.saveDataWithKeyAndValue(LoginMainActivity.this, "owner_name", ownerName);
                                ApiClient.saveDataWithKeyAndValue(LoginMainActivity.this, "owner_email", owner_Email);
                                ApiClient.saveDataWithKeyAndValue(LoginMainActivity.this, "owner_id", owner_id);
                                ApiClient.saveDataWithKeyAndValue(LoginMainActivity.this, "owner_code", owner_Code);
                                ApiClient.saveDataWithKeyAndValue(LoginMainActivity.this, "access_token", access_token);
                                ApiClient.saveDataWithKeyAndValue(LoginMainActivity.this, "image", ownerPhoto);
                                ApiClient.saveDataWithKeyAndValue(LoginMainActivity.this, "mobile", ownerMobile);
                                ApiClient.saveDataWithKeyAndValue(LoginMainActivity.this, "no_of_vehicle", vehicleCount);
                                ApiClient.saveDataWithKeyAndValue(LoginMainActivity.this, "no_of_driver", driverCount);

                                Intent intent = new Intent(LoginMainActivity.this, MainActivity.class);
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
                        syncLogin(Mobile,Password);
                    } else {
                        Toast.makeText(LoginMainActivity.this, "Please try after sometime...",
                                Toast.LENGTH_LONG).show();
                        //stopLoading();
                    }
                } else
                    Toast.makeText(LoginMainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
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
    public String getCurrentAppVersion() {
        PackageManager manager = Objects.requireNonNull(this.getPackageManager());
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.v("PackageName = ","" + info.packageName + "\nVersionCode = "+ info.versionCode + "\nVersionName = "
                + info.versionName);
        return info.versionName;
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

}
