package com.satyajit.booksbay.fragments;


import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.satyajit.booksbay.R;
import com.satyajit.booksbay.activity.GenresActivity;
import com.satyajit.booksbay.activity.SearchActivity;
import com.satyajit.booksbay.adapters.CategoryAdapter;
import com.satyajit.booksbay.adapters.ListsAdapterLibPage;
import com.satyajit.booksbay.adapters.YouMightLikeAdapter;
import com.satyajit.booksbay.models.BooksInfoModel;
import com.satyajit.booksbay.models.CatsModel;
import com.satyajit.booksbay.utils.AutoFitGridLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryFragment extends Fragment {




    private RecyclerView catsRecyler, new_arrival_books, ncert_recycler,top_rated_books, high_hits_books, eng_recycler;



    private TypedArray myImageList;
    private String[] myImageNameList;

    private TextView catsTv;

    ArrayList<BooksInfoModel> books = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private LinearLayout icon_prev, icon_prev2,icon_prev3,icon_prev4, icon_prev5, icon_prev6;

    SearchView bookSearch;

    TextView quote_tv;

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    YouMightLikeAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_library, container, false);


        initUI(v);


        setUpCats();

        setClickListener();

        setUpNcert();

    //if (u_may_like_recycler.getAdapter()==null)
          getNewBookss();

             setUpEng();

             setUpSearch();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private ArrayList<CatsModel> addCats(){

        ArrayList<CatsModel> list = new ArrayList<>();

        for(int i = 0; i < myImageNameList.length; i++){
            CatsModel catsModel = new CatsModel();
            catsModel.setName(myImageNameList[i]);
            catsModel.setImage_drawable(myImageList.getResourceId(i,R.drawable.temp));
            list.add(catsModel);
        }

        return list;
    }



    private void initUI(View v){


        catsRecyler = v.findViewById(R.id.cats_recycler);
        myImageNameList = getActivity().getResources().getStringArray(R.array.cats);
        myImageList = getActivity().getResources().obtainTypedArray(R.array.cats_images);





        quote_tv = v.findViewById(R.id.quote_info);

        catsTv = v.findViewById(R.id.more_genres);
        new_arrival_books = v.findViewById(R.id.new_arrival_books);
        icon_prev = v.findViewById(R.id.icon_prev);
        icon_prev2 = v.findViewById(R.id.icon_prev2);
        ncert_recycler = v.findViewById(R.id.ncert_recycler);


        top_rated_books = v.findViewById(R.id.top_rated_books);
        icon_prev3 = v.findViewById(R.id.icon_prev3);
        icon_prev4 = v.findViewById(R.id.icon_prev4);

        high_hits_books = v.findViewById(R.id.high_hits_books);


        icon_prev5 = v.findViewById(R.id.icon_prev5);

        icon_prev6 = v.findViewById(R.id.icon_prev6);


        eng_recycler = v.findViewById(R.id.eng_recycler);

        bookSearch = v.findViewById(R.id.searchView);

    }

    private void setUpCats(){

        //Add categories

         ArrayList<CatsModel> imageModelArrayList = addCats();
        CategoryAdapter adapter = new CategoryAdapter(getActivity(), imageModelArrayList);
        catsRecyler.setAdapter(adapter);

        //catsRecyler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));


        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        catsRecyler.setLayoutManager(staggeredGridLayoutManager);

    }

    void setUpSearch(){

        bookSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {



                Intent i = new Intent(getActivity(), SearchActivity.class);
                i.putExtra("QUERY",s);

                startActivity(i);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }

    private void setClickListener(){

        catsTv.setOnClickListener(view -> {
            getActivity().startActivity(new Intent(getActivity(), GenresActivity.class));
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

    }


    void getNewBookss(){

        //You might like section

        ArrayList<BooksInfoModel> books = new ArrayList<>();


        db.collection("books")
                .limit(8)
                .orderBy("created", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        icon_prev.setVisibility(View.GONE);
                        icon_prev2.setVisibility(View.GONE);
                        new_arrival_books.setVisibility(View.VISIBLE);

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());

                            BooksInfoModel booksInfoModel = new BooksInfoModel();
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
                            booksInfoModel.setGenre(String.valueOf(document.getData().get("genre")));

                            books.add(booksInfoModel);

                        }

                        loadToNewBooksToRecyler(books);

                        getTopBookss();

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });


    }
    void setQuote(){




        db.collection("quotes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {


                        quote_tv.setText("“ "+task.getResult().getDocuments().get((int)(Math.random()*task.getResult().size())).get("text")+" ”");







                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());



                    }
                });


    }


    void loadToNewBooksToRecyler(ArrayList<BooksInfoModel> books){

         adapter = new YouMightLikeAdapter(getActivity(), books);
        new_arrival_books.setAdapter(adapter);
        new_arrival_books.setHasFixedSize(true);
        new_arrival_books.setItemViewCacheSize(8);

        adapter.notifyDataSetChanged();
        new_arrival_books.setLayoutManager(new AutoFitGridLayoutManager(getActivity(),250));


    }

    private void setUpNcert(){

        //Add categories

        String[] classNameList = getActivity().getResources().getStringArray(R.array.class_ncert);
        ArrayList<CatsModel> imageModelArrayList = addCls(classNameList);
        CategoryAdapter adapter = new CategoryAdapter(getActivity(), imageModelArrayList);
        ncert_recycler.setAdapter(adapter);



        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        ncert_recycler.setLayoutManager(staggeredGridLayoutManager);

    }


    private void setUpEng(){

        //Add categories

        String[] classNameList = getActivity().getResources().getStringArray(R.array.eng_books);




        ArrayList<CatsModel> imageModelArrayList = addCls(classNameList);

        CategoryAdapter adapter = new CategoryAdapter(getActivity(), imageModelArrayList);

        eng_recycler.setAdapter(adapter);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);

        eng_recycler.setLayoutManager(staggeredGridLayoutManager);

    }




    private ArrayList<CatsModel> addCls(String[] classNameList){

        ArrayList<CatsModel> list = new ArrayList<>();

        List<Integer> colorsAr = new ArrayList(classNameList.length);

        int[] x = getActivity().getResources().getIntArray(R.array.books);

        for (int i = 0; i < classNameList.length; i++)
            colorsAr.add(x[i]);

        Collections.shuffle(colorsAr);

        for(int i = 0; i < classNameList.length; i++){
            CatsModel catsModel = new CatsModel();
            catsModel.setName(classNameList[i]);
            catsModel.setImage_drawable(colorsAr.get(i));
            if (classNameList.length==4)
            Log.d("TAT",colorsAr.get(i).toString());
            list.add(catsModel);
        }

        return list;
    }

    void getTopBookss(){

        //RATEed




        db.collection("books")
                .limit(8)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        icon_prev3.setVisibility(View.GONE);
                        icon_prev4.setVisibility(View.GONE);
                        top_rated_books.setVisibility(View.VISIBLE);

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());

                            BooksInfoModel booksInfoModel = new BooksInfoModel();
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
                            booksInfoModel.setGenre(String.valueOf(document.getData().get("genre")));

                            books.add(booksInfoModel);

                        }


                        getBooksHits();
                        loadTopRatedBooksRecyler();

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });


    }


    void getBooksHits(){

        //You might like section

        ArrayList<BooksInfoModel> books = new ArrayList<>();


        db.collection("books")
                .limit(8)
                .orderBy("hits", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        icon_prev5.setVisibility(View.GONE);
                        icon_prev6.setVisibility(View.GONE);
                        high_hits_books.setVisibility(View.VISIBLE);

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());

                            BooksInfoModel booksInfoModel = new BooksInfoModel();
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
                            booksInfoModel.setGenre(String.valueOf(document.getData().get("genre")));

                            books.add(booksInfoModel);

                        }

                        loadTopHitsBooksRecyler(books);

                        setQuote();

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });


    }

    void loadTopHitsBooksRecyler(ArrayList<BooksInfoModel> books){

        ListsAdapterLibPage adapter = new ListsAdapterLibPage(getActivity(), books);
        high_hits_books.setAdapter(adapter);
        high_hits_books.setHasFixedSize(true);
        high_hits_books.setItemViewCacheSize(8);
        adapter.notifyDataSetChanged();
        high_hits_books.setLayoutManager(new AutoFitGridLayoutManager(getActivity(),500));


    }



    void loadTopRatedBooksRecyler(){

        ListsAdapterLibPage adapter = new ListsAdapterLibPage(getActivity(), books);
        top_rated_books.setAdapter(adapter);
        top_rated_books.setHasFixedSize(true);
        top_rated_books.setItemViewCacheSize(8);
        sortItems();
        adapter.notifyDataSetChanged();
        top_rated_books.setLayoutManager(new AutoFitGridLayoutManager(getActivity(),500));


    }

    void sortItems(){

        //Sort the items alphabetically

        Collections.sort(books, (lhs, rhs) -> {

            Log.d("TESTING", String.valueOf(Long.compare(rhs.getTime(),lhs.getTime())));

            return Float.compare(rhs.getRating()[0],lhs.getRating()[0]);
        });

    }


}
