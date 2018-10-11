package swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.AppConfig;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.MatePhotosViewer;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.R;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.entities.Card;


public class PositionsImageAdapter extends RecyclerView.Adapter<PositionsImageAdapter.PositionViewHolder> {

    Context c;
    ArrayList<String> images;
    SwipeRefreshLayout swiper;
    String title;

    public PositionsImageAdapter(Context c, ArrayList<String> images, SwipeRefreshLayout swiper, String title ) {
        this.c = c;
        this.images = images;
        this.swiper = swiper;
        this.title = title;

    }

    public void addImage(String image){
        images.add(image);
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public PositionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.position_card,viewGroup,false);

        PositionsImageAdapter.PositionViewHolder holder=new PositionsImageAdapter.PositionViewHolder(v);

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull PositionViewHolder positionViewHolder, int i) {
        Glide.with(c)
                .load(images.get(i))
                .into(positionViewHolder.position_image);

    }

    public void refresh(){
        images.clear();
        AsyncHttpClient client  = new AsyncHttpClient();
        client.get(String.format(AppConfig.POSITION_ENDPOINT, title), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.i("Request", String.format(AppConfig.POSITION_ENDPOINT, title));
                Log.i("Response", response.toString());
                for(int i = 0; i <  response.length(); i++){
                    PositionsImageAdapter.this.addImage(response.optString(i));
                    swiper.setRefreshing(false);
                }


            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.e("Response", t.getMessage());


            }
        } );
    }

    @Override
    public int getItemCount() {
        return  images.size();
    }

    class PositionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView position_image;

        public PositionViewHolder(@NonNull View itemView) {
            super(itemView);
            position_image = itemView.findViewById(R.id.position_image);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            ArrayList photos_extra = new ArrayList();
            photos_extra.addAll(images);

            Bundle b =new Bundle();
            b.putInt("position", getAdapterPosition());
            b.putStringArrayList("photos",photos_extra);
            b.putString("display_name", title);
            b.putString("uuid","none");
            Intent i = new Intent(c, MatePhotosViewer.class).putExtras(b);
            c.startActivity(i);

        }
    }
}
