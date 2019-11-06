package com.satyajit.booksbay.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.satyajit.booksbay.R;
import com.satyajit.booksbay.activity.ClassBooksActivity;
import com.satyajit.booksbay.activity.GenreSelectionActivity;
import com.satyajit.booksbay.models.CatsModel;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<CatsModel> imageModelArrayList;
    Context c;

    public CategoryAdapter(Context ctx, ArrayList<CatsModel> imageModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.imageModelArrayList = imageModelArrayList;
        c = ctx;
    }

    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.cats_recycler_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.MyViewHolder holder, int position) {

        //holder.iv.setImageResource(imageModelArrayList.get(position).getImage_drawable());
        if (imageModelArrayList.get(position).getName().contains("Class")||imageModelArrayList.get(position).getName().contains("#")){

            holder.iv.setBackgroundColor(imageModelArrayList.get(position).getImage_drawable());
        }

        else {
            Glide
                    .with(c)
                    .load(imageModelArrayList.get(position).getImage_drawable())
                    .centerCrop()
                    //.placeholder(R.drawable.loading_spinner)
                    .into(holder.iv);
        }

        String t = imageModelArrayList.get(position).getName();

        if (imageModelArrayList.get(position).getName().contains("#"))
            t = t.replace("#","");

        holder.time.setText(t);


        holder.time.setOnClickListener(view -> {

            if (imageModelArrayList.get(position).getName().contains("Class")){

                Intent i = new Intent(c, ClassBooksActivity.class);
                i.putExtra("TITLE", imageModelArrayList.get(position).getName());
                c.startActivity(i);

            }

            else {
                Intent i = new Intent(c, GenreSelectionActivity.class);
                i.putExtra("TITLE", imageModelArrayList.get(position).getName());
                c.startActivity(i);

            }

            ((AppCompatActivity) c).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });






    }

    @Override
    public int getItemCount() {
        return imageModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView time;
        ImageView iv;


        public MyViewHolder(View itemView) {
            super(itemView);

            time =  itemView.findViewById(R.id.tv);
            iv = itemView.findViewById(R.id.iv);

        }

    }
}