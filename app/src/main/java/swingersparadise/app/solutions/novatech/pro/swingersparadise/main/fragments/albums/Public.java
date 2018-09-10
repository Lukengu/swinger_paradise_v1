package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.fragments.albums;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.Register;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters.FriendsAdapter;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters.PhotosAdapter;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;

public class Public extends Fragment {

    RecyclerView mRecycler;
    PhotosAdapter photosAdapter;
    SwipeRefreshLayout swiper;
    ArrayList<String> photos = new ArrayList<>();
    static final int GET_FROM_GALLERY = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ProgressDialog progressDialog;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matches_list,
                container, false);

        mRecycler = view.findViewById(R.id.mRecycler);
        swiper =  view.findViewById(R.id.swiper);
        photosAdapter = new PhotosAdapter(getActivity(),photos,swiper,PhotosAdapter.PUBLIC);
        mRecycler.setAdapter(photosAdapter);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecycler.setLayoutManager(mGridLayoutManager);
        mRecycler.setHasFixedSize(true);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);


        setHasOptionsMenu(true);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                photosAdapter.refresh();

            }
        });


        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.nav_from_camera) {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
            return true;
        }




        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {

    }

    @Override
    public void onStart() {
        super.onStart();
        photosAdapter.clear();
        FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("albums").child(user.getUid()).child("public");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot photo : dataSnapshot.getChildren()){
                    photosAdapter.addPhotos(photo.getValue(String.class));
                   // Toast.makeText(getActivity(), photo.getValue(String.class), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

            Bitmap bitmap = null;
            try {
                // Uri profileImage = data.getData();

                //  ((Register) getActivity()).setProfileImage(profileImage);
                progressDialog.show();
                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),data.getData());

                SaveToFirebase(selectedImage);
                progressDialog.dismiss();


                //profile_image.setImageBitmap(Bitmap.createScaledBitmap(selectedImage, 136, 136, false));
               /* if(spref.contains("selected_image"))
                    editor.remove("selected_image").commit();

                editor.putString("selected_image",   getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath().concat("/profile_img.jpg"));
                saveBitmapToPath(selectedImage);*/

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            progressDialog.show();
            Bundle extras = data.getExtras();
            Bitmap  selectedImage  = (Bitmap) extras.get("data");

            SaveToFirebase(selectedImage);



          //  profile_image.setImageBitmap(Bitmap.createScaledBitmap(selectedImage, 136, 136, false));
            //editor.putString("selected_image",   getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath().concat("/profile_img.jpg"));
            //saveBitmapToPath(selectedImage);

            // ((Register) getActivity()).setProfileImage( Uri.fromFile( new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath().concat("/profile_img.jpg"))));
            //saveBitmapToPath(imageBitmap);
        }






    }

    public void SaveToFirebase(Bitmap bitmap) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final  DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        final String id  = db.child("albums").child(user.getUid()).child("public").push().getKey();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        final String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
       // DatabaseReference ref = FirebaseDatabase.getInstance()
        //        .getReference("albums/"+user.getUid()+"/public")
              //  .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
           //     .child(id)

        //ref.setValue(imageEncoded);*/
        final File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        storageDir.mkdirs();
        final File file = new File(storageDir, imageEncoded+".jpg");
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



        final Uri uri = Uri.fromFile(file);

        StorageReference ref = FirebaseStorage.getInstance().getReference().child("albums/"+ user.getUid()+"/public");

        if(uri != null) {
            ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            db.child("albums").child(user.getUid()).child("public").child(id).setValue(imageEncoded+".jpg");
                            file.delete();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }



    }
}
