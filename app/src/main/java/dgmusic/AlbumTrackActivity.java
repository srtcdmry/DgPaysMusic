package dgmusic;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.dgmusic.R;
import com.google.gson.Gson;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

public class AlbumTrackActivity extends AppCompatActivity implements OnTrackResultsSelectedInterface, Results {

    private String url;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<TrackResult> trackResults = new ArrayList<>();
    private HttpGetRequest getRequest;
    private Gson gson = new Gson();
    final MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_track);

        Intent mainIntent = getIntent();
        final DeezerResult result = Parcels.unwrap(mainIntent.getParcelableExtra("SELECTED_RESULT"));

        url = result.getTracklist();

        ImageButton stopButton = findViewById(R.id.main5_stop);

        RecyclerView rv = findViewById(R.id.main5_rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter2(trackResults, this);
        rv.setAdapter(mAdapter);
        DividerItemDecoration id = new DividerItemDecoration(this, mLayoutManager.getOrientation());
        rv.addItemDecoration(id);

        if (getRequest != null && !getRequest.isCancelled()) {
            getRequest.cancel(true);
        }
        if (trackResults.size() != 0) {
            trackResults.clear();
        }

        getRequest = new HttpGetRequest(this);
        getRequest.execute(url);

        stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView image = findViewById(R.id.main5_stop);

                    if(mediaPlayer.isPlaying()) {
                        image.setImageResource(R.mipmap.ic_launcher_play_blackwhite_round);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }

                }
            });
    }

    @Override
    public void onResultSelected(TrackResult trackResult) {
        try {
            String music = trackResult.getPreview();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(music);
            mediaPlayer.prepare();
            mediaPlayer.start();
            ImageView b = findViewById(R.id.main5_stop);
            b.setImageResource(R.drawable.pause);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleResult(String result) {
        TrackResponse responseTrack = gson.fromJson(result, TrackResponse.class);
        trackResults.addAll(responseTrack.getData());
        mAdapter.notifyDataSetChanged();

    }
}
