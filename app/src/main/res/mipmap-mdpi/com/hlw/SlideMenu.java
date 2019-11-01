package oditek.com.hlw;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;

import de.hdodenhof.circleimageview.CircleImageView;
import oditek.com.hlw.webservices.AppController;

public class SlideMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Button btnOneWay, btnRentals;
    RelativeLayout rl1, rl2, rl3, rl4, rl5, rl6, myprofile;
    TextView tvHeadingText, tvName, tvAdd;
    CircleImageView profile_image;
    String ProfileImage = "", ProfileName = "", clickedFrom = "";
    private ImageLoader imageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_menu_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");

        rl1 = findViewById(R.id.rl1);
        rl2 = findViewById(R.id.rl2);
        rl3 = findViewById(R.id.rl3);
        rl4 = findViewById(R.id.rl4);
        rl5 = findViewById(R.id.rl5);
        rl6 = findViewById(R.id.rl6);
        tvHeadingText = findViewById(R.id.tvHeadingText);
        tvHeadingText.setTypeface(typeFaceLight);


        rl1.setVisibility(View.VISIBLE);
        rl2.setVisibility(View.GONE);
        rl3.setVisibility(View.GONE);
        rl4.setVisibility(View.GONE);
        rl5.setVisibility(View.GONE);
        rl6.setVisibility(View.GONE);

        String device_id = Settings.Secure.getString(oditek.com.hlw.SlideMenu.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        clickedFrom = getIntent().getStringExtra("ClickedFrom");

        String device_type = "Android";

        System.out.println("Device id====" + device_id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        imageLoader = AppController.getInstance().getImageLoader();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        myprofile = headerView.findViewById(R.id.myprofile);
        profile_image = headerView.findViewById(R.id.profile_image);
        tvName = headerView.findViewById(R.id.tvName);
        tvAdd = headerView.findViewById(R.id.tvAdd);
        tvName.setTypeface(typeFaceLight);
        tvAdd.setTypeface(typeFaceLight);

        ProfileImage = oditek.com.hlw.webservices.ApiClient.getDataFromKey(oditek.com.hlw.SlideMenu.this, "image");
        ProfileName = oditek.com.hlw.webservices.ApiClient.getDataFromKey(oditek.com.hlw.SlideMenu.this, "name");

        tvName.setText(ProfileName);

        btnOneWay = findViewById(R.id.btnOneWay);
        btnOneWay.setTypeface(typeFaceLight);
        btnRentals = findViewById(R.id.btnRentals);
        btnRentals.setTypeface(typeFaceLight);

        Fragment fragment = new oditek.com.hlw.BookYourTripFragment();
        btnOneWay.setVisibility(View.VISIBLE);
        btnRentals.setVisibility(View.VISIBLE);
        tvHeadingText.setText("HLW");
        Bundle bundle = new Bundle();
        bundle.putString("ClickedFromMenu", clickedFrom);
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

        btnOneWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRentals.setBackgroundResource(R.drawable.diagonal);
                btnOneWay.setBackgroundResource(R.drawable.diagonal_filled);
                btnOneWay.setTextColor(ContextCompat.getColor(oditek.com.hlw.SlideMenu.this, R.color.colorPrimaryDark));
                btnRentals.setTextColor(ContextCompat.getColor(oditek.com.hlw.SlideMenu.this, R.color.white));
                Toast.makeText(oditek.com.hlw.SlideMenu.this, "btnOneWay", Toast.LENGTH_SHORT).show();
            }
        });

        btnRentals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRentals.setBackgroundResource(R.drawable.diagonal_filled);
                btnOneWay.setBackgroundResource(R.drawable.diagonal);
                btnRentals.setTextColor(ContextCompat.getColor(oditek.com.hlw.SlideMenu.this, R.color.colorPrimaryDark));
                btnOneWay.setTextColor(ContextCompat.getColor(oditek.com.hlw.SlideMenu.this, R.color.white));
                Toast.makeText(oditek.com.hlw.SlideMenu.this, "btnRentals", Toast.LENGTH_SHORT).show();
/*               Fragment fragment= new BookYourTripFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
               transaction.replace(R.id.content_frame, fragment); // fragment container id in first parameter is the  container(Main layout id) of Activity
               transaction.addToBackStack(null);  // this will manage backstack
              transaction.commit();
              //this block of code is for clicking on button of a activity it will call fragment
              */
            }
        });

        myprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new oditek.com.hlw.MyProfileFragment();
                callTransaction(fragment, "MyProfile", "My Profile");
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ProfileImage.equalsIgnoreCase("")) {
            System.out.println(ProfileImage);
        } else {
            Glide
                    .with(oditek.com.hlw.SlideMenu.this)
                    .load(ProfileImage).thumbnail(0.5f)
                    .centerCrop()
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(profile_image);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            btnOneWay.setVisibility(View.VISIBLE);
            btnRentals.setVisibility(View.VISIBLE);
            tvHeadingText.setText("HLW");
            getSupportFragmentManager().popBackStack();
        } else {
            //super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.slide_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment1 = null;

        if (id == R.id.nav_book) {
            btnOneWay.setVisibility(View.VISIBLE);
            btnRentals.setVisibility(View.VISIBLE);
            fragment1 = new oditek.com.hlw.BookYourTripFragment();
            Bundle bundle = new Bundle();
            bundle.putString("ClickedFromMenu", "BookNow");
            fragment1.setArguments(bundle);
            callTransaction(fragment1,"BookNow", "HLW");
        } else if (id == R.id.nav_total) {
            fragment1 = new YourTotalTripsFragment();
            callTransaction(fragment1, "TotalTrip", "Your Total Trips");
        } else if (id == R.id.nav_bookings) {

        } else if (id == R.id.nav_emergency) {
            fragment1 = new oditek.com.hlw.EmergencyContactsFragment();
            callTransaction(fragment1, "Emergency Contacts", "Emergency Contacts");
        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_about) {
            fragment1 = new oditek.com.hlw.AboutUs();
            callTransaction(fragment1, "About Us", "About Us");
        } else if (id == R.id.nav_sign) {
            SignoutDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void callTransaction(Fragment fragment1, String from, String title) {
        if (from.equalsIgnoreCase("BookNow")) {
            btnOneWay.setVisibility(View.VISIBLE);
            btnRentals.setVisibility(View.VISIBLE);
            tvHeadingText.setText(title);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment1);
            ft.addToBackStack(null);
            ft.commit();
        } else {
            btnOneWay.setVisibility(View.GONE);
            btnRentals.setVisibility(View.GONE);
            tvHeadingText.setText(title);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment1);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    public void SignoutDialog() {
        new iOSDialogBuilder(oditek.com.hlw.SlideMenu.this)
                .setTitle("Logout")
                .setSubtitle("Are you sure you to logout?")
                .setBoldPositiveLabel(true)
                .setCancelable(false)
                .setPositiveListener("Logout", new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                        oditek.com.hlw.webservices.ApiClient.clearData(oditek.com.hlw.SlideMenu.this);
                        Intent in = new Intent(oditek.com.hlw.SlideMenu.this, oditek.com.hlw.SplashScreen.class);
                        startActivity(in);
                        finish();
                    }
                })
                .setNegativeListener("Cancel", new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .build().show();
    }
}
