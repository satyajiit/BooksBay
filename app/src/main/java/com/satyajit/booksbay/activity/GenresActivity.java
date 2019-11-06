package com.satyajit.booksbay.activity;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.satyajit.booksbay.R;
import com.satyajit.booksbay.adapters.CategoryAdapter;
import com.satyajit.booksbay.adapters.SingleItemListAdapter;
import com.satyajit.booksbay.models.CatsLists;
import com.satyajit.booksbay.utils.LoaderPop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class GenresActivity extends AppCompatActivity {


    private RecyclerView catsRecyler;

    private LinearLayout errorView;

    ArrayList<CatsLists> list = new ArrayList<>();

    LoaderPop load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.genre_activity);

        setCustomTitlebar();

        initUI();

        getList();






    }

    void setCustomTitlebar(){
        //Set Title bar with Custom Typeface
        TextView tv = new TextView(getApplicationContext());
        Typeface Cav = Typeface.createFromAsset(getAssets(),"fonts/cav.ttf");
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText("Genres");
        tv.setTextSize(20);
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        tv.setTypeface(Cav);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(tv);


        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //Enable the up button


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


   void getList(){



       load.show(); //Show loading screen

        FirebaseStorage storage = FirebaseStorage.getInstance();


       storage.setMaxDownloadRetryTimeMillis(8000); //Waits for 8 seconds
       storage.setMaxOperationRetryTimeMillis(8000);
       storage.setMaxUploadRetryTimeMillis(8000);

       StorageReference listRef = storage.getReference().child("Genres");

        listRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference prefix : listResult.getPrefixes()) {
                       Log.d("TAG", prefix.getName());

                        CatsLists catsListModel = new CatsLists();
                        catsListModel.setName(prefix.getName());

                        list.add(catsListModel);

                    }

                    showLists();

                })
                .addOnFailureListener(e -> {
                    // Uh-oh, an error occurred!
                    load.cancel();
                    errorView.setVisibility(View.VISIBLE);
                });



    }

    void initUI(){

        catsRecyler = findViewById(R.id.cats_recycler_lists);
        load = new LoaderPop(this);
        errorView = findViewById(R.id.error_view);
    }

    void showLists(){


        SingleItemListAdapter adapter = new SingleItemListAdapter(this, list);
        catsRecyler.setAdapter(adapter);

        sortItems();

        adapter.notifyDataSetChanged();
        catsRecyler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        load.cancel();
        errorView.setVisibility(View.GONE);


    }


    public void retry(View view) {
        getList();
    }

    void sortItems(){

        //Sort the items alphabetically

        Collections.sort(list, (lhs, rhs) -> lhs.getName().compareTo(rhs.getName()));

    }
}
