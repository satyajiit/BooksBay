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

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewBooks extends Fragment {


    BooksListAdapter adapter;

    private RecyclerView booksRecycler;

    private LinearLayout errorView,noDataView;

    ArrayList<BooksInfoModel> list = new ArrayList<>();

    LoaderPop load;

    String Title;

    TextView retry_con;

    public NewBooks() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_books_single, container, false);

        initUI(v);

        final NoNetworkPop np = new NoNetworkPop(getActivity());

        if (!new CheckNetwork(getActivity()).isNetworkAvailable()) {



            np.show();

            np.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {

                    getFromDB();
                }
            });



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

    void listener(){


        retry_con.setOnClickListener(view -> getFromDB());

    }


    void getFromDB(){


        load.show();
        // Access a Cloud Firestore instance from Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (Title.charAt(0)=='#'){


            //It is a Eng book

            db.collection("ENG BOOKS")
                    .orderBy("created", Query.Direction.DESCENDING)
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

        else {

            //Other Books

            db.collection("books")
                    .orderBy("created", Query.Direction.DESCENDING)
                    .whereEqualTo("genre", Title)
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

        //end of else block
        }

    }




    void initUI(View v){

        Title = ((GenreSelectionActivity)getActivity()).title; //Gets the title location
        booksRecycler = v.findViewById(R.id.books_recycler_list);
        load = new LoaderPop(getActivity());
        errorView = v.findViewById(R.id.error_view);
        noDataView = v.findViewById(R.id.no_data_view);
        retry_con = v.findViewById(R.id.retry_con);
    }

    void showLists(){


         adapter = new BooksListAdapter(getActivity(), list);
        booksRecycler.setAdapter(adapter);



       // sortItems();

        adapter.notifyDataSetChanged();
        booksRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        load.cancel();
        errorView.setVisibility(View.GONE);



    }

    private void checkEmpty() {



            //NO BOOKS show the empty animation
            noDataView.setVisibility(View.VISIBLE);



    }





}
