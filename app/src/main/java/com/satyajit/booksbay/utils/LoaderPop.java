package com.satyajit.booksbay.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.satyajit.booksbay.R;



public class LoaderPop {


    private Context context;
     private Dialog dialog;

    public LoaderPop(Context context){

        //this.view = view; //parent view
        this.context = context;
    }

    public void show(){

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.loading_pop);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button
        LottieAnimationView lv = dialog.findViewById(R.id.lv);

            lv.setAnimation("loading1.json");

        dialog.show();

    }

    public void cancel(){
        if (dialog.isShowing()&&dialog!=null)
            dialog.dismiss();
    }
    public Dialog getDialog(){
        return dialog;
    }
}
