package com.aashvi.soc.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.aashvi.soc.Activity.BeginScreen;
import com.aashvi.soc.Activity.HistoryDetails;
import com.aashvi.soc.Helper.ConnectionHelper;
import com.aashvi.soc.Helper.CustomDialog;
import com.aashvi.soc.Helper.SharedHelper;
import com.aashvi.soc.Constants.URLHelper;
import com.aashvi.soc.R;
import com.aashvi.soc.Utils.Utilities;
import com.aashvi.soc.SOC;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.aashvi.soc.SOC.trimMessage;


public class PastTrips extends Fragment {
    public static final String TAG = "PastTrips";
    Activity activity;
    Context context;
    Boolean isInternet;
    PostAdapter postAdapter;
    RecyclerView recyclerView;
    RelativeLayout errorLayout;
    LinearLayout lnrList;
    ConnectionHelper helper;
    CustomDialog customDialog;
    View rootView;
    ImageView backArrow;
    PastTripsListener mListener;


    public PastTrips() {
        // Required empty public constructor
    }


    public static PastTrips newInstance() {
        PastTrips fragment = new PastTrips();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_past_trips, container, false);
        findViewByIdAndInitialize();

        if (isInternet) {
            getHistoryList();
        }

        return rootView;
    }


    public void getHistoryList() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GET_HISTORY_API, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Log.v("GetHistoryList", response.toString());
                if (response != null) {
                    if (response.length() > 0){
                        postAdapter = new PostAdapter(response);
                        //  recyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        if (postAdapter != null && postAdapter.getItemCount() > 0) {
                            lnrList.setVisibility(View.VISIBLE);
                            errorLayout.setVisibility(View.GONE);
                            recyclerView.setAdapter(postAdapter);
                        } else {
                            lnrList.setVisibility(View.GONE);
                            errorLayout.setVisibility(View.VISIBLE);
                        }
                    }else{
                        lnrList.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    }


                } else {
                    lnrList.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
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
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }

                        } else if (response.statusCode == 401) {
                            refreshAccessToken("PAST_TRIPS");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));

                        } else {
                            displayMessage(getString(R.string.please_try_again));

                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));

                    }

                } else {
                    displayMessage(getString(R.string.please_try_again));

                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
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

                Log.v("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                if (tag.equalsIgnoreCase("PAST_TRIPS")) {
                    getHistoryList();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
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

    public void findViewByIdAndInitialize() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        errorLayout = (RelativeLayout) rootView.findViewById(R.id.errorLayout);
        lnrList = (LinearLayout) rootView.findViewById(R.id.lnrList);
        backArrow = (ImageView) rootView.findViewById(R.id.backArrow);
        errorLayout.setVisibility(View.GONE);
        helper = new ConnectionHelper(getActivity());
        isInternet = helper.isConnectingToInternet();

//        backArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mListener.moveToHomeCategoryFragment();
//            }
//        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void displayMessage(String toastString) {
        try{
            Snackbar.make(getView(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void GoToBeginActivity() {
        Toast.makeText(getContext(),getString(R.string.session_timeout),Toast.LENGTH_SHORT).show();
        Intent mainIntent = new Intent(activity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    private class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
        JSONArray jsonArray;

        public PostAdapter(JSONArray array) {
            this.jsonArray = array;
        }

        public void append(JSONArray array) {
            try {
                for (int i = 0; i < array.length(); i++) {
                    this.jsonArray.put(array.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public PostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.history_list_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PostAdapter.MyViewHolder holder, int position) {
            Glide.with(activity).load(jsonArray.optJSONObject(position).optString("static_map")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(holder.tripImg);
            if (jsonArray.optJSONObject(position).optJSONObject("payment") != null){
                holder.tripAmount.setText("$"+jsonArray.optJSONObject(position).optJSONObject("payment").optString("total"));
            }
            try {
                if (!jsonArray.optJSONObject(position).optString("assigned_at","").isEmpty()) {
                    String form = jsonArray.optJSONObject(position).optString("assigned_at");
                    try {
                        holder.tripDate.setText(getDate(form)+"th "+getMonth(form)+" "+getYear(form)+" at "+getTime(form));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JSONObject serviceObj = jsonArray.getJSONObject(position).optJSONObject("service_type");
                if (serviceObj!=null){
                    holder.car_name.setText(serviceObj.optString("name"));
                    Glide.with(activity).load(Utilities.getImageURL(serviceObj.optString("image"))).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).dontAnimate().into(holder.driver_image);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tripTime,car_name;
            TextView tripDate, tripAmount;
            ImageView tripImg,driver_image;

            public MyViewHolder(View itemView) {
                super(itemView);
                tripDate = (TextView) itemView.findViewById(R.id.tripDate);
                tripTime = (TextView) itemView.findViewById(R.id.tripTime);
                tripAmount = (TextView) itemView.findViewById(R.id.tripAmount);
                tripImg = (ImageView) itemView.findViewById(R.id.tripImg);
                car_name = (TextView) itemView.findViewById(R.id.car_name);
                driver_image = (ImageView) itemView.findViewById(R.id.driver_image);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), HistoryDetails.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Log.e("Intent", "" + jsonArray.optJSONObject(getAdapterPosition()).toString());
                        intent.putExtra("post_value", jsonArray.optJSONObject(getAdapterPosition()).toString());
                        intent.putExtra("tag", "past_trips");
                        startActivity(intent);
                    }
                });

            }
        }
    }

    private String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }
    private String getDate(String date) throws ParseException{
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String dateName = new SimpleDateFormat("dd").format(cal.getTime());
        return dateName;
    }
    private String getYear(String date) throws ParseException{
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
        return yearName;
    }

    private String getTime(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String timeName = new SimpleDateFormat("hh:mm a").format(cal.getTime());
        String str = timeName.replace("a.m", "AM").replace("p.m","PM");
        return str;
    }

    public interface PastTripsListener{
        public void moveToHomeCategoryFragment();
    }


    @Override
    public void onResume() {
        super.onResume();
//        if (context instanceof ServiceFlowFragment.ServiceFlowFgmtListener) {
//            mListener = (PastTripsListener) context;
//        } else {
//            throw new RuntimeException(context.toString() + " must implement ServiceFlowFgmtListener");
//        }
    }



}
