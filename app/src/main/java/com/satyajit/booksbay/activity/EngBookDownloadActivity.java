package com.satyajit.booksbay.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.satyajit.booksbay.PdfReadActivity.PdfReadActivity;
import com.satyajit.booksbay.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class EngBookDownloadActivity extends AppCompatActivity {
    ImageView bg_image, img_book;

    RatingBar ratingBar;

    File file_local;

    Drawable image;

    TextView quote_tv, intro_tv_info, retryError, size_file_about, ratingTv, on_img_author, on_image_name_about, book_name_about, author_click_from_info, hits_about, genre_about;

    ScrollView content;

    int run = 0;

    ImageView imCloud;

    CardView genre_select_about;

    RelativeLayout content2;

    View shim, errorView, line_img;

    ProgressBar download_progress_about;

    String book_name, uploader, hits, intro, author, genre, sent_from;

    String rating_count, img_link_uri, file_loc;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    StorageReference storageRef;

    TextView bookmark, removeBookmark;

    RelativeLayout downloadBook_rel;

    Button read_btn_page;

    Typeface Cav;



    float rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_book);

        getDataFromIntent(savedInstanceState);

        setCustomTitlebar();

        initUI();

        setDataToViews();

        setQuote();
    }


    private void getDataFromIntent(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {


                book_name = extras.getString("BOOK_NAME");
                author = extras.getString("AUTHOR");
                genre = extras.getString("GENRE");
                hits = extras.getString("HITS");
                uploader = extras.getString("UPLOADER");
                rating = extras.getFloat("RATING");
                img_link_uri = extras.getString("IMG_NAME");
                rating_count = extras.getString("RATING_COUNT");
                file_loc = extras.getString("FILE_LOC");


                sent_from = extras.getString("SENT_FROM","NO");




            }
        }
    }


    void showContent(){


        content.setVisibility(View.VISIBLE);
        content2.setVisibility(View.VISIBLE);
        shim.setVisibility(View.GONE);

        if (checkIfAlreadyDownloaded()) {
            download_progress_about.setProgress(100);
            changeIconColor();
            showBookmarkedTv();
        }

    }


    void initUI(){


        bg_image = findViewById(R.id.bg_info_imagee);


        ratingBar = findViewById(R.id.rating_about_book);


        quote_tv = findViewById(R.id.quote_info);


        content = findViewById(R.id.content_about);

        content2 = findViewById(R.id.content_about2);

        shim  = findViewById(R.id.shimmer_about);

        errorView  = findViewById(R.id.error_about);

        retryError  = findViewById(R.id.retry_con);

        on_img_author = findViewById(R.id.on_image_author);

        ratingTv = findViewById(R.id.rating_tv);

        on_image_name_about = findViewById(R.id.on_image_name_about);

        book_name_about = findViewById(R.id.book_name_about);

        author_click_from_info = findViewById(R.id.author_click_from_info);

        hits_about = findViewById(R.id.hits_about);

        download_progress_about = findViewById(R.id.download_progress_about);



        img_book = findViewById(R.id.img_book);

        line_img = findViewById(R.id.line_img);

        genre_about = findViewById(R.id.genre_about);

        size_file_about =  findViewById(R.id.size_file_about);

        intro_tv_info = findViewById(R.id.intro_tv_info);

        genre_select_about = findViewById(R.id.genre_select_about);

        removeBookmark = findViewById(R.id.remove_bookmark);

        bookmark = findViewById(R.id.bookmark_btn);

        imCloud = findViewById(R.id.about_ic);

        downloadBook_rel = findViewById(R.id.rel_download);

        read_btn_page = findViewById(R.id.read_btn_page);


    }



    void setDataToViews(){

        ratingBar.setRating(rating);


        on_img_author.setText(author);

        ratingTv.setText(rating_count+" "+getResources().getString(R.string.ratings));

        on_image_name_about.setText(book_name);

        book_name_about.setText(book_name);

        hits_about.setText(hits);



        author_click_from_info.setText(author+" >>");

        genre_about.setText(genre);



        intro_tv_info.setOnClickListener(view -> intro_tv_info.setText(intro));



        StorageReference storageRef = storage.getReference().child(img_link_uri);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(getApplicationContext())
                .load(uri)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {


                        image = resource;

                        on_image_name_about.setVisibility(View.GONE);
                        on_img_author.setVisibility(View.GONE);

                        line_img.setVisibility(View.GONE);

                        Glide.with(getApplicationContext())
                                .load(resource)
                                .centerCrop()
                                .apply(RequestOptions.bitmapTransform(new BlurTransformation(3, 2)))
                                .into(bg_image);


                        return false;
                    }
                })
                .into(img_book));




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


        genre_select_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sent_from.equals("GENRE_SECTION")) finish();
                else {
                    Intent i = new Intent(getApplicationContext(), GenreSelectionActivity.class);
                    i.putExtra("TITLE", genre);
                    startActivity(i);

                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

            }
        });


    }



    void getFromDB(){



        DocumentReference docRef = db.collection("ENG BOOKS").document(book_name+"_"+author);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                    intro = String.valueOf(document.getData().get("intro")).trim();


                    StringBuffer temp=new StringBuffer();

                    for (int i=0; i<intro.length(); i++){

                        if (intro.charAt(i)=='.'||intro.charAt(i)=='?') {
                            temp.append(intro.charAt(i));
                            temp.append("\n");
                            if ((i+1)<intro.length())
                                if (intro.charAt(i+1)==' ') i++;

                            if ((i+2)<intro.length())
                                if (intro.charAt(i+2)==' ') i++;
                        }

                        else temp.append(intro.charAt(i));
                    }

                    intro = temp.toString().trim();

                    if (intro.length()>160)
                        intro_tv_info.setText(intro.substring(0,160)+getResources().getString(R.string.more_text_intro));
                    else intro_tv_info.setText(intro);

                    if (intro==null||intro.length()<5)
                        intro_tv_info.setText(getResources().getString(R.string.not_avail));

                    showContent();

                } else {
                    fucked();
                }
            } else {
                fucked();
            }
        });



    }


    void setCustomTitlebar(){

        //Set Title bar with Custom Typeface
        TextView tv = new TextView(getApplicationContext());
        Cav = Typeface.createFromAsset(getAssets(),"fonts/cav.ttf");
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);

        if (book_name.length()>18)
            tv.setText(book_name.substring(0,18)+"...");

        else
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

    void fetchFromStorage(){

        storageRef = storage.getReference().child(file_loc);
        storageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {


                float t = (float)storageMetadata.getSizeBytes()/1048576;



                //Toast.makeText(AboutBookActivity.this, storageMetadata.getPath(), Toast.LENGTH_LONG).show();



                size_file_about.setText(String.format(Locale.US,"%.1f",t));

                getFromDB();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                fucked();
            }
        });




    }

    void setQuote(){




        db.collection("quotes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {


                        quote_tv.setText("“ "+task.getResult().getDocuments().get((int)(Math.random()*task.getResult().size())).get("text")+" ”");


                        fetchFromStorage();




                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());

                        fucked();

                    }
                });


    }


    public void startAuthorActivity(View view) {

        Intent i = new Intent(this, AuthorActivity.class);

        i.putExtra("AUTHOR", author);

        i.putExtra("FROM","ENG");

        startActivity(i);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


    }

    void increaseHits(){


        DocumentReference upd = db.collection("books").document(book_name+"_"+author);
        upd
                .update("hits", (Long.parseLong(hits))+1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

    }



    public void RateBook(View v){

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        if (sharedPref.getBoolean(book_name+"_"+author,false))
            toastMsg(getString(R.string.already_rated));

        else {

            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.rate_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setTitle("Title...");

            RatingBar r = (dialog).findViewById(R.id.rate_bar);
            Button click = (dialog).findViewById(R.id.rate_btn);

            r.setRating(1);


            SharedPreferences.Editor editor = sharedPref.edit();


            click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String key = "";
                    long temp = 0;

                    switch ((int) r.getRating()) {

                        case 1:
                            key = "1 STAR";

                            break;
                        case 2:
                            key = "2 STAR";

                            break;
                        case 3:
                            key = "3 STAR";

                            break;
                        case 4:
                            key = "4 STAR";

                            break;
                        case 5:
                            key = "5 STAR";

                            break;


                    }

                    DocumentReference upd = db.collection("ENG BOOKS").document(book_name + "_" + author);
                    upd
                            .update(key, FieldValue.increment(1))
                            .addOnSuccessListener(aVoid -> {

                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                toastMsg("Thank you for the rating!!");

                                editor.putBoolean(book_name + "_" + author, true);
                                editor.apply();

                            })
                            .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));


                    dialog.dismiss();


                }

            });


            dialog.show();

        }

    }




    boolean checkIfAlreadyDownloaded(){

        file_local = new File(getFilesDir(), file_loc.substring(file_loc.lastIndexOf("/")));

        return file_local.exists();
    }




    void downloadBookFromStorage(){

        toastMsg(getString(R.string.download_started));

        read_btn_page.setEnabled(false);

        download_progress_about.setVisibility(View.VISIBLE);

        Bitmap bitmap = ((BitmapDrawable)image).getBitmap();

        Drawable drawable = getDrawable(R.drawable.circular_progress_bar);
        download_progress_about.setProgress(2);   // Main Progress
        download_progress_about.setSecondaryProgress(100); // Secondary Progress
        download_progress_about.setMax(100); // Maximum Progress
        download_progress_about.setProgressDrawable(drawable);



        file_local = new File(getFilesDir(), file_loc.substring(file_loc.lastIndexOf("/")));

        File file = new File(getFilesDir(),file_loc.substring(file_loc.lastIndexOf("/")));


        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        storageRef = storage.getReference().child(file_loc.replace("Img","Genres").replace("jpg","epub"));

        storageRef.getFile(file_local).addOnSuccessListener(taskSnapshot -> {

            downloadBook_rel.setEnabled(true);

            read_btn_page.setEnabled(true);

            toastMsg(getResources().getString(R.string.download_complete));

            changeIconColor();

            showBookmarkedTv();

            increaseHits();

            if (run == 1) openBook();


        }).addOnFailureListener(e -> {
            //Dismiss Progress Dialog\\



            toastMsg(getResources().getString(R.string.download_failed));


        }).addOnProgressListener(taskSnapshot -> {

            int progress =  (int) ((taskSnapshot.getBytesTransferred()*100) / taskSnapshot.getTotalByteCount());


            download_progress_about.setProgress(progress);

        });


    }


    void changeIconColor(){


        Drawable unwrappedDrawable = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_cloud_download);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.green));



        imCloud.setImageDrawable(wrappedDrawable);

    }

    public void downloadBook(View view) {



        downloadBook_rel.setEnabled(false);

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





    }

    void showDMCA(){


        AlertDialog ad = new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.dmca_msg))


                .setPositiveButton(getResources().getString(R.string.open), (dialog, which) ->
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://Satyajiit.xyz"))))
                .setNegativeButton(getResources().getString(R.string.close), null)
                .show();

        TextView textView = ad.findViewById(android.R.id.message);

        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/cav.ttf");

        textView.setTypeface(face);





    }


    public void bookMarkBtn(View view) {

        if (read_btn_page.isEnabled()) {
            view.setEnabled(false);
            downloadBookFromStorage();
        }

    }

    public void removeBookMark(View view) {

        bookmark.setEnabled(true);

        if (checkIfAlreadyDownloaded()) {

            if (deleteBookFromLocal()) {

                toastMsg("Removed!");

                notBookmarked();

                run = 0;

                download_progress_about.setProgress(0);

                resetIcon();

            }

            else toastMsg("ERROR CODE 111"); //Can't delete
        }

        else toastMsg("ERROR CODE 211");  //Not exists

    }

    void showBookmarkedTv(){


        bookmark.setVisibility(View.GONE);
        removeBookmark.setVisibility(View.VISIBLE);

        SharedPreferences sharedPref = getSharedPreferences("AboutBookActivity",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(book_name+"_"+author+"_link",genre+"@MAIN");
        Log.d("ORANGE",book_name+"_"+author+"_link");
        editor.apply();

    }

    void notBookmarked(){

        //In case not bookmarked

        bookmark.setVisibility(View.VISIBLE);
        removeBookmark.setVisibility(View.GONE);

    }


    boolean deleteBookFromLocal() {


        run = 0;
        return file_local.delete();

    }


    void resetIcon(){



        imCloud.setImageDrawable(getDrawable(R.drawable.ic_cloud_download));


    }

    public void readBook(View view) {




        if (checkIfAlreadyDownloaded())
            openBook();

        else
        {
            downloadBook_rel.setEnabled(false);
            run = 1;
            downloadBookFromStorage();

        }


    }

    void openBook(){






        Intent i = new Intent(this, PdfReadActivity.class);
        i.putExtra("FILE",getFilesDir()+file_loc.substring(file_loc.lastIndexOf("/")));

        AlertDialog ad = new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.night_mode))


                .setPositiveButton(getResources().getString(R.string.yes), (dialogInterface, x) -> {
                    i.putExtra("NM",1);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                })

                .setNegativeButton(getResources().getString(R.string.no), ((dialog, which) ->
                {
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

                ))
                .show();

        TextView textView = ad.findViewById(android.R.id.message);

        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/cav.ttf");

        textView.setTypeface(face);






    }






}
