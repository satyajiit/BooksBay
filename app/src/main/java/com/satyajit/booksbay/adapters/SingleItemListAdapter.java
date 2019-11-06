package com.satyajit.booksbay.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.satyajit.booksbay.R;

import com.satyajit.booksbay.activity.GenreSelectionActivity;
import com.satyajit.booksbay.models.CatsLists;

import java.util.ArrayList;

public class SingleItemListAdapter extends RecyclerView.Adapter<SingleItemListAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<CatsLists> genresModelArrayList;
    private Context con;

    public SingleItemListAdapter(Context ctx, ArrayList<CatsLists> genresModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.genresModelArrayList = genresModelArrayList;
        con = ctx;
    }

    @NonNull
    @Override
    public SingleItemListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.more_cats_recycler_item, parent, false);

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull SingleItemListAdapter.MyViewHolder holder, final int position) {



        holder.name.setText(genresModelArrayList.get(position).getName());

        holder.crd.setOnClickListener(view -> {
            Intent i = new Intent(con, GenreSelectionActivity.class);
            i.putExtra("TITLE", genresModelArrayList.get(position).getName());
            con.startActivity(i);

            ((AppCompatActivity) con).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });


    }

    @Override
    public int getItemCount() {
        return genresModelArrayList.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        CardView crd;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_tv);
            crd = itemView.findViewById(R.id.card_view);
        }

    }

}
