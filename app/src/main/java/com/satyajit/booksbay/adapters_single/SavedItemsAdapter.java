package com.satyajit.booksbay.adapters_single;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.folioreader.Config;
import com.folioreader.Constants;
import com.folioreader.FolioReader;
import com.folioreader.model.locators.ReadLocator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.satyajit.booksbay.PdfReadActivity.PdfReadActivity;
import com.satyajit.booksbay.R;
import com.satyajit.booksbay.activity.AboutBookActivity;
import com.satyajit.booksbay.models.BooksInfoModel;

import java.util.ArrayList;

public class SavedItemsAdapter extends RecyclerView.Adapter<SavedItemsAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<BooksInfoModel> booksModelArrayList;
    Context c;
    Uri u;

    public SavedItemsAdapter(Context ctx, ArrayList<BooksInfoModel> booksModelArrayList){

        inflater = LayoutInflater.from(ctx);
        this.booksModelArrayList = booksModelArrayList;
        c = ctx;
    }

    @Override
    public SavedItemsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.bookshelf_item, parent, false);
        SavedItemsAdapter.MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {



        int[] colors = c.getResources().getIntArray(R.array.books);


        int rand = (int)(Math.random() * colors.length);


        final String temp = booksModelArrayList.get(position).getName();


        holder.book_image.setBackgroundColor(colors[rand]);

        holder.book_name.setText(temp);

        holder.on_image_book_name.setText(temp);




        String author = booksModelArrayList.get(position).getAuthor();

        holder.on_image_author_bookshelf.setText(author);

        String genre = booksModelArrayList.get(position).getGenre();

        String loc = booksModelArrayList.get(position).getUploader();

        holder.book_image.setOnClickListener(view -> {



            if (genre.equals("epub")) {


                Config config = new Config()
                        .setDirection(Config.Direction.VERTICAL)
                        .setFont(Constants.FONT_ANDADA)
                        .setFontSize(2)
                        .setNightMode(false)
                        .setThemeColorRes(R.color.colorPrimary)
                        .setShowTts(true);


                SharedPreferences sharedPref = ((AppCompatActivity) c).getPreferences(AboutBookActivity.MODE_PRIVATE);

                if (sharedPref.getString(temp + "_" + author + "_locator", "BLANK").equals("BLANK"))
                    FolioReader.get()
                            .setReadLocatorListener(readLocator -> {
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(temp + "_" + author + "_locator", readLocator.toJson());
                                editor.apply();
                            })
                            .setConfig(config, true)
                            .openBook(loc);
                else
                    FolioReader.get()
                            .setReadLocatorListener(readLocator -> {
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(temp + "_" + author + "_locator", readLocator.toJson());
                                editor.apply();
                            })
                            .setReadLocator(ReadLocator.fromJson(sharedPref.getString(temp + "_" + author + "_locator", "BLANK")))
                            .setConfig(config, true)
                            .openBook(loc);


            } //end of epub books


            else if(genre.equals("pdf")){



                Intent i = new Intent(c, PdfReadActivity.class);

                i.putExtra("FILE",loc);

                AlertDialog ad = new AlertDialog.Builder(c)
                        .setMessage(c.getString(R.string.night_mode))


                        .setPositiveButton(c.getResources().getString(R.string.yes), (dialogInterface, x) -> {
                            i.putExtra("NM",1);
                            c.startActivity(i);

                        })

                        .setNegativeButton(c.getResources().getString(R.string.no), ((dialog, which) ->
                        {
                            c.startActivity(i);

                        }

                        ))
                        .show();

                TextView textView = ad.findViewById(android.R.id.message);

                Typeface face=Typeface.createFromAsset(c.getAssets(),"fonts/cav.ttf");

                textView.setTypeface(face);


            }


            }




        );








    }

    @Override
    public int getItemCount() {
        return booksModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView book_name,on_image_book_name, on_image_author_bookshelf;
        ImageView book_image;




        public MyViewHolder(View itemView) {
            super(itemView);

            book_name =  itemView.findViewById(R.id.book_name_bookshelf);
            on_image_book_name = itemView.findViewById(R.id.on_image_bookshelf);
            on_image_author_bookshelf = itemView.findViewById(R.id.on_image_author_bookshelf);
            book_image = itemView.findViewById(R.id.img_book);

            // iv = itemView.findViewById(R.id.iv);

        }

    }
}