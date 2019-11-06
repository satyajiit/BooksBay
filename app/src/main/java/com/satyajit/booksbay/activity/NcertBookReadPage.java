package com.satyajit.booksbay.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.folioreader.Config;
import com.folioreader.Constants;
import com.folioreader.FolioReader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.satyajit.booksbay.R;
import com.satyajit.booksbay.adapters.SingleItemListAdapter;
import com.satyajit.booksbay.adapters_single.ChaptersListNcertAdapter;
import com.satyajit.booksbay.models.CatsLists;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class NcertBookReadPage extends AppCompatActivity {

    ImageView bg_image, img_book;


    File file_local;

    TextView quote_tv, retryError, on_image_name_about, book_name_about, on_image_author;

    ScrollView content;

    int run = 0;

    ImageView imCloud;

    RecyclerView chapters_rec;

    RelativeLayout content2;

    View shim, errorView, line_img;


    String book_name;

    String  img_link_uri, file_loc;

    Button read_btn_ncert;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    ArrayList<CatsLists> list = new ArrayList<>();

    StorageReference storageRef;

    TextView bookmark, removeBookmark;



    Typeface Cav;




    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ncert_book_read_page);

        getDataFromIntent(savedInstanceState);

        setCustomTitlebar();

        initUI();

        setDataToViews();


    }


    private void getDataFromIntent(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {


                book_name = extras.getString("BOOK_NAME");
                img_link_uri = extras.getString("IMG_NAME");
                file_loc = extras.getString("FILE_LOC");


                Log.d("TAG!",file_loc);
                Log.d("TAG!",file_loc);



            }
        }

                // book_name = "Math-Magic";
                //img_link_uri ="NCERT/Class_VI_Mathematics_Mathematics.jpg";
               // file_loc = "NCERT/Class_VI_Mathematics_Mathematics";
    }


    void showContent(){


        content.setVisibility(View.VISIBLE);
        content2.setVisibility(View.VISIBLE);
        shim.setVisibility(View.GONE);



    }


    void initUI(){


        bg_image = findViewById(R.id.bg_info_imagee);


        chapters_rec = findViewById(R.id.chapters_rec);

        quote_tv = findViewById(R.id.quote_info);


        content = findViewById(R.id.content_about);

        content2 = findViewById(R.id.content_about2);

        shim  = findViewById(R.id.shimmer_about);

        errorView  = findViewById(R.id.error_about);

        retryError  = findViewById(R.id.retry_con);


        on_image_name_about = findViewById(R.id.on_image_name_about);

        book_name_about = findViewById(R.id.book_name_about);





        img_book = findViewById(R.id.img_book);

        line_img = findViewById(R.id.line_img);






        removeBookmark = findViewById(R.id.remove_bookmark);

        bookmark = findViewById(R.id.bookmark_btn);

        imCloud = findViewById(R.id.about_ic);



        on_image_author = findViewById(R.id.on_image_author);

        read_btn_ncert = findViewById(R.id.read_btn_ncert);


    }



    void setDataToViews(){



        on_image_name_about.setText(book_name);

        book_name_about.setText(book_name);









        StorageReference storageRef = storage.getReference().child(img_link_uri);


        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {





                Glide.with(getApplicationContext())
                        .load(uri)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {




                                on_image_name_about.setVisibility(View.GONE);


                                line_img.setVisibility(View.GONE);

                                on_image_author.setVisibility(View.GONE);

                                Glide.with(getApplicationContext())
                                        .load(resource)
                                        .centerCrop()
                                        .apply(RequestOptions.bitmapTransform(new BlurTransformation(3, 2)))
                                        .into(bg_image);


                                return false;
                            }
                        })
                        .into(img_book);


                setQuote();


            }
        });




        retryError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do something Again shit
                errorView.setVisibility(View.GONE);
                shim.setVisibility(View.VISIBLE);
                content.setVisibility(View.GONE);
                content2.setVisibility(View.GONE);
                setQuote();
            }
        });





    }



    void setCustomTitlebar(){

        //Set Title bar with Custom Typeface
        TextView tv = new TextView(getApplicationContext());
        Cav = Typeface.createFromAsset(getAssets(),"fonts/cav.ttf");
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        tv.setText(book_name);
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

    void fucked(){

        //Something fucked the system

        errorView.setVisibility(View.VISIBLE);
        shim.setVisibility(View.GONE);


    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }



    void setQuote(){




        db.collection("quotes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {


                        quote_tv.setText("“ "+task.getResult().getDocuments().get((int)(Math.random()*task.getResult().size())).get("text")+" ”");


                  loadToChapters();




                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());

                        fucked();

                    }
                });


    }




    boolean checkIfAlreadyDownloaded(){

        String substring = list.get(0).getName().substring(list.get(0).getName().lastIndexOf('_') + 1, list.get(0).getName().lastIndexOf('/'));


        file_local = new File(getFilesDir()+"/"+list.get(0).getName().substring(0,list.get(0).getName().lastIndexOf('_'))+"/"+ substring +"_"+list.get(0).getName().substring(list.get(0).getName().lastIndexOf("/")+1));

        System.out.println(file_local);

        return file_local.exists();
    }




    void downloadBookFromStorage(){


        toastMsg("Download Started!");


        read_btn_ncert.setEnabled(false);

        storageRef = storage.getReference().child(list.get(0).getName());

        String substring = list.get(0).getName().substring(list.get(0).getName().lastIndexOf('_') + 1, list.get(0).getName().lastIndexOf('/'));

        new File(getFilesDir()+"/"+list.get(0).getName().substring(0,list.get(0).getName().lastIndexOf('_'))).mkdirs();

        File file_local = new File(getFilesDir()+"/"+list.get(0).getName().substring(0,list.get(0).getName().lastIndexOf('_'))+"/"+ substring +"_"+list.get(0).getName().substring(list.get(0).getName().lastIndexOf("/")+1));


        storageRef.getFile(file_local).addOnSuccessListener(taskSnapshot -> {



            toastMsg(getResources().getString(R.string.download_complete));



            showBookmarkedTv();

            read_btn_ncert.setEnabled(true);



            if (run == 1) openBook();


        }).addOnFailureListener(e -> {
            //Dismiss Progress Dialog\\



            toastMsg(getResources().getString(R.string.download_failed));
            read_btn_ncert.setEnabled(true);



        });


    }



    public void downloadBook(View view) {


        view.setEnabled(false);

        if (checkIfAlreadyDownloaded())
            toastMsg(getResources().getString(R.string.already_downloaded));

        else
            downloadBookFromStorage();


    }


    void toastMsg(String s){


        Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        LinearLayout toastLayout = (LinearLayout) toast.getView();
        TextView toastTV = (TextView) toastLayout.getChildAt(0);
        toastTV.setTypeface(Cav);
        toast.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.dmca)
            showDMCA();


        else if (id == R.id.help)
            showHelp();

        return super.onOptionsItemSelected(item);

    }


    void showHelp(){


        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:satyajiit0@gmail.com")));


    }

    void showDMCA(){


        AlertDialog ad = new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.dmca_msg))


                .setPositiveButton(getResources().getString(R.string.open), (dialog, which) ->
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://Satyajiit.xyz/terms.html"))))
                .setNegativeButton(getResources().getString(R.string.close), null)
                .show();

        TextView textView = ad.findViewById(android.R.id.message);

        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/cav.ttf");

        textView.setTypeface(face);





    }


    public void bookMarkBtn(View view) {

        view.setEnabled(false);

        if (!checkIfAlreadyDownloaded())
        downloadBookFromStorage();

        else
            showBookmarkedTv();

    }

    public void removeBookMark(View view) {

        bookmark.setEnabled(true);

        if (checkIfAlreadyDownloaded()) {

            if (deleteBookFromLocal()) {

                toastMsg("Removed!");

                notBookmarked();

                run = 0;



            }

            else toastMsg("ERROR CODE 111"); //Can't delete
        }

        else toastMsg("ERROR CODE 211");  //Not exists

    }

    void showBookmarkedTv(){


        bookmark.setVisibility(View.GONE);
        removeBookmark.setVisibility(View.VISIBLE);

    }

    void notBookmarked(){

        //In case not bookmarked

        bookmark.setVisibility(View.VISIBLE);
        removeBookmark.setVisibility(View.GONE);

    }


    boolean deleteBookFromLocal() {


        run = 0;

        String substring = list.get(0).getName().substring(list.get(0).getName().lastIndexOf('_') + 1, list.get(0).getName().lastIndexOf('/'));


         file_local = new File(getFilesDir()+"/"+list.get(0).getName().substring(0,list.get(0).getName().lastIndexOf('_'))+"/"+ substring +"_"+list.get(0).getName().substring(list.get(0).getName().lastIndexOf("/")+1));


        return file_local.delete();

    }




    public void readBook(View view) {




        if (checkIfAlreadyDownloaded())
            openBook();

        else
        {

            run = 1;
            downloadBookFromStorage();

        }


    }

    void openBook(){

        Config config = new Config()
                .setDirection(Config.Direction.VERTICAL)
                .setFont(Constants.FONT_ANDADA)
                .setFontSize(2)
                .setNightMode(false)
                .setThemeColorRes(R.color.colorPrimary)
                .setShowTts(true);





        String substring = list.get(0).getName().substring(list.get(0).getName().lastIndexOf('_') + 1, list.get(0).getName().lastIndexOf('/'));
        FolioReader.get()
                .setConfig(config, true)
                .openBook(getFilesDir()+"/"+list.get(0).getName().substring(0,list.get(0).getName().lastIndexOf('_'))+"/"+ substring +"_"+list.get(0).getName().substring(list.get(0).getName().lastIndexOf("/")+1));

    }



    void loadToChapters(){


        StorageReference listRef = storage.getReference().child(file_loc);

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {

                        for (StorageReference item : listResult.getItems()) {
                            CatsLists catsListModel = new CatsLists();
                            catsListModel.setName(file_loc+"/"+item.getName());

                            list.add(catsListModel);
                        }

                        showContent();

                        if (checkIfAlreadyDownloaded())
                            showBookmarkedTv();

                        showLists();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       fucked();
                    }
                });


    }

    void showLists(){


        ChaptersListNcertAdapter adapter = new ChaptersListNcertAdapter(this, list);
        chapters_rec.setAdapter(adapter);


        adapter.notifyDataSetChanged();
        chapters_rec.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));




    }



}
