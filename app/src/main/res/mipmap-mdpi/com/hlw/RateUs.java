package oditek.com.hlw;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;
import java.util.List;

public class RateUs extends AppCompatActivity {
    ImageView ivEmoji, ivBack;
    TextView tvEmojiName, tvTell, tvLeave;
    Button btnSubmit;
    EditText etComment;
    CardView cardView;
    RecyclerView mRecyclerView;
    List<oditek.com.hlw.RateUsModel> list = new ArrayList<>();
    SimpleRatingBar ratingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_us);

        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");

        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ivEmoji = findViewById(R.id.ivEmoji);
        tvEmojiName = findViewById(R.id.tvEmojiName);
        tvEmojiName.setTypeface(typeFaceBold);
        tvTell = findViewById(R.id.tvTell);
        tvTell.setTypeface(typeFaceBold);
        tvLeave = findViewById(R.id.tvLeave);
        tvLeave.setTypeface(typeFaceBold);
        etComment = findViewById(R.id.etComment);
        etComment.setTypeface(typeFaceLight);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setTypeface(typeFaceBold);
        ratingbar = findViewById(R.id.ratingbar);

        cardView = (CardView) findViewById(R.id.card_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        getData();

        ratingbar.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(SimpleRatingBar ratingBar, float rating,
                                        boolean fromUser) {

                if (ratingbar.getRating() == 1) {
                    ivEmoji.setImageResource(R.mipmap.ride_very_bad);
                    tvEmojiName.setText("Very Bad!");
                    getData();
                } else if (ratingbar.getRating() == 2) {
                    ivEmoji.setImageResource(R.mipmap.ride_bad);
                    tvEmojiName.setText("A bad one!");
                    getData();
                } else if (ratingbar.getRating() == 3) {
                    ivEmoji.setImageResource(R.mipmap.ride_ok);
                    tvEmojiName.setText("An ok ok ride!");
                    getData();
                } else if (ratingbar.getRating() == 4) {
                    ivEmoji.setImageResource(R.mipmap.ride_good);
                    tvEmojiName.setText("A good ride!");
                    getData();
                } else if (ratingbar.getRating() == 5) {
                    ivEmoji.setImageResource(R.mipmap.ride_great);
                    tvEmojiName.setText("A great ride!");
                    getData2();
                }
            }
        });
    }

    private void getData() {
        ArrayList<oditek.com.hlw.RateUsModel> list = new ArrayList<>();

        oditek.com.hlw.RateUsModel s = new oditek.com.hlw.RateUsModel();
        s.setCheckbox("Charged Extra");
        list.add(s);

        s = new oditek.com.hlw.RateUsModel();
        s.setCheckbox("Delayed Pickup");
        list.add(s);

        s = new oditek.com.hlw.RateUsModel();
        s.setCheckbox("Driver Unprofessional");
        list.add(s);

        s = new oditek.com.hlw.RateUsModel();
        s.setCheckbox("Long/incorrect Route");
        list.add(s);

        s = new oditek.com.hlw.RateUsModel();
        s.setCheckbox("Did not take this ride");
        list.add(s);

        s = new oditek.com.hlw.RateUsModel();
        s.setCheckbox("My reason is not listed");
        list.add(s);

        oditek.com.hlw.RateUsAdapter rateUsAdapter = new oditek.com.hlw.RateUsAdapter(oditek.com.hlw.RateUs.this, list);
        mRecyclerView.setAdapter(rateUsAdapter);

    }

    private void getData2() {
        ArrayList<oditek.com.hlw.RateUsModel> list = new ArrayList<>();

        oditek.com.hlw.RateUsModel s = new oditek.com.hlw.RateUsModel();
        s.setCheckbox("Polite and professional driver");
        list.add(s);

        s = new oditek.com.hlw.RateUsModel();
        s.setCheckbox("On-time pickup");
        list.add(s);

        s = new oditek.com.hlw.RateUsModel();
        s.setCheckbox("Comfortable ride");
        list.add(s);

        s = new oditek.com.hlw.RateUsModel();
        s.setCheckbox("Driver familiar with the route ");
        list.add(s);

        s = new oditek.com.hlw.RateUsModel();
        s.setCheckbox("Value for money");
        list.add(s);

        s = new oditek.com.hlw.RateUsModel();
        s.setCheckbox("My reason is not listed");
        list.add(s);

        oditek.com.hlw.RateUsAdapter rateUsAdapter = new oditek.com.hlw.RateUsAdapter(oditek.com.hlw.RateUs.this, list);
        mRecyclerView.setAdapter(rateUsAdapter);

    }
}
