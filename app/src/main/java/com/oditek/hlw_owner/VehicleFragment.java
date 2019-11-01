package com.oditek.hlw_owner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class VehicleFragment extends Fragment {
    TextView tvRl2;
    ImageView ivBack;
    RecyclerView recyclerView;
    CardView cardView;
    NetworkConnection nw;
    private int i = 0;
    ProgressDialog progressDialog;
    String ownerID,access_Token;
    VehicleAdapter vehicleAdapter;
    String vehicleID_;
    Typeface typeFaceLight,typeFaceBold;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_vehicle, container, false);
        //

        typeFaceLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins-Regular.ttf");
        typeFaceBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins-SemiBold.ttf");

        tvRl2=view.findViewById(R.id.tvRl2);
        tvRl2.setText("VEHICLE");
        tvRl2.setTypeface(typeFaceBold);
        ivBack=view.findViewById(R.id.ivBack);
        nw = new NetworkConnection(getActivity());
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ApiClient.saveDataWithKeyAndValue(getActivity(),"BackPress","true");
                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.frame_container, new HomeFragment());
                ft.commit();

            }
        });
        //
        cardView = (CardView) view.findViewById(R.id.cardview_Vehicle);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        ownerID= ApiClient.getDataFromKey(getActivity(),"owner_id");
        access_Token= ApiClient.getDataFromKey(getActivity(),"access_token");
        if (nw.isConnectingToInternet()) {
            syncVehicleListingData(ownerID,access_Token);
        } else {
            CustomDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
        }
   return view;
    }
    private void syncVehicleListingData(final String OwnerID, final String AccessToken) {
        startLoading();
        String tag_json_req = "sync_VehicleListingData";

        StringRequest data = new StringRequest(Request.Method.POST,
                getActivity().getResources().getString(R.string.base_url) + getActivity().getResources()
                        .getString(R.string.vehicle_listing_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            stopLoading();
                            Log.d("response=========", response);
                            JSONObject jsonObject = new JSONObject(response);
                            String status_ = jsonObject.getString("status");
                            if ("0".equalsIgnoreCase(status_)) {
                                String msg_ = jsonObject.getString("msg");
                                ErrorDialog("Error", msg_);
                            } else if ("3".equalsIgnoreCase(status_)) {
                                String msg_ = jsonObject.getString("msg");
                                StatusDialog("", msg_);
                            }else {
                                JSONArray jsonArray = jsonObject.getJSONArray("dataArray");
                                System.out.println("json----" + jsonObject);
                                vehicleAdapter = new VehicleAdapter(getActivity(), getVehicleData(jsonArray));
                                recyclerView.setAdapter(vehicleAdapter);
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
                        syncVehicleListingData(OwnerID,AccessToken);
                    } else {
                        Toast.makeText(getActivity(), "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                } else
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("owner_id", OwnerID);
                params.put("access_token", AccessToken);
                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }

    private ArrayList<VehicleModel> getVehicleData(JSONArray jsonArray) {
        ArrayList<VehicleModel> vehicleData = new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    VehicleModel rowData = new VehicleModel();
                    JSONObject ltData = jsonArray.getJSONObject(i);
                    rowData.setVehicleID(ltData.getString("vehicle_id"));;
                    rowData.setVehicleImage(ltData.getString("v_image"));;
                    rowData.setVehicleBrand(ltData.getString("model_name"));;
                    rowData.setRegdNo(ltData.getString("reg_no"));;
                    rowData.setVehicleType(ltData.getString("cat_name"));;
                    rowData.setVehicleImageType(ltData.getString("cat_iamge"));;
                    rowData.setMfgName(ltData.getString("manf_name"));;
                    rowData.setVehicleStatus(ltData.getString("status"));;
                    rowData.setDriverName(ltData.getString("driver_name"));;
                    vehicleData.add(rowData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vehicleData;
    }
    public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder> {
        private Context mContext;
        RelativeLayout relative_VehicleMain;
        List<VehicleModel> list;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_rowlayout, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }


        public VehicleAdapter(Context mContext, List<VehicleModel> list) {
            this.mContext = mContext;
            this.list = list;

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final VehicleModel vehicleModel = list.get(position);
            final String vehicleImage = vehicleModel.getVehicleImage();
            final String vehicleBrand = vehicleModel.getVehicleBrand();
            final String vehicleType = vehicleModel.getVehicleType();
            final String vehicleRegdNo = vehicleModel.getRegdNo();
            final String vehicleImageType = vehicleModel.getVehicleImageType();
            final String vehicleMfgName = vehicleModel.getMfgName();
            final String vehicleStatus = vehicleModel.getVehicleStatus();
            final String driverName = vehicleModel.getDriverName();
            if (vehicleImage.equalsIgnoreCase(null) || vehicleImage.equalsIgnoreCase("")) {
                Glide
                        .with(mContext)
                        .load(R.mipmap.car)
                        .into(holder.vehicleImage);
            }else{
                Glide
                        .with(mContext)
                        .load(vehicleImage)
                        .into(holder.vehicleImage);
            }
            if (vehicleImageType.equalsIgnoreCase(null) || vehicleImageType.equalsIgnoreCase("")) {
                Glide
                        .with(mContext)
                        .load(R.mipmap.car)
                        .into(holder.iv_vehicleType);
            }else{
                Glide
                        .with(mContext)
                        .load(vehicleImageType)
                        .into(holder.iv_vehicleType);
            }
            if (vehicleStatus.equalsIgnoreCase("1")) {
                holder.tv_VehicleStatus.setText("ACTIVE");
                holder.tv_VehicleStatus.setTextColor(Color.parseColor("#2DCE0D"));
            } else {
                holder.tv_VehicleStatus.setText("IN-ACTIVE");
                holder.tv_VehicleStatus.setTextColor(Color.parseColor("#F72313"));
            }

            holder.tv_MfgName.setText((vehicleMfgName)+" :");
            holder.vehicleBrand.setText(vehicleBrand);
            holder.vehicleType.setText(": "+(vehicleType));
            holder.vehicleRegdNo.setText("["+(vehicleRegdNo)+"]");
            if(driverName.equalsIgnoreCase("")){
                holder.tv_driverName.setText("-NA-");
            }else{
                holder.tv_driverName.setText(driverName);
            }


            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(mContext, VehicleDetailsActivity.class);
                    intent.putExtra("VehicleID",vehicleModel.getVehicleID());
                    intent.putExtra("CarImage",vehicleModel.getVehicleImage());
                    intent.putExtra("CarMfgNType",vehicleModel.getMfgName()+":"+vehicleModel.getVehicleBrand());
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView vehicleBrand,vehicleType,vehicleRegdNo,tv_VehicleStatus,tv_MfgName,tv_driverName;
            public ImageView vehicleImage,iv_vehicleType;
            public CardView mCardView;

            public ViewHolder(View itemView) {
                super(itemView);
                vehicleImage = itemView.findViewById(R.id.iv_vehicle);
                iv_vehicleType = itemView.findViewById(R.id.iv_vehicleType);
                vehicleBrand = itemView.findViewById(R.id.tv_vehicleBrand);
                vehicleBrand.setTypeface(typeFaceBold);
                vehicleType = itemView.findViewById(R.id.tv_vehicleType);
                vehicleType.setTypeface(typeFaceLight);
                vehicleRegdNo = itemView.findViewById(R.id.tv_vehicleRegdno);
                vehicleRegdNo.setTypeface(typeFaceLight);
                tv_MfgName = itemView.findViewById(R.id.tv_MfgName);
                tv_MfgName.setTypeface(typeFaceBold);
                tv_VehicleStatus = itemView.findViewById(R.id.tv_VehicleStatus);
                tv_VehicleStatus.setTypeface(typeFaceBold);
                tv_driverName = itemView.findViewById(R.id.tv_driverName);
                tv_driverName.setTypeface(typeFaceLight);
                mCardView = (CardView) itemView.findViewById(R.id.cardview_Vehicle);
            }

        }

    }
    public void CustomDialog(String title, String message) {

        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getActivity())
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
        progressDialog = CustomProgressDialog.custom(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    void stopLoading() {
        if ((progressDialog != null) && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    public void ErrorDialog(String title, String subtitle) {
        new iOSDialogBuilder(getActivity())
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
    public void StatusDialog(String title, String subtitle) {
        new iOSDialogBuilder(getActivity())
                .setTitle(title)
                .setSubtitle(subtitle)
                .setBoldPositiveLabel(true)
                .setCancelable(false)
                .setPositiveListener("Ok", new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        ApiClient.clearData(getActivity());
                        Intent intent = new Intent(getActivity(), LoginMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .build().show();
    }
}

