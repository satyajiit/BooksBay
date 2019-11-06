package com.satyajit.booksbay.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.satyajit.booksbay.R;
import com.satyajit.booksbay.adapters.BooksListAdapter;
import com.satyajit.booksbay.models.BooksInfoModel;
import com.satyajit.booksbay.utils.LoaderPop;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class AuthorActivity extends AppCompatActivity {


    String author;
    View errorView;
    ArrayList<BooksInfoModel> list = new ArrayList<>();
    LoaderPop load;
    BooksListAdapter adapter;
    RecyclerView booksRecycler;
    TextView books_count_author;

    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_author);
        getDataFromIntent(savedInstanceState);
        initUI();
        load.show();
        setCustomTitlebar();

        getBooksOfAuthorFromDB();


    }


    private void getDataFromIntent(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {

                author = extras.getString("AUTHOR","SATYAJIT");
                from = extras.getString("FROM","BLANK");
            }

        }

    }


    public void retry(View view) {

    }


    void setCustomTitlebar(){

        //Set Title bar with Custom Typeface
        TextView tv = new TextView(getApplicationContext());
        Typeface Cav = Typeface.createFromAsset(getAssets(),"fonts/cav.ttf");
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText(author);
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

    void getBooksOfAuthorFromDB(){




            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (from.equals("ENG"))
                db.collection("ENG BOOKS")
                        .whereEqualTo("author",author)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    books_count_author.setText(getResources().getString(R.string.available)+" ("+task.getResult().size()+")");

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData().get("author"));


                                        final BooksInfoModel booksInfoModel = new BooksInfoModel();
                                        booksInfoModel.setName(String.valueOf(document.getData().get("name")));
                                        booksInfoModel.setTime((long) document.getData().get("created"));
                                        booksInfoModel.setDownload_count((long) document.getData().get("hits"));
                                        booksInfoModel.setAuthor(String.valueOf(document.getData().get("author")));
                                        booksInfoModel.setUploader(String.valueOf(document.getData().get("uploader")));

                                        long[] stars = new long[5];
                                        stars[0] = (long) document.getData().get("1 STAR");
                                        stars[1] = (long) document.getData().get("2 STAR");
                                        stars[2] = (long) document.getData().get("3 STAR");
                                        stars[3] = (long) document.getData().get("4 STAR");
                                        stars[4] = (long) document.getData().get("5 STAR");

                                        booksInfoModel.setStars(stars);

                                        booksInfoModel.setGenre("#"+ document.getData().get("genre"));


                                        list.add(booksInfoModel);


                                    }

                                    showLists();

                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());

                                    fucked();

                                }
                            }
                        });


            else   db.collection("books")
                    .whereEqualTo("author",author)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                books_count_author.setText(getResources().getString(R.string.available)+" ("+task.getResult().size()+")");

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData().get("author"));


                                    final BooksInfoModel booksInfoModel = new BooksInfoModel();
                                    booksInfoModel.setName(String.valueOf(document.getData().get("name")));
                                    booksInfoModel.setTime((long) document.getData().get("created"));
                                    booksInfoModel.setDownload_count((long) document.getData().get("hits"));
                                    booksInfoModel.setAuthor(String.valueOf(document.getData().get("author")));
                                    booksInfoModel.setUploader(String.valueOf(document.getData().get("uploader")));

                                    long[] stars = new long[5];
                                    stars[0] = (long) document.getData().get("1 STAR");
                                    stars[1] = (long) document.getData().get("2 STAR");
                                    stars[2] = (long) document.getData().get("3 STAR");
                                    stars[3] = (long) document.getData().get("4 STAR");
                                    stars[4] = (long) document.getData().get("5 STAR");

                                    booksInfoModel.setStars(stars);

                                    booksInfoModel.setGenre(String.valueOf(document.getData().get("genre")));


                                    list.add(booksInfoModel);


                                }

                                showLists();

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());

                                fucked();

                            }
                        }
                    });


        }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); //Terminate the current Activity
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        return true;
    }


    void fucked(){

        //Something fucked the system

        errorView.setVisibility(View.VISIBLE);
        if (load!=null)
                load.cancel();


    }

  void initUI(){


      errorView  = findViewById(R.id.error_view);
      load = new LoaderPop(this);
      booksRecycler = findViewById(R.id.books_recycler_list);
      books_count_author = findViewById(R.id.books_count_author);;

  }

    void showLists() {


        adapter = new BooksListAdapter(this, list);
        booksRecycler.setAdapter(adapter);

        if (load.getDialog()!=null)
            if (load.getDialog().isShowing())
                load.cancel();


        adapter.notifyDataSetChanged();
        booksRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        errorView.setVisibility(View.GONE);

    }


}
