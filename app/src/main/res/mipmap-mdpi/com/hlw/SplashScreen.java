package oditek.com.hlw;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    ImageView splash;
    Button btnMobileNumber;
    private static int SPLASH_TIME_LENGTH = 3000;
    TextView tvWelcome, tvDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);

        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");

        splash = findViewById(R.id.logo);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvDetails = findViewById(R.id.tvDetails);
        tvWelcome.setTypeface(typeFaceLight);
        tvDetails.setTypeface(typeFaceLight);

        btnMobileNumber = findViewById(R.id.btnMobileNumber);
        btnMobileNumber.setTypeface(typeFaceBold);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, SPLASH_TIME_LENGTH);

        btnMobileNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(oditek.com.hlw.SplashScreen.this, oditek.com.hlw.Login.class);
                startActivity(in);
            }
        });
    }
}
