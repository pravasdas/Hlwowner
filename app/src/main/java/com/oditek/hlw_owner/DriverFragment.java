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


public class DriverFragment extends Fragment {
    TextView tvRl2;
    ImageView ivBack;
    RecyclerView recyclerView;
    CardView cardView;
    NetworkConnection nw;
    private int i = 0;
    ProgressDialog progressDialog;
    String ownerID, access_Token;
    DriverAdapter driverAdapter;
    ArrayList<DriverModel> driverList;
    String driverID_;
    Typeface typeFaceLight,typeFaceBold;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver, container, false);

        typeFaceLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins-Regular.ttf");
        typeFaceBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins-SemiBold.ttf");

        tvRl2 = view.findViewById(R.id.tvRl2);
        tvRl2.setText("DRIVER");
        tvRl2.setTypeface(typeFaceBold);
        ivBack = view.findViewById(R.id.ivBack);
        nw = new NetworkConnection(getActivity());
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ApiClient.saveDataWithKeyAndValue(getActivity(),"BackPress","yes");
                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.frame_container, new HomeFragment());
                ft.commit();

            }
        });
        //
        cardView = (CardView) view.findViewById(R.id.cardview_Driver);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        ownerID = ApiClient.getDataFromKey(getActivity(), "owner_id");
        access_Token = ApiClient.getDataFromKey(getActivity(), "access_token");
        if (nw.isConnectingToInternet()) {
            syncDriverListingData(ownerID, access_Token);
        } else {
            CustomDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
        }
        return view;
    }

    private void syncDriverListingData(final String OwnerID, final String AccessToken) {
        startLoading();
        String tag_json_req = "sync_DriverListingData";

        StringRequest data = new StringRequest(Request.Method.POST,
                getActivity().getResources().getString(R.string.base_url) + getActivity().getResources()
                        .getString(R.string.driver_listing_url),
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
                                StatusDialog("Attention!!!", msg_);
                            } else if ("1".equalsIgnoreCase(status_)) {
                                JSONArray jsonArray = jsonObject.getJSONArray("dataArray");
                                System.out.println("json----" + jsonArray);
                                driverAdapter = new DriverAdapter(getActivity(), getDriverData(jsonArray));
                                recyclerView.setAdapter(driverAdapter);
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
                        syncDriverListingData(OwnerID, AccessToken);
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

    private ArrayList<DriverModel> getDriverData(JSONArray jsonArray) {
        ArrayList<DriverModel> driverData = new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    DriverModel rowData = new DriverModel();
                    JSONObject ltData = jsonArray.getJSONObject(i);
                    rowData.setDriverId(ltData.getString("driver_id"));
                    rowData.setPhoto(ltData.getString("image"));
                    rowData.setName(ltData.getString("name"));
                    rowData.setContact(ltData.getString("mobile"));
                    rowData.setMfgName(ltData.getString("manf_name"));
                    rowData.setVehicleName(ltData.getString("vehicle_name"));
                    rowData.setRegdNo(ltData.getString("vehicle_no"));
                    rowData.setStatus(ltData.getString("duty_status"));
                    driverData.add(rowData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return driverData;
    }


    public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.ViewHolder> {
        private Context mContext;

        List<DriverModel> list;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_rowlayout, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }


        public DriverAdapter(Context mContext, List<DriverModel> list) {
            this.mContext = mContext;
            this.list = list;
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final DriverModel driverModel = list.get(position);
            final String driverPhoto = driverModel.getPhoto();
            final String status = driverModel.getStatus();
            final String name = driverModel.getName();
            final String contact = driverModel.getContact();
            final String mfgName = driverModel.getMfgName();
            final String vehicleName = driverModel.getVehicleName();
            final String regdNo = driverModel.getRegdNo();
            if (driverPhoto.equalsIgnoreCase(null) || driverPhoto.equalsIgnoreCase("")) {
                Glide
                        .with(mContext)
                        .load(R.mipmap.profile)
                        .into(holder.iv_Photo);
            } else {
                Glide
                        .with(mContext)
                        .load(driverPhoto)
                        .into(holder.iv_Photo);
            }
            if (status.equalsIgnoreCase("1")) {
                holder.tv_DriverStatus.setText("ON DUTY");
                holder.tv_DriverStatus.setTextColor(Color.parseColor("#2DCE0D"));
            } else {
                holder.tv_DriverStatus.setText("OFF DUTY");
                holder.tv_DriverStatus.setTextColor(Color.parseColor("#F72313"));
            }
            holder.tv_Name.setText(name);
            holder.tv_Contact.setText(contact);
            holder.tv_vehicleNo.setText("["+(regdNo)+"]");
            holder.tv_MfgName.setText((mfgName)+" :");
            holder.tv_vehicleName.setText(vehicleName);
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, DriverDetailsActivity.class);
                    intent.putExtra("DriverId", driverModel.getDriverId());
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tv_Name, tv_Contact, tv_vehicleNo, tv_vehicleName,tv_DriverStatus,tv_MfgName;
            public ImageView iv_Photo;
            public CardView mCardView;

            public ViewHolder(View itemView) {
                super(itemView);
                iv_Photo = itemView.findViewById(R.id.iv_Photo);
                tv_Name = itemView.findViewById(R.id.tv_Name);
                tv_Name.setTypeface(typeFaceBold);
                tv_Contact = itemView.findViewById(R.id.tv_Contact);
                tv_Contact.setTypeface(typeFaceLight);
                tv_vehicleName = itemView.findViewById(R.id.tv_vehicleName);
                tv_vehicleName.setTypeface(typeFaceLight);
                tv_vehicleNo = itemView.findViewById(R.id.tv_vehicleNo);
                tv_vehicleNo.setTypeface(typeFaceLight);
                tv_DriverStatus = itemView.findViewById(R.id.tv_DriverStatus);
                tv_DriverStatus.setTypeface(typeFaceBold);
                tv_MfgName = itemView.findViewById(R.id.tv_MfgName);
                tv_MfgName.setTypeface(typeFaceLight);
                mCardView = (CardView) itemView.findViewById(R.id.cardview_Driver);
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

