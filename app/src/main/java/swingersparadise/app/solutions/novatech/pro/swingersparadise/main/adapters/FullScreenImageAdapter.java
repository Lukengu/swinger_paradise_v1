package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;

public class FullScreenImageAdapter extends PagerAdapter {

    private Context _context;
    private ArrayList<String> _photos;
    private LayoutInflater inflater;
    private  String uuid;


    public FullScreenImageAdapter(Context context,
                                  ArrayList<String> photos , String uuid) {
        this._context = context;
        this._photos = photos;
        this.uuid = uuid;
        inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return _photos.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((ConstraintLayout) object);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

       // Toast.makeText(_context,_photos.get(position), Toast.LENGTH_LONG ).show();
       // Toast.makeText(_context,uuid, Toast.LENGTH_LONG ).show();


        View viewLayout = inflater.inflate(R.layout.full_screen_image, container,
                false);

        final ImageView imgDisplay = viewLayout.findViewById(R.id.imgDisplay);

        if(!"none".equals(uuid)) {
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("albums/" + uuid + "/public/" + _photos.get(position));


            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(final Uri uri) {

                    Glide.with(_context)
                            .load(uri.toString())
                            .into(imgDisplay);
                }
            });
        } else {
            Glide.with(_context)
                    .load(_photos.get(position))
                    .into(imgDisplay);
        }


       container.addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ConstraintLayout) object);

    }
}
