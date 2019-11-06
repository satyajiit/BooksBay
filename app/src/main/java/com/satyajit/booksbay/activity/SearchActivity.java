package com.satyajit.booksbay.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.satyajit.booksbay.R;
import com.satyajit.booksbay.SignInActivity.SignInActivity;
import com.satyajit.booksbay.adapters.BooksListAdapter;
import com.satyajit.booksbay.models.BooksInfoModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SearchActivity extends AppCompatActivity {

    String query;

    com.airbnb.lottie.LottieAnimationView srchView, noResultView;

    TextView no_res_tv;

    Button request_book;

    RecyclerView results;

    ArrayList<BooksInfoModel> list = new ArrayList<>();

    BooksListAdapter adapter;

    Typeface Cav;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {


                query = extras.getString("QUERY");

            }
        }


        setCustomTitlebar();

        initUI();

        searchInDb();


    }


    void setCustomTitlebar(){

        //Set Title bar with Custom Typeface
        TextView tv = new TextView(getApplicationContext());
         Cav = Typeface.createFromAsset(getAssets(),"fonts/cav.ttf");
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText(query);
        tv.setTextSize(20);
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        tv.setTypeface(Cav);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(tv);


        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //Enable the up button


    }

    void initUI(){


        srchView = findViewById(R.id.searching);
        noResultView = findViewById(R.id.no_result_anim);
        no_res_tv = findViewById(R.id.no_res_tv);
        request_book = findViewById(R.id.request_book);
        results = findViewById(R.id.results);


    }

    void searchInDb(){


        showSearching();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("books")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {




                        for (QueryDocumentSnapshot document : task.getResult()) {



                            if (String.valueOf(document.getData().get("name")).toLowerCase().contains(query.toLowerCase())||String.valueOf(document.getData().get("author")).toLowerCase().contains(query.toLowerCase())) {

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

                        }



                       loadEngBooks();

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());

                        showNoResults();
                    }
                });



    }

    private void loadEngBooks() {


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("ENG BOOKS")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {




                        for (QueryDocumentSnapshot document : task.getResult()) {



                            if (String.valueOf(document.getData().get("name")).toLowerCase().contains(query.toLowerCase())||String.valueOf(document.getData().get("author")).toLowerCase().contains(query.toLowerCase())) {


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

                        }



                        showLists();

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());

                        showNoResults();
                    }
                });

    }

    void showSearching(){

        srchView.setVisibility(View.VISIBLE);
        noResultView.setVisibility(View.GONE);
        no_res_tv.setVisibility(View.GONE);
        request_book.setVisibility(View.GONE);
        results.setVisibility(View.GONE);

    }

    void showNoResults(){

        srchView.setVisibility(View.GONE);
        noResultView.setVisibility(View.VISIBLE);
        no_res_tv.setVisibility(View.VISIBLE);
        request_book.setVisibility(View.VISIBLE);
        results.setVisibility(View.GONE);

    }

    void showResults(){


        srchView.setVisibility(View.GONE);
        noResultView.setVisibility(View.GONE);
        no_res_tv.setVisibility(View.GONE);
        request_book.setVisibility(View.GONE);
        results.setVisibility(View.VISIBLE);


    }

    void showLists(){



        adapter = new BooksListAdapter(this, list);

        results.setAdapter(adapter);



        adapter.notifyDataSetChanged();



        results.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


    if (adapter.getItemCount()<1)
        showNoResults();
    else
        showResults();



    }

    void toastMsg(String s){


        Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTypeface(Cav);
        toast.show();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); //Terminate the current Activity
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        return true;
    }

    public void requestBook(View view) {


        if (mAuth.getCurrentUser()==null){

            toastMsg("Please Login first!!");
            startActivity(new Intent(this, SignInActivity.class));

        }

        else {

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.request_book);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setTitle("Title...");

            Button click = (dialog).findViewById(R.id.request_btn);

            EditText name = (dialog).findViewById(R.id.book_name_request);

            EditText book_details = (dialog).findViewById(R.id.book_details_request);



            Animation shake;
            shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

            click.setOnClickListener(view1 -> {


                if (String.valueOf(name.getText()).length() < 2) {

                    name.startAnimation(shake);
                    toastMsg("Enter Correct Book name.");

                } else if (String.valueOf(book_details.getText()).length() < 2) {

                    book_details.startAnimation(shake);
                    toastMsg("Enter correct details..");
                }
                else {

                    Map<String, Object> data = new HashMap<>();
                    data.put("name", String.valueOf(name.getText()));
                    data.put("details", String.valueOf(book_details.getText()));
                    data.put("user", mAuth.getCurrentUser().getEmail());

                    db.collection("Requests")
                            .add(data)
                            .addOnSuccessListener(documentReference -> toastMsg("Request Success..Thank You"))
                            .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));


                    dialog.dismiss();

                }


            });


            dialog.show();
        }

    }




}
