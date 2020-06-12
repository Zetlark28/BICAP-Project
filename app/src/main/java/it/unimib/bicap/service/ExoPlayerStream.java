package it.unimib.bicap.service;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import it.unimib.bicap.HomePage;
import it.unimib.bicap.HomePageSomministratore;
import it.unimib.bicap.R;
import it.unimib.bicap.databinding.ActivityExoplayerStreamBinding;
import it.unimib.bicap.databinding.ActivityPdfViewerBinding;
import it.unimib.bicap.db.DBManager;

// TODO: aggiungere cose fighe tipo la progressbar during buffering e vedere se quel fullscreen ci sta per davvero


public class ExoPlayerStream extends AppCompatActivity {

    private SimpleExoPlayerView exoPlayerView;
    private SimpleExoPlayer exoPlayer;
    private Uri videoUri;
    private String nomeProgetto;
    private String idProgetto;
    private String passi;
    private String nPasso;
    private DBManager dbManager;
    private ActivityExoplayerStreamBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_exoplayer_stream);
        dbManager = new DBManager(getApplicationContext());
        idProgetto = getIntent().getStringExtra("idProgetto");
        passi = getIntent().getStringExtra("listaPassi");
        nomeProgetto = getIntent().getStringExtra("NomeProgetto");
        nPasso = getIntent().getStringExtra("nPasso");

        //binding = ActivityExoplayerStreamBinding.inflate(getLayoutInflater());
        //View view = binding.getRoot();
        //setContentView(view);
        exoPlayerView = findViewById(R.id.exoplayerview);
        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

            // TODO: Passare il link del video ed assegnarlo alla seguente variabile
            String linkVideo = getIntent().getStringExtra("linkVideo");
            // Prova di video
            //linkVideo = "https://firebasestorage.googleapis.com/v0/b/bicap-ffecb.appspot.com/o/Video%2FFile-49?alt=media&token=f049e892-e69b-4360-b017-1c792a8ab431";
            videoUri = Uri.parse(linkVideo);
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videoUri, dataSourceFactory,extractorsFactory, null, null);
            exoPlayerView.setPlayer(exoPlayer);
            exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
            exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        } catch (Exception e) {
            Log.i("ExoPlayerStream", "Error while reproducing video with ExoPlayer " + e.toString());
        }
        exoPlayer.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady && playbackState == exoPlayer.STATE_READY) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }

                if (playbackState == ExoPlayer.STATE_ENDED) {

                    dbManager.updatePasso(Integer.parseInt(idProgetto), Integer.parseInt(nPasso)+1);
                    Intent intentIntermediate = new Intent(getApplicationContext(), Intermediate.class);
                    intentIntermediate.putExtra("mode", "daTerminare");
                    intentIntermediate.putExtra("idProgetto", idProgetto);
                    intentIntermediate.putExtra("listaPassi", passi);
                    intentIntermediate.putExtra("NomeProgetto", nomeProgetto);
                    startActivity(intentIntermediate);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    finish();
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("AppState", "OnPause");
        exoPlayer.setPlayWhenReady(false);
    }

    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Torna alla Homepage");
        builder.setMessage("Sicuro di voler tornare indietro?\n" + "Questo renderà visibile il questionario nella sezione \"Survey Sospesi\"");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent HomePageSomministratoreRicarica = new Intent(getApplicationContext(), HomePage.class);
                startActivity(HomePageSomministratoreRicarica);
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void onBackPressed() {
        showDialog();
    }
}

