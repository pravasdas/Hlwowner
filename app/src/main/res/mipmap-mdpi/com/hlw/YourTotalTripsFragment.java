package oditek.com.hlw;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class YourTotalTripsFragment extends Fragment {
    RecyclerView mRecyclerView;
    CardView cardView;
    LinearLayout linear_totalcar_details;
    //Button btnOneWay;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_your_total_trips, container, false);

        cardView = (CardView) view.findViewById(R.id.card_viewtotal);
        linear_totalcar_details = (LinearLayout) view.findViewById(R.id.linear_totalcar_details);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewtotal);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        getData();
        return view;
    }

    private void getData() {
        ArrayList<oditek.com.hlw.YourTotalTripModel> list = new ArrayList<>();

        oditek.com.hlw.YourTotalTripModel s = new oditek.com.hlw.YourTotalTripModel();
        s.setCar(R.mipmap.car_micro_blue);
        s.setDayTime("Wed 28th Dec 10.30am");
        s.setCarDetails("Micro. CRN:5674665");
        s.setPickupLocation("14, Canal Road, Jagannath Nagar, Rasulgarh, Bhubaneswar, Odisha");
        s.setDropLocation("AIIMS, Sijua, Patrapada, Bhubaneswar");
        s.setTripScheduledPic(R.mipmap.driver_image);
        s.setPrice("Rs. 325/-");
        list.add(s);

        s = new oditek.com.hlw.YourTotalTripModel();
        s.setCar(R.mipmap.car_micro_blue);
        s.setDayTime("Wed 28th Dec 10.30am");
        s.setCarDetails("Micro. CRN:5674665");
        s.setPickupLocation("14, Canal Road, Jagannath Nagar, Rasulgarh, Bhubaneswar, Odisha");
        s.setDropLocation("AIIMS, Sijua, Patrapada, Bhubaneswar");
        s.setTripScheduledPic(R.mipmap.driver_image);
        s.setRidecancel(R.mipmap.ride_canceled);
        s.setPrice("null");
        list.add(s);

        s = new oditek.com.hlw.YourTotalTripModel();
        s.setCar(R.mipmap.car_micro_blue);
        s.setDayTime("Wed 28th Dec 10.30am");
        s.setCarDetails("Micro. CRN:5674665");
        s.setPickupLocation("14, Canal Road, Jagannath Nagar, Rasulgarh, Bhubaneswar, Odisha");
        s.setDropLocation("AIIMS, Sijua, Patrapada, Bhubaneswar");
        s.setTripScheduledPic(R.mipmap.driver_image);
        s.setPrice("Scheduled");
        list.add(s);

        YourTotalTripAdapter yourTotalTripAdapter = new YourTotalTripAdapter(getActivity(), list);
        mRecyclerView.setAdapter(yourTotalTripAdapter);
    }


    public class YourTotalTripAdapter extends RecyclerView.Adapter<YourTotalTripAdapter.ViewHolder> {
        private Context mContext;
        LinearLayout linear_totalcar_details;

        List<oditek.com.hlw.YourTotalTripModel> list;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.your_totaltrip_rowlayout, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }


        public YourTotalTripAdapter(Context mContext, List<oditek.com.hlw.YourTotalTripModel> list) {
            this.mContext = mContext;
            this.list = list;

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            oditek.com.hlw.YourTotalTripModel yourTotalTripmodel = list.get(position);
            final int car = yourTotalTripmodel.getCar();
            final String dayTime = yourTotalTripmodel.getDayTime();
            final String carDetails = yourTotalTripmodel.getCarDetails();
            final String pickupLocation = yourTotalTripmodel.getPickupLocation();
            final String dropLocation = yourTotalTripmodel.getDropLocation();
            final String price = yourTotalTripmodel.getPrice();
            final int tripScheduledPic = yourTotalTripmodel.getTripScheduledPic();
            final int RideCanceled = yourTotalTripmodel.getRidecancel();

            holder.car.setImageResource(car);
            holder.dayTime.setText(dayTime);
            holder.carDetails.setText(carDetails);
            holder.pickupLocation.setText(pickupLocation);
            holder.dropLocation.setText(dropLocation);
            holder.tvPrice.setText(price);
            holder.tripScheduledPic.setImageResource(tripScheduledPic);
            holder.ivRideCanceled.setImageResource(RideCanceled);


            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, oditek.com.hlw.YourTotalTripsDetails.class);
                    // intent.putExtra("blogId_", blogmodel.blogId);
                    mContext.startActivity(intent);
                }
            });

            if (price != null && !price.isEmpty() && !price.equals("null")) {
                holder.tvPrice.setVisibility(View.VISIBLE);
            } else {
                holder.tvPrice.setVisibility(View.GONE);
            }
// && !price.isEmpty() && !price.equals("null")
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView dayTime, carDetails, pickupLocation, dropLocation, tvPrice;
            public ImageView car, tripScheduledPic, ivRideCanceled;
            public CardView mCardView;

            public ViewHolder(View itemView) {
                super(itemView);
                car = itemView.findViewById(R.id.cartotal);
                dayTime = itemView.findViewById(R.id.daytime_total);
                carDetails = itemView.findViewById(R.id.cardetails_total);
                pickupLocation = itemView.findViewById(R.id.pickup_location);
                dropLocation = itemView.findViewById(R.id.drop_location);
                tripScheduledPic = itemView.findViewById(R.id.trip_driver_pic);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                ivRideCanceled = itemView.findViewById(R.id.ivRideCanceled);


                mCardView = (CardView) itemView.findViewById(R.id.card_viewtotal);
            }

        }

    }

}
