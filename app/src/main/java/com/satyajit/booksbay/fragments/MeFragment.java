package com.satyajit.booksbay.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.satyajit.booksbay.BuildConfig;
import com.satyajit.booksbay.R;
import com.satyajit.booksbay.SignInActivity.SignInActivity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {


    public static MeFragment newInstance() {
        return new MeFragment();
    }


    LinearLayout share_with_frnds_lin, clicker_love_this_app_crd, logOut_lin, aboutUsLin;

    CardView logOut_crd;

    TextView sign_in_tv;

    private FirebaseAuth mAuth;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    StorageReference storageRef = storage.getReference();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ImageView user_profile_pic;

    HashMap<String,String> backup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment




        View v = inflater.inflate(R.layout.fragment_me, container, false);


        iniUI(v);

       // mAuth.signOut();

        setClickListeners();

        userOptions();

        if (mAuth.getCurrentUser()!=null)
                updateTvAndPic();

    return v;
    }


    void iniUI(View v){


        share_with_frnds_lin = v.findViewById(R.id.share_with_frnds_lin);

        clicker_love_this_app_crd = v.findViewById(R.id.clicker_love_this_app_crd);

        sign_in_tv = v.findViewById(R.id.sign_in_tv);

        user_profile_pic = v.findViewById(R.id.user_profile_pic);

        logOut_crd = v.findViewById(R.id.logOut_crd);

        logOut_lin = v.findViewById(R.id.logOut_lin);

        aboutUsLin = v.findViewById(R.id.aboutUsLin);

        mAuth = FirebaseAuth.getInstance();




    }

    void userOptions(){


        FirebaseAuth.AuthStateListener test = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                updateTvAndPic();


                startSync();
            }

            else
                removeUser();

        };

        mAuth.addAuthStateListener(test);

    }

    private void startSync() {

        // Access a Cloud Firestore instance from your Activity


        //Read from Local
        File directory = new File(getActivity().getFilesDir()+"");
        File[] files = directory.listFiles();
        SharedPreferences sharedPref = getActivity().getSharedPreferences("AboutBookActivity", MODE_PRIVATE);

        // Update one field, creating the document if it does not already exist.
        Map<String, Object> field = new HashMap<>();


        String author, type, name, genre;

        for (int i = 0; i < files.length; i++) {

            String temp = files[i].getName();


            if (temp.contains(".") && temp.contains("_")) {

                Map<String, Object> data = new HashMap<>();

                 author = temp.substring(temp.indexOf('_') + 1, temp.indexOf('.'));
                 type = temp.substring(temp.indexOf('.') + 1);
                 name = temp.substring(0, temp.indexOf('_'));
                 genre =  sharedPref.getString(temp.substring(0, temp.indexOf('_'))+"_"+author+"_link","BLANK");
                 genre = genre.substring(0,genre.indexOf('@'));

                data.put("author",author);
                data.put("type",type);
                data.put("name",name);
                data.put("genre",genre);





                field.put(name+"_"+author, data);

            }

        }








        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .set(field, SetOptions.merge()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                verifyIfFileExist();
            }
            });


    }


    void verifyIfFileExist(){

        backup = new HashMap<>();

        DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("TGG", "DocumentSnapshot data: " + document.getData());

                    HashMap<String, Object> books = (HashMap<String, Object>) document.getData();


                    for (Map.Entry mapElement : books.entrySet()) {


                        HashMap<String, String> book_data = (HashMap<String, String>) mapElement.getValue();

                        File file_loc = new File(getActivity().getFilesDir()+"/"+book_data.get("name")+"_"+book_data.get("author")+"."+book_data.get("type"));

                        String temp;

                        if(book_data.get("type").equalsIgnoreCase("pdf"))
                            temp = "ENG/"+book_data.get("genre")+"/"+book_data.get("name")+"_"+book_data.get("author")+"."+book_data.get("type");
                        else
                            temp = "Genres/"+book_data.get("genre")+"/"+book_data.get("name")+"_"+book_data.get("author")+"."+book_data.get("type");

                        if (!file_loc.exists()) {

                            backup.put(book_data.get("name") + "_" + book_data.get("author") + "." + book_data.get("type"), temp);


                        }



                    }

                    if (backup.size()>0) new AsyncTaskRunner().execute(); //Need to Sync

                } else {
                    Log.d("TGG", "No such document");
                }
            } else {
                Log.d("TGG", "get failed with ", task.getException());
            }
        });


    }



    private void removeUser() {


        logOut_crd.setVisibility(View.GONE);
        sign_in_tv.setText(Objects.requireNonNull(getActivity()).getString(R.string.sign_in));
        user_profile_pic.setImageResource(R.drawable.user_ic);

    }

    void updateTvAndPic(){

        String temp = mAuth.getCurrentUser().getDisplayName();

        if (temp.indexOf(' ')!=-1)
            temp = temp.substring(0,temp.indexOf(' '));

        if (temp.length()>8)
            temp = temp.substring(0,8)+"...";

        sign_in_tv.setText(temp);

        Glide.with(this)
                .load(mAuth.getCurrentUser().getPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(user_profile_pic);


        logOut_crd.setVisibility(View.VISIBLE);

        //sign_in_tv.setTextSize(21);

    }


    public void rateUs(){

        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.rate_us_ps_pop);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);

        Button click = (dialog).findViewById(R.id.click_rate);

        click.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID));
            startActivity(browserIntent);

            dialog.dismiss();
        });




        dialog.show();


    }


    void setClickListeners(){



        share_with_frnds_lin.setOnClickListener(view -> {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getActivity().getString(R.string.app_name));
                String shareMessage= getActivity().getString(R.string.share_msg);
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch(Exception e) {
                //e.toString();
            }
        });

        sign_in_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mAuth.getCurrentUser() == null)
                startActivity(new Intent(getActivity(), SignInActivity.class));

            }
        });


        clicker_love_this_app_crd.setOnClickListener(view -> rateUs());


        logOut_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();

                Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT).show();

            }
        });

        aboutUsLin.setOnClickListener(view -> {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://satyajiit.xyz"));
            startActivity(browserIntent);

        });


    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        Dialog dialog;

        int per = 100/backup.size(), total = 0;

        Button close;

        File f;

        @Override
        protected String doInBackground(String... params) {
             // Calls onProgressUpdate()




            for (Map.Entry mapElement : backup.entrySet()) {

                f = new File(getActivity().getFilesDir()+"/"+mapElement.getKey());

                Log.d("TGG", "get failed with "+ String.valueOf(mapElement.getValue()));

                StorageReference book = storageRef.child(String.valueOf(mapElement.getValue()));

                book.getFile(f).addOnSuccessListener(taskSnapshot -> {

                    SharedPreferences sharedPref = getActivity().getSharedPreferences("AboutBookActivity", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    String s = mapElement.getValue().toString();

                    editor.putString(mapElement.getKey().toString().substring(0,mapElement.getKey().toString().indexOf('.'))+"_link",s.substring(s.indexOf('/'),s.lastIndexOf('/'))+"@MAIN");
                    Log.d("ORANGE",s.substring(s.indexOf('/'),s.lastIndexOf('/'))+"@MAIN");
                    editor.apply();

                    publishProgress("Sleeping...");
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors



                    }
                });


            }


            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation


        }


        @Override
        protected void onPreExecute() {


             dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.sync_pop);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setTitle("Title...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);


            close = dialog.findViewById(R.id.click_close);

            dialog.findViewById(R.id.click_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }


        @Override
        protected void onProgressUpdate(String... text) {
            //finalResult.setText(text[0]);

            TextView tv = (dialog.findViewById(R.id.sync_tv));
            total = total + per;
            tv.setText("Done "+total+"% ...");

            if (total==100)
            close.setText(getString(R.string.close));

        }
    }


}
