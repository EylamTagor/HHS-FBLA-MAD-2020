package com.hhsfbla.mad.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hhsfbla.mad.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Represents the first page users see when they open the app; authenticates users with google and facebook sign in methods
 */
public class LoginActivity extends AppCompatActivity {
    private SignInButton loginGoogleBtn;
    private LoginButton loginFacebookBtn;
    private FirebaseUser fuser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private CallbackManager mCallbackManager;
    private static final String TAG = "LOGINACTIVITY";
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog progressDialog;

    private static final int GOOGLE_SIGN_IN = 123;

    /**
     * Creates the page and initializes all page components, such as textviews, image views, buttons, and dialogs,
     *
     * @param savedInstanceState the save state of the activity or page
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        mCallbackManager = CallbackManager.Factory.create();
        if (fuser != null)
            addUser();

        setTitle("Login");
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loginGoogleBtn = findViewById(R.id.loginGoogleBtn);
        loginGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        setGooglePlusButtonProperties();

//        loginFacebookBtn = findViewById(R.id.loginFacebookBtn);
//
//        loginFacebookBtn.setReadPermissions(Arrays.asList("email", "public_profile"));
//        loginFacebookBtn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                progressDialog.setMessage("Loading...");
//                progressDialog.show();
//                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Toast.makeText(LoginActivity.this, "Facebook Login Failed", Toast.LENGTH_SHORT).show();
//            }
//        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        mAuth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                fuser = mAuth.getCurrentUser();
                if (fuser != null) {
                    addUser();

                } else {

                }
            }
        };
        db = FirebaseFirestore.getInstance();
        fuser = mAuth.getCurrentUser();
    }

    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    /**
     * This method gets called after an action to get data from the user
     * Receives callback from sign in method and acts accordingly (tries to sign in with google or facebook)
     *
     * @param requestCode the request code of the request
     * @param resultCode a code representing the state of the result of the action
     * @param data the data gained from the activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Facebook
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        //Google
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                progressDialog.dismiss();
                Log.d(TAG, e.getMessage());
                Toast.makeText(LoginActivity.this, "Google Sign In Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Checks if the user has a chapter and is entered in the databse
     * If they are, sends them to home page
     * if not, sneds them to choose chapter
     */
    private void addUser() {
        //Checks if fuser already exists
        db.collection("users")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    if(document.getId().equals(mAuth.getCurrentUser().getUid()) && !document.get("chapter").equals("")) {
                        progressDialog.dismiss();
                        updateUI();
                        return;
                    }
                }
                progressDialog.dismiss();
                sendtoSignup();
            }
        });
    }

    /**
     * Handles facebook login
     *
     * @param token the token used to get credentials for login
     */
    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            fuser = mAuth.getCurrentUser();
                            addUser();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "A user with this email exists already", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Sends user to homepage
     */
    private void updateUI() {
        fuser = mAuth.getCurrentUser();
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Sends user to chapter signup page
     */
    private void sendtoSignup() {
        fuser = mAuth.getCurrentUser();
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Runs when the activity starts
     * adds listener for authentication state
     */
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);
        fuser = mAuth.getCurrentUser();
    }

    /**
     * Called when the activity stops
     * stops listening for authentication changes
     */
    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
    }

    /**
     * Sets cosmetics of google sign in button
     */
    public void setGooglePlusButtonProperties() {
        for (int i = 0; i < loginGoogleBtn.getChildCount(); i++) {
            View v = loginGoogleBtn.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTextSize(24);
                return;
            }
        }
    }

    /**
     * Authenticates in firebase with google account
     *
     * @param acct the google account that was used to sign in
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            fuser = mAuth.getCurrentUser();
                            addUser();
                        } else {
                            Toast.makeText(LoginActivity.this, "A user with this email exists already", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}


