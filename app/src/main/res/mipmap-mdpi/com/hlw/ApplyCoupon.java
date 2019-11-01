package oditek.com.hlw;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ApplyCoupon extends AppCompatActivity {
    ImageView back_Applycoupon;
    Button btnApply;
    TextView tv1,tv2;
    EditText et1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_coupon);

        back_Applycoupon =(ImageView) findViewById(R.id.back_Applycoupon);

        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");


        tv1=findViewById(R.id.tv1);
        tv1.setTypeface(typeFaceLight);
        tv2=findViewById(R.id.tv2);
        tv2.setTypeface(typeFaceBold);
        et1=findViewById(R.id.et1);
        et1.setTypeface(typeFaceLight);
        btnApply =findViewById(R.id.btnApply);
        btnApply.setTypeface(typeFaceBold);


        back_Applycoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
