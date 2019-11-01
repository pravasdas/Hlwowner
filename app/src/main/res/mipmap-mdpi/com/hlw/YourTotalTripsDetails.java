package oditek.com.hlw;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class YourTotalTripsDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_total_trips_details);

        RelativeLayout rl1 = findViewById(R.id.rl1);
        RelativeLayout rl2 = findViewById(R.id.rl2);
        RelativeLayout rl3 = findViewById(R.id.rl3);
        rl1.setVisibility(View.GONE);
        rl2.setVisibility(View.GONE);
        rl3.setVisibility(View.VISIBLE);
        ImageView ivBack1 = findViewById(R.id.ivBack1);
        ivBack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
