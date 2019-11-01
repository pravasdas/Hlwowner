package com.oditek.hlw_owner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.oditek.hlw_owner.webservices.ApiClient;
import com.oditek.hlw_owner.webservices.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverDetailsActivity extends AppCompatActivity {
    TextView tvRl2, tv_driverName;
    ImageView ivBack;
    CircleImageView driver_image;
    RecyclerView recyclerView;
    CardView cardView;
    NetworkConnection nw;
    private int i = 0;
    ProgressDialog progressDialog;
    String ownerID, access_Token, driverID;
    DriverDetailsAdapter driverDetailsAdapter;
    String name_, image_;
    Typeface typeFaceLight,typeFaceBold;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);

        typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");

        tvRl2 = findViewById(R.id.tvRl2);
        tvRl2.setText("DRIVER DETAILS");
        tvRl2.setTypeface(typeFaceBold);
        ivBack = findViewById(R.id.ivBack);
        tv_driverName = findViewById(R.id.tv_driverName);
        driver_image = findViewById(R.id.driver_image);
        nw = new NetworkConnection(this);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //
        cardView = (CardView) findViewById(R.id.cardview_driverDetails);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DriverDetailsActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        Bundle bundle = getIntent().getExtras();
        driverID = bundle.getString("DriverId");
        System.out.println("Driver Id Is----" + driverID);
        ownerID = ApiClient.getDataFromKey(this, "owner_id");
        access_Token = ApiClient.getDataFromKey(this, "access_token");
        if (nw.isConnectingToInternet()) {
            syncDriverDetailsData(ownerID, driverID, access_Token);
        } else {
            CustomDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
        }
    }

    private void syncDriverDetailsData(final String OwnerID, final String driverID, final String AccessToken) {
        startLoading();
        String tag_json_req = "sync_DriverDetailsData";

        StringRequest data = new StringRequest(Request.Method.POST,
                this.getResources().getString(R.string.base_url) + this.getResources()
                        .getString(R.string.driver_details_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            stopLoading();
                            Log.d("response=========", response);
                            JSONObject jsonObject = new JSONObject(response);
                            System.out.println("json----" + jsonObject);
                            String status_ = jsonObject.getString("status");
                            if (status_.equalsIgnoreCase("1")) {
                                name_ = jsonObject.getString("name");
                                tv_driverName.setText(name_);
                                tv_driverName.setTypeface(typeFaceBold);
                                image_ = jsonObject.getString("image");

                                if (image_.equalsIgnoreCase(null) || image_.equalsIgnoreCase("")) {
                                    Glide
                                            .with(DriverDetailsActivity.this)
                                            .load(R.mipmap.profile)
                                            .into(driver_image);
                                } else {
                                    Glide
                                            .with(DriverDetailsActivity.this)
                                            .load(image_)
                                            .into(driver_image);
                                }

                                JSONArray jsonArray = jsonObject.getJSONArray("details");
                                getDriverDetailsData(jsonArray);
                            } else {

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
                        syncDriverDetailsData(OwnerID, driverID, AccessToken);
                    } else {
                        Toast.makeText(DriverDetailsActivity.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                } else
                    Toast.makeText(DriverDetailsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("owner_id", OwnerID);
                params.put("driver_id", driverID);
                params.put("access_token", AccessToken);
                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }

    private void getDriverDetailsData(JSONArray jsonArray) {

        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                ArrayList<DriverDetailsModel> driverDetailsData = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    DriverDetailsModel rowData = new DriverDetailsModel();
                    JSONObject ltData = jsonArray.getJSONObject(i);
                    rowData.setName(ltData.getString("key"));
                    rowData.setValue(ltData.getString("value"));
                    driverDetailsData.add(rowData);
                }
                driverDetailsAdapter = new DriverDetailsAdapter(this, driverDetailsData);
                recyclerView.setAdapter(driverDetailsAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public class DriverDetailsAdapter extends RecyclerView.Adapter<DriverDetailsAdapter.ViewHolder> {
        private Context mContext;
        List<DriverDetailsModel> list;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_details_rowlayout, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }


        public DriverDetailsAdapter(Context mContext, List<DriverDetailsModel> list) {
            this.mContext = mContext;
            this.list = list;

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            DriverDetailsModel driverDetailsModel = list.get(position);
            final String name = driverDetailsModel.getName();
            final String value = driverDetailsModel.getValue();

            holder.name.setText(name);
            if (value.equalsIgnoreCase(null) || value.equalsIgnoreCase("")) {
                holder.value.setText("-NA-");
            } else {
                holder.value.setText(value);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView name, value;
            public CardView mCardView;

            public ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.tv_name);
                name.setTypeface(typeFaceBold);
                value = itemView.findViewById(R.id.tv_value);
                value.setTypeface(typeFaceLight);
                mCardView = (CardView) itemView.findViewById(R.id.cardview_driverDetails);
            }

        }

    }

    public void CustomDialog(String title, String message) {

        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTextGravity(Gravity.CENTER_HORIZONTAL)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message);
        builder.addButton("OK", Color.parseColor("#FFFFFF"), Color.parseColor("#FF4CAF50"),
                CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        builder.show();
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

}
