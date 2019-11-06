package com.satyajit.booksbay.activity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.satyajit.booksbay.R;
import com.satyajit.booksbay.fragments.BookSelfFragment;
import com.satyajit.booksbay.fragments.LibraryFragment;
import com.satyajit.booksbay.fragments.MeFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    Fragment fragment = null;
    int startPos = 2;
    BottomNavigationView navigation;

    final Fragment fragment1 = new BookSelfFragment();
    final Fragment fragment2 = new LibraryFragment();
    final Fragment fragment3 = new MeFragment();
    Fragment active = fragment2;

    boolean doubleBackToExitPressedOnce = false;

    FragmentManager fragmentTransaction = getSupportFragmentManager();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

                //Actions on Nav item is selected
                int pos = 0;



                switch (item.getItemId()) {
                    case R.id.navigation_self:
                        fragmentTransaction.beginTransaction().hide(active).show(fragment1).commit();
                        active = fragment1;
                        pos = 1;
                        break;
                    case R.id.navigation_lib:
                        fragmentTransaction.beginTransaction().hide(active).show(fragment2).commit();
                        active = fragment2;
                        pos = 2;
                        break;
                    case R.id.navigation_me:
                        fragmentTransaction.beginTransaction().hide(active).show(fragment3).commit();
                        active = fragment3;
                        pos = 3;
                        break;

                }

                if (fragment != null) {





                    if (startPos>pos)
                        fragmentTransaction.beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right );
                    else if (startPos<pos)
                        fragmentTransaction.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);





                    startPos = pos;
                }


                return true;

            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initUI();
        setUpListeners();


        //Set the main fragment or inflate the fragment
        // fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.beginTransaction().add(R.id.frame_layout, fragment1).hide(fragment1).commit();
        fragmentTransaction.beginTransaction().add(R.id.frame_layout, fragment3).hide(fragment3).commit();
        fragmentTransaction.beginTransaction().add(R.id.frame_layout, fragment2).commit(); //Set fragment as default

        //fragmentTransaction.beginTransaction().addToBackStack(null);

        //fragmentTransaction.beginTransaction().commit();




    }

    void initUI(){


        navigation =  findViewById(R.id.navigation);

    }

    void setUpListeners(){


        //Btm Nav Listener
        navigation.setSelectedItemId(R.id.navigation_lib); //sets Library as the default item
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener); //Set the Click Listener for Nav Bar



    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);

    }


}