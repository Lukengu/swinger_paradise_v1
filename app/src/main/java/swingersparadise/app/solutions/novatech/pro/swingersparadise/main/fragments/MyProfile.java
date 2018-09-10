package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.google.firebase.storage.UploadTask;
import com.pusher.pushnotifications.PushNotifications;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    Button save_profile,manage_photos;
    DatabaseReference users_db;
    CoordinatorLayout parent;
    Card card;
    private static final int GET_FROM_GALLERY = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    SharedPreferences spref;
    SharedPreferences.Editor editor;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_profile,
                container, false);

        spref = getActivity().getSharedPreferences("Profile", Context.MODE_PRIVATE);
        editor = spref.edit();


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
        manage_photos = view.findViewById(R.id.manage_photos);
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

       adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_textview_no_bg,
                getActivity().getResources().getStringArray(R.array.ethnicity));
        ethnicity.setAdapter(adapter);

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


        registerForContextMenu(profile_image);
        manage_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((Content) getActivity()).selectAlbum();
            }
        });


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
                    Snackbar snackbar = Snackbar.make(parent, "Wrong Gender ", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sbView.setBackground(getActivity().getDrawable(R.drawable.snackbar_error));
                    } else {
                        sbView.setBackground(getActivity().getResources().getDrawable(R.drawable.snackbar_error));
                    }
                    snackbar.show();

                    return;
                }
                if(marital_status.getSelectedItem().toString().equals("Marital Status")){
                    Snackbar snackbar = Snackbar.make(parent, "Wrong Marital Status", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sbView.setBackground(getActivity().getDrawable(R.drawable.snackbar_error));
                    } else {
                        sbView.setBackground(getActivity().getResources().getDrawable(R.drawable.snackbar_error));
                    }
                    snackbar.show();

                    return;
                }

                if(ethnicity.getSelectedItem().toString().equals("Ethnicity")){
                    Snackbar snackbar = Snackbar.make(parent, "Wrong Ethnicity ", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sbView.setBackground(getActivity().getDrawable(R.drawable.snackbar_error));
                    } else {
                        sbView.setBackground(getActivity().getResources().getDrawable(R.drawable.snackbar_error));
                    }
                    snackbar.show();

                    return;
                }

                if(body_part.getSelectedItem().toString().equals("Body Parts")){
                    Snackbar snackbar = Snackbar.make(parent, "Wrong Body Parts ", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sbView.setBackground(getActivity().getDrawable(R.drawable.snackbar_error));
                    } else {
                        sbView.setBackground(getActivity().getResources().getDrawable(R.drawable.snackbar_error));
                    }
                    snackbar.show();

                    return;
                }

                if(build.getSelectedItem().toString().equals("Build")){
                    Snackbar snackbar = Snackbar.make(parent, "Wrong Build ", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sbView.setBackground(getActivity().getDrawable(R.drawable.snackbar_error));
                    } else {
                        sbView.setBackground(getActivity().getResources().getDrawable(R.drawable.snackbar_error));
                    }
                    snackbar.show();

                    return;
                }
                if(hair_colour.getSelectedItem().toString().equals("Hair Colour")){
                    Snackbar snackbar = Snackbar.make(parent, "Wrong Hair Colour ", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sbView.setBackground(getActivity().getDrawable(R.drawable.snackbar_error));
                    } else {
                        sbView.setBackground(getActivity().getResources().getDrawable(R.drawable.snackbar_error));
                    }
                    snackbar.show();

                    return;
                }

                if(smoking.getSelectedItem().toString().equals("Smoking")){
                    Snackbar snackbar = Snackbar.make(parent, "Wrong Smoking Status ", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sbView.setBackground(getActivity().getDrawable(R.drawable.snackbar_error));
                    } else {
                        sbView.setBackground(getActivity().getResources().getDrawable(R.drawable.snackbar_error));
                    }
                    snackbar.show();

                    return;
                }

                if(smoking.getSelectedItem().toString().equals("Drinking")){
                    Snackbar snackbar = Snackbar.make(parent, "Wrong Drinking Status ", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sbView.setBackground(getActivity().getDrawable(R.drawable.snackbar_error));
                    } else {
                        sbView.setBackground(getActivity().getResources().getDrawable(R.drawable.snackbar_error));
                    }
                    snackbar.show();
                    return;
                }

                users_db.child(user.getUid()).child("display_name").setValue(display_name.getText().toString());
                users_db.child(user.getUid()).child("age").setValue(age.getText().toString());
                users_db.child(user.getUid()).child("gender").setValue(gender.getSelectedItem().toString());
                users_db.child(user.getUid()).child("marital_status").setValue(marital_status.getSelectedItem().toString());
                users_db.child(user.getUid()).child("ethnicity").setValue(ethnicity.getSelectedItem().toString());
                users_db.child(user.getUid()).child("body_part").setValue(body_part.getSelectedItem().toString());
                users_db.child(user.getUid()).child("build").setValue(build.getSelectedItem().toString());
                users_db.child(user.getUid()).child("hair_color").setValue(hair_colour.getSelectedItem().toString());
                users_db.child(user.getUid()).child("smoking").setValue(smoking.getSelectedItem().toString());
                users_db.child(user.getUid()).child("drinking").setValue(drinking.getSelectedItem().toString());
                users_db.child(user.getUid()).child("about_me").setValue(about_me.getText().toString());
                users_db.child(user.getUid()).child("sexual_prefs").setValue(sexual_preferences.getSelectedItemsAsString());
                if(spref.contains("selected_image")){


                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                    StorageReference ref = firebaseStorage.getReference().child("profiles/"+ user.getUid());
                    final String file_name  = spref.getString("selected_image", "");
                    final Uri file_path = Uri.fromFile(new File(file_name));
                    if(file_path != null) {
                        ref.putFile(file_path)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    }
                                });
                    }

                }


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


                        try {
                            display_name.setText(dataSnapshot.child("display_name").getValue().toString());
                        }  catch(NullPointerException e){}
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu,v,menuInfo);
        menu.setHeaderTitle("Profile Photo");
        menu.add(0, v.getId(), 0, "Take Photo");
        menu.add(1, v.getId(), 0, "From Gallery");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if("From Gallery".equals(item.getTitle())){
            startActivityForResult(
                    new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI
                    ),
                    GET_FROM_GALLERY
            );
        }
        if("Take Photo".equals(item.getTitle())){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
        return true;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

            Bitmap bitmap = null;
            try {
                // Uri profileImage = data.getData();

                //  ((Register) getActivity()).setProfileImage(profileImage);


                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),data.getData());
                profile_image.setImageBitmap(Bitmap.createScaledBitmap(selectedImage, 200, 200, false));
                if(spref.contains("selected_image"))
                    editor.remove("selected_image").commit();

                editor.putString("selected_image",   getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath().concat("/profile_img.jpg"));
                saveBitmapToPath(selectedImage);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap selectedImage  = (Bitmap) extras.get("data");

            profile_image.setImageBitmap(Bitmap.createScaledBitmap(selectedImage, 200, 200, false));
            editor.putString("selected_image",   getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath().concat("/profile_img.jpg"));
            saveBitmapToPath(selectedImage);

            // ((Register) getActivity()).setProfileImage( Uri.fromFile( new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath().concat("/profile_img.jpg"))));
            //saveBitmapToPath(imageBitmap);
        }






    }
    protected void saveBitmapToPath(Bitmap bitmap) {
        //String root = Environment.getExternalStorageDirectory().toString();
        //File myDir = new File(root +  get);
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        storageDir.mkdirs();
        File file = new File(storageDir, "profile_img.jpg");
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
