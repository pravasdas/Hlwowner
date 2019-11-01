package com.oditek.hlw_owner;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.oditek.hlw_owner.webservices.ApiClient;

public class MainActivity extends AppCompatActivity {

   /* Toolbar toolbar;
    TextView tvRl2;*/
   boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //tvRl2=findViewById(R.id.tvRl2);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

       // nav_toolbar.setTitle("HOME");
        loadFragment(new HomeFragment());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                   // nav_toolbar.setTitle("HOME");
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_myProfile:
                   // nav_toolbar.setTitle("MY PROFILE");
                    fragment = new MyProfileFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_notification:
                    //nav_toolbar.setTitle("NOTIFICATIONS");
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();

    }
    @Override
    public void onBackPressed() {
       /* int count = getSupportFragmentManager().getBackStackEntryCount();
        System.out.println(count);
        if (count == 0) {
            getSupportFragmentManager().popBackStack();
            //additional code
            super.onBackPressed();
        }*/
        if (doubleBackToExitPressedOnce) {
            //super.onBackPressed();
            finishAffinity();
            return;
        }else if(getSupportFragmentManager().getBackStackEntryCount() == 0){
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit HLW", Toast.LENGTH_SHORT).show();
        }
    }

}