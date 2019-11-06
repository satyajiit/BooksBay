package com.satyajit.booksbay.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.satyajit.booksbay.R;
import com.satyajit.booksbay.adapters.DummyChildDataItem;
import com.satyajit.booksbay.adapters.DummyParentDataItem;
import com.satyajit.booksbay.adapters.SingleItemListAdapter;
import com.satyajit.booksbay.adapters.SingleItemListAdapterNcert;
import com.satyajit.booksbay.models.BooksInfoModel;
import com.satyajit.booksbay.models.CatsLists;
import com.satyajit.booksbay.utils.LoaderPop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ClassBooksActivity extends AppCompatActivity {

    LoaderPop load;


    RecyclerView catsRecyler;

    String title;

    ArrayList<DummyParentDataItem> arrDummyData = new ArrayList<>();
    ArrayList<DummyChildDataItem> childDataItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.genre_activity);



        initUI();

        getDataFromIntent(savedInstanceState);

        setCustomTitlebar();

        getList();





    }

    void setCustomTitlebar(){


        //Set Title bar with Custom Typeface
        TextView tv = new TextView(getApplicationContext());
        Typeface Cav = Typeface.createFromAsset(getAssets(),"fonts/cav.ttf");
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);

        tv.setText(title);
        tv.setTextSize(20);
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        tv.setTypeface(Cav);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(tv);


        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //Enable the up button


    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); //Terminate the current Activity
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        return true;
    }

    void initUI(){

        catsRecyler = findViewById(R.id.cats_recycler_lists);
        load = new LoaderPop(this);

    }

    void getList(){


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        load.show(); //Show loading screen

        db.collection("NCERT BOOKS")
                .document(title)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        load.cancel();

                        Map<String, Object> map = documentSnapshot.getData();
                        for (Map.Entry<String, Object> entry : Objects.requireNonNull(map).entrySet()) {

                            childDataItems = new ArrayList<>();

                            String subject = entry.getKey();

                            List<String> group = (List<String>) map.get(subject);

                            for (int i = 0; i<group.size(); i++)
                                childDataItems.add(new DummyChildDataItem(Objects.requireNonNull(group).get(i)));

                            arrDummyData.add(new DummyParentDataItem(subject,title,childDataItems));





                            }


                       // }




//                        for (String key :  Objects.requireNonNull(documentSnapshot.getData()).keySet()) {
//
//                            childDataItems = new ArrayList<>();
//                            catsListModel.setName(key);
//
//                            list.add(catsListModel);
//                        }

                        showLists();

                    }

                }
                );




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

    void showLists(){


        SingleItemListAdapterNcert adapter = new SingleItemListAdapterNcert(arrDummyData);

        catsRecyler.setAdapter(adapter);



        adapter.notifyDataSetChanged();
        catsRecyler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        load.cancel();


    }
}
