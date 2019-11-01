package oditek.com.hlw;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUp extends AppCompatActivity {
    RelativeLayout rl1, rl2, rl3, rl4, rl5, rl6;
    TextView tvRl2, tv_name, tv_Number, tv_email, tv_Password, tv_conPassword;
    TextView tv_fb, tv_gm;
    EditText etNumber, et_name, et_email, etPassword, etConPassword;
    private TextInputLayout etPasswordLayout, etRePasswordLayout;
    private String emailval_, mobileVal_, passwordVal_, cnfPasswordval_, con_code;
    Button btnSignUp;
    private int i = 0;
    NetworkConnection nw;
    AVLoadingIndicatorView progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_layout);

        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");

        nw = new NetworkConnection(oditek.com.hlw.SignUp.this);
        progressDialog = findViewById(R.id.progressDialog);
        tvRl2 = findViewById(R.id.tvRl2);
        tvRl2.setTypeface(typeFaceLight);
        tv_name = findViewById(R.id.tv_name);
        tv_name.setTypeface(typeFaceBold);
        tv_Number = findViewById(R.id.tv_Number);
        tv_Number.setTypeface(typeFaceBold);
        tv_email = findViewById(R.id.tv_email);
        tv_email.setTypeface(typeFaceBold);
        tv_Password = findViewById(R.id.tv_Password);
        tv_Password.setTypeface(typeFaceBold);
        tv_conPassword = findViewById(R.id.tv_conPassword);
        tv_conPassword.setTypeface(typeFaceBold);
        tv_fb = findViewById(R.id.tv_fb);
        tv_fb.setTypeface(typeFaceLight);
        tv_gm = findViewById(R.id.tv_gm);
        tv_gm.setTypeface(typeFaceLight);
        et_name = findViewById(R.id.et_name);
        et_name.setTypeface(typeFaceLight);
        etNumber = findViewById(R.id.etNumber);
        etNumber.setTypeface(typeFaceLight);
        et_email = findViewById(R.id.et_email);
        et_email.setTypeface(typeFaceLight);
        etPassword = findViewById(R.id.etPassword);
        etPassword.setTypeface(typeFaceLight);
        etConPassword = findViewById(R.id.etConPassword);
        etConPassword.setTypeface(typeFaceLight);

        etPasswordLayout = (TextInputLayout) findViewById(R.id.etPasswordLayout);
        etRePasswordLayout = (TextInputLayout) findViewById(R.id.etRePasswordLayout);

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
        tvRl2.setText("Sign Up");
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //Toolbar Section End

        etNumber = findViewById(R.id.etNumber);
        etNumber.setTypeface(typeFaceLight);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setTypeface(typeFaceBold);

        Bundle bundle = getIntent().getExtras();
        final String number = bundle.getString("number");
        con_code = bundle.getString("concode");
        etNumber.setText(number);
        etNumber.setEnabled(false);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                emailval_ = et_email.getText().toString().trim();
                mobileVal_ = etNumber.getText().toString().trim();
                passwordVal_ = etPassword.getText().toString().trim();
                cnfPasswordval_ = etConPassword.getText().toString().trim();

                if (et_name.getText().toString().trim().length() == 0) {
                    et_name.setError(getResources().getString(R.string.error_name));

                } else if (et_email.getText().toString().trim().length() == 0) {
                    et_email.setError(getResources().getString(R.string.error_email));

                } else if (!isValidEmail(emailval_)) {
                    et_email.setError(getResources().getString(R.string.error_email2));

                } else if (etNumber.getText().toString().trim().length() == 0) {
                    etNumber.setError(getResources().getString(R.string.error_mobile));
                } else if (!isValidPhoneNumber(mobileVal_)) {
                    etNumber.setError(getResources().getString(R.string.error_mobile2));

                } else if (etPassword.getText().toString().trim().length() == 0) {
                    etPasswordLayout.setPasswordVisibilityToggleEnabled(false);
                    etRePasswordLayout.setPasswordVisibilityToggleEnabled(true);
                    etPassword.setError(getResources().getString(R.string.error_password));

                } else if (!isValidPassword(etPassword.getText().toString().trim())) {
                    etPasswordLayout.setPasswordVisibilityToggleEnabled(false);
                    etRePasswordLayout.setPasswordVisibilityToggleEnabled(true);
                    etPassword.setError(getResources().getString(R.string.err_msg_password_strength));

                } else if (etConPassword.getText().toString().trim().length() == 0) {
                    etPasswordLayout.setPasswordVisibilityToggleEnabled(false);
                    etRePasswordLayout.setPasswordVisibilityToggleEnabled(true);
                    etConPassword.setError(getResources().getString(R.string.error_cnf_password));

                } else if (!cnfPasswordval_.equals(etPassword.getText().toString().trim())) {
                    etPasswordLayout.setPasswordVisibilityToggleEnabled(true);
                    etRePasswordLayout.setPasswordVisibilityToggleEnabled(false);
                    etConPassword.setError(getResources().getString(R.string.error_match_password));
                } else if (nw.isConnectingToInternet()) {
                    String name_ = et_name.getText().toString();
                    String mobile_ = etNumber.getText().toString();
                    String emailID_ = et_email.getText().toString();
                    String password_ = etPassword.getText().toString();
                    String social_type = "";
                    String oauth_provider = "";
                    String oauth_uid = "";
                    syncSignupData(name_, mobile_, emailID_, password_, con_code, social_type, oauth_provider, oauth_uid);
                } else {
                    errorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                }
            }

        });

    }

    private void syncSignupData(final String name_, final String mobile_, final String emailID_, final String password_,
                                final String con_code, final String social_type, final String oauth_provider, final String oauth_uid) {
        startLoading();
        String tag_json_req = "sync_signup_data";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.sign_up_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        stopLoading();
                        try {
                            JSONObject json = new JSONObject(response);
                            System.out.println("json----" + json);
                            String status_ = json.getString("status");
                            String msg_ = json.getString("msg");
                            if (status_.equalsIgnoreCase("1")) {//true case
                                new iOSDialogBuilder(oditek.com.hlw.SignUp.this)
                                        .setTitle(msg_)
                                        .setSubtitle("")
                                        .setBoldPositiveLabel(true)
                                        .setCancelable(false)
                                        .setPositiveListener("Ok", new iOSDialogClickListener() {
                                            @Override
                                            public void onClick(iOSDialog dialog) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(oditek.com.hlw.SignUp.this, oditek.com.hlw.SignUpOtp.class);
                                                intent.putExtra("number", mobile_);
                                                intent.putExtra("concode", con_code);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .build().show();
                            } else {//false case
                                errorDialog(msg_, "");
                            }

                        } catch (ClassCastException e) {

                        } catch (JSONException e) {

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
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
                        syncSignupData(name_, mobile_, emailID_, password_, con_code, social_type, oauth_provider, oauth_uid);

                    } else {
                        Toast.makeText(oditek.com.hlw.SignUp.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                } else
                    Toast.makeText(oditek.com.hlw.SignUp.this, error.getMessage(),
                            Toast.LENGTH_LONG).show();
            }
        })


        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name_);
                params.put("mobile", mobile_);
                params.put("email", emailID_);
                params.put("password", password_);
                params.put("con_code", con_code);
                params.put("social_type", "");
                params.put("oauth_provider", "");
                params.put("oauth_uid", "");

                Log.d("params are :", "" + params);
                return params;
            }
        };

        data.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                0,
                0));
        oditek.com.hlw.webservices.AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }


    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=\\S+$).{8,20}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    private class PasswordTextWatcher implements TextWatcher {

        private View view;

        private PasswordTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable editable) {
            if (etPassword.getText().toString().length() == 0) {
                etPasswordLayout.setPasswordVisibilityToggleEnabled(true);
            } else {
                if (!isValidPassword(etPassword.getText().toString().trim())) {
                    etPasswordLayout.setPasswordVisibilityToggleEnabled(false);
                    etPassword.setError(getResources().getString(R.string.err_msg_password_strength));
                } else {
                    etPasswordLayout.setPasswordVisibilityToggleEnabled(true);
                }
            }
        }

    }

    public void errorDialog(String title, String subtitle) {

        new iOSDialogBuilder(oditek.com.hlw.SignUp.this)
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
