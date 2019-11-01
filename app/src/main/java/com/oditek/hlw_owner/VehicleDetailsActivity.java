package com.oditek.hlw_owner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.oditek.hlw_owner.webservices.ApiClient;
import com.oditek.hlw_owner.webservices.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleDetailsActivity extends AppCompatActivity {
    TextView tvRl2, tv_VehicleName;
    ImageView ivBack, vehicle_image;
    RecyclerView recyclerView;
    CardView cardView;
    NetworkConnection nw;
    private int i = 0;
    ProgressDialog progressDialog;
    String ownerID, access_Token;
    VehicleDetailsAdapter vehicleDetailsAdapter;
    String name_, image_, vehicleID,carImage,carName;
    Typeface typeFaceLight, typeFaceBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

        typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");

        tvRl2 = findViewById(R.id.tvRl2);
        tvRl2.setText("VEHICLE DETAILS");
        tvRl2.setTypeface(typeFaceBold);
        tv_VehicleName = findViewById(R.id.tv_VehicleName);
        tv_VehicleName.setTypeface(typeFaceBold);
        vehicle_image = findViewById(R.id.vehicle_image);
        ivBack = findViewById(R.id.ivBack);
        nw = new NetworkConnection(this);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //
        cardView = (CardView) findViewById(R.id.cardview_VehicleDetails);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(VehicleDetailsActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        Bundle bundle = getIntent().getExtras();
        vehicleID = bundle.getString("VehicleID");
        carImage = bundle.getString("CarImage");
        carName = bundle.getString("CarMfgNType");
        System.out.println("Vehicle Id Is----" + vehicleID);
        System.out.println("Car Image===== Is----" + carImage);
        System.out.println("Car Name===== Is----" + carName);
        ownerID = ApiClient.getDataFromKey(this, "owner_id");
        access_Token = ApiClient.getDataFromKey(this, "access_token");
        if (nw.isConnectingToInternet()) {
            syncVehicleDetailsData(ownerID, vehicleID, access_Token);
        } else {
            ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
        }

    }

    private void syncVehicleDetailsData(final String OwnerID, final String vehicleID, final String AccessToken) {
        startLoading();
        String tag_json_req = "sync_VehicleDetailsData";

        StringRequest data = new StringRequest(Request.Method.POST,
                this.getResources().getString(R.string.base_url) + this.getResources()
                        .getString(R.string.vehicle_details_url),
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
                            if (status_.equalsIgnoreCase("1")) {
                                name_ = jsonObject.getString("cat_name");
                                tv_VehicleName.setText(name_);
                                image_ = jsonObject.getString("cat_iamge");
                                if (image_.equalsIgnoreCase(null) || image_.equalsIgnoreCase("")) {
                                    Glide
                                            .with(VehicleDetailsActivity.this)
                                            .load(R.mipmap.car)
                                            .into(vehicle_image);
                                } else {
                                    Glide
                                            .with(VehicleDetailsActivity.this)
                                            .load(image_)
                                            .into(vehicle_image);
                                }
                                JSONArray jsonArray = jsonObject.getJSONArray("details");
                                getVehicleDetailsData(jsonArray);
                            } else if (status_.equalsIgnoreCase("0")) {
                                ErrorDialog("", msg_);
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
                        syncVehicleDetailsData(OwnerID, vehicleID, AccessToken);
                    } else {
                        Toast.makeText(VehicleDetailsActivity.this, "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                } else
                    Toast.makeText(VehicleDetailsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("owner_id", OwnerID);
                params.put("access_token", AccessToken);
                params.put("vehicle_id", vehicleID);
                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }

    private void getVehicleDetailsData(JSONArray jsonArray) {
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                ArrayList<VehicleDetailsModel> vehicleDetailsData = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    VehicleDetailsModel rowData = new VehicleDetailsModel();
                    JSONObject ltData = jsonArray.getJSONObject(i);
                    rowData.setName(ltData.getString("key"));
                    ;
                    rowData.setValue(ltData.getString("value"));
                    ;
                    vehicleDetailsData.add(rowData);
                }
                vehicleDetailsAdapter = new VehicleDetailsAdapter(this, vehicleDetailsData);
                recyclerView.setAdapter(vehicleDetailsAdapter);

            } else {

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public class VehicleDetailsAdapter extends RecyclerView.Adapter<VehicleDetailsAdapter.ViewHolder> {
        private Context mContext;
        List<VehicleDetailsModel> list;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_details_rowlayout, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }


        public VehicleDetailsAdapter(Context mContext, List<VehicleDetailsModel> list) {
            this.mContext = mContext;
            this.list = list;

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            VehicleDetailsModel vehicleDetailsModel = list.get(position);
            final String name = vehicleDetailsModel.getName();
            final String value = vehicleDetailsModel.getValue();

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
                mCardView = (CardView) itemView.findViewById(R.id.cardview_VehicleDetails);
            }

        }

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
}
