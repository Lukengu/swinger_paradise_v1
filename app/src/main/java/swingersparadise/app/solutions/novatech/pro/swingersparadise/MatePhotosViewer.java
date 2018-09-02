package swingersparadise.app.solutions.novatech.pro.swingersparadise;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.adapters.FullScreenImageAdapter;
import swingersparadise.app.solutions.novatech.pro.swingersparadise.main.view.PageIndicator;

public class MatePhotosViewer extends AppCompatActivity {
    ViewPager pager;
    private LinearLayout indicators;
    private PageIndicator pageIndicator;
    int position;
    ArrayList<String> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mate_photos_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        indicators = findViewById(R.id.indicators);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        setTitle(b.getString("display_name"));
        photos = b.getStringArrayList("photos");
        String uuid = b.getString("uuid");
        pager = findViewById(R.id.pager);
        FullScreenImageAdapter fullScreenImageAdapter = new FullScreenImageAdapter(this,photos, uuid);
        pager.setAdapter(fullScreenImageAdapter);
        pageIndicator = new PageIndicator(this, indicators, pager, R.drawable.pager_indicators);
        pageIndicator.setPageCount(photos.size());
        pageIndicator.show();
        position = b.getInt("position");
        pager.setCurrentItem(position);

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(++position == photos.size() - 1  )
                            position = 0;

                        pager.setCurrentItem(position);
                    }
                });
            }
        },9000,9000);


    }

}
