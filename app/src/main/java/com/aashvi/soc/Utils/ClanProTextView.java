package com.aashvi.soc.Utils;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created by Freeware Sys on 3/27/2017.
 */

public class ClanProTextView extends AppCompatTextView {


    public ClanProTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if(!isInEditMode())
            applyFont(context);
    }

    private void applyFont(Context context){
        setTypeface(Typefaces.get(context, "ClanPro-NarrNews.otf"));
    }


}
