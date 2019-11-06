package com.satyajit.booksbay.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.satyajit.booksbay.GenreFragments.HotBooks;
import com.satyajit.booksbay.GenreFragments.NewBooks;
import com.satyajit.booksbay.GenreFragments.TrendingBooks;
import com.satyajit.booksbay.R;
import com.satyajit.booksbay.adapters.TabAdapter;
import com.satyajit.booksbay.utils.CheckNetwork;
import com.satyajit.booksbay.utils.NoNetworkPop;

public class GenreSelectionActivity extends AppCompatActivity {

    public String title;
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_genre_selection);

        getDataFromIntent(savedInstanceState);

        setCustomTitlebar();

        initUI();


        setupAdapter();






    }

    private void getDataFromIntent(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                title= null;
            } else {
                title= extras.getString("TITLE");
            }
        } else {
            title= (String) savedInstanceState.getSerializable("TITLE");
        }
    }


    void setCustomTitlebar(){
        //Set Title bar with Custom Typeface
        TextView tv = new TextView(getApplicationContext());
        Typeface Cav = Typeface.createFromAsset(getAssets(),"fonts/cav.ttf");
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);

        if (title == null) title = "Romance";

        if (title.charAt(0)=='#') tv.setText(title.substring(1));

        else tv.setText(title);

        tv.setTextSize(20);
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        tv.setTypeface(Cav);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(tv);
        getSupportActionBar().setElevation(0);


        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //Enable the up button


    }

    void initUI(){

        viewPager =  findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); //Terminate the current Activity
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    void setupAdapter(){


        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new NewBooks(), "NEW");
        adapter.addFragment(new TrendingBooks(), "TRENDING");
        adapter.addFragment(new HotBooks(), "HOT");




        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        //viewPager.setCurrentItem(2);



    }


    public void back(View view) {
        finish();
    }
}
