package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.view;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters.AlbumAdapter;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.utils.GenderConverter;


@Layout(R.layout.item)
public class TinderCard implements android.view.View.OnClickListener {

    @View(R.id.profile_image)
    private ImageView profileImageView;

    @View(R.id.gender)
    private ImageView gender;

    @View(R.id.recycle_view)
    private RecyclerView mRecyclerView;

    @View(R.id.connect_no)
    private ImageView connect_no;


    @View(R.id.connect_yes)
    private ImageView connect_yes;

    @View(R.id.unlock_action)
    private ImageView unlock_action;

    @View(R.id.container)
    private ConstraintLayout container;

    @View(R.id.back_to_profile)
    private ImageView back_to_profile;

   // @View(R.id.swiperefresh)
    //private android.support.v4.widget.SwipeRefreshLayout swiperefresh;








    private Card mCard;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

    private PropertyChangeSupport changes = new PropertyChangeSupport(this);

    private FirebaseAuth firebaseAuth;
  //  private DatabaseReference user_db;


    public TinderCard(Context context, Card card, SwipePlaceHolderView swipeView) {
        mContext = context;
        mCard = card;
        mSwipeView = swipeView;
    }

    @Resolve
    private void onResolved() {

        firebaseAuth = FirebaseAuth.getInstance();
        //user_db = FirebaseDatabase.getInstance().getReference().child("users");

        //Glide.with(mContext).load(mProfile.getImageUrl()).into(profileImageView);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("profiles/" + mCard.getUuid());


        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {

                Glide.with(mContext)
                        .load(uri.toString())
                        .into(profileImageView);
               // changes.firePropertyChange("mCard",null, mCard);


            }

        });
        gender.setImageDrawable( mContext.getResources().getDrawable(GenderConverter.convert(mCard)));


        RefreshList();

       // swiperefresh.setRefreshing(true);


       /* swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {


                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        RefreshList();
                    }
                }
        );*/



        connect_no.setOnClickListener(this);
        connect_yes.setOnClickListener(this);
        unlock_action.setOnClickListener(this);
        back_to_profile.setOnClickListener(this);







    }

    private void RefreshList(){
        AlbumAdapter albumAdapter = new AlbumAdapter(mContext, mCard);
        mRecyclerView.setAdapter(albumAdapter);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mContext, 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);


    }



    @SwipeOut
    private void onSwipedOut () {
        Log.d("EVENT", "onSwipedOut");
        //  user_db.child(mCard.getOpposite_gender()).child(mCard.getUuid()).child("Connections").child("Nope").child(firebaseAuth.getCurrentUser().getUid()).setValue("true");
        mSwipeView.addView(this);
        changes.firePropertyChange("mCard",null, mCard);
    }

    @SwipeCancelState
    private void onSwipeCancelState () {
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn () {
        Log.d("EVENT", "onSwipedIn");
        // user_db.child(mCard.getOpposite_gender()).child(mCard.getUuid()).child("Connections").child("Yep").child(firebaseAuth.getCurrentUser().getUid()).setValue("true");
        changes.firePropertyChange("mCard",null, mCard);

    }




    @SwipeInState
    private void onSwipeInState () {
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState () {
        Log.d("EVENT", "onSwipeOutState");
    }



    public void addPropertyChangeListener(
            PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }
    public void removePropertyChangeListener(
            PropertyChangeListener l) {
        changes.removePropertyChangeListener(l);
    }

    @Override
    public void onClick(android.view.View view) {

        switch(view.getId()){
            case  R.id.connect_no:
                mSwipeView.doSwipe(false);
                break;
            case R.id.connect_yes:
                mSwipeView.doSwipe(true);
                break;
            case R.id.unlock_action:
                // Send notification to unlock
                 Snackbar.make(container, "Your request to unlock  sent to "+mCard.getDisplay_name()+"", Snackbar.LENGTH_LONG).show();
                break;

            case R.id.back_to_profile:
                // Send notification to unlock
                //Snackbar.make(container, "Your request to unlock "+mCard.getDisplay_name()+"'s private profile has been sent", Snackbar.LENGTH_LONG).show();
                 AlertDialog alertDialog =  new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("");
                alertDialog.setMessage("This will take you to profile you have viewed already. Do you wish to continue ?");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.show();
                break;
        }
    }
    public Card getCard(){
        return mCard;
    }
}