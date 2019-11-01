package oditek.com.hlw;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import oditek.com.hlw.utils.DirectionsJSONParser;

import static android.content.ContentValues.TAG;

public class OnewayBookNowActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, DatePickerDialog.OnDateSetListener {

    oditek.com.hlw.NetworkConnection nw;
    private GoogleMap mMap;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    LatLng latLng;
    String address_line = "", clicked = "", dateToShow = "", OneWayVehicleType = "", car_click;
    MarkerOptions markerOptions;
    EditText etPick2, etDrop2;
    TextView text_datetime, headToolTxt, tv1, tv2, tv3, tv4, tv5, tv6, tv7, btnTxt, tv_selectedCarText;
    LinearLayout linear_dateshow, linear_paymentMode, linear_applyCoupon, linear_totalFare, linear_paymentMode2, linear_applyCoupon2, linear_totalFare2, linearBookRideFor, linearBookRideFor2,
            linear_paymentModeShare, linear_applyCouponShare, linear_totalFareShare, linearBookRideForShare, linearNoOfSeat;
    ImageView back_booknow, iv_selectedCarImage;
    RelativeLayout rlBottom_Confirm, rlBottom_Confirmshowdate, relativeBooklater,
            relativeOnewayBook, relativeShare, rlBottom, rlircle, rlMain;
    Long time;
    Calendar date;
    private DatePickerDialog dpd;
    public String PickLat, PickLong, DropLat, DropLong, PickAdd, DropAdd;
    public Double PickLatitude, PickLongitude, DropLatitude, DropLongitude;
    ArrayList<LatLng> MarkerPoints;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oneway_book_now);


        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");

        clicked = getIntent().getStringExtra("ClickedFrom");
        dateToShow = getIntent().getStringExtra("DateToShow");
        OneWayVehicleType = getIntent().getStringExtra("OneWayVehicleType");
        car_click = getIntent().getStringExtra("CarClicked");
        PickLat = getIntent().getStringExtra("PickLatitude");
        PickLong = getIntent().getStringExtra("PickLongitude");
        DropLat = getIntent().getStringExtra("DropLatitude");
        DropLong = getIntent().getStringExtra("DropLongitude");

        System.out.println("Plat==="+PickLat + "Plong==="+PickLong);
        System.out.println("Dlat==="+DropLat + "Dlong==="+DropLong);

        PickLatitude = Double.parseDouble(PickLat);
        PickLongitude = Double.parseDouble(PickLong);
        DropLatitude = Double.parseDouble(DropLat);
        DropLongitude = Double.parseDouble(DropLong);
        PickAdd = getIntent().getStringExtra("PickAdd");
        DropAdd = getIntent().getStringExtra("DropAdd");


        etPick2 = (EditText) findViewById(R.id.etPick2);
        etPick2.setTypeface(typeFaceBold);
        etPick2.setText(PickAdd);
        etPick2.setEnabled(false);
        etDrop2 = (EditText) findViewById(R.id.etDrop2);
        etDrop2.setTypeface(typeFaceBold);
        etDrop2.setText(DropAdd);
        etDrop2.setEnabled(false);
        headToolTxt = (TextView) findViewById(R.id.headToolTxt);
        headToolTxt.setTypeface(typeFaceLight);
        text_datetime = (TextView) findViewById(R.id.text_datetime);
        back_booknow = (ImageView) findViewById(R.id.back_booknow);
        tv_selectedCarText = findViewById(R.id.tv_selectedCarText);
        tv_selectedCarText.setTypeface(typeFaceLight);
        iv_selectedCarImage = findViewById(R.id.iv_selectedCarImage);

        rlBottom_Confirm = (RelativeLayout) findViewById(R.id.rlBottom_Confirm);
        rlBottom = (RelativeLayout) findViewById(R.id.rlBottom);
        rlircle = (RelativeLayout) findViewById(R.id.rlircle);
        rlMain = (RelativeLayout) findViewById(R.id.rlMain);
        rlBottom_Confirmshowdate = (RelativeLayout) findViewById(R.id.rlBottom_Confirmshowdate);
        relativeBooklater = (RelativeLayout) findViewById(R.id.relativeBooklater);
        relativeOnewayBook = (RelativeLayout) findViewById(R.id.relativeOnewayBook);
        relativeShare = (RelativeLayout) findViewById(R.id.relativeShare);
        linear_dateshow = (LinearLayout) findViewById(R.id.linear_dateshow);
        linear_paymentMode = findViewById(R.id.linear_paymentMode);
        linear_applyCoupon = findViewById(R.id.linear_applyCoupon);
        linear_totalFare = findViewById(R.id.linear_totalFare);
        linearBookRideFor = findViewById(R.id.linearBookRideFor);

        linear_paymentMode2 = findViewById(R.id.linear_paymentMode2);
        linear_applyCoupon2 = findViewById(R.id.linear_applyCoupon2);
        linear_totalFare2 = findViewById(R.id.linear_totalFare2);
        linearBookRideFor2 = findViewById(R.id.linearBookRideFor2);

        linear_paymentModeShare = findViewById(R.id.linear_paymentModeShare);
        linear_applyCouponShare = findViewById(R.id.linear_applyCouponShare);
        linear_totalFareShare = findViewById(R.id.linear_totalFareShare);
        linearBookRideForShare = findViewById(R.id.linearBookRideForShare);
        linearNoOfSeat = findViewById(R.id.linearNoOfSeat);

        tv1 = findViewById(R.id.tv1);
        tv1.setTypeface(typeFaceLight);
        tv2 = findViewById(R.id.tv2);
        tv2.setTypeface(typeFaceBold);
        tv3 = findViewById(R.id.tv3);
        tv3.setTypeface(typeFaceLight);
        tv4 = findViewById(R.id.tv4);
        tv4.setTypeface(typeFaceBold);
        tv5 = findViewById(R.id.tv5);
        tv5.setTypeface(typeFaceLight);
        tv6 = findViewById(R.id.tv6);
        tv6.setTypeface(typeFaceLight);
        tv7 = findViewById(R.id.tv7);
        tv7.setTypeface(typeFaceLight);
        btnTxt = findViewById(R.id.btnTxt);
        btnTxt.setTypeface(typeFaceBold);


        nw = new oditek.com.hlw.NetworkConnection(oditek.com.hlw.OnewayBookNowActivity.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.current_location);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        //Date dt = new Date(extras.getString("noteDate"));

        if (clicked.equalsIgnoreCase("BookLater")) {
            Bundle bundle = getIntent().getExtras();
            dateToShow = bundle.getString("DateToShow");
            text_datetime.setText(dateToShow);
            relativeOnewayBook.setVisibility(View.GONE);
            relativeBooklater.setVisibility(View.VISIBLE);
            relativeShare.setVisibility(View.GONE);
        } else {
            relativeOnewayBook.setVisibility(View.VISIBLE);
            relativeBooklater.setVisibility(View.GONE);
            relativeShare.setVisibility(View.GONE);
        }


        if (clicked.equalsIgnoreCase("ShareBookNow") && OneWayVehicleType.equals("1")) {
            relativeOnewayBook.setVisibility(View.GONE);
            relativeBooklater.setVisibility(View.GONE);
            relativeShare.setVisibility(View.VISIBLE);
        }

        if (clicked.equalsIgnoreCase("MicroBookNow") && OneWayVehicleType.equals("2")) {
            relativeOnewayBook.setVisibility(View.VISIBLE);
            relativeBooklater.setVisibility(View.GONE);
            relativeShare.setVisibility(View.GONE);
        }

        if (car_click != null && car_click.equalsIgnoreCase("Share")) {
            tv_selectedCarText.setText("Share");
            iv_selectedCarImage.setImageResource(R.mipmap.car_share_blue);
        } else if (car_click != null && car_click.equalsIgnoreCase("Micro")) {
            tv_selectedCarText.setText("Micro");
            iv_selectedCarImage.setImageResource(R.mipmap.car_micro_blue);
        } else if (car_click != null && car_click.equalsIgnoreCase("Mini")) {
            tv_selectedCarText.setText("Mini");
            iv_selectedCarImage.setImageResource(R.mipmap.car_mini_blue);
        } else if (car_click != null && car_click.equalsIgnoreCase("Sedan")) {
            tv_selectedCarText.setText("Sedan");
            iv_selectedCarImage.setImageResource(R.mipmap.car_sedan_blue);
        }

        linear_paymentMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PaymentMethodBottomSheetDialog();
            }
        });


        linear_paymentMode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PaymentMethodBottomSheetDialog();
            }
        });

        linear_paymentModeShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PaymentMethodBottomSheetDialog();
            }
        });


        linearBookRideFor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BookRideForBottomSheetDialog();

            }
        });

        linearBookRideFor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BookRideForBottomSheetDialog();
            }
        });

        linearBookRideForShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BookRideForBottomSheetDialog();
            }
        });

        linear_applyCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(oditek.com.hlw.OnewayBookNowActivity.this, oditek.com.hlw.ApplyCoupon.class);
                startActivity(intent);
            }
        });


        linear_applyCoupon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(oditek.com.hlw.OnewayBookNowActivity.this, oditek.com.hlw.ApplyCoupon.class);
                startActivity(intent);
            }
        });

        linear_applyCouponShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(oditek.com.hlw.OnewayBookNowActivity.this, oditek.com.hlw.ApplyCoupon.class);
                startActivity(intent);
            }
        });

        linear_totalFare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogTotalFare();
            }
        });

        linear_totalFare2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogTotalFare();
            }
        });

        linear_totalFareShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogTotalFare();
            }
        });

        linearNoOfSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoOfSeatsShareBottomSheetDialog();
            }
        });

        back_booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rlBottom_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(oditek.com.hlw.OnewayBookNowActivity.this, oditek.com.hlw.ConfirmBookingActivity.class);
                intent.putExtra("ClickedFrom", "ConfirmBooking");
                startActivity(intent);
                finish();
            }

        });

        rlBottom_Confirmshowdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(oditek.com.hlw.OnewayBookNowActivity.this, oditek.com.hlw.ConfirmBookingActivity.class);
                startActivity(intent);
            }
        });

        linear_dateshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(oditek.com.hlw.OnewayBookNowActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(false);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(false);
        }

        //mMap.animateCamera( CameraUpdateFactory.zoomTo( 11.0f ));
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(oditek.com.hlw.OnewayBookNowActivity.this)
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
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
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
                                status.startResolutionForResult(oditek.com.hlw.OnewayBookNowActivity.this, MY_PERMISSIONS_REQUEST_LOCATION);

                            } catch (IntentSender.SendIntentException e) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });

            if (ContextCompat.checkSelfPermission(oditek.com.hlw.OnewayBookNowActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(PickLatitude, PickLongitude))
                .anchor(0.5f, 0.5f)
                .title("Pick Up Location")
                .snippet(PickAdd)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_green)));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(DropLatitude, DropLongitude))
                .anchor(0.5f, 0.5f)
                .title("Drop Location")
                .snippet(DropAdd)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin_red)));

