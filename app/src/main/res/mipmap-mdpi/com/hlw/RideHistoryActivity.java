package oditek.com.hlw;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class RideHistoryActivity extends AppCompatActivity {
    CardView cardView_Ridehistory;
    ListView recyclerViewRideHistory;
    ArrayList<RideHistoryModel> list;
    private EditText et_Searchride_history;
    Button cancel_search;
    oditek.com.hlw.RideHistoryAdapter rideHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);

        Typeface typeFaceLight = Typeface.createFromAsset(this.getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(this.getAssets(), "fonts/Poppins-SemiBold.ttf");


        et_Searchride_history = (EditText) findViewById(R.id.et_Searchride_history);
        et_Searchride_history.setTypeface(typeFaceLight);
        cancel_search = (Button) findViewById(R.id.cancel_search);
        cancel_search.setTypeface(typeFaceLight);
        cardView_Ridehistory = (CardView) findViewById(R.id.cardView_Ridehistory);
        recyclerViewRideHistory = (ListView) findViewById(R.id.recyclerViewRideHistory);

        getData();

        /*et_Searchride_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel_search.setVisibility(View.GONE);
            }
        });*/
        cancel_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recyclerViewRideHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RideHistoryModel selection = (RideHistoryModel) parent.getItemAtPosition(position);
                String list_countryCode_ = selection.getLocAddress();
                Intent intent = new Intent();
                intent.putExtra("Address", list_countryCode_);
                setResult(5000, intent);
                finish();

            }
        });

        et_Searchride_history.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                rideHistoryAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void getData() {
        list = new ArrayList<RideHistoryModel>();

        RideHistoryModel hlw = new RideHistoryModel();
        hlw.setLocName("INOX, Bhawani Mall");
        hlw.setLocAddress("Unit-II, Saheed Nagar, Bhubaneswar, Odisha, India");
        list.add(hlw);

        hlw = new RideHistoryModel();
        hlw.setLocName("Forum Esplanade");
        hlw.setLocAddress("Rasulgarh Industrial Area Estate, Rasulgarh, Bhubaneswar, Odisha, India");
        list.add(hlw);

        hlw = new RideHistoryModel();
        hlw.setLocName("Mayfair Lagoon");
        hlw.setLocAddress("Mayfair Road, Jaydev Vihar, Bhubaneswar, Odisha, India");
        list.add(hlw);

        hlw = new RideHistoryModel();
        hlw.setLocName("Patia Big Bazaar");
        hlw.setLocAddress("Adarsh Vihar, Aryapalli, Patia, Bhubaneswar, Odisha, India");
        list.add(hlw);

        rideHistoryAdapter = new oditek.com.hlw.RideHistoryAdapter(oditek.com.hlw.RideHistoryActivity.this, R.layout.ridehistory_rowlayout, list);
        recyclerViewRideHistory.setAdapter(rideHistoryAdapter);
    }
}



