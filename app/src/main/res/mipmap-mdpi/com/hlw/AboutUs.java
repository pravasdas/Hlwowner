package oditek.com.hlw;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class AboutUs extends Fragment {

TextView tv1,tv2,tv3;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);

        Typeface typeFaceLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins-SemiBold.ttf");

        tv1=view.findViewById(R.id.tv1);
        tv1.setTypeface(typeFaceLight);

        tv2=view.findViewById(R.id.tv2);
        tv2.setTypeface(typeFaceLight);

        tv3=view.findViewById(R.id.tv3);
        tv3.setTypeface(typeFaceLight);
        return view;
    }

}
