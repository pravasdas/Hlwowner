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

public class ChangePassword extends AppCompatActivity {
    RelativeLayout rl1, rl2, rl3, rl4, rl5, rl6, rlOldPass;
    TextView tvRl2, tvOldPass, tvNewPass, tvConPass;
    EditText etOldPass, etNewPass, etConPass;
    Button btnUpdate, btnLoginPasswordChange;
    oditek.com.hlw.NetworkConnection nw;
    AVLoadingIndicatorView progressDialog;
    String user_id = "", access_token = "";
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");

        nw = new oditek.com.hlw.NetworkConnection(oditek.com.hlw.ChangePassword.this);
        progressDialog = findViewById(R.id.progressDialog);
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
        tvRl2.setText("Change Password");
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvOldPass = findViewById(R.id.tvOldPass);
        tvNewPass = findViewById(R.id.tvNewPass);
        tvConPass = findViewById(R.id.tvConPass);
        etOldPass = findViewById(R.id.etOldPass);
        etNewPass = findViewById(R.id.etNewPass);
        etConPass = findViewById(R.id.etConPass);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnLoginPasswordChange = findViewById(R.id.btnLoginPasswordChange);
        rlOldPass = findViewById(R.id.rlOldPass);


        tvOldPass.setTypeface(typeFaceLight);
        tvNewPass.setTypeface(typeFaceLight);
        tvConPass.setTypeface(typeFaceLight);
        etOldPass.setTypeface(typeFaceLight);
        etNewPass.setTypeface(typeFaceLight);
        etConPass.setTypeface(typeFaceLight);
        btnUpdate.setTypeface(typeFaceBold);
        btnLoginPasswordChange.setTypeface(typeFaceBold);

        Bundle bundle = getIntent().getExtras();
        final String changePassword = bundle.getString("change_password");
        final String Mobile = bundle.getString("mobile");


        user_id = oditek.com.hlw.webservices.ApiClient.getDataFromKey(oditek.com.hlw.ChangePassword.this, "user_id");
        access_token = oditek.com.hlw.webservices.ApiClient.getDataFromKey(oditek.com.hlw.ChangePassword.this, "access_token");

        if ("1".equalsIgnoreCase(changePassword)) {
            btnLoginPasswordChange.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.GONE);
            rlOldPass.setVisibility(View.GONE);

            btnLoginPasswordChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String etConPassVal = etNewPass.getText().toString().trim();

                    if (etNewPass.getText().toString().trim().length() == 0) {
                        etNewPass.setError(getResources().getString(R.string.error_new_password));
                    } else if (etConPass.getText().toString().trim().length() == 0) {
                        etConPass.setError(getResources().getString(R.string.error_cnf_password));
                    } else if (!etConPassVal.equals(etNewPass.getText().toString().trim())) {
                        etConPass.setError(getResources().getString(R.string.error_match_password));
                    } else if (nw.isConnectingToInternet()) {

                        String password = etConPassVal;
                        String mobile = Mobile;
                        startLoading();

                        syncChangePasswordData(mobile, password);

                    } else {
                        ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                    }
                }
            });
        } else {
            btnUpdate.setVisibility(View.VISIBLE);
            btnLoginPasswordChange.setVisibility(View.GONE);
            rlOldPass.setVisibility(View.GONE);

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String etConPassVal = etNewPass.getText().toString().trim();

                    if (etOldPass.getText().toString().trim().length() == 0) {
                        etOldPass.setError(getResources().getString(R.string.error_old_password));
                    } else if (etNewPass.getText().toString().trim().length() == 0) {
                        etNewPass.setError(getResources().getString(R.string.error_new_password));
                    } else if (etConPass.getText().toString().trim().length() == 0) {
                        etConPass.setError(getResources().getString(R.string.error_cnf_password));
                    } else if (!etConPassVal.equals(etNewPass.getText().toString().trim())) {
                        etConPass.setError(getResources().getString(R.string.error_match_password));
                    } else if (nw.isConnectingToInternet()) {

                        String userid = user_id;
                        String accesstoken = access_token;
                        String oldPass = etOldPass.getText().toString().trim();
                        String newPass = etNewPass.getText().toString().trim();
                        startLoading();

                        syncChangePassData(userid, accesstoken, oldPass, newPass);
                    } else {
                        ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                    }
                }
            });

        }
    }

    private void syncChangePassData(final String userid, final String accesstoken, final String oldPass, final String newPass) {
        startLoading();
        String tag_json_req = "sync_UpdatePass";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.update_pass_url),
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
                                new iOSDialogBuilder(oditek.com.hlw.ChangePassword.this)
                                        .setTitle(msg)
                                        .setSubtitle("")
                                        .setBoldPositiveLabel(true)
                                        .setCancelable(false)
                                        .setPositiveListener("Ok", new iOSDialogClickListener() {
                                            @Override
                                            public void onClick(iOSDialog dialog) {
                                                dialog.dismiss();
                                                onBackPressed();
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
                        syncChangePassData(userid, accesstoken, oldPass, newPass);
                    } else {
                        Toast.makeText(oditek.com.hlw.ChangePassword.this, "Please try after sometime...",
                                Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                } else
                    Toast.makeText(oditek.com.hlw.ChangePassword.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("user_id", userid);
                params.put("access_token", accesstoken);
                params.put("old_password", oldPass);
                params.put("new_password", newPass);


                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        oditek.com.hlw.webservices.AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }

    private void syncChangePasswordData(final String Mobile, final String Password) {
        startLoading();
        String tag_json_req = "sync_ChangePass";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.update_password_url),
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
                                new iOSDialogBuilder(oditek.com.hlw.ChangePassword.this)
                                        .setTitle(msg)
                                        .setSubtitle("")
                                        .setBoldPositiveLabel(true)
                                        .setCancelable(false)
                                        .setPositiveListener("Ok", new iOSDialogClickListener() {
                                            @Override
                                            public void onClick(iOSDialog dialog) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(oditek.com.hlw.ChangePassword.this, oditek.com.hlw.LoginWithPass.class);
                                                intent.putExtra("number", Mobile);
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
                        syncChangePasswordData(Mobile, Password);
                    } else {
                        Toast.makeText(oditek.com.hlw.ChangePassword.this, "Please try after sometime...",
                                Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                } else
                    Toast.makeText(oditek.com.hlw.ChangePassword.this, error.getMessage(), Toast.LENGTH_LONG).show();
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
        oditek.com.hlw.webservices.AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }

    public void ErrorDialog(String title, String subtitle) {
        new iOSDialogBuilder(oditek.com.hlw.ChangePassword.this)
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
