package com.oditek.hlw_owner.webservices;

import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

import com.oditek.hlw_owner.CustomProgressDialog;

public class VolleyAPIService {
    IResult mResultCallback = null;
    Context mContext;
    ProgressDialog progressDialog = null;

    public VolleyAPIService(IResult resultCallback, Context context) {
        mResultCallback = resultCallback;
        mContext = context;
    }

    public void postDataVolley(final String requestType, String url, final Map<String, String> getPostParams) {
        System.out.println("requestType:" + requestType);
        System.out.println("url:" + url);
        System.out.println("sendObj:" + getPostParams);
        try {
            startLoading();
            RequestQueue queue = Volley.newRequestQueue(mContext);
            StringRequest data = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    stopLoading();
                    if (mResultCallback != null)
                        mResultCallback.notifySuccess(requestType, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    stopLoading();
                    if (mResultCallback != null)
                        mResultCallback.notifyError(requestType, error);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return getPostParams;
                }
            };
            queue.add(data);
        } catch (Exception e) {
            stopLoading();
        }
    }

    void startLoading() {
        if ((progressDialog != null) && progressDialog.isShowing()) {
        } else {
            progressDialog = CustomProgressDialog.custom(mContext);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    void stopLoading() {
        if ((progressDialog != null) && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
