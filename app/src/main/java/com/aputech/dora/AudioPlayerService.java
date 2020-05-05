package com.aputech.dora;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;

import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

public class AudioPlayerService extends Service {
    private SimpleExoPlayer player;
    Uri uri;
    @Override
    public void onCreate() {
        super.onCreate();
        final Context context =this;
        player = ExoPlayerFactory.newSimpleInstance(context,new DefaultTrackSelector());
        DefaultDataSourceFactory dataSourceFactory =new DefaultDataSourceFactory(context, Util.getUserAgent(context,"AudioDemo"));
        ExtractorsFactory extractorsFactory= new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(Uri.parse("https://firebasestorage.googleapis.com/v0/b/dora-275f8.appspot.com/o/psycho.mp3?alt=media&token=fdd979c3-ef4b-40f8-94c0-ddea4a096ee8"),
                dataSourceFactory,
                extractorsFactory,
                null,
                null);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }

    @Override
    public void onDestroy() {
        player.release();
        player=null;
        super.onDestroy();
    }

    public AudioPlayerService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
