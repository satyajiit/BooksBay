package com.satyajit.booksbay.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.satyajit.booksbay.R;
import com.satyajit.booksbay.activity.AboutBookActivity;
import com.satyajit.booksbay.models.BooksInfoModel;

import java.util.ArrayList;
import java.util.Locale;

public class ListsAdapterLibPage extends RecyclerView.Adapter<ListsAdapterLibPage.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<BooksInfoModel> genresModelArrayList;
    private Context con;

    Uri u;

    public ListsAdapterLibPage(Context ctx, ArrayList<BooksInfoModel> genresModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.genresModelArrayList = genresModelArrayList;
        con = ctx;
    }

    @NonNull
    @Override
    public ListsAdapterLibPage.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.lib_screen_single_item, parent, false);

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ListsAdapterLibPage.MyViewHolder holder, final int position) {


        final String temp = genresModelArrayList.get(position).getName();

          final String author = genresModelArrayList.get(position).getAuthor();

        holder.name.setText(temp);

        holder.on_iamge.setText(temp);

        holder.author.setText(author);

        holder.on_image_auth.setText(author);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference().child("Img/"+genresModelArrayList.get(position).getGenre()+"/"+temp+"_"+author+".jpg");

        final String uploader_temp;



        if (!genresModelArrayList.get(position).getUploader().equals("0"))
            uploader_temp = con.getResources().getString(R.string.upload_text)+genresModelArrayList.get(position).getUploader();

        else
            uploader_temp = con.getResources().getString(R.string.upload_text_admin);



        final float r = (genresModelArrayList.get(position).getRating()[0]);

        holder.rate.setRating(r);
        holder.rate_tv.setText(String.format(Locale.US,"%.1f",r));

        int[] colors = con.getResources().getIntArray(R.array.books);


        int rand = (int)(Math.random() * colors.length);

        holder.book_image.setBackgroundColor(colors[rand]);



        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {

            u = uri;


            //if (con!=null)

            Glide.with(con.getApplicationContext())
                    .load(uri)
                    .centerCrop()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            holder.on_iamge.setVisibility(View.GONE);
                            holder.on_image_auth.setVisibility(View.GONE);
                            holder.on_image_line.setVisibility(View.GONE);

                            return false;
                        }
                    })
                    .into(holder.book_image);



        });



        holder.crd.setOnClickListener(view -> {


            Intent i = new Intent(con, AboutBookActivity.class);

            i.putExtra("GENRE", genresModelArrayList.get(position).getGenre());

            i.putExtra("BOOK_NAME", temp);

            i.putExtra("AUTHOR", author);

            i.putExtra("RATING", r);

            String t = String.valueOf(genresModelArrayList.get(position).getRating()[1]);

            i.putExtra("RATING_COUNT", t.substring(0,t.indexOf('.')));

            //Toast.makeText(con, String.valueOf(genresModelArrayList.get(position).getRating()[1]), Toast.LENGTH_SHORT).show();

            i.putExtra("HITS", String.valueOf(genresModelArrayList.get(position).getDownload_count()));

            i.putExtra("UPLOADER", uploader_temp);


            i.putExtra("IMG_NAME", "Img/"+genresModelArrayList.get(position).getGenre()+"/"+temp+"_"+author+".jpg");

            i.putExtra("FILE_LOC", "Genres/"+genresModelArrayList.get(position).getGenre()+"/"+temp+"_"+author+".epub");

            i.putExtra("SENT_FROM", "HOME");

            con.startActivity(i);



            ((AppCompatActivity) con).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);



        });


    }

    @Override
    public int getItemCount() {
        return genresModelArrayList.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name,author, on_iamge, on_image_auth, rate_tv;
        RelativeLayout crd;
        RatingBar rate;
        ImageView book_image;
        View on_image_line;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_tv);
            book_image = itemView.findViewById(R.id.img_book);
            author = itemView.findViewById(R.id.author);
            on_iamge = itemView.findViewById(R.id.on_image);
            on_image_auth = itemView.findViewById(R.id.on_image_author);
            on_image_line = itemView.findViewById(R.id.line_img);
            rate = itemView.findViewById(R.id.ratingBar);
            crd = itemView.findViewById(R.id.crd);
            rate_tv = itemView.findViewById(R.id.rating_tv);

        }

    }

}
