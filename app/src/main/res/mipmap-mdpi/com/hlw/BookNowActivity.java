package oditek.com.hlw;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookNowActivity extends AppCompatActivity {
    CardView cardView;
    List<oditek.com.hlw.BookNowModel> list = new ArrayList<>();
    ImageView ivback_bookRental;
    LinearLayout linear_dateshow, linear_paymentMode, linear_applyCoupon, linear_totalFare, linear_applyCoupon2, linear_totalFare2, linearBookRideFor;
    ImageView back_booknow;
    RelativeLayout rlBottom_Confirm, rlBottom_Confirmshowdate, relativeBooklater, relativeOnewayBook;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_now);
        cardView = (CardView) findViewById(R.id.card_view);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        ivback_bookRental = findViewById(R.id.ivback_bookRental);
        List<oditek.com.hlw.BookNowModel> bookNowModels = Arrays.asList(new oditek.com.hlw.BookNowModel("1 hr 10 km"),
                new oditek.com.hlw.BookNowModel("BBSR_CUTTACK 1 hr 30 km 2019"),
                new oditek.com.hlw.BookNowModel("2 hr 20 km"),
                new oditek.com.hlw.BookNowModel("3 hr 30 km"),
                new oditek.com.hlw.BookNowModel("BBSR_NANDANKANAN 5 hr 60 km 2019"),
                new oditek.com.hlw.BookNowModel("6 hr 60 km"),
                new oditek.com.hlw.BookNowModel("8 hr 80 km"),
                new oditek.com.hlw.BookNowModel("10 hr 100 km"));
        //basic yak shaving required
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new BookNowAdapter(this, bookNowModels));

        ivback_bookRental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        linear_paymentMode = findViewById(R.id.linear_paymentMode);
        linear_applyCoupon = findViewById(R.id.linear_applyCoupon);
        linear_totalFare = findViewById(R.id.linear_totalFare);
        linearBookRideFor = findViewById(R.id.linearBookRideFor);

        linearBookRideFor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookRideForBottomSheetDialog();
            }
        });

        linear_paymentMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentMethodBottomSheetDialog();
            }
        });

        linear_applyCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(oditek.com.hlw.BookNowActivity.this, oditek.com.hlw.ApplyCoupon.class);
                startActivity(intent);
            }
        });

        linear_totalFare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogTotalFare();
            }
        });
    }

    public void DialogTotalFare() {

        final Dialog dialog = new Dialog(oditek.com.hlw.BookNowActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fare_details_dialog_box);
        dialog.show();
        dialog.setTitle(null);

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
}
