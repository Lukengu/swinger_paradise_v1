package swingersparadise.app.solutions.novatech.pro.swingersparadise;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class SwingersApp extends Application {

    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference lastOnlineRef;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        //---WHENEVER USER IS NOT LOGGED IN THIS FEATURE WILL NOT WORK---
        //---DO IT WHENEVER YOU GET TIME---
        if(mUser!=null){
            //---FIREBASE OFFLINE FEATURE---
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            mAuth=FirebaseAuth.getInstance();
            mUserDatabase =  FirebaseDatabase.getInstance().getReference("users/"+mAuth.getUid()+"/online_presence");
            lastOnlineRef =  FirebaseDatabase.getInstance().getReference("/users/"+mAuth.getUid()+"/lastOnline");

            final DatabaseReference connectedRef =  FirebaseDatabase.getInstance().getReference(".info/connected");

            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean connected = dataSnapshot.getValue(Boolean.class);
                    if (connected) {
                        DatabaseReference con = mUserDatabase.push();
                        con.onDisconnect().removeValue();
                        lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                        con.setValue(Boolean.TRUE);

                    } else {

                        mUserDatabase.onDisconnect().removeValue();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

}
