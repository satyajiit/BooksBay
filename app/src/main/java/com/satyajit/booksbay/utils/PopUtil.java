package com.satyajit.booksbay.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.satyajit.booksbay.R;


public class PopUtil {

    private Context context;


    public PopUtil(Context context){

        this.context = context;
    }



    public void show(String asset){

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.view_pop);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        final WebView webView = dialog.findViewById(R.id.policy_webView);
        Button pop_ok = dialog.findViewById(R.id.pop_ok);

        pop_ok.setOnClickListener(view -> dialog.dismiss());

        webView.loadUrl("file:///android_asset/pages/"+asset+".html");



        dialog.show();


    }




}
