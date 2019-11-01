package oditek.com.hlw;

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
import com.hbb20.CountryCodePicker;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import oditek.com.hlw.webservices.ApiClient;
import oditek.com.hlw.webservices.AppController;

public class ChangePhone extends AppCompatActivity {
    RelativeLayout rl1, rl2, rl3, rl4, rl5, rl6;
    TextView tvRl2,tvTop;
    NetworkConnection nw;
    Button btnUpdateMobile;
    EditText etNumber;
    AVLoadingIndicatorView progressDialog;
    CountryCodePicker CountryCode;
    String user_id = "", access_token = "";
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);

        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");

        nw = new NetworkConnection(oditek.com.hlw.ChangePhone.this);
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
        tvRl2.setTypeface(typeFaceLight);
        tvRl2.setText("Change Mobile Number");

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvTop=findViewById(R.id.tvTop);
        tvTop.setTypeface(typeFaceBold);
        etNumber=findViewById(R.id.etNumber);
        etNumber.setTypeface(typeFaceLight);
        CountryCode = findViewById(R.id.CountryCode);
        CountryCode.setTypeFace(typeFaceLight);
        btnUpdateMobile=findViewById(R.id.btnUpdateMobile);
        btnUpdateMobile.setTypeface(typeFaceBold);

        user_id = ApiClient.getDataFromKey(oditek.com.hlw.ChangePhone.this, "user_id");
        access_token = ApiClient.getDataFromKey(oditek.com.hlw.ChangePhone.this, "access_token");


        btnUpdateMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
                final String newNumber = etNumber.getText().toString().trim();

                if (newNumber.length() == 0) {
                    ErrorDialog("Please enter OTP", "");
                } else if (nw.isConnectingToInternet()) {
                    startLoading();
                    syncUpdateNewMobileData(user_id,access_token,newNumber);
                } else {
                    ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                }
            }
        });
    }

    private void syncUpdateNewMobileData(final String user_id, final String access_token,
                                      final String newmobile) {
        startLoading();
        String tag_json_req = "sync_UpdateMobileNumber";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.update_mobile_url),
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
                                ApiClient.saveDataWithKeyAndValue(oditek.com.hlw.ChangePhone.this, "mobile", newmobile);
                                new iOSDialogBuilder(oditek.com.hlw.ChangePhone.this)
                                        .setTitle(msg)
                                        .setSubtitle("")
                                        .setBoldPositiveLabel(true)
                                        .setCancelable(false)
                                        .setPositiveListener("Ok", new iOSDialogClickListener() {
                                            @Override
                                            public void onClick(iOSDialog dialog) {
                                                dialog.dismiss();
                                                onBackPressed();
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
                        syncUpdateNewMobileData(user_id, access_token, newmobile);
                    } else {
                        Toast.makeText(oditek.com.hlw.ChangePhone.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                } else
                    Toast.makeText(oditek.com.hlw.ChangePhone.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("access_token", access_token);
                params.put("new_mobile", newmobile);

                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }

    public void ErrorDialog(String title, String subtitle) {
        new iOSDialogBuilder(oditek.com.hlw.ChangePhone.this)
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
