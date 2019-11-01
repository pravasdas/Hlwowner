package oditek.com.hlw;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import oditek.com.hlw.webservices.AppController;
import oditek.com.hlw.webservices.VolleyMultipartRequest;

public class MyProfileFragment extends Fragment {
    TextView EditProfileImage, tvEdit2, tvSave;
    EditText etFirst, etPhone, etEmail, etPass;
    RelativeLayout rlPass, rlPhone;
    NetworkConnection nw;
    CircleImageView profile_image;
    int REQUEST_CAMERA = 1;
    int SELECT_FILE = 2;
    String imageFilePath = "";
    byte[] imageInByte;
    String value = "";
    private static final int PERMISSION_REQUEST_CODE = 200;
    private ImageLoader imageLoader;
    String user_id = "", access_token = "";
    AVLoadingIndicatorView progressDialog;
    private int i = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.my_profile_fragment, container, false);

        nw = new NetworkConnection(getActivity());
        progressDialog = view.findViewById(R.id.progressDialog);
        EditProfileImage = view.findViewById(R.id.tvEdit);
        tvEdit2 = view.findViewById(R.id.tvEdit2);
        rlPass = view.findViewById(R.id.rlPass);
        rlPhone = view.findViewById(R.id.rlPhone);

        etFirst = view.findViewById(R.id.etFirst);
        etPhone = view.findViewById(R.id.etPhone);
        etEmail = view.findViewById(R.id.etEmail);
        etPass = view.findViewById(R.id.etPass);
        tvSave = view.findViewById(R.id.tvSave);
        profile_image = view.findViewById(R.id.profile_image);
        imageLoader = AppController.getInstance().getImageLoader();

        etFirst.setEnabled(false);
        etPhone.setEnabled(false);
        etEmail.setEnabled(false);
        etPass.setEnabled(false);

        user_id = oditek.com.hlw.webservices.ApiClient.getDataFromKey(getActivity(), "user_id");
        access_token = oditek.com.hlw.webservices.ApiClient.getDataFromKey(getActivity(), "access_token");

        if (nw.isConnectingToInternet()) {
            syncGetProfileDetails(user_id, access_token);
        } else {
            ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
        }

        tvEdit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvEdit2.setVisibility(View.GONE);
                tvSave.setVisibility(View.VISIBLE);
                etFirst.setEnabled(true);
                etPhone.setEnabled(false);
                etEmail.setEnabled(true);
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvEdit2.setVisibility(View.VISIBLE);
                tvSave.setVisibility(View.GONE);
                etFirst.setEnabled(false);
                etPhone.setEnabled(false);
                etEmail.setEnabled(false);

                String emailval_ = etEmail.getText().toString().trim();

                if (etFirst.getText().toString().trim().length() == 0) {
                    etFirst.setError(getResources().getString(R.string.error_name_feedback));
                } else if (etEmail.getText().toString().trim().length() == 0) {
                    etEmail.setError(getResources().getString(R.string.error_email));
                } else if (!isValidEmail(emailval_)) {
                    etEmail.setError(getResources().getString(R.string.error_email2));
                } else if (nw.isConnectingToInternet()) {

                    String userid = user_id;
                    String accesstoken = access_token;
                    String email = etEmail.getText().toString().trim();
                    String name = etFirst.getText().toString().trim();
                    startLoading();

                    syncUpdateNameEmailData(userid, accesstoken, email, name);
                } else {
                    ErrorDialog("No Internet!", "Make sure that WI-FI or Mobile data is turned on, then try again...");
                }

            }
        });

        rlPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePassword.class);
                startActivity(intent);
            }
        });

        rlPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new iOSDialogBuilder(getActivity())
                        .setTitle("Do you want to change your mobile number?")
                        .setSubtitle("OTP wil be sent to Registered Mobile Number")
                        .setBoldPositiveLabel(true)
                        .setCancelable(false)
                        .setPositiveListener("Ok", new iOSDialogClickListener() {
                            @Override
                            public void onClick(iOSDialog dialog) {
                                dialog.dismiss();
                                String for_changePhone = "0";
                                Intent intent = new Intent(getActivity(), ChangePhoneOTP.class);
                                intent.putExtra("change_phone", for_changePhone);
                                startActivity(intent);
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

        EditProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();

            }
        });


        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)
                    getContext(), Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CODE);
            }
        }


        return view;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

       private void showCameraDialog() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change Profile Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void showFileChooser() {
        boolean result = oditek.com.hlw.webservices.ApiClient.checkPermission(getActivity());
        if (result) {
            showCameraDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case oditek.com.hlw.webservices.ApiClient.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showCameraDialog();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        imageFilePath = destination.toString();
        profile_image.setImageBitmap(thumbnail);

        BitmapFactory.decodeFile(imageFilePath);

        // sharedPreferenceClass.setValue_string("User_Image",imageFilePath);
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            imageInByte = bytes.toByteArray();
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //imageInByte = imageFilePath.getBytes();
        updateProfileImage(user_id, access_token, thumbnail);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getActivity().managedQuery(selectedImageUri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        imageFilePath = cursor.getString(column_index).toString();
        BitmapFactory.decodeFile(imageFilePath);
        value = String.valueOf(Uri.fromFile(new File(imageFilePath)));

        Bitmap bm = BitmapFactory.decodeFile(imageFilePath);
        //value = Utils.base64Encode(bm);
        profile_image.setImageBitmap(bm);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            imageInByte = bytes.toByteArray();
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //imageInByte = imageFilePath.getBytes();
        updateProfileImage(user_id, access_token, bm);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void updateProfileImage(final String user_id, final String access_token, final Bitmap image) {
        // loading or check internet connection or something...
        // ... then
        String tag_json_req = "sync_profile_image";

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.update_profile_image_url), new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    startLoading();
                    JSONObject jsonObject = new JSONObject(resultResponse);
                    System.out.println("json----" + jsonObject);
                    String status_ = jsonObject.getString("status");
                    String msg = jsonObject.getString("msg");

                    if ("0".equalsIgnoreCase(status_)) {
                        ErrorDialog(msg, "");
                    } else {
                        stopLoading();
                        syncGetProfileDetails(user_id, access_token);
                        ErrorDialog(msg, "");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        System.out.println(response);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("access_token", access_token);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                long imagename = System.currentTimeMillis();///IMAGE NAME SETTING DURING
                params.put("file", new DataPart(imagename + ".jpg", getFileDataFromDrawable(image)));
                return params;
            }
        };

        //VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
        multipartRequest.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().getRequestQueue().add(multipartRequest).addMarker(tag_json_req);
    }

    private void syncGetProfileDetails(final String user_id, final String access_token) {
        startLoading();
        String tag_json_req = "sync_get_profile";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.get_profile_details_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            stopLoading();
                            Log.d("response=========", response);
                            JSONObject jsonObject = new JSONObject(response);
                            System.out.println("json----" + jsonObject);

                            if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                String nameTxt = jsonArray.getJSONObject(0).getString("name");
                                String emailTxt = jsonArray.getJSONObject(0).getString("email");
                                String mobileTxt = jsonArray.getJSONObject(0).getString("mobile");
                                String imageTxt = jsonArray.getJSONObject(0).getString("image");

                                etFirst.setText(nameTxt);
                                etEmail.setText(emailTxt);
                                etPhone.setText(mobileTxt);

                                oditek.com.hlw.webservices.ApiClient.saveDataWithKeyAndValue(getActivity(), "image", imageTxt);
                                oditek.com.hlw.webservices.ApiClient.saveDataWithKeyAndValue(getActivity(), "name", nameTxt);

                                if (imageTxt.equalsIgnoreCase("")) {
                                    System.out.println(imageTxt);
                                } else {

                                    Glide
                                            .with(getActivity())
                                            .load(imageTxt)
                                            .centerCrop()
                                            .placeholder(R.drawable.user)
                                            .error(R.drawable.user)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(profile_image);
                                }
                            } else
                                Toast.makeText(getActivity(), jsonObject.getString("msg"),
                                        Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() == null) {
                    if (i < 3) {
                        Log.e("Retry due to error ", "for time : " + i);
                        i++;
                        syncGetProfileDetails(user_id, access_token);
                    } else {
                        Toast.makeText(getActivity(), "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                } else
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("access_token", access_token);

                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }

    private void syncUpdateNameEmailData(final String user_id, final String access_token, final String email, final String name) {
        startLoading();
        String tag_json_req = "sync_update_name_email";

        StringRequest data = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url) + getResources()
                        .getString(R.string.update_name_email_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            stopLoading();
                            Log.d("response=========", response);
                            JSONObject jsonObject = new JSONObject(response);
                            System.out.println("json----" + jsonObject);

                            String status_ = jsonObject.getString("status");
                            String msg = jsonObject.getString("msg");

                            if ("0".equalsIgnoreCase(status_)) {
                                ErrorDialog(msg, "");
                            } else {
                                ErrorDialog(msg, "");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() == null) {
                    if (i < 3) {
                        Log.e("Retry due to error ", "for time : " + i);
                        i++;
                        syncUpdateNameEmailData(user_id, access_token, email, name);
                    } else {
                        Toast.makeText(getActivity(), "Check your network connection.",
                                Toast.LENGTH_LONG).show();
                        stopLoading();
                    }
                } else
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("access_token", access_token);
                params.put("email", email);
                params.put("name", name);

                Log.d("params are :", "" + params);
                return params;
            }
        };
        data.setRetryPolicy(new
                DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(data, tag_json_req);
    }

    public void ErrorDialog(String title, String subtitle) {
        new iOSDialogBuilder(getActivity())
                .setTitle(title)
                .setSubtitle(subtitle)
                .setBoldPositiveLabel(true)
                .setCancelable(false)
                .setPositiveListener("Ok", new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .build().show();
    }

    void startLoading() {
        progressDialog.show();
        progressDialog.setVisibility(View.VISIBLE);
        progressDialog.setIndicator("Loading...");
    }

    void stopLoading() {
        progressDialog.hide();
        progressDialog.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        syncGetProfileDetails(user_id, access_token);
    }
}
