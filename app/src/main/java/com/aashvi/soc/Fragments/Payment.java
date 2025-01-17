package com.aashvi.soc.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.aashvi.soc.Activity.AddCard;
import com.aashvi.soc.Activity.BeginScreen;
import com.aashvi.soc.Activity.Home;
import com.aashvi.soc.Adapter.PaymentListAdapter;
import com.aashvi.soc.Constants.URLHelper;
import com.aashvi.soc.Helper.ConnectionHelper;
import com.aashvi.soc.Helper.CustomDialog;
import com.aashvi.soc.Helper.SharedHelper;
import com.aashvi.soc.R;
import com.aashvi.soc.Utils.Utilities;
import com.aashvi.soc.SOC;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.aashvi.soc.SOC.trimMessage;


public class Payment extends Fragment {
    public static final String TAG = "Payment";
    private final int ADD_CARD_CODE = 435;
    Activity activity;
    Context context;
    View rootView;
    CustomDialog customDialog;
    ImageView backArrow;
    TextView addCard;
    ListView payment_list_view;
    ArrayList<JSONObject> listItems;
    ListAdapter paymentAdapter;
    TextView empty_text;
    Utilities utils = new Utilities();

    //Internet
    ConnectionHelper helper;
    Boolean isInternet;

    public static Payment newInstance() {
        Payment fragment = new Payment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_payment, container, false);
        findViewByIdAndInitialize();
        getCardList();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToMainActivity();
            }
        });

        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToAddCard();
            }
        });

        payment_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    JSONObject object = new JSONObject(paymentAdapter.getItem(i).toString());
                    utils.print("MyTest",""+paymentAdapter.getItem(i));
                    DeleteCardDailog(object);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                return false;
            }
        });

        return rootView;
    }

    private void DeleteCardDailog(final JSONObject object) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getString(R.string.are_you_sure))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    deleteCard(object);
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        });
        dialog .show();
    }

    public void deleteCard(JSONObject inputObj){
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try{
            object.put("card_id", inputObj.optString("card_id"));
            object.put("_method", "DELETE");

        }catch (Exception e){
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.DELETE_CARD_FROM_ACCOUNT_API, object , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("SendRequestResponse",response.toString());
                customDialog.dismiss();
                getCardList();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if(response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500){
                            try{
                                displayMessage(errorObj.getString("message"));
                            }catch (Exception e){
                                displayMessage(errorObj.optString("error"));
                                //displayMessage(getString(R.string.something_went_wrong));
                            }
                            utils.print("MyTest",""+errorObj.toString());
                        }else if(response.statusCode == 401){
                            displayMessage(getString(R.string.something_went_wrong));
                        }else if(response.statusCode == 422){

                            json = trimMessage(new String(response.data));
                            if(json !="" && json != null) {
                                displayMessage(json);
                            }else{
                                displayMessage(getString(R.string.please_try_again));
                            }
                        }else if(response.statusCode == 503){
                            displayMessage(getString(R.string.server_down));
                        }else{
                            displayMessage(getString(R.string.please_try_again));
                        }

                    }catch (Exception e){
                        displayMessage(getString(R.string.something_went_wrong));
                    }

                }else{
                    displayMessage(getString(R.string.please_try_again));
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization",""+SharedHelper.getKey(context, "token_type")+" "+SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        SOC.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void getCardList(){

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.CARD_PAYMENT_LIST, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                utils.print("GetPaymentList",response.toString());
                if(response != null && response.length() > 0){
                    listItems = getArrayListFromJSONArray(response);
                    if (listItems.isEmpty()){
                        empty_text.setVisibility(View.VISIBLE);
                        payment_list_view.setVisibility(View.GONE);
                    }else{
                        empty_text.setVisibility(View.GONE);
                        payment_list_view.setVisibility(View.VISIBLE);
                    }

                    paymentAdapter = new PaymentListAdapter(context,R.layout.payment_list_item,listItems);
                    payment_list_view.setAdapter(paymentAdapter);
                }else {
                    empty_text.setVisibility(View.VISIBLE);
                    payment_list_view.setVisibility(View.GONE);
                }

                customDialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if(response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500){
                            try{
                                displayMessage(errorObj.optString("message"));
                            }catch (Exception e){
                                displayMessage(getString(R.string.something_went_wrong));
                            }

                        }else if(response.statusCode == 401){
                            refreshAccessToken("PAYMENT_LIST");
                        }else if(response.statusCode == 422){

                            json = trimMessage(new String(response.data));
                            if(json !="" && json != null) {
                                displayMessage(json);
                            }else{
                                displayMessage(getString(R.string.please_try_again));
                            }
                        }else if(response.statusCode == 503){
                            displayMessage(getString(R.string.server_down));

                        }else{
                            displayMessage(getString(R.string.please_try_again));

                        }

                    }catch (Exception e){
                        displayMessage(getString(R.string.something_went_wrong));

                    }

                }else{
                    displayMessage(getString(R.string.please_try_again));

                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization",""+ SharedHelper.getKey(context, "token_type")+" "+SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        SOC.getInstance().addToRequestQueue(jsonArrayRequest);
    }


    private void refreshAccessToken(final String tag) {


        JSONObject object = new JSONObject();
        try {

            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.CLIENT_ID);
            object.put("client_secret", URLHelper.CLIENT_SECRET_KEY);
            object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGIN, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                utils.print("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                if(tag.equalsIgnoreCase("PAYMENT_LIST")) {
                    getCardList();
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context,"loggedIn",getString(R.string.False));
                    GoToBeginActivity();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        SOC.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    private ArrayList<JSONObject> getArrayListFromJSONArray(JSONArray jsonArray){

        ArrayList<JSONObject> aList=new ArrayList<JSONObject>();

        try {
            if (jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    aList.add(jsonArray.getJSONObject(i));

                }

            }
        }catch (JSONException je){
            je.printStackTrace();
        }

        return  aList;

    }


    public void findViewByIdAndInitialize(){
        backArrow = (ImageView) rootView.findViewById(R.id.backArrow);
        addCard = (TextView) rootView.findViewById(R.id.addCard);
        payment_list_view = (ListView) rootView.findViewById(R.id.payment_list_view);
        empty_text = (TextView) rootView.findViewById(R.id.empty_text);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
    }

    public void displayMessage(String toastString){
        Snackbar.make(getView(),toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public void GoToBeginActivity(){
        Intent mainIntent = new Intent(activity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void GoToMainActivity(){
        Intent mainIntent = new Intent(activity, Home.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void GoToAddCard(){

        Intent mainIntent = new Intent(activity, AddCard.class);
        startActivityForResult(mainIntent,ADD_CARD_CODE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
               boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCardList();
                }
            }
        }
    }

}
