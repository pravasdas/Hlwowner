package oditek.com.hlw;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

public class Login extends AppCompatActivity {
    RelativeLayout rl1, rl2, rl3, rl4, rl5, rl6;
    TextView tvRl2, tvTop;
    EditText etNumber;
    Button btnLogin;
    String mobileVal, countryCode, apiKey = "";
    NetworkConnection nw;
    CountryCodePicker CountryCode;
    AVLoadingIndicatorView progressDialog;
    private int i = 0;
    private static final int REQUEST_FINE_LOCATION = 0;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");

        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);

        nw = new NetworkConnection(oditek.com.hlw.Login.this);
        rl1 = findViewById(R.id.rl1);
        rl2 = findViewById(R.id.rl2);
        rl3 = findViewById(R.id.rl3);
        rl4 = findViewById(R.id.rl4);
        rl5 = findViewById(R.id.rl5);
        rl6 = findViewById(R.id.rl6);
        tvRl2 = findViewById(R.id.tvRl2);

        etNumber = findViewById(R.id.etNumber);
        btnLogin = findViewById(R.id.btnLogin);
        tvTop = findViewById(R.id.tvTop);
        CountryCode = findViewById(R.id.CountryCode);
        CountryCode.setTypeFace(typeFaceLight);
        tvTop.setTypeface(typeFaceBold);
        etNumber.setTypeface(typeFaceLight);
        btnLogin.setTypeface(typeFaceBold);
        progressDialog = findViewById(R.id.progressDialog);

        rl1.setVisibility(View.GONE);
        rl2.setVisibility(View.VISIBLE);
        rl3.setVisibility(View.GONE);
        rl4.setVisibility(View.GONE);
        rl5.setVisibility(View.GONE);
        rl6.setVisibility(View.GONE);
        tvRl2.setTypeface(typeFaceLight);
        tvRl2.setText("Login with Mobile");

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mobileVal = etNumber.getText().toString().trim();
                countryCode = ("+" + CountryCode.getSelectedCountryCode());
                ApiClient.saveDataWithKeyAndValue(oditek.com.hlw.Login.this, "CountryCode", countryCode);

                if (mobileVal.length() == 0) {
                    ErrorDialog("Invalid Mobile Number", "Please enter a valid mobile number");

                } else if (!isValidPhoneNumber(mobileVal)) {
                    ErrorDialog("Invalid Mobile Number", "Please enter a valid mobile number");

                } else if (nw.isConnectingToInternet()) {
                    startLoading();
//                    new ApiHelper(Login.this, "validateMobile", countryCode, mobileVal, getValidateMobileListener).execute();
                    mobileVal = etNumber.getText().toString().trim();
                    countryCode = ("+" + CountryCode.getSelectedCountryCode());
                    syncValidateMobileData(countryCode, mobileVal);
                } else {
                    ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                }
            }
        });

    }

    private void loadPermissions(String perm, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // granted
                } else {
                    // no granted
                }
                return;
            }

        }

    }

    private void syncValidateMobileData(final String ConCode, final String Mobile) {
        startLoading();
        String tag_json_req = "sync_ValidateMobile";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.validate_mobile_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            stopLoading();
                            Log.d("response=========", response);
                            JSONObject jsonObject = new JSONObject(response);
                            System.out.println("json----" + jsonObject);

                            String status_ = jsonObject.getString("status");
                            String otp_ = jsonObject.getString("otp");

                            if ("0".equalsIgnoreCase(status_)) {
                                NewUserDialog();
                            } else {
                                final String number = mobileVal;
                                countryCode = ("+" + CountryCode.getSelectedCountryCode());
                                Intent intent = new Intent(oditek.com.hlw.Login.this, LoginOtp.class);
                                intent.putExtra("number", number);
                                intent.putExtra("con_code", countryCode);
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
                        syncValidateMobileData(ConCode, Mobile);
                    } else {
                        Toast.makeText(oditek.com.hlw.Login.this, "Please try after sometime...",
                                Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                } else
                    Toast.makeText(oditek.com.hlw.Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("con_code", ConCode);
                params.put("mobile", Mobile);


                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
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

    public void NewUserDialog() {
        new iOSDialogBuilder(oditek.com.hlw.Login.this)
                .setTitle("Mobile number doesn't exist\nin our record.Do you want to\nRegister Now?")
                .setSubtitle("")
                .setBoldPositiveLabel(true)
                .setCancelable(false)
                .setPositiveListener("Yes", new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                        final String number = etNumber.getText().toString();
                        final String countrycode = ("+" + CountryCode.getSelectedCountryCode());
                        Intent in = new Intent(oditek.com.hlw.Login.this, SignUp.class);
                        in.putExtra("number", number);
                        in.putExtra("concode", countrycode);
                        startActivity(in);
                    }
                })
                .setNegativeListener("No", new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .build().show();
    }

    public void ErrorDialog(String title, String subtitle) {
        new iOSDialogBuilder(oditek.com.hlw.Login.this)
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