//        Polyline line = mMap.addPolyline(new PolylineOptions()
//                .add(new LatLng(PickLatitude, PickLongitude), new LatLng(DropLatitude, DropLongitude))
//                .width(5)
//                .color(Color.RED));

        LatLng origin = new LatLng(PickLatitude, PickLongitude);
        LatLng dest = new LatLng(DropLatitude, DropLongitude);

        String url= getDirectionsUrl(origin,dest);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(12f).build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            buildGoogleApiClient();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (ContextCompat.checkSelfPermission(oditek.com.hlw.OnewayBookNowActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(oditek.com.hlw.OnewayBookNowActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(oditek.com.hlw.OnewayBookNowActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(oditek.com.hlw.OnewayBookNowActivity.this,
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
                    if (ContextCompat.checkSelfPermission(oditek.com.hlw.OnewayBookNowActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(false);
                    }
                } else {
                    Toast.makeText(oditek.com.hlw.OnewayBookNowActivity.this, "permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        if (dpd == null) {
            dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                    oditek.com.hlw.OnewayBookNowActivity.this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
        } else {
            dpd.initialize(
                    oditek.com.hlw.OnewayBookNowActivity.this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
        }
        dpd.show(oditek.com.hlw.OnewayBookNowActivity.this.getFragmentManager(), "DatePickerDialog");
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
        new TimePickerDialog(oditek.com.hlw.OnewayBookNowActivity.this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
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
                Intent intent = new Intent(oditek.com.hlw.OnewayBookNowActivity.this, oditek.com.hlw.OnewayBookNowActivity.class);
                intent.putExtra("ClickedFrom", "BookLater");
                intent.putExtra("DateToShow", datetosend);
                startActivity(intent);
            }
        }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();

    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
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

    public void DialogTotalFare() {

        final Dialog dialog = new Dialog(oditek.com.hlw.OnewayBookNowActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fare_details_dialog_box);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button dialogButtonOK = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if decline button is clicked, close the custom dialog
        dialogButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });
    }

    public void BookRideForBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.book_ride_for_bottomsheet, null);

        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();

        Button btnMyself = dialog.findViewById(R.id.btnMyself);

        btnMyself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void PaymentMethodBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.payment_method_bottomsheet, null);

        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();

        Button btnPayment = dialog.findViewById(R.id.btnPayment);

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void NoOfSeatsShareBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.no_of_seats_bottomsheet, null);

        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();

        Button btnDone = dialog.findViewById(R.id.btnDone);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor +"&key=AIzaSyBYm_JbaHbjE7DAj9aAMC23MMvj5oF1pOI";
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        } catch (Exception e) {
            Log.d("Exception url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
                Log.d("Background URL", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                System.out.println("jObject=" + jObject);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";
            System.out.println("result=" + result);
            if (result.size() < 1) {
                return;
            }
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.parseColor("#0067C7"));

                /*ValueAnimator markerAnimator = ObjectAnimator.ofObject(markerOptions, "position",
                        new LatLngEvaluator(origin_latLng,dest_latlng),markerOptions.getPosition(), points);
                markerAnimator.setDuration(Long.parseLong(distance));
                markerAnimator.setInterpolator(new LinearInterpolator());
                markerAnimator.start();*/

            }

            System.out.println("Distance:" + distance + ", Duration:" + duration);
            mMap.addPolyline(lineOptions);
        }
    }
}
