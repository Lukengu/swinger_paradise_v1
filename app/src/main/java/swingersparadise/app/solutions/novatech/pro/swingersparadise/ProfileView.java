package swingersparadise.app.solutions.novatech.pro.swingersparadise;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;

public class ProfileView extends AppCompatActivity {

    private ImageView profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //String uuid = getIntentv().getExtras().getString("uuid");
        Card card = (Card) getIntent().getExtras().getSerializable("card");

        profile_image = findViewById(R.id.profile_image);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("profiles/" + card.getUuid());
        setTitle(card.getDisplay_name().concat("'s").concat(" Profile"));
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {
                Glide.with(ProfileView.this)
                        .load(uri.toString())
                        .into(profile_image);

            }

        });
        ((TextView) findViewById(R.id.display_name)).setText(card.getDisplay_name());
        ((TextView) findViewById(R.id.age)).setText(!String.valueOf(card.getAge()).equals("0")? String.valueOf(card.getAge()) : "");
        ((TextView) findViewById(R.id.prof_gender)).setText(card.getGender());
        ((TextView) findViewById(R.id.prof_marital_status)).setText(card.getMarital_status());
        ((TextView) findViewById(R.id.ethnicity)).setText(card.getEthnicity());
        ((TextView) findViewById(R.id.country)).setText(card.getCountry());

        ((TextView) findViewById(R.id.body_part)).setText(card.getBody_part());
        ((TextView) findViewById(R.id.build)).setText(card.getBuild());
        ((TextView) findViewById(R.id.hair_color)).setText(card.getHair_color());
        ((TextView) findViewById(R.id.smoking)).setText(card.getSmoking());
        ((TextView) findViewById(R.id.driking)).setText(card.getDrinking());
        ((TextView) findViewById(R.id.sexual_preferences)).setText(card.getSexual_prefs());
        ((TextView) findViewById(R.id.about_me)).setText(card.getAbout_me());

    }



}
