package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pusher.pushnotifications.PushNotifications;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.Content;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.ui.MultiSelectionSpinner;

import static swingersparadise.app.solutions.novatech.pro.swingersparadise.utils.ArrayUtils.getPosition;


public class MyProfile extends Fragment {

    EditText display_name,age,about_me;
    private MultiSelectionSpinner sexual_preferences;
    private Spinner marital_status,gender,build,ethnicity,body_part,hair_colour,smoking,drinking;
    ImageView profile_image;
    FirebaseUser user;
    Button save_profile;
    DatabaseReference users_db;
    CoordinatorLayout parent;
    Card card;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_profile,
                container, false);




        display_name = view.findViewById(R.id.display_name);
        age = view.findViewById(R.id.age);
        marital_status = view.findViewById(R.id.marital_status);

        marital_status = view.findViewById(R.id.marital_status);
        gender = view.findViewById(R.id.gender);
        build = view.findViewById(R.id.build);
        ethnicity = view.findViewById(R.id.ethnicity);
        body_part = view.findViewById(R.id.body_part);
        hair_colour = view.findViewById(R.id.hair_colour);
        smoking = view.findViewById(R.id.smoking);
        drinking = view.findViewById(R.id.drinking);
        about_me = view.findViewById(R.id.about_me);
        sexual_preferences = view.findViewById(R.id.sexual_preferences);
        profile_image = view.findViewById(R.id.profile_image);
        save_profile = view.findViewById(R.id.save_profile);
        users_db = FirebaseDatabase.getInstance().getReference().child("users");
        parent = view.findViewById(R.id.parent);


        sexual_preferences.setItems(getActivity().getResources().getStringArray(R.array.sexual_preferences));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_textview_no_bg,
                getActivity().getResources().getStringArray(R.array.marital_status));
        marital_status.setAdapter(adapter);


        adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_textview_no_bg,
                getActivity().getResources().getStringArray(R.array.gender));
        gender.setAdapter(adapter);

        adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_textview_no_bg,
                getActivity().getResources().getStringArray(R.array.build));
        build.setAdapter(adapter);

/*        adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_textview_no_bg,
                getActivity().getResources().getStringArray(R.array.ethnicity));
        ethnicity.setAdapter(adapter);
*/
        adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_textview_no_bg,
                getActivity().getResources().getStringArray(R.array.body_part));
        body_part.setAdapter(adapter);

        adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_textview_no_bg,
                getActivity().getResources().getStringArray(R.array.hair_colour));
        hair_colour.setAdapter(adapter);


        adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_textview_no_bg,
                getActivity().getResources().getStringArray(R.array.smoking));
        smoking.setAdapter(adapter);


        adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_textview_no_bg,
                getActivity().getResources().getStringArray(R.array.drinking));


        drinking.setAdapter(adapter);






        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(display_name.getText().toString())) {
                    display_name.setError("Display name required");
                    return;
                }
                users_db.child(user.getUid()).child("display_name").setValue(display_name.getText().toString());

                if(TextUtils.isEmpty(age.getText().toString())) {
                    age.setError("Provide your age");
                    return;
                }
                if(gender.getSelectedItem().toString().equals("Gender")){
                    Snackbar.make(parent, "Profile Saved", Snackbar.LENGTH_LONG).show();
                    return;
                }

                users_db.child(user.getUid()).child("display_name").setValue(display_name.getText().toString());
                users_db.child(user.getUid()).child("age").setValue(age.getText().toString());
                users_db.child(user.getUid()).child("age").setValue(age.getText().toString());



                Snackbar.make(parent, "Profile Saved", Snackbar.LENGTH_LONG).show();

            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        load();

        return view;
    }

    private void load() {
        users_db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists() && dataSnapshot.getKey().equals(user.getUid())) {



                        display_name.setText(dataSnapshot.child("display_name").getValue().toString());
                        try {
                            age.setText(dataSnapshot.child("age").getValue().toString());
                        } catch(NullPointerException e){}

                        try {
                            age.setText(dataSnapshot.child("age").getValue().toString());
                        } catch(NullPointerException e){}

                        try {
                            gender.setSelection(getPosition(dataSnapshot.child("gender").getValue().toString(), getActivity().getResources().getStringArray(R.array.gender)), true);
                        } catch(NullPointerException e){}
                        try {
                            marital_status.setSelection(getPosition(dataSnapshot.child("marital_status").getValue().toString(), getActivity().getResources().getStringArray(R.array.marital_status)), true);
                        } catch(NullPointerException e){}

                        try {
                            build.setSelection(getPosition(dataSnapshot.child("build").getValue().toString(), getActivity().getResources().getStringArray(R.array.build)), true);

                        } catch(NullPointerException e){}

                        try {
                            ethnicity.setSelection(getPosition(dataSnapshot.child("ethnicity").getValue().toString(), getActivity().getResources().getStringArray(R.array.ethnicity)), true);

                        } catch(NullPointerException e){}

                        try {
                            body_part.setSelection(getPosition(dataSnapshot.child("body_part").getValue().toString(), getActivity().getResources().getStringArray(R.array.body_part)), true);

                        } catch(NullPointerException e){}

                        try {
                            hair_colour.setSelection(getPosition(dataSnapshot.child("hair_color").getValue().toString(), getActivity().getResources().getStringArray(R.array.hair_colour)), true);

                        } catch(NullPointerException e){}

                        try {
                            smoking.setSelection(getPosition(dataSnapshot.child("smoking").getValue().toString(), getActivity().getResources().getStringArray(R.array.smoking)), true);

                        } catch(NullPointerException e){}
                        try {

                            drinking.setSelection(getPosition(dataSnapshot.child("drinking").getValue().toString(), getActivity().getResources().getStringArray(R.array.drinking)), true);

                        } catch(NullPointerException e){}

                        try {
                            sexual_preferences.setSelection(dataSnapshot.child("sexual_prefs").getValue().toString().split(","));
                        } catch(NullPointerException e){

                        }
                        try{
                            about_me.setText(dataSnapshot.child("about_me").getValue().toString());

                        } catch(NullPointerException e){}


                        StorageReference ref = FirebaseStorage.getInstance().getReference().child("profiles/" + user.getUid() );


                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                //  Toast.makeText(getActivity(), uri.toString(), Toast.LENGTH_LONG).show();
                                Glide.with(getActivity())
                                        .load(uri.toString())
                                        .into(profile_image);
                                // changes.firePropertyChange("mCard",null, mCard);


                            }

                        });



                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
