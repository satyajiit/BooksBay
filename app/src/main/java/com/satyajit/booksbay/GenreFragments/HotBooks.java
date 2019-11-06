package com.satyajit.booksbay.GenreFragments;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.satyajit.booksbay.R;
import com.satyajit.booksbay.activity.GenreSelectionActivity;
import com.satyajit.booksbay.adapters.BooksListAdapter;
import com.satyajit.booksbay.models.BooksInfoModel;
import com.satyajit.booksbay.utils.CheckNetwork;
import com.satyajit.booksbay.utils.LoaderPop;
import com.satyajit.booksbay.utils.NoNetworkPop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class HotBooks extends Fragment {


    BooksListAdapter adapter;

    private RecyclerView booksRecycler;

    private LinearLayout errorView,noDataView;

    ArrayList<BooksInfoModel> list = new ArrayList<>();

    LoaderPop load;

    String Title;

    TextView retry_con;


    public HotBooks() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_books_single, container, false);

        initUI(v);


        if (!new CheckNetwork(getActivity()).isNetworkAvailable()) {


            NoNetworkPop np = new NoNetworkPop(getActivity());

            if (np.getDialog()!=null) {
                if (!np.getDialog().isShowing())
                    np.show();

                np.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        getFromDB();
                    }
                });
            }


        }
        else{

            if (adapter==null)
                getFromDB();
            else
                loadExistingData();

        }


        listener();

        return v;
    }


    void loadExistingData(){

        booksRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        booksRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));


        if (adapter.getItemCount()<1) checkEmpty();

    }


    void getFromDB(){

        if (load.getDialog()!=null)
            if (!load.getDialog().isShowing())
                load.show();

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        if (Title.charAt(0)=='#'){


            //It is a Eng book

            db.collection("ENG BOOKS")
                    .orderBy("hits", Query.Direction.DESCENDING)
                    .whereEqualTo("genre", Title.substring(1))
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            if (task.getResult().isEmpty()) { //Got No files

                                if (load.getDialog() != null)
                                    load.cancel();
                                checkEmpty();

                            }

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData().get("author"));


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

                                booksInfoModel.setGenre(Title);


                                list.add(booksInfoModel);

                            }

                            showLists();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            load.cancel();
                            errorView.setVisibility(View.VISIBLE);
                        }
                    });



        }

        else

            db.collection("books")
                .whereEqualTo("genre",Title)
                .orderBy("hits", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {


                        if (task.getResult().isEmpty()) { //Got No files

                            if (load.getDialog()!=null)
                            load.cancel();
                            checkEmpty();

                        }

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData().get("author"));


                            final BooksInfoModel booksInfoModel = new BooksInfoModel();
                            booksInfoModel.setName(String.valueOf(document.getData().get("name")));
                            booksInfoModel.setTime((long)document.getData().get("created"));
                            booksInfoModel.setDownload_count((long)document.getData().get("hits"));
                            booksInfoModel.setAuthor(String.valueOf(document.getData().get("author")));
                            booksInfoModel.setUploader(String.valueOf(document.getData().get("uploader")));


                            long[] stars = new long[5];
                            stars[0] = (long)document.getData().get("1 STAR");
                            stars[1] =  (long)document.getData().get("2 STAR");
                            stars[2] = (long)document.getData().get("3 STAR");
                            stars[3] =  (long)document.getData().get("4 STAR");
                            stars[4] = (long)document.getData().get("5 STAR");

                            booksInfoModel.setStars(stars);

                            booksInfoModel.setGenre(Title);


                            list.add(booksInfoModel);

                        }

                        showLists();

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());

                        if (load.getDialog()!=null)
                            if (load.getDialog().isShowing())
                                load.cancel();
                        errorView.setVisibility(View.VISIBLE);
                    }
                });


    }



    void initUI(View v){

        Title = ((GenreSelectionActivity)getActivity()).title; //Gets the title location
        booksRecycler = v.findViewById(R.id.books_recycler_list);
        load = new LoaderPop(getActivity());
        errorView = v.findViewById(R.id.error_view);
        noDataView = v.findViewById(R.id.no_data_view);
        retry_con = v.findViewById(R.id.retry_con);
    }

    void listener(){


        retry_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load.show();
                getFromDB();
            }
        });

    }


    void showLists(){


        //if (getActivity()!=null) {
            adapter = new BooksListAdapter(getActivity(), list);

            booksRecycler.setAdapter(adapter);

            sortItems();

            adapter.notifyDataSetChanged();

        //}

        booksRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        if (load.getDialog()!=null)
            if (load.getDialog().isShowing())
                load.cancel();

        errorView.setVisibility(View.GONE);



    }

    private void checkEmpty() {



        //NO BOOKS show the empty animation
        noDataView.setVisibility(View.VISIBLE);



    }

    void sortItems(){

        //Sort the items alphabetically

        Collections.sort(list, (lhs, rhs) -> {

            Log.d("TESTING", String.valueOf(Long.compare(rhs.getTime(),lhs.getTime())));

            return Float.compare(rhs.getRating()[0],lhs.getRating()[0]);
        });

    }






}
