package com.aashvi.soc.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.aashvi.soc.Fragments.HomeCategoryFragment;
import com.aashvi.soc.Helper.SharedHelper;
import com.aashvi.soc.Models.ServiceListModel;
import com.aashvi.soc.R;
import com.aashvi.soc.Utils.Utilities;

import org.json.JSONArray;

import java.util.ArrayList;


public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.ViewHolder> implements View.OnClickListener {
    ArrayList<ServiceListModel> listModels = new ArrayList<ServiceListModel>();
    Context context;
    JSONArray jsonArraylist;
    private RadioButton lastChecked = null;
    BottomSheetBehavior behavior;
    String TAG = "ServiceListAdapter";
    private int lastCheckedPos = 0;
    JSONArray jsonArray;
    Utilities utils = new Utilities();
    HomeCategoryFragment.HomeCategoryFgmtListener mListener;
    ArrayList<ServiceListModel> lstServiceModel;

    public ServiceListAdapter(HomeCategoryFragment.HomeCategoryFgmtListener mListener, JSONArray jsonArray, ArrayList<ServiceListModel> lstServiceModel) {
        this.jsonArray = jsonArray;
        this.mListener = mListener;
        this.lstServiceModel = lstServiceModel;
    }

    @Override
    public ServiceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_list_item, null);
        context = parent.getContext();
        return new ServiceListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ServiceListAdapter.ViewHolder holder, final int position) {
        holder.service_list_name.setText(jsonArray.optJSONObject(position).optString("name"));
        System.out.println("=====>"+Utilities.getImageURL(jsonArray.optJSONObject(position).optString("image")));
        Picasso.with(context).load(Utilities.getImageURL(jsonArray.optJSONObject(position).optString("image")))
                .centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE)
                .fit()
                .into(holder.service_image_icon);

        holder.service_list_layout.setTag(position);


        holder.service_list_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(context, "service_type", "" + jsonArray.optJSONObject(Integer.parseInt(view.getTag().toString())).optString("id"));
                ServiceListModel serviceListModel = lstServiceModel.get(Integer.parseInt(view.getTag().toString()));
                utils.print("service_type",""+SharedHelper.getKey(context, "service_type"));
                SharedHelper.putKey(context, "hourly_fare", serviceListModel.getHourlyFare());
                SharedHelper.putKey(context, "service_name", serviceListModel.getServiceType());
                utils.print(TAG, "onItemClicked: " + serviceListModel);
                mListener.moveToServiceFlowFragment();
            }
        });
    }

    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    @Override
    public void onClick(View v) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView service_list_name;
        ImageView service_image_icon, imageDark;
        CheckBox service_checkbox;
        CardView service_list_layout;


        public ViewHolder(View itemView) {
            super(itemView);
            service_image_icon = (ImageView) itemView.findViewById(R.id.service_image_icon);
            service_list_name = (TextView) itemView.findViewById(R.id.service_list_name);
            service_list_layout = (CardView) itemView.findViewById(R.id.service_list_layout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            Log.v(TAG, "Response 1 " + getAdapterPosition());
//            Fragment fragment = new MapFragment();
//            FragmentManager manager = ((Home) context).getSupportFragmentManager();
//            @SuppressLint("CommitTransaction") FragmentTransaction transaction = manager.beginTransaction();
//            transaction.replace(R.id.container, fragment);
//            transaction.addToBackStack("MapFragment");
//            transaction.commit();
        }
    }
}
