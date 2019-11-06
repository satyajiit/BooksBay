package com.satyajit.booksbay.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class YouMightLikeAdapter extends RecyclerView.Adapter<YouMightLikeAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<BooksInfoModel> booksModelArrayList;
    Context c;
    Uri u;

    public YouMightLikeAdapter(Context ctx, ArrayList<BooksInfoModel> booksModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.booksModelArrayList = booksModelArrayList;
        c = ctx;
    }

    @Override
    public YouMightLikeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.u_might_like_recycler_item, parent, false);
        YouMightLikeAdapter.MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        //holder.iv.setImageResource(imageModelArrayList.get(position).getImage_drawable());

        //Glide
//                .with(c)
//                .load(imageModelArrayList.get(position).getImage_drawable())
//                .centerCrop()
//                //.placeholder(R.drawable.loading_spinner)
//                .into(holder.iv);

        int[] colors = c.getResources().getIntArray(R.array.books);


        int rand = (int)(Math.random() * colors.length);


        final String temp = booksModelArrayList.get(position).getName();

        final String author = booksModelArrayList.get(position).getAuthor();

        holder.book_image.setBackgroundColor(colors[rand]);

        holder.book_name.setText(temp);

        holder.on_image_book_name.setText(temp);

        holder.on_image_author.setText(author);

        holder.author_name_u_may_like.setText(author);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference().child("Img/"+booksModelArrayList.get(position).getGenre()+"/"+temp+"_"+author+".jpg");

        final String uploader_temp;

        if (!booksModelArrayList.get(position).getUploader().equals("0"))
            uploader_temp = c.getResources().getString(R.string.upload_text)+booksModelArrayList.get(position).getUploader();

        else
            uploader_temp = c.getResources().getString(R.string.upload_text_admin);


        final float r = (booksModelArrayList.get(position).getRating()[0]);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {

            u = uri;


            //if (con!=null)

            Glide.with(c.getApplicationContext())
                    .load(uri)
                    .centerCrop()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                            holder.on_image_book_name.setVisibility(View.GONE);
                            holder.on_image_author.setVisibility(View.GONE);
                            holder.on_image_line.setVisibility(View.GONE);

                            return false;
                        }
                    })
                    .into(holder.book_image);



        });

        holder.book_image.setOnClickListener(view -> {
            Intent i = new Intent(c, AboutBookActivity.class);

            i.putExtra("GENRE", booksModelArrayList.get(position).getGenre());

            i.putExtra("BOOK_NAME", temp);

            i.putExtra("AUTHOR", author);

            i.putExtra("RATING", r);

            String t = String.valueOf(booksModelArrayList.get(position).getRating()[1]);

            i.putExtra("RATING_COUNT", t.substring(0,t.indexOf('.')));

            //Toast.makeText(con, String.valueOf(genresModelArrayList.get(position).getRating()[1]), Toast.LENGTH_SHORT).show();

            i.putExtra("HITS", String.valueOf(booksModelArrayList.get(position).getDownload_count()));

            i.putExtra("UPLOADER", uploader_temp);


            i.putExtra("IMG_NAME", "Img/"+booksModelArrayList.get(position).getGenre()+"/"+temp+"_"+author+".jpg");

            i.putExtra("FILE_LOC", "Genres/"+booksModelArrayList.get(position).getGenre()+"/"+temp+"_"+author+".epub");

            i.putExtra("SENT_FROM", "HOME");

            c.startActivity(i);



            ((AppCompatActivity) c).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });


//        holder.time.setOnClickListener(view -> {
//
//            Intent i = new Intent(c, AboutBookActivity.class);
//            i.putExtra("TITLE", booksModelArrayList.get(position).getName());
//            c.startActivity(i);
//
//            ((AppCompatActivity) c).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//
//        });






    }

    @Override
    public int getItemCount() {
        return booksModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView book_name,on_image_author,on_image_book_name,author_name_u_may_like;
        ImageView iv,book_image;
        View on_image_line;



        public MyViewHolder(View itemView) {
            super(itemView);

            book_name =  itemView.findViewById(R.id.book_name_u_may_like);
            on_image_book_name = itemView.findViewById(R.id.on_image);
            on_image_author = itemView.findViewById(R.id.on_image_author);
            book_image = itemView.findViewById(R.id.img_book);
            author_name_u_may_like = itemView.findViewById(R.id.author_name_u_may_like);
            on_image_line =  itemView.findViewById(R.id.line_img);
           // iv = itemView.findViewById(R.id.iv);

        }

    }
}