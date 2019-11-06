package com.satyajit.booksbay.adapters_single;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.folioreader.Config;
import com.folioreader.Constants;
import com.folioreader.FolioReader;
import com.folioreader.model.locators.ReadLocator;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.satyajit.booksbay.R;
import com.satyajit.booksbay.activity.GenreSelectionActivity;
import com.satyajit.booksbay.models.CatsLists;

import java.io.File;
import java.util.ArrayList;

public class ChaptersListNcertAdapter extends RecyclerView.Adapter<ChaptersListNcertAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<CatsLists> genresModelArrayList;
    private Context con;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public ChaptersListNcertAdapter(Context ctx, ArrayList<CatsLists> genresModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.genresModelArrayList = genresModelArrayList;
        con = ctx;
    }

    @NonNull
    @Override
    public ChaptersListNcertAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.ncert_chapter_items, parent, false);

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ChaptersListNcertAdapter.MyViewHolder holder, final int position) {



        holder.name.setText("Chapter "+(Integer)(position+1));

        if (checkIfAlreadyDownloaded(genresModelArrayList.get(position).getName())) {
            changeIconColor(holder.im);
            holder.download_progress_about.setProgress(100);

        }

        holder.crd.setOnClickListener(view -> {
//            Intent i = new Intent(con, GenreSelectionActivity.class);
//            i.putExtra("TITLE", genresModelArrayList.get(position).getName());
//            con.startActivity(i);
//
//            ((AppCompatActivity) con).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            view.setEnabled(false);
            readBook(holder.download_progress_about,genresModelArrayList.get(position).getName(),holder.im,view);
        });


    }

    @Override
    public int getItemCount() {
        return genresModelArrayList.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        CardView crd;
        ProgressBar download_progress_about;
        ImageView im;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_tv);
            crd = itemView.findViewById(R.id.card_view);
            download_progress_about = itemView.findViewById(R.id.download_progress_about);
            im = itemView.findViewById(R.id.about_ic);
        }

    }

//    boolean checkIfAlreadyDownloaded(){
//
//        file_local = new File(getFilesDir(), file_loc.substring(file_loc.lastIndexOf("/")));
//
//        return file_local.exists();
//    }

    boolean checkIfAlreadyDownloaded(String file_loc){

        String substring = file_loc.substring(file_loc.lastIndexOf('_') + 1, file_loc.lastIndexOf('/'));
        File file_local = new File(con.getFilesDir()+"/"+file_loc.substring(0,file_loc.lastIndexOf('_'))+"/"+ substring +"_"+file_loc.substring(file_loc.lastIndexOf("/")+1));

        return file_local.exists();
    }

    void openBook(String file_loc){

        Config config = new Config()
                .setFont(Constants.FONT_ANDADA)
                .setFontSize(2)
                .setDirection(Config.Direction.VERTICAL)
                .setNightMode(false)
                .setThemeColorRes(R.color.colorPrimary)
                .setShowTts(true);



        String substring = file_loc.substring(file_loc.lastIndexOf('_') + 1, file_loc.lastIndexOf('/'));





        SharedPreferences sharedPref = ((AppCompatActivity)con).getPreferences(Context.MODE_PRIVATE);


        String temp =con.getFilesDir()+"/"+file_loc.substring(0,file_loc.lastIndexOf('_'))+"/"+
                substring +"_"+file_loc.substring(file_loc.lastIndexOf("/")+1);


        if (sharedPref.getString(substring+"_locator","BLANK").equals("BLANK"))
            FolioReader.get()
                    .setReadLocatorListener(readLocator -> {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        
                        editor.putString(substring+"_locator",readLocator.toJson());
                        editor.apply();
                    })
                    .setConfig(config, true)
                    .openBook(temp);
        else
            FolioReader.get()
                    .setReadLocatorListener(readLocator -> {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(substring+"_locator",readLocator.toJson());
                        editor.apply();
                    })
                    .setReadLocator(ReadLocator.fromJson(sharedPref.getString(substring+"_locator","BLANK")))
                    .setConfig(config, true)
                    .openBook(temp);







        System.out.println(file_loc.substring(0,file_loc.lastIndexOf('_'))+"/"+ substring +"_"+file_loc.substring(file_loc.lastIndexOf("/")+1));

    }


    public void readBook(ProgressBar download_progress_about,String file_loc, ImageView im, View v) {




        if (checkIfAlreadyDownloaded(file_loc)) {
            openBook(file_loc);
            v.setEnabled(true);
        }
        else
        {


            downloadBookFromStorage(download_progress_about, file_loc, im, v);

        }


    }


    void downloadBookFromStorage(ProgressBar download_progress_about,String file_loc, ImageView im, View v){

        download_progress_about.setVisibility(View.VISIBLE);

        Toast.makeText(con, "Download Started!!", Toast.LENGTH_LONG).show();

        Drawable drawable = con.getDrawable(R.drawable.circular_progress_bar);
        download_progress_about.setProgress(2);   // Main Progress
        download_progress_about.setSecondaryProgress(100); // Secondary Progress
        download_progress_about.setMax(100); // Maximum Progress
        download_progress_about.setProgressDrawable(drawable);

        StorageReference storageRef;


        String substring = file_loc.substring(file_loc.lastIndexOf('_') + 1, file_loc.lastIndexOf('/'));

        new File(con.getFilesDir()+"/"+file_loc.substring(0,file_loc.lastIndexOf('_'))).mkdirs();

        File file_local = new File(con.getFilesDir()+"/"+file_loc.substring(0,file_loc.lastIndexOf('_'))+"/"+ substring +"_"+file_loc.substring(file_loc.lastIndexOf("/")+1));



        storageRef = storage.getReference().child(file_loc.replace("Img","Genres").replace("jpg","epub"));

        storageRef.getFile(file_local).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                //downloadBook_rel.setEnabled(true);

                //toastMsg(getResources().getString(R.string.download_complete));

                v.setEnabled(true);


                changeIconColor(im);

                openBook(file_loc);

               // showBookmarkedTv();

               // if (run == 1) openBook();


            }
        }).addOnFailureListener(e -> {
            //Dismiss Progress Dialog\\



            //toastMsg(getResources().getString(R.string.download_failed));


        }).addOnProgressListener(taskSnapshot -> {

            int progress =  (int) ((taskSnapshot.getBytesTransferred()*100) / taskSnapshot.getTotalByteCount());


            download_progress_about.setProgress(progress);

        });


    }

    void changeIconColor(ImageView imCloud){


        Drawable unwrappedDrawable = AppCompatResources.getDrawable(con.getApplicationContext(), R.drawable.ic_cloud_download);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, con.getResources().getColor(R.color.green));



        imCloud.setImageDrawable(wrappedDrawable);

    }


}
