package com.satyajit.booksbay.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.satyajit.booksbay.R;
import com.satyajit.booksbay.activity.MainActivity;

public class NoNetworkPop {


    private Context context;
    private Dialog dialog;



    public NoNetworkPop(Context context){

        //this.view = view; //parent view
        this.context = context;
    }

    public void show(){

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.no_network_pop);
        dialog.setCanceledOnTouchOutside(false);

        dialog.onBackPressed();

        // set the custom dialog components - text, image and button

        TextView retry_tv = dialog.findViewById(R.id.retry_no_internet);




        dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {

            //cancel or close activity on back btn press
            @Override
            public void onCancel(DialogInterface dialog)
            {
                ((AppCompatActivity)context).finish();
            }
        });



        retry_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (new CheckNetwork(context).isNetworkAvailable()) dialog.dismiss();
                else {
                    Animation animShake = AnimationUtils.loadAnimation(context, R.anim.shake);
                    view.startAnimation(animShake);
                }

            }
        });


        dialog.show();

    }

    public Dialog getDialog() {
        return dialog;
    }

    public void cancel(){
        if (dialog.isShowing()&&dialog!=null)
            dialog.dismiss();
    }
}
