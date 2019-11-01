package oditek.com.hlw;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;


public class EmergencyContactsFragment extends Fragment {
    Button btnAddContacts;
    CardView cardView;
    TextView tvAlert;
    RecyclerView mRecyclerView;
    List<EmergencyContactsModel> list = new ArrayList<>();
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_emergency_contacts, container, false);

        btnAddContacts = view.findViewById(R.id.btnAddContacts);
        tvAlert = view.findViewById(R.id.tvAlert);
//        tvname = (TextView) view.findViewById(R.id.tvname);
//        tvphone = (TextView) view.findViewById(R.id.tvphone);
        //tvmail = (TextView) view.findViewById(R.id.tvmail);

        cardView = (CardView) view.findViewById(R.id.card_view);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        btnAddContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAlert.setVisibility(View.GONE);
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                READ_CONTACTS_PERMISSIONS_REQUEST);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
            if (c.moveToFirst()) {

                String phoneNumber = "", emailAddress = "";
                String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                //http://stackoverflow.com/questions/866769/how-to-call-android-contacts-list   our upvoted answer

                String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if (hasPhone.equalsIgnoreCase("1"))
                    hasPhone = "true";
                else
                    hasPhone = "false";

                if (Boolean.parseBoolean(hasPhone)) {
                    Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                    while (phones.moveToNext()) {
                        phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    phones.close();
                }

                // Find Email Addresses
                Cursor emails = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
                while (emails.moveToNext()) {
                    emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                }
                emails.close();

                //mainActivity.onBackPressed();
                // Toast.makeText(mainactivity, "go go go", Toast.LENGTH_SHORT).show();

//                tvname.setText("Name: " + name);
//                tvphone.setText("Phone: " + phoneNumber);
                Log.d("curs", name + " num" + phoneNumber + " " + "mail" + emailAddress);


                EmergencyContactsModel s = new EmergencyContactsModel();
                s.setName(name);
                s.setPhone(phoneNumber);
                list.add(s);

                EmergencyContactAdapter emergencyAdapter = new EmergencyContactAdapter(getActivity(), list);
                mRecyclerView.setAdapter(emergencyAdapter);


            }
            c.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }


    public class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.ViewHolder> {
        private Context mContext;
        List<EmergencyContactsModel> list;


        @Override
        public EmergencyContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_rowlayout, parent, false);

            EmergencyContactAdapter.ViewHolder vh = new EmergencyContactAdapter.ViewHolder(v);

            return vh;
        }


        public EmergencyContactAdapter(Context mContext, List<EmergencyContactsModel> list) {
            this.mContext = mContext;
            this.list = list;
        }

        @Override
        public void onBindViewHolder(EmergencyContactAdapter.ViewHolder holder, final int position) {
            EmergencyContactsModel model = list.get(position);
            final String name = model.getName();
            final String phone = model.getPhone();

            holder.tvName.setText(name);
            holder.tvPhone.setText(phone);

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new iOSDialogBuilder(getActivity())
                            .setTitle("Remove")
                            .setSubtitle("Are you sure you want to remove this from your emergency contact list")
                            .setBoldPositiveLabel(true)
                            .setCancelable(false)
                            .setPositiveListener("Remove", new iOSDialogClickListener() {
                                @Override
                                public void onClick(iOSDialog dialog) {
                                    dialog.dismiss();
                                    removeItem(position);
                                }
                            })
                            .setNegativeListener("Cancel", new iOSDialogClickListener() {
                                @Override
                                public void onClick(iOSDialog dialog) {
                                    dialog.dismiss();
                                }
                            })
                            .build().show();

                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tvName, tvPhone;
            public ImageView ivDelete;
            public CardView mCardView;
            public SwitchButton switch_button;


            public ViewHolder(View itemView) {
                super(itemView);
                ivDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
                tvName = (TextView) itemView.findViewById(R.id.tvName);
                tvPhone = (TextView) itemView.findViewById(R.id.tvPhone);
                switch_button = (SwitchButton) itemView.findViewById(R.id.switch_button);

                mCardView = (CardView) itemView.findViewById(R.id.card_view);

            }
        }

        private void removeItem(int position) {
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
        }
    }

    public void Dialog() {
        new iOSDialogBuilder(getActivity())
                .setTitle("Remove")
                .setSubtitle("Are you sure you want to remove this from your emergency contact list")
                .setBoldPositiveLabel(true)
                .setCancelable(false)
                .setPositiveListener("Remove", new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();

                    }
                })
                .setNegativeListener("Cancel", new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .build().show();
    }

}

