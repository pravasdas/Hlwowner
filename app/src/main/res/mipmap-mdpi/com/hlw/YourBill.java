package oditek.com.hlw;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class YourBill extends AppCompatActivity {

    ImageView back_yourBill;
    TextView tv1, tv2, tvStart, tvEnd, tv4;
    Button btnRateUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_bill);

        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");

        tv1 = findViewById(R.id.tv1);
        tv1.setTypeface(typeFaceBold);
        tv2 = findViewById(R.id.tv2);
        tv2.setTypeface(typeFaceBold);
        tvStart = findViewById(R.id.tvStart);
        tvStart.setTypeface(typeFaceLight);
        tvEnd = findViewById(R.id.tvEnd);
        tvEnd.setTypeface(typeFaceLight);
        tv4 = findViewById(R.id.tv4);
        tv4.setTypeface(typeFaceLight);
        btnRateUs = findViewById(R.id.btnRateUs);
        btnRateUs.setTypeface(typeFaceBold);


        back_yourBill = findViewById(R.id.back_yourBill);
        back_yourBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(oditek.com.hlw.YourBill.this, RateUs.class);
                startActivity(intent);
            }
        });
    }
}
