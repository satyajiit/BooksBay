package com.satyajit.booksbay.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.satyajit.booksbay.R;
import com.satyajit.booksbay.activity.AboutBookActivity;
import com.satyajit.booksbay.adapters.YouMightLikeAdapter;
import com.satyajit.booksbay.adapters_single.SavedItemsAdapter;
import com.satyajit.booksbay.models.BooksInfoModel;
import com.satyajit.booksbay.utils.AutoFitGridLayoutManager;

import java.io.File;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookSelfFragment extends Fragment {

    RecyclerView savedItems;

    ArrayList<BooksInfoModel> books = new ArrayList<>();

    public static BookSelfFragment newInstance() {
        return new BookSelfFragment();
    }

    LottieAnimationView data_la;

    TextView text2;

    Button refresh;

    SwipeRefreshLayout swipe_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_fragment_book_self, container, false);

        initUI(v);

        getStoredBooks();


        return v;
    }


    void initUI(View v) {

        savedItems = v.findViewById(R.id.items_stored);
        text2 = v.findViewById(R.id.text2);
        data_la = v.findViewById(R.id.data_la);
        refresh = v.findViewById(R.id.refresh);
        swipe_layout = v.findViewById(R.id.swipe_layout);

        swipe_layout.setOnRefreshListener(() -> getStoredBooks());


        refresh.setOnClickListener(view -> getStoredBooks());


    }

    void getStoredBooks(){



        books = new ArrayList<>();



        File directory = new File(getActivity().getFilesDir()+"");
        File[] files = directory.listFiles();


        for (int i = 0; i < files.length; i++)
        {

            String temp = files[i].getName();



            if (temp.contains(".")&&temp.contains("_")) {
                BooksInfoModel booksInfoModel = new BooksInfoModel();

                booksInfoModel.setName(temp.substring(0, temp.indexOf('_')));

               // Log.d("TGG", "document "+temp);

                String author = temp.substring(temp.indexOf('_') + 1, temp.indexOf('.'));

                booksInfoModel.setAuthor(author);

                booksInfoModel.setGenre(temp.substring(temp.indexOf('.') + 1));


                booksInfoModel.setUploader(getActivity().getFilesDir()+"/"+temp);
                books.add(booksInfoModel);

            }


        }

        loadToRecycler();
        swipe_layout.setRefreshing(false);


    }


    void showEmpty(){

        text2.setVisibility(View.VISIBLE);
        data_la.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.VISIBLE);

    }


    void hideEmpty(){

        text2.setVisibility(View.GONE);
        data_la.setVisibility(View.GONE);
        refresh.setVisibility(View.GONE);

    }


    void loadToRecycler(){




        SavedItemsAdapter adapter = new SavedItemsAdapter(getActivity(), books);
        savedItems.setAdapter(adapter);
        savedItems.setHasFixedSize(true);
        savedItems.setItemViewCacheSize(8);

        if (adapter.getItemCount()==0)
            showEmpty();
        else
            hideEmpty();


        adapter.notifyDataSetChanged();
        savedItems.setLayoutManager(new AutoFitGridLayoutManager(getActivity(),333));

    }


}
