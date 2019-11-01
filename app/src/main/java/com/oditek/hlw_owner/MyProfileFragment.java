package com.oditek.hlw_owner;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.oditek.hlw_owner.webservices.ApiClient;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileFragment extends Fragment {
    TextView tvRl2;
    ImageView ivBack;
    TextView EditProfileImage, tvEdit2, tvSave,tvFirst,tvEmail,tvPhone,tvPassword,etFirst,etEmail;
    EditText  etPhone, etPass;
    RelativeLayout rlPass, rlPhone;
    String change_password="2";
    String profileImage,ownerName,ownerEmail,ownerMobile;
    CircleImageView profile_image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_myprofile, container, false);
        //
        Typeface typeFaceLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins-SemiBold.ttf");

        tvRl2=view.findViewById(R.id.tvRl2);
        tvRl2.setText("MY PROFILE");
        tvRl2.setTypeface(typeFaceBold);
        ivBack=view.findViewById(R.id.ivBack);
        profile_image=view.findViewById(R.id.profile_image);
        tvFirst=view.findViewById(R.id.tvFirst);
        tvFirst.setTypeface(typeFaceLight);
        tvEmail=view.findViewById(R.id.tvEmail);
        tvEmail.setTypeface(typeFaceLight);
        tvPhone=view.findViewById(R.id.tvPhone);
        tvPhone.setTypeface(typeFaceLight);
        tvPassword=view.findViewById(R.id.tvPassword);
        tvPassword.setTypeface(typeFaceLight);
        etFirst=view.findViewById(R.id.etFirst);
        etFirst.setTypeface(typeFaceLight);
        etPhone=view.findViewById(R.id.etPhone);
        etPhone.setTypeface(typeFaceLight);
        etEmail=view.findViewById(R.id.etEmail);
        etEmail.setTypeface(typeFaceLight);
        etPass=view.findViewById(R.id.etPass);
        etPass.setTypeface(typeFaceLight);
        rlPass=view.findViewById(R.id.rlPass);
        //
        profileImage= ApiClient.getDataFromKey(getActivity(),"image");
        ownerName= ApiClient.getDataFromKey(getActivity(),"owner_name");
        ownerEmail= ApiClient.getDataFromKey(getActivity(),"owner_email");
        ownerMobile= ApiClient.getDataFromKey(getActivity(),"mobile");
        Glide
                .with(this)
                .load(profileImage)
                .into(profile_image);
        etFirst.setText(ownerName);
        etEmail.setText(ownerEmail);
        etPhone.setText(ownerMobile);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiClient.saveDataWithKeyAndValue(getActivity(),"BackPress","true");
                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.frame_container, new HomeFragment());
                ft.commit();

            }
        });
      /*  rlPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),ChangePasswordActivity.class);
                intent.putExtra("from_myprofile",change_password);
                startActivity(intent);
            }
        });*/

        return view;
    }


}
