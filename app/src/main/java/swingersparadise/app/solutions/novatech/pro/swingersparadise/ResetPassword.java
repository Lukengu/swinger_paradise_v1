package swingersparadise.app.solutions.novatech.pro.swingersparadise;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPassword extends AppCompatActivity {

    EditText email;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    ConstraintLayout root_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        email = findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();
        root_view = findViewById(R.id.root_view);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {


                }
            }
        };

    }

    public void reset_password(View view) {
        if(TextUtils.isEmpty(email.getText().toString())){
            email.setError("Email Required");
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait ...");

            progressDialog.show();
            FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Snackbar snackbar = Snackbar.make(root_view, "An Email has been sent to "+ email.getText().toString(), Snackbar.LENGTH_LONG);
                                snackbar.show();
                                email.setText("");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Snackbar snackbar = Snackbar.make(root_view, e.getMessage(), Snackbar.LENGTH_LONG);

                            View sbView = snackbar.getView();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                sbView.setBackground(getDrawable(R.drawable.snackbar_error));
                            } else {
                                sbView.setBackground(getResources().getDrawable(R.drawable.snackbar_error));
                            }



                            snackbar.show();
                            email.setText("");
                        }
                    });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }
}
