package com.satyajit.booksbay.SignInActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.satyajit.booksbay.R;
import com.satyajit.booksbay.utils.PopUtil;

public class SignInActivity extends AppCompatActivity {


    SignInButton signIn;
    com.facebook.login.widget.LoginButton fb_login;
    CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_sign_in);



        initUI();
        setGooglePlusButtonText();


//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.satyajit.booksbay",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }


        signOut();

    }



    void initUI(){


        signIn = findViewById(R.id.sign_in_google);
        fb_login = findViewById(R.id.sign_in_facebook);
        fb_login.setPermissions("email", "public_profile");
        mAuth = FirebaseAuth.getInstance();


        mCallbackManager = CallbackManager.Factory.create();

        signIn.setOnClickListener(view -> signIn());

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)

                .requestIdToken("YOUR_TOKEN")
                .requestEmail()
                .requestProfile()
               .build();

        // Build a GoogleSignInClient with the options specified by gso.
         mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        fb_login.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("OPERATOR1", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(SignInActivity.this, getString(R.string.fail), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Log.d("OPERATOR3", "facebook:onError", error);
                Toast.makeText(SignInActivity.this, getString(R.string.fail), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 808);
    }


    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });

        LoginManager.getInstance().logOut();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 808) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(SignInActivity.this, getString(R.string.fail), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                // ...
            }
        }

        else
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }



    protected void setGooglePlusButtonText() {
        // Find the TextView that is inside of the SignInButton and set its text



        Typeface Cav = Typeface.createFromAsset(getAssets(),"fonts/cav.ttf");



        for (int i = 0; i < signIn.getChildCount(); i++) {
            View v = signIn.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(getString(R.string.login_with_google));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
                tv.setTypeface(Cav);


                ViewGroup.LayoutParams params = tv.getLayoutParams();

                params.height = 170;
                tv.setLayoutParams(params);

                return;
            }
        }
    }

    public void onClickFacebookButton(View view) {




            fb_login.performClick();



    }

    public void skipBtn(View view) {
        finish();
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TESTING", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(SignInActivity.this, getString(R.string.welcome)+" "+user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            finish();

                            //updateUI(user);
                        } else {
                            Toast.makeText(SignInActivity.this, getString(R.string.fail), Toast.LENGTH_SHORT).show();
                            finish();
                            Log.w("TESTING99", "signInWithCredential:failure", task.getException());

                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("DONE", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information

                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(SignInActivity.this, getString(R.string.welcome)+" "+user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(SignInActivity.this, getString(R.string.fail)+" 989", Toast.LENGTH_SHORT).show();
                        finish();

                    }

                    // ...
                });
    }


    public void terms(View view) {

        new PopUtil(this).show("terms");

    }

    public void disclaimer(View view) {

        new PopUtil(this).show("terms");

    }
}
