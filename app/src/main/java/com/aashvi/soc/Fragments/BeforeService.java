package com.aashvi.soc.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.aashvi.soc.Activity.ShowInvoicePicture;
import com.aashvi.soc.Helper.SharedHelper;
import com.aashvi.soc.R;


public class BeforeService extends Fragment {
    public static final String TAG = "BeforeService";
    Context context;
    View rootView;
    ImageView imgBeforeServiceInvoice;
    TextView lblBeforeServiceInvoice;


    public BeforeService() {
        // Required empty public constructor
    }


    public static BeforeService newInstance() {
        BeforeService fragment = new BeforeService();
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
        rootView = inflater.inflate(R.layout.before_service, container, false);
        findViewByIdAndInitialize();


        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void findViewByIdAndInitialize() {

        imgBeforeServiceInvoice = (ImageView) rootView.findViewById(R.id.imgBeforeServiceInvoice);
        lblBeforeServiceInvoice = (TextView) rootView.findViewById(R.id.lblBeforeServiceInvoice);

       // Toast.makeText(context,SharedHelper.getKey(context, "before_comment")+"===="+SharedHelper.getKey(context, "before_image"),Toast.LENGTH_LONG).show();

        if(!SharedHelper.getKey(context, "before_comment").equalsIgnoreCase("")) {
            lblBeforeServiceInvoice.setText("" + SharedHelper.getKey(context, "before_comment"));
        } else {
            lblBeforeServiceInvoice.setText("No comments");
        }

        if(!SharedHelper.getKey(context, "before_image").equalsIgnoreCase("")) {
            Picasso.with(context).load(SharedHelper.getKey(context, "before_image")).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(imgBeforeServiceInvoice);
        } else {
            imgBeforeServiceInvoice.setBackgroundResource(R.drawable.no_image);
        }
        imgBeforeServiceInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!SharedHelper.getKey(context, "before_image").equalsIgnoreCase(""))
                {
                    Intent intent = new Intent(context, ShowInvoicePicture.class);
                    intent.putExtra("image", "" + SharedHelper.getKey(context, "before_image"));
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(context,"Before Invoice image not found!",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();

    }


}
