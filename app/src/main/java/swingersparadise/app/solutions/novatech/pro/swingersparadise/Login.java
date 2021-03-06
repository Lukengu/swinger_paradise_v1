package swingersparadise.app.solutions.novatech.pro.swingersparadise;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;
import com.pusher.pushnotifications.PushNotifications;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import retrofit2.Call;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.utils.BitmapUtils;


public class Login extends Activity {

    CallbackManager callbackManager;
    TextView register_link,reset_password;
    LoginButton loginButton;
    EditText email, password;

    private static final String EMAIL = "email";
    private static final String PUBLIC_PROFILE = "public_profile";
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference users_db;
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_FACEBOOK_SIGNIN = 64206;
    private static final int RC_TWITTER = 140;
    private static final int RC_MOBILE_SIGNIN=11111;
    private TwitterLoginButton mLoginButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setTitle("Sign In");


        //Twitter
        TwitterAuthConfig authConfig =  new TwitterAuthConfig(
                getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();

        Twitter.initialize(twitterConfig);


        //Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        register_link =  findViewById(R.id.register_link);
        reset_password =  findViewById(R.id.reset_password);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        //register_link.setMovementMethod(LinkMovementMethod.getInstance());
        //reset_password.setMovementMethod(LinkMovementMethod.getInstance());
        users_db = FirebaseDatabase.getInstance().getReference().child("users");



        mLoginButton = findViewById(R.id.button_twitter_login);
        mLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "twitterLogin:success" + result);
                handleTwitterSession(result.data);
            }



            @Override
            public void failure(TwitterException exception) {
                Log.w(TAG, "twitterLogin:failure", exception);
                //updateUI(null);
            }
        });
        // [END initialize_twitter_login]


        String text = "Don't have a account yet . Please ";
        String reset = "<a href=\"app://reset_password\">Forgot</a> Password?";






          PushNotifications.start(this, getString(R.string.pusher_instance_id));
          PushNotifications.subscribe(getString(R.string.pusher_channel));


        mAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {

                    startActivity(new Intent(Login.this, Content.class));

                }
            }
        };



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            register_link.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
            reset_password.setText(Html.fromHtml(reset, Html.FROM_HTML_MODE_COMPACT));
        } else {
            register_link.setText(Html.fromHtml(text));
            reset_password.setText(Html.fromHtml(reset));
        }
        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, ResetPassword.class));
            }
        });
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL, PUBLIC_PROFILE));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
                //Toast.makeText(Login.this, "Cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.e("FACEBOOK ERROR", exception.getMessage());

                //Toast.makeText(Login.this, exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        //Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);







        Set<String> subscriptions =  PushNotifications.getSubscriptions();
        Iterator<String> iterator = subscriptions.iterator();
        while(iterator.hasNext()) {

            Log.e("SUB", (String) iterator.next() );
        }

    }

    protected void onResume() {
        super.onResume();
     /*   PushNotifications.setOnMessageReceivedListenerForVisibleActivity(this, new PushNotificationReceivedListener() {
            @Override
            public void onMessageReceived(RemoteMessage remoteMessage) {
                Log.i("MainActivity", "A remote message was received while this activity is visible!");
                Map<String, String> params = remoteMessage.getData();
                JSONObject object = new JSONObject(params);
                Log.e("JSON_OBJECT", object.toString());

            }
        });*/

    }

    private void handleTwitterSession(final TwitterSession session) {
        Log.d(TAG, "handleTwitterSession:" + session);
        // [START_EXCLUDE silent]
        final ProgressBar progressBar = new ProgressBar(this);
        progressBar.setEnabled(true);
        progressBar.setIndeterminate(true);
        // [END_EXCLUDE]
        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final  FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);


                            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
                            Call<User> call = twitterApiClient.getAccountService().verifyCredentials(true, false, true);
                            call.enqueue(new Callback<User>() {
                                @Override
                                public void success(Result<User> result) {
                                    // Do something with result, which provides a User
                                    User twiter_user = result.data;
                                    String fullname = twiter_user.name;
                                    final String userSocialProfile = twiter_user.profileImageUrl;
                                    String userEmail = twiter_user.email;
                                   // String userFirstName = fullname.substring(0, fullname.lastIndexOf(" "));
                                   // String userLastNmae = fullname.substring(fullname.lastIndexOf(" "));
                                    String userScreenName = twiter_user.screenName;

                                    users_db.child(user.getUid()).child("name").setValue(fullname);
                                    users_db.child(user.getUid()).child("email").setValue(userEmail);
                                    users_db.child(user.getUid()).child("display_name").setValue(userScreenName);
                                    new Thread( new Runnable(){

                                        @Override
                                        public void run() {
                                            try {

                                                StorageReference storageReference =  FirebaseStorage.getInstance().getReference().child("profiles/"+ user.getUid());
                                                BitmapUtils.saveBitmapToPath(getRemoteProfilePicture(userSocialProfile), Login.this, "profile_img.jpg");
                                                String file_name = Login.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath().concat("/profile_img.jpg");
                                                final Uri file_path = Uri.fromFile(new File(file_name));
                                                if(file_path != null) {
                                                    storageReference.putFile(file_path);
                                                }

                                            } catch(IOException e) {
                                            }
                                        }
                                    }).start();



                                }

                                @Override
                                public void failure(TwitterException exception) {
                                    // Do something on failure
                                }
                            });




                            startActivity(new Intent(Login.this,Content.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                          //  updateUI(null);
                        }

                        // [START_EXCLUDE]
                        progressBar.setVisibility(View.GONE);
                        // [END_EXCLUDE]
                    }
                });
    }



    private void handleFacebookAccessToken(final AccessToken accessToken) {
        Log.d(TAG, "handleFacebookAccessToken:" + accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //Save User Data in Firebase Db before starting  the intent

                            GraphRequest request =  GraphRequest.newMeRequest(accessToken, new  GraphRequest.GraphJSONObjectCallback(){

                                @Override
                                public void onCompleted(final JSONObject object, GraphResponse response) {
                                    final FirebaseUser user = mAuth.getCurrentUser();
                                   // Log.d("FacebookProfile", object.toString());
                                    users_db.child(user.getUid()).child("name").setValue(object.optString("name"));
                                    users_db.child(user.getUid()).child("email").setValue(object.optString("email"));
                                   // users_db.child(user.getUid()).child("display_name").setValue(object.optString("email"));
                                   // users_db.child(user.getUid()).child("gender").setValue(object.optString("gender"));
                                    //users_db.child(user.getUid()).child("birthday").setValue(object.optString("birthday"));
                                    //Uri uri  = Uri.fromParts()

                                    new Thread( new Runnable(){

                                        @Override
                                        public void run() {
                                            try {

                                                StorageReference storageReference =  FirebaseStorage.getInstance().getReference().child("profiles/"+ user.getUid());
                                                BitmapUtils.saveBitmapToPath(getRemoteProfilePicture("https://graph.facebook.com/" + object.getString("id") + "/picture?type=large"), Login.this, "profile_img.jpg");
                                                String file_name = Login.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath().concat("/profile_img.jpg");
                                                final Uri file_path = Uri.fromFile(new File(file_name));
                                                if(file_path != null) {
                                                    storageReference.putFile(file_path);
                                                }

                                            } catch(IOException e) {
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();


                                }
                            });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,gender, birthday,picture");
                            request.setParameters(parameters);
                            request.executeAsync();


                            //FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            startActivity(new Intent(Login.this, Content.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();


                        }


                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode){

            case RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e);
                    // ...
                }
                break;
            case RC_FACEBOOK_SIGNIN:
                callbackManager.onActivityResult(requestCode, resultCode, data);
                break;
            case RC_TWITTER:
                mLoginButton.onActivityResult(requestCode, resultCode, data);
                break;

        }

    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            // Log.d("FacebookProfile", object.toString());
                            users_db.child(user.getUid()).child("name").setValue(account.getGivenName().concat(" ").concat(account.getFamilyName()));

                            users_db.child(user.getUid()).child("email").setValue(account.getEmail());
                            users_db.child(user.getUid()).child("display_name").setValue(account.getDisplayName());
                            StorageReference storageReference =  FirebaseStorage.getInstance().getReference().child("profiles/"+ user.getUid());


                            new Thread( new Runnable(){

                                @Override
                                public void run() {
                                    try {

                                        StorageReference storageReference =  FirebaseStorage.getInstance().getReference().child("profiles/"+ user.getUid());
                                        BitmapUtils.saveBitmapToPath(getRemoteProfilePicture(account.getPhotoUrl().toString()), Login.this, "profile_img.jpg");
                                        String file_name = Login.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath().concat("/profile_img.jpg");
                                        final Uri file_path = Uri.fromFile(new File(file_name));
                                        if(file_path != null) {
                                            storageReference.putFile(file_path);
                                        }

                                    } catch(IOException e) {
                                    }
                                }
                            }).start();


                            startActivity(new Intent(Login.this, Content.class));
                          //  updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_content), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void facebook_login(View view) {
        loginButton.performClick();
    }

    @Override
    protected void onStart() {
        super.onStart();
       // FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(authStateListener);


        //updateUI(currentUser);
    }


    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }

    public void open_register(View view) {
        startActivity(new Intent(Login.this, Register.class));
        
    }

    public void google_signin(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private static Bitmap getRemoteProfilePicture(String url) throws IOException {
        URL imageURL = new URL(url);
        Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

        return bitmap;
    }

    public void email_sign_in(View view) {
      if(TextUtils.isEmpty(email.getText().toString())){
          email.setError("Email is required");
      } else if(TextUtils.isEmpty(password.getText().toString())){
          password.setError("Password is required");

      } else {
          final ProgressDialog progressDialog = new ProgressDialog(this);
          progressDialog.show();

          mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                   progressDialog.dismiss();
                  if(task.isSuccessful()){
                      startActivity(new Intent(Login.this, Content.class));
                  }
              }
          })
                  .addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
                      }
                  });
      }
    }

    public void twitter_signin(View view) {
        mLoginButton.performClick();
    }

    public void mobile_signin(View view) {
        startActivityForResult(new Intent(Login.this, MobileLogin.class), RC_MOBILE_SIGNIN);
    }
}
