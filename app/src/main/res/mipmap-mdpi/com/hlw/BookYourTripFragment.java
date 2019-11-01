package oditek.com.hlw;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.wang.avi.AVLoadingIndicatorView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import oditek.com.hlw.datamodel.OnewayCarListModel;
import oditek.com.hlw.webservices.AppController;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class BookYourTripFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, DatePickerDialog.OnDateSetListener {

    NetworkConnection nw;
    private GoogleMap mMap;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean topLayout = false, buttomOneway = false, buttomrental = false;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    LatLng latLng;
    private ImageView imgMyLocation;
    String address_line = "", car_click = "Micro";
    MarkerOptions markerOptions;
    LinearLayout linear_Share, linear_Micro, linear_Mini, linear_Sedan, booknow, book_micro;
    LinearLayout location_button_layout, mainLinearOneway;
    RelativeLayout rentalLayout, rental_Booknow, rental_Booklater, relativeBooklater, relativeOnewayBook, rlMain, booklater;
    ImageView iv_share, iv_micro, iv_mini, iv_sedan;
    TextView tv_share, tv_share2, tv_micro, tv_micro2, tv_mini, tv_mini2, tv_sedan, tv_sedan2, etDrop, etPick;
    TextView tv_booknow, tv_booklater, tv_txxt, tv_rentalTxt, tv_rentalHourly, tv_rentalMultiple, tv_rentalAlways;
    TextView tv_rentalBooknow, tv_booklaterRental, tv_microRental, tv_ttxt;
    Button sharebook;
    Long time;
    Calendar date;
    String OneWayVehicleType = "4";
    private double currentLatitude;
    private double currentLongitude;
    private DatePickerDialog dpd;
    AVLoadingIndicatorView progressDialog;
    private int i = 0;
    private TextView markerTitle;
    boolean isOneWayClicked = true;
    ImageButton ibRideHistory;
    private LinearLayout vchCategoryLinear;
    RecyclerView recyclerViewOneWay;
    public String PickLatitude = "", PickLongitude = "", DropLatitude = "", DropLongitude = "", add, SelectedTv = "1";
    public boolean selectedOne = false, checked = false;
    String CameraLatitude = "", CameraLongitude = "";
    public double DropLat, DropLong, PickLat, PickLong;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.book_your_trip_layout, container, false);

        Typeface typeFaceLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins-SemiBold.ttf");

        markerTitle = view.findViewById(R.id.tv_title);
        LinearLayout linear_Share = (LinearLayout) view.findViewById(R.id.linear_Share);
        /*LinearLayout linear_Micro = (LinearLayout) view.findViewById(R.id.linear_Micro);
        LinearLayout linear_Mini = (LinearLayout) view.findViewById(R.id.linear_Mini);
        LinearLayout linear_Sedan = (LinearLayout) view.findViewById(R.id.linear_Sedan);*/
        LinearLayout booknow = (LinearLayout) view.findViewById(R.id.booknow);
        mainLinearOneway = (LinearLayout) view.findViewById(R.id.mainLinearOneway);
        rentalLayout = (RelativeLayout) view.findViewById(R.id.rentalLayout);
        //iv_share = view.findViewById(R.id.iv_share);
        /*iv_micro = view.findViewById(R.id.iv_micro);
        iv_mini = view.findViewById(R.id.iv_mini);
        iv_sedan = view.findViewById(R.id.iv_sedan);*/
        etPick = view.findViewById(R.id.etPick);
        etPick.setTypeface(typeFaceBold);
        etDrop = view.findViewById(R.id.etDrop);
        etDrop.setTypeface(typeFaceBold);
        sharebook = (Button) view.findViewById(R.id.sharebook);
        sharebook.setTypeface(typeFaceBold);
        book_micro = view.findViewById(R.id.book_micro);
        booklater = view.findViewById(R.id.booklater);
        rental_Booknow = view.findViewById(R.id.rental_Booknow);
        rental_Booklater = view.findViewById(R.id.rental_Booklater);
        relativeBooklater = (RelativeLayout) view.findViewById(R.id.relativeBooklater);
        ibRideHistory = view.findViewById(R.id.ibRideHistory);
        nw = new NetworkConnection(getActivity());
        progressDialog = view.findViewById(R.id.progressDialog);

        /*tv_share = view.findViewById(R.id.tv_share);
        tv_share.setTypeface(typeFaceLight);
        tv_share2 = view.findViewById(R.id.tv_share2);
        tv_share2.setTypeface(typeFaceLight);*/
     /*   tv_micro = view.findViewById(R.id.tv_micro);
        tv_micro.setTypeface(typeFaceLight);
        tv_micro2 = view.findViewById(R.id.tv_micro2);
        tv_micro2.setTypeface(typeFaceLight);
        tv_mini = view.findViewById(R.id.tv_mini);
        tv_mini.setTypeface(typeFaceLight);
        tv_mini2 = view.findViewById(R.id.tv_mini2);
        tv_mini2.setTypeface(typeFaceLight);
        tv_sedan = view.findViewById(R.id.tv_sedan);
        tv_sedan.setTypeface(typeFaceLight);
        tv_sedan2 = view.findViewById(R.id.tv_sedan2);
        tv_sedan2.setTypeface(typeFaceLight);*/
        tv_booknow = view.findViewById(R.id.tv_booknow);
        tv_booknow.setTypeface(typeFaceBold);
        tv_booklater = view.findViewById(R.id.tv_booklater);
        tv_booklater.setTypeface(typeFaceBold);
        tv_txxt = view.findViewById(R.id.tv_txxt);
        tv_txxt.setTypeface(typeFaceLight);
        tv_rentalTxt = view.findViewById(R.id.tv_rentalTxt);
        tv_rentalTxt.setTypeface(typeFaceLight);
        tv_rentalHourly = view.findViewById(R.id.tv_rentalHourly);
        tv_rentalHourly.setTypeface(typeFaceLight);
        tv_rentalMultiple = view.findViewById(R.id.tv_rentalMultiple);
        tv_rentalMultiple.setTypeface(typeFaceLight);
        tv_rentalAlways = view.findViewById(R.id.tv_rentalAlways);
        tv_rentalAlways.setTypeface(typeFaceLight);
        tv_rentalBooknow = view.findViewById(R.id.tv_rentalBooknow);
        tv_rentalBooknow.setTypeface(typeFaceBold);
        tv_booklaterRental = view.findViewById(R.id.tv_booklaterRental);
        tv_booklaterRental.setTypeface(typeFaceBold);
        tv_ttxt = view.findViewById(R.id.tv_ttxt);
        tv_ttxt.setTypeface(typeFaceLight);
        tv_microRental = view.findViewById(R.id.tv_microRental);
        tv_microRental.setTypeface(typeFaceLight);
        imgMyLocation = view.findViewById(R.id.imgMyLocation);
        location_button_layout = view.findViewById(R.id.location_button_layout);
        rlMain = view.findViewById(R.id.rlMain);

        ibRideHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RideHistoryActivity.class);
                startActivityForResult(intent, 5000);
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.current_location);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), "AIzaSyBYm_JbaHbjE7DAj9aAMC23MMvj5oF1pOI");
        }

        imgMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latLng != null) {
                    getMyLocation();
                }
            }
        });

        etPick.setTextColor(getResources().getColor(R.color.black));
        etDrop.setTextColor(getResources().getColor(R.color.grey));

        etPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPick.setTextColor(getResources().getColor(R.color.black));
                etDrop.setTextColor(getResources().getColor(R.color.grey));

                selectedOne = false;

                String PickMatch = etPick.getText().toString().trim();
                String PickMatch2 = markerTitle.getText().toString().trim();

                if (checked || PickMatch.equalsIgnoreCase(PickMatch2)) {
                    // Set the fields to specify which types of place data to return.
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                            Place.Field.ADDRESS, Place.Field.LAT_LNG);
                    // Start the autocomplete intent.
                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(getActivity());
                    startActivityForResult(intent, 1200);
                } else {

                    LatLng coordinate = new LatLng(PickLat, PickLong);
                    //Store these lat lng values somewhere. These should be constant.
                    CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                            coordinate, 13);
                    mMap.animateCamera(location);

                }
            }
        });

        etDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedOne = true;
                etPick.setTextColor(getResources().getColor(R.color.grey));
                etDrop.setTextColor(getResources().getColor(R.color.black));

                String DropMatch = etDrop.getText().toString().trim();
                String DropMatch2 = markerTitle.getText().toString().trim();


                if (checked || etDrop.getText().toString().isEmpty() || DropMatch.equalsIgnoreCase(DropMatch2)) {
                    // Set the fields to specify which types of place data to return.
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                            Place.Field.ADDRESS, Place.Field.LAT_LNG);
                    // Start the autocomplete intent.
                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(getActivity());
                    startActivityForResult(intent, 1000);
                } else {

                    LatLng coordinate = new LatLng(DropLat, DropLong);
                    //Store these lat lng values somewhere. These should be constant.
                    CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                            coordinate, 13);
                    mMap.animateCamera(location);

                }

            }
        });

        rental_Booknow.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BookNowActivity.class);
                startActivity(intent);
            }
        });

        rental_Booklater.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (etDrop.getText().toString().trim().length() == 0) {
                    etDrop.setError(getResources().getString(R.string.drop_location));
                } else if (etPick.getText().toString().trim().length() == 0) {
                    etPick.setError(getResources().getString(R.string.pick_location));
                } else {
                    //showDateTimePicker();
                    showDatePicker();
                }
            }
        });

        /*linear_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OneWayVehicleType = "1";
                car_click = "Share";
                iv_share.setImageResource(R.mipmap.car_share_blue);
                iv_micro.setImageResource(R.mipmap.car_micro_white);
                iv_mini.setImageResource(R.mipmap.car_mini_white);
                iv_sedan.setImageResource(R.mipmap.car_sedan_white);
                sharebook.setVisibility(View.VISIBLE);
                book_micro.setVisibility(View.GONE);

            }
        });*/

        //This below code is for clicking from an Activity to show particular layout in another fragment
        final Button buttonOneway = (Button) getActivity().findViewById(R.id.btnOneWay);
        final Button buttonRental = (Button) getActivity().findViewById(R.id.btnRentals);

        buttonOneway.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                isOneWayClicked = true;
                rentalLayout.setVisibility(View.GONE);
                mainLinearOneway.setVisibility(View.VISIBLE);
                etDrop.setVisibility(View.VISIBLE);
                ibRideHistory.setVisibility(View.VISIBLE);

                buttonOneway.setBackgroundResource(R.drawable.diagonal_filled);
                buttonRental.setBackgroundResource(R.drawable.diagonal);
                buttonOneway.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
                buttonRental.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

              /*  if (nw.isConnectingToInternet()) {
                    String latitude_= String.valueOf(currentLatitude);
                    String longitude_= String.valueOf(currentLongitude);
                    String user_id_ = ApiClient.getDataFromKey(getActivity(),"user_id");
                    String access_token_=ApiClient.getDataFromKey(getActivity(),"access_token");

                    syncGetCabLocation(user_id_,access_token_,"0","0", latitude_, longitude_);
                }else {
                    ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                }*/

            }
        });

        buttonRental.setOnClickListener(new View.OnClickListener()

        {

            @Override
            public void onClick(View v) {
                isOneWayClicked = false;
                rentalLayout.setVisibility(View.VISIBLE);//rental bottom bar
                mainLinearOneway.setVisibility(View.GONE);//oneway bottombar
                etDrop.setVisibility(View.GONE);
                ibRideHistory.setVisibility(View.GONE);
                buttonRental.setBackgroundResource(R.drawable.diagonal_filled);
                buttonOneway.setBackgroundResource(R.drawable.diagonal);
                buttonRental.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
                buttonOneway.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

                /*if (nw.isConnectingToInternet()) {
                    String latitude_ = String.valueOf(currentLatitude);
                    String longitude_ = String.valueOf(currentLongitude);
                    String user_id_ = ApiClient.getDataFromKey(getActivity(), "user_id");
                    String access_token_ = ApiClient.getDataFromKey(getActivity(), "access_token");

                    //syncGetCabLocation(user_id_, access_token_, "1", "5", latitude_, longitude_,"Rental");
                } else {
                    ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                }*/


            }
        });

        /*linear_Micro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OneWayVehicleType = "2";
                car_click = "Micro";
                iv_share.setImageResource(R.mipmap.car_share_white);
                iv_micro.setImageResource(R.mipmap.car_micro_blue);
                iv_mini.setImageResource(R.mipmap.car_mini_white);
                iv_sedan.setImageResource(R.mipmap.car_sedan_white);
                sharebook.setVisibility(View.GONE);
                book_micro.setVisibility(View.VISIBLE);

            }
        });*/

        /*linear_Mini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OneWayVehicleType = "3";
                car_click = "Mini";
                iv_share.setImageResource(R.mipmap.car_share_white);
                iv_micro.setImageResource(R.mipmap.car_micro_white);
                iv_mini.setImageResource(R.mipmap.car_mini_blue);
                iv_sedan.setImageResource(R.mipmap.car_sedan_white);
                sharebook.setVisibility(View.GONE);
                book_micro.setVisibility(View.VISIBLE);

            }
        });*/

        /*linear_Sedan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OneWayVehicleType = "4";
                car_click = "Sedan";
                iv_share.setImageResource(R.mipmap.car_share_white);
                iv_micro.setImageResource(R.mipmap.car_micro_white);
                iv_mini.setImageResource(R.mipmap.car_mini_white);
                iv_sedan.setImageResource(R.mipmap.car_sedan_blue);
                sharebook.setVisibility(View.GONE);
                book_micro.setVisibility(View.VISIBLE);

            }
        });*/

        booknow.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {


                if (etDrop.getText().toString().trim().length() == 0) {
                    etDrop.setError(getResources().getString(R.string.drop_location));
                } else if (etPick.getText().toString().trim().length() == 0) {
                    etPick.setError(getResources().getString(R.string.pick_location));
                } else {
                    String PickAdd = etPick.getText().toString().trim();
                    String DropAdd = etDrop.getText().toString().trim();
                    System.out.println("currentLongitude2-" + currentLongitude + ":::::currentLatitude2" + currentLatitude);

                    Intent booknow = new Intent(getActivity(), OnewayBookNowActivity.class);
                    booknow.putExtra("ClickedFrom", "MicroBookNow");
                    booknow.putExtra("OneWayVehicleType", "2");
                    booknow.putExtra("CarClicked", car_click);
                    booknow.putExtra("PickLatitude", String.valueOf(currentLatitude));
                    booknow.putExtra("PickLongitude", String.valueOf(currentLongitude));
                    booknow.putExtra("DropLatitude", DropLatitude);
                    booknow.putExtra("DropLongitude", DropLongitude);
                    booknow.putExtra("PickAdd", PickAdd);
                    booknow.putExtra("DropAdd", DropAdd);
                    startActivity(booknow);
                }
                etDrop.requestFocus();
            }
        });

        sharebook.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {

                if (etDrop.getText().toString().trim().length() == 0) {
                    etDrop.setError(getResources().getString(R.string.drop_location));
                } else if (etPick.getText().toString().trim().length() == 0) {
                    etPick.setError(getResources().getString(R.string.pick_location));
                } else {
                    Intent booknow = new Intent(getActivity(), OnewayBookNowActivity.class);
                    booknow.putExtra("ClickedFrom", "ShareBookNow");
                    booknow.putExtra("OneWayVehicleType", "1");
                    booknow.putExtra("CarClicked", "Share");
                    booknow.putExtra("PickLatitude", String.valueOf(currentLatitude));
                    booknow.putExtra("PickLongitude", String.valueOf(currentLongitude));
                    booknow.putExtra("DropLatitude", DropLatitude);
                    booknow.putExtra("DropLongitude", DropLongitude);
                    startActivity(booknow);
                }
            }
        });

        booklater.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                //showDateTimePicker();

                if (etDrop.getText().toString().trim().length() == 0) {
                    etDrop.setError(getResources().getString(R.string.drop_location));
                } else if (etPick.getText().toString().trim().length() == 0) {
                    etPick.setError(getResources().getString(R.string.pick_location));
                } else {
                    showDatePicker();
                }
            }
        });

        recyclerViewOneWay = (RecyclerView) view.findViewById(R.id.recyclerViewOneWay);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewOneWay.setLayoutManager(linearLayoutManager);
        //recyclerViewOneWay.setLayoutManager();



        return view;
    }

    private void getMyLocation() {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f);
        mMap.animateCamera(cameraUpdate);
        //mMap.addMarker(markerOptions).showInfoWindow();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1200) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getAddress() + ", " + place.getId());
                etPick.setText(place.getAddress());
                etPick.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey));

                LatLng latLng = place.getLatLng();
                PickLatitude = String.valueOf(latLng.latitude);
                PickLongitude = String.valueOf(latLng.longitude);

                currentLatitude = Double.valueOf(PickLatitude);
                currentLongitude = Double.valueOf(PickLongitude);

                Log.i(TAG, "PickLatitude:===" + PickLatitude + "PickLongitude:===" + PickLongitude);

                LatLng coordinate = new LatLng(currentLatitude, currentLongitude);
                //Store these lat lng values somewhere. These should be constant.
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                        coordinate, 13);
                mMap.animateCamera(location);


            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getAddress() + ", " + place.getId());
                etDrop.setText(place.getAddress());
                etDrop.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                etDrop.setError(null);

                String DropLatLong = place.getLatLng().toString();
                Log.i(TAG, "Droplatlong===" + DropLatLong);

                LatLng latLng = place.getLatLng();
                DropLatitude = String.valueOf(latLng.latitude);
                DropLongitude = String.valueOf(latLng.longitude);

                Log.i(TAG, "DropLatitude:===" + DropLatitude + "DropLongitude:===" + DropLongitude);

                Float Dlat = Float.parseFloat(DropLatitude);
                Float Dlong = Float.parseFloat(DropLongitude);

                LatLng coordinate = new LatLng(Dlat, Dlong);
                //Store these lat lng values somewhere. These should be constant.
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                        coordinate, 13);
                mMap.animateCamera(location);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        if (resultCode == 5000) {
            String add = data.getStringExtra("Address");
            etDrop.setText(add);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);

        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                Log.e("TripFragment:::", "setOnCameraMoveStartedListener");
                mainLinearOneway.animate().translationY(mainLinearOneway.getHeight()).setDuration(200);
                rentalLayout.animate().translationY(rentalLayout.getHeight()).setDuration(200);
                rlMain.animate().translationY(-270).setDuration(200);
                location_button_layout.setVisibility(View.GONE);
            }

        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                rentalLayout.animate().translationY(0).setDuration(200);
                mainLinearOneway.animate().translationY(0).setDuration(200);
                rlMain.animate().translationY(0).setDuration(200);
                location_button_layout.setVisibility(View.VISIBLE);
                Log.e("TripFragment:::", "setOnCameraIdleListener");
            }
        });
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @SuppressWarnings("deprecated")
    @Override
    public void onConnected(Bundle bundle) {
        if (mGoogleApiClient.isConnected()) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);
            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult locationSettingsResult) {

                    final Status status = locationSettingsResult.getStatus();
                    final LocationSettingsStates LS_state = locationSettingsResult.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                status.startResolutionForResult(getActivity(), MY_PERMISSIONS_REQUEST_LOCATION);

                            } catch (IntentSender.SendIntentException e) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });

            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            } else {
//If everything went fine lets get latitude and longitude
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                address_line = getCompleteAddressString(currentLatitude, currentLongitude);
                String device_id = Settings.Secure.getString(getActivity().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                String device_type = "Android";
                System.out.println("Device id====" + device_id);

                if (nw.isConnectingToInternet()) {
                    String latitude_ = String.valueOf(currentLatitude);
                    String longitude_ = String.valueOf(currentLongitude);
                    String location_ = address_line;
                    String user_id_ = oditek.com.hlw.webservices.ApiClient.getDataFromKey(getActivity(), "user_id");
                    String access_token_ = oditek.com.hlw.webservices.ApiClient.getDataFromKey(getActivity(), "access_token");
                    String mobile_ = oditek.com.hlw.webservices.ApiClient.getDataFromKey(getActivity(), "mobile");
                    //Below API call for RegisterDeviceInfo in zero position
                    syncRegisterDeviceData(user_id_, access_token_, mobile_, device_id, device_type, latitude_, longitude_, location_);
                    //Below API call for default Oneway getCabLocation in zero position
                    syncGetVehicleCategory();
                } else {
                    ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                }

//.makeText(getActivity(), currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Showing Current Location Marker on Map
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        markerOptions = new MarkerOptions();
        markerOptions.position(latLng);

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            currentLongitude = locations.getLongitude();
            currentLatitude = locations.getLatitude();
            System.out.println("currentLongitude-" + currentLongitude + ":::::currentLatitude" + currentLatitude);
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 3);

                if (null != listAddresses && listAddresses.size() > 0) {

                    String state = listAddresses.get(0).getAdminArea();
                    String country = listAddresses.get(0).getCountryName();
                    address_line = listAddresses.get(0).getAddressLine(0);
                    String subLocality = listAddresses.get(0).getSubLocality();

                    String address = address_line;
                    markerOptions.title(address_line);

                    if (selectedOne) {
                        etDrop.setText(add);
                        DropLat = currentLatitude;
                        DropLong = currentLongitude;
                    } else {
                        etPick.setText(add);
                        PickLat = currentLatitude;
                        PickLong = currentLongitude;
                    }
                }

                //sendDriverLocationToServer(longitude, latitude, address_line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//        mCurrLocationMarker = mMap.addMarker(markerOptions);
//        //Set Custom InfoWindow Adapter
//        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(getActivity());
//        mMap.setInfoWindowAdapter(adapter);
//        mMap.addMarker(markerOptions).showInfoWindow();

        mMap.clear();
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.setPadding(0, 250, 0, 0);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(13f).build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                String lat = String.valueOf(cameraPosition.target.latitude);
                String lng = String.valueOf(cameraPosition.target.longitude);
                add = getCompleteAddressString(Double.parseDouble(lat), Double.parseDouble(lng));
                markerTitle.setText(add);

                CameraLatitude = String.valueOf(cameraPosition.target.latitude);
                CameraLongitude = String.valueOf(cameraPosition.target.longitude);

                if (selectedOne) {
                    etDrop.setText(add);
                    DropLat = Double.parseDouble(CameraLatitude);
                    DropLong = Double.parseDouble(CameraLongitude);
                } else {
                    etPick.setText(add);
                    PickLat = Double.parseDouble(CameraLatitude);
                    PickLong = Double.parseDouble(CameraLongitude);
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();
//        try {
//            buildGoogleApiClient();
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        if (dpd == null) {
            dpd = DatePickerDialog.newInstance(
                    oditek.com.hlw.BookYourTripFragment.this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
        } else {
            dpd.initialize(
                    oditek.com.hlw.BookYourTripFragment.this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
        }
        dpd.show(getActivity().getFragmentManager(), "DatePickerDialog");
        Date today = calendar.getTime();
        Calendar calendar1 = dateToCalendar(today);


        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        Calendar calendar2 = dateToCalendar(tomorrow);

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date dayaftertomorrow = calendar.getTime();
        Calendar calendar3 = dateToCalendar(dayaftertomorrow);

        List<Calendar> dates = new ArrayList<>();
        dates.add(calendar1);
        dates.add(calendar2);
        dates.add(calendar3);
        Calendar[] enabledDays1 = dates.toArray(new Calendar[dates.size()]);
        dpd.setSelectableDays(enabledDays1);
        dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
    }

    private Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private void showTimePicker(final String dateTxt) {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new TimePickerDialog(getActivity(), R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time;
                if (hourOfDay >= 0 && hourOfDay < 12) {
                    time = hourOfDay + ":" + minute + "AM";
                } else {
                    if (hourOfDay == 12) {
                        time = hourOfDay + " : " + minute + "PM";
                    } else {
                        hourOfDay = hourOfDay - 12;
                        time = hourOfDay + " : " + minute + "PM";
                    }
                }
                /*date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);*/
                String timeTxt = time;
                String datetosend = dateTxt + " " + timeTxt;
                System.out.println(datetosend);
                Log.v(TAG, "The choosen one " + date.getTime());
                Intent intent = new Intent(getActivity(), OnewayBookNowActivity.class);
                intent.putExtra("ClickedFrom", "BookLater");
                intent.putExtra("DateToShow", datetosend);
                intent.putExtra("PickLatitude", String.valueOf(currentLatitude));
                intent.putExtra("PickLongitude", String.valueOf(currentLongitude));
                intent.putExtra("DropLatitude", DropLatitude);
                intent.putExtra("DropLongitude", DropLongitude);
                startActivity(intent);
            }
        }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String dateText = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date parsedDate = null;

        try {
            parsedDate = dateFormat.parse(dateText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Date date = new Date();
        String dateToSendText = DateFormat.getDateInstance().format(parsedDate);
        showTimePicker(dateToSendText);
    }


    private void syncRegisterDeviceData(final String user_id_, final String access_token_,
                                        final String mobile_, final String device_id_, final String device_type_,
                                        final String latitude_, final String longitude_, final String location_) {
        startLoading();
        String tag_json_req = "sync_RegisterDeviceData";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.register_DeviceInfo_url),
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
//                                ErrorDialog(msg, "");
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
                        syncRegisterDeviceData(user_id_, access_token_, mobile_, device_id_, device_type_, latitude_, longitude_, location_);
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


                //String userid = ApiClient.getDataFromKey(getActivity(),"user_id");


                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id_);
                params.put("access_token", access_token_);
                params.put("mobile", mobile_);
                params.put("device_id", device_id_);
                params.put("device_type", device_type_);
                params.put("latitude", latitude_);
                params.put("longitude", longitude_);
                params.put("location", location_);

                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }

    private void syncGetCabLocation(final String user_id_, final String access_token_,
                                    final String ride_type_, final String cat_id_, final String latitude_,
                                    final String longitude_,final String carClick) {
        startLoading();
        String tag_json_req = "sync_GetCabLocation";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.get_CabLocation),
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
//                                ErrorDialog(msg, "");
                            } else {
                                JSONArray dataArr = jsonObject.getJSONArray("data");
                                for (int i = 0; i < dataArr.length(); i++) {
                                    double lat = Double.parseDouble(dataArr.getJSONObject(i).getString("latitude"));
                                    double lng = Double.parseDouble(dataArr.getJSONObject(i).getString("longitude"));
                                    String location = dataArr.getJSONObject(i).getString("location");
                                    LatLng latLng = new LatLng(lat, lng);
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(latLng);
                                    //mMap.clear();
                                    markerOptions.title(location);
                                    if(carClick.equalsIgnoreCase("Share")){
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.rsz_share_carmarker));
                                    }else if(carClick.equalsIgnoreCase("Sedan")){
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.rsz_sedan_carmarker));
                                    }else if(carClick.equalsIgnoreCase("Mini")){
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.rsz_mini_carmarker));
                                    }else if(carClick.equalsIgnoreCase("Micro")){
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.rsz_1micro_carmarker));
                                    }

                                    //markerOptions.getPosition();
                                    mMap.addMarker(markerOptions);
                                }
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
                        syncGetCabLocation(user_id_, access_token_, ride_type_, cat_id_, latitude_, longitude_,carClick);
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


                //String userid = ApiClient.getDataFromKey(getActivity(),"user_id");


                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id_);
                params.put("access_token", access_token_);
                params.put("ride_type", ride_type_);
                params.put("cat_id", cat_id_);
                params.put("latitude", latitude_);
                params.put("longitude", longitude_);
                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
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

    void startLoading() {
        progressDialog.show();
        progressDialog.setVisibility(View.VISIBLE);
        progressDialog.setIndicator("Loading...");
    }

    void stopLoading() {
        progressDialog.hide();
        progressDialog.setVisibility(View.GONE);
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i))/*.append("\n"*/;
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current address", strReturnedAddress.toString());
            } else {
                Log.w("My Current address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current address", "Canont get Address!");
        }
        return strAdd;
    }


    private void syncGetVehicleCategory() {
        startLoading();
        String tag_json_req = "sync_category";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.vehicle_category),
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

                            String user_id_ = oditek.com.hlw.webservices.ApiClient.getDataFromKey(getActivity(), "user_id");
                            String access_token_ = oditek.com.hlw.webservices.ApiClient.getDataFromKey(getActivity(), "access_token");


                            if ("0".equalsIgnoreCase(status_)) {
//                                ErrorDialog(msg, "");
                            } else {
                                JSONArray dataArr = jsonObject.getJSONArray("data");
                                syncGetCabLocation(user_id_, access_token_, "0", dataArr.getJSONObject(1).getString("cat_id"),
                                        String.valueOf(currentLatitude), String.valueOf(currentLongitude), dataArr.getJSONObject(1).getString("cat_name"));
                                BookYourTripOnewayAdapter adapter = new BookYourTripOnewayAdapter(getActivity(), getVehicleCategoryData(dataArr));
                                recyclerViewOneWay.setAdapter(adapter);
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
                        syncGetVehicleCategory();
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

                String user_id_ = oditek.com.hlw.webservices.ApiClient.getDataFromKey(getActivity(), "user_id");
                String access_token_ = oditek.com.hlw.webservices.ApiClient.getDataFromKey(getActivity(), "access_token");

                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id_);
                params.put("access_token", access_token_);
                params.put("latitude", String.valueOf(currentLatitude));
                params.put("longitude", String.valueOf(currentLongitude));

                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }

    private List<OnewayCarListModel> getVehicleCategoryData(JSONArray jsonArray) {
        List<OnewayCarListModel> categoryData = new ArrayList<>();
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    OnewayCarListModel rowData = new OnewayCarListModel();
                    JSONObject ltData = jsonArray.getJSONObject(i);

                    rowData.carId = ltData.getString("cat_id");
                    rowData.carName = ltData.getString("cat_name");
                    rowData.blueImageUrl = ltData.getString("image_blue");
                    rowData.whiteImageUrl = ltData.getString("image_white");
                    rowData.duration = ltData.getString("min_time");

                    categoryData.add(rowData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return categoryData;
    }


    public class BookYourTripOnewayAdapter extends RecyclerView.Adapter<oditek.com.hlw.BookYourTripFragment.BookYourTripOnewayAdapter.ViewHolder> {
        private Context mContext;
        LinearLayout linear_totalcar_details;
        private ImageLoader imageLoader;
        List<OnewayCarListModel> list;
        int selectedPosition = 1;

        @Override
        public oditek.com.hlw.BookYourTripFragment.BookYourTripOnewayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookyourtrip_oneway_rowlayout, parent, false);

            oditek.com.hlw.BookYourTripFragment.BookYourTripOnewayAdapter.ViewHolder vh = new ViewHolder(v);
            return vh;
        }


        public BookYourTripOnewayAdapter(Context mContext, List<OnewayCarListModel> list) {
            this.mContext = mContext;
            this.list = list;
            imageLoader = AppController.getInstance().getImageLoader();

        }

        @Override
        public void onBindViewHolder(final oditek.com.hlw.BookYourTripFragment.BookYourTripOnewayAdapter.ViewHolder holder, final int position) {
            final OnewayCarListModel bookYourTripOnewaymodel = list.get(position);

            holder.car_type.setText(bookYourTripOnewaymodel.carName);
            holder.avl_time.setText(bookYourTripOnewaymodel.duration);

            if(selectedPosition==position){
                imageLoader.get(bookYourTripOnewaymodel.blueImageUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        holder.carpic.setImageBitmap(response.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            }else {
                imageLoader.get(bookYourTripOnewaymodel.whiteImageUrl, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        holder.carpic.setImageBitmap(response.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            }

            final String latitude_ = String.valueOf(currentLatitude);
            final String longitude_ = String.valueOf(currentLongitude);
            final String user_id_ = oditek.com.hlw.webservices.ApiClient.getDataFromKey(getActivity(), "user_id");
            final String access_token_ = oditek.com.hlw.webservices.ApiClient.getDataFromKey(getActivity(), "access_token");

            holder.mCardView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   selectedPosition = position;
                   notifyDataSetChanged();

                   car_click = bookYourTripOnewaymodel.carName;
                   String catId = bookYourTripOnewaymodel.carId;

                   mMap.clear();

                   syncGetCabLocation(user_id_,access_token_,"0",catId,latitude_,longitude_,car_click);

                   if (car_click.equalsIgnoreCase("Share")) {
                       sharebook.setVisibility(View.VISIBLE);
                       book_micro.setVisibility(View.GONE);
                   } else if (car_click.equalsIgnoreCase("Sedan") || car_click.equalsIgnoreCase("Mini") || car_click.equalsIgnoreCase("Micro"))
                   {
                       sharebook.setVisibility(View.GONE);
                       book_micro.setVisibility(View.VISIBLE);
                   }
               }
           });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView car_type, avl_time;
            public ImageView carpic;
            public CardView mCardView;

            public ViewHolder(View itemView) {
                super(itemView);
                carpic = itemView.findViewById(R.id.iv_share);
                car_type = itemView.findViewById(R.id.tv_share);
                avl_time = itemView.findViewById(R.id.tv_share2);
                mCardView = (CardView) itemView.findViewById(R.id.card_viewoneway);
            }

        }

    }
}
