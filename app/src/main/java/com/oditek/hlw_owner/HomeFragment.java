package com.oditek.hlw_owner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {
    TextView tvRl2,owner_Name,tv_driverNos,tv_Driver,tv_vehicleNos,tv_Vehicle,tv_myProfile,tv_Vehicl,tv_Drive,tv_Notifi;
    ImageView ivBack,iv_logout,owner_image;
    RelativeLayout relative_Myprofile,relative_Vehicle,relative_Driver;
    String ownerImage,ownerName,vehicleCount,driverCount,ownerID,mobileNo,accessToken;
    public double longitude, latitude;
    String currentAddress,deviceType,device_ID;
    NetworkConnection nw;
    AVLoadingIndicatorView progressDialog;
    private int i = 0;
    boolean allowBack;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        Typeface typeFaceLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins-SemiBold.ttf");

        nw = new NetworkConnection(getActivity());
        tvRl2=view.findViewById(R.id.tvRl2);
        tvRl2.setText("DASHBOARD");
        tvRl2.setTypeface(typeFaceBold);
        ivBack=view.findViewById(R.id.ivBack);
        ivBack.setVisibility(View.GONE);
        iv_logout=view.findViewById(R.id.iv_logout);
        iv_logout.setVisibility(View.VISIBLE);
        relative_Myprofile=view.findViewById(R.id.relative_Myprofile);
        relative_Vehicle=view.findViewById(R.id.relative_Vehicle);
        relative_Driver=view.findViewById(R.id.relative_Driver);
        tv_driverNos=view.findViewById(R.id.tv_driverNos);
        tv_Driver=view.findViewById(R.id.tv_Driver);
        tv_Driver.setTypeface(typeFaceBold);
        tv_vehicleNos=view.findViewById(R.id.tv_vehicleNos);
        tv_Vehicle=view.findViewById(R.id.tv_Vehicle);
        tv_Vehicle.setTypeface(typeFaceBold);
        owner_Name=view.findViewById(R.id.owner_Name);
        owner_image=view.findViewById(R.id.owner_image);
        tv_myProfile=view.findViewById(R.id.tv_myProfile);
        tv_myProfile.setTypeface(typeFaceLight);
        tv_Vehicl=view.findViewById(R.id.tv_Vehicl);
        tv_Vehicl.setTypeface(typeFaceLight);
        tv_Drive=view.findViewById(R.id.tv_Drive);
        tv_Drive.setTypeface(typeFaceLight);
        tv_Notifi=view.findViewById(R.id.tv_Drive);
        tv_Notifi.setTypeface(typeFaceLight);
        progressDialog =view.findViewById(R.id.progressDialog);

        device_ID = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        deviceType = "Android";
        ownerID= ApiClient.getDataFromKey(getActivity(),"owner_id");
        mobileNo= ApiClient.getDataFromKey(getActivity(),"mobile");
        accessToken= ApiClient.getDataFromKey(getActivity(),"access_token");
        driverCount= ApiClient.getDataFromKey(getActivity(),"no_of_driver");
        tv_driverNos.setText(driverCount);
        tv_driverNos.setTypeface(typeFaceBold);
        vehicleCount= ApiClient.getDataFromKey(getActivity(),"no_of_vehicle");
        tv_vehicleNos.setText(vehicleCount);
        tv_vehicleNos.setTypeface(typeFaceBold);
        ownerName= ApiClient.getDataFromKey(getActivity(),"owner_name");
        owner_Name.setText(ownerName);
        owner_Name.setTypeface(typeFaceBold);
        ownerImage= ApiClient.getDataFromKey(getActivity(),"image");
        Glide
                .with(this)
                .load(ownerImage)
                .into(owner_image);

        relative_Myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.frame_container, new MyProfileFragment());
                ft.commit();
            }
        });
        relative_Vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.frame_container, new VehicleFragment());
                ft.commit();
            }
        });
        relative_Driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.frame_container, new DriverFragment());
                ft.commit();
            }
        });

        //
        //Getting current latitude and longitude using network provider
        Location location = getLocation();
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentAddress = addresses.get(0).getAddressLine(0);

        if (nw.isConnectingToInternet()) {

            syncRegisterDeviceInfo(ownerID,accessToken,mobileNo,device_ID,deviceType,latitude,longitude,currentAddress);

        } else {

            NoInternetDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");

        }

        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignoutDialog();
            }
        });

        System.out.println("Latitude======"+latitude);
        System.out.println("Longitude======"+longitude);
        System.out.println("WonerID======"+ownerID);
        System.out.println("Mobile======"+mobileNo);
        System.out.println("AccessToken======"+accessToken);
        System.out.println("Location======"+currentAddress);
        System.out.println("DeviceType======"+deviceType);
        System.out.println("DeviceID======"+device_ID);
        return view;
    }
    private void syncRegisterDeviceInfo(final String WonerID,final String AccessToken,final String Mobile,final String DeviceID
    ,final String DeviceType,final double Latitude,final double Longitude,final String Location) {
        startLoading();
        String tag_json_req = "sync_RegisterDeviceInfo";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.registered_deviceinfo_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            stopLoading();
                            Log.d("responsedevice=========", response);
                            JSONObject jsonObject = new JSONObject(response);
                            System.out.println("json----" + jsonObject);

                            String status_ = jsonObject.getString("status");
                            String msg_ = jsonObject.getString("msg");

                            if ("0".equalsIgnoreCase(status_)) {
                                ErrorDialog("Error",msg_);
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
                        syncRegisterDeviceInfo(WonerID,AccessToken,Mobile,DeviceID,DeviceType,Latitude,Longitude,Location);
                    } else {
                        Toast.makeText(getActivity(), "Please try after sometime...",
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
                params.put("owner_id", WonerID);
                params.put("access_token", AccessToken);
                params.put("mobile", Mobile);
                params.put("device_id", DeviceID);
                params.put("device_type", DeviceType);
                params.put("latitude", String.valueOf(Latitude));
                params.put("longitude", String.valueOf(Longitude));
                params.put("location", Location);
                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }

    public void SignoutDialog() {
        // Create Alert using Builder
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getActivity())
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTextGravity(Gravity.CENTER_HORIZONTAL)
                .setTitle("Logout")
                .setCancelable(false)
                .setMessage("Are you sure you to logout?");
        builder.addButton("Logout", Color.parseColor("#FFFFFF"), Color.parseColor("#FF4CAF50"), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                        //On Successful sign out we navigate the user back to LoginActivity
                        ApiClient.clearData(getActivity());
                        Intent intent = new Intent(getActivity(), LoginMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().finish();
                    }

        });

        builder.addButton("Dismiss", Color.parseColor("#FFFFFF"), Color.parseColor("#FFFFFF"), CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    public Location getLocation() {
        Location location = null;
        final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
        final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            try {
                LocationManager locationManager = (LocationManager) getActivity()
                        .getSystemService(Context.LOCATION_SERVICE);

                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
//updating when location is changed
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };

                boolean isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                boolean isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (!isNetworkEnabled && !isGPSEnabled) {
//call getLocation again in onResume method in this case
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                } else {

                    if (isNetworkEnabled) {

                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }

                    } else {

                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        Log.d("GPS", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        }

                    }


                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

//Request for the ACCESS FINE LOCATION
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//call the getLocation funtion again on permission granted callback
        }
        return location;
    }
    private void NoInternetDialog(String title, String message) {

        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getActivity())
                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                .setTextGravity(Gravity.CENTER_HORIZONTAL)
                .setTitle(title)
                .setCancelable(false)
                .setMessage(message);
        builder.addButton("OK", Color.parseColor("#FFFFFF"), Color.parseColor("#C91211"),
                CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent in = new Intent(getActivity(),MainActivity.class);
                        startActivity(in);
                    }
                });

        builder.show();
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
}
