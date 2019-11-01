package oditek.com.hlw;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PickUpArriving extends AppCompatActivity {

    LinearLayout toolbar_pickuparriving, cancel, donotCancel, llDriverProfile;
    ImageView ivBack;
    Button btnCancelRide;
    TextView tvName, tvCarName, tvCarNo, tvRating, tvOTP, tvYourRide, tvStart, tvEnd, tvCall, tvShare, tvHelp, tvDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_arriving);

        Typeface typeFaceLight = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");


        RelativeLayout rl1 = findViewById(R.id.rl1);
        RelativeLayout rl2 = findViewById(R.id.rl2);
        rl1.setVisibility(View.GONE);
        rl2.setVisibility(View.VISIBLE);
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvName = findViewById(R.id.tvName);
        tvName.setTypeface(typeFaceBold);
        tvCarName = findViewById(R.id.tvCarName);
        tvCarName.setTypeface(typeFaceLight);
        tvCarNo = findViewById(R.id.tvCarNo);
        tvCarNo.setTypeface(typeFaceLight);
        tvRating = findViewById(R.id.tvRating);
        tvRating.setTypeface(typeFaceBold);
        tvOTP = findViewById(R.id.tvOTP);
        tvOTP.setTypeface(typeFaceBold);
        tvYourRide = findViewById(R.id.tvYourRide);
        tvYourRide.setTypeface(typeFaceBold);
        tvStart = findViewById(R.id.tvStart);
        tvStart.setTypeface(typeFaceBold);
        tvEnd = findViewById(R.id.tvEnd);
        tvEnd.setTypeface(typeFaceBold);
        btnCancelRide = findViewById(R.id.btnCancelRide);
        btnCancelRide.setTypeface(typeFaceBold);
        tvCall = findViewById(R.id.tvCall);
        tvCall.setTypeface(typeFaceLight);
        tvShare = findViewById(R.id.tvShare);
        tvShare.setTypeface(typeFaceLight);
        tvHelp = findViewById(R.id.tvHelp);
        tvHelp.setTypeface(typeFaceLight);
        tvDriver = findViewById(R.id.tvDriver);
        tvDriver.setTypeface(typeFaceLight);



        LinearLayout linear_ShareRide=(LinearLayout)findViewById(R.id.linear_ShareRide);
        linear_ShareRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareTextUrl();
            }
        });

        LinearLayout linear_callDriver=(LinearLayout)findViewById(R.id.linear_callDriver);
        linear_callDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:7978311216"));
                startActivity(intent);
            }
        });


        llDriverProfile = findViewById(R.id.llDriverProfile);
        llDriverProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(oditek.com.hlw.PickUpArriving.this, oditek.com.hlw.YourBill.class);
                startActivity(intent);
            }
        });

        btnCancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(oditek.com.hlw.PickUpArriving.this);
                dialog.setContentView(R.layout.cancel_ride_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                cancel = (LinearLayout) dialog.findViewById(R.id.cancel);
                // if button is clicked, close the custom dialog
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(oditek.com.hlw.PickUpArriving.this, oditek.com.hlw.SlideMenu.class);
                        intent.putExtra("ClickedFrom","PickUpArriving");
                        startActivity(intent);
                    }
                });

                dialog.show(

                );

            }
        });
    }
    private void shareTextUrl()
    {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
        share.putExtra(Intent.EXTRA_TEXT, "<Riding in HLW cab driven by John Catar (+912345678876). OTP: 1234 is needed to start the ride after booking the cab. Final bill amount will be shown on driver device. http://yoururl.com>");
        startActivity(Intent.createChooser(share, "Share on..."));
    }
}
