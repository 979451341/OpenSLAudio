package com.ywl5320.openslaudio;

import android.Manifest;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("opensl_audio");
    }

    AssetManager assetManager;

    static boolean isPlayingAsset = false;
    static boolean created = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assetManager = getAssets();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS}, 5);
        }
        ((SeekBar) findViewById(R.id.volume)).setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int lastProgress = 100;
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        assert progress >= 0 && progress <= 100;
                        lastProgress = progress;
                    }
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        int attenuation = 100 - lastProgress;
                        int millibel = attenuation * -50;
                        setVolumeAudioPlayer(millibel);
                    }
                });

    }

    public static native boolean createAssetAudioPlayer(AssetManager assetManager, String filename);
    // true == PLAYING, false == PAUSED
    public static native void setPlayingAssetAudioPlayer(boolean isPlaying);
    public static native void setStop();
    public native void createEngine();
    public native void createBufferQueueAudioPlayer();
    public native void shutdown();
    public native void setVolumeAudioPlayer(int millibel);
    public void clickplay(View view) {

        if (!created) {
            createEngine();
            //createBufferQueueAudioPlayer();
            created = createAssetAudioPlayer(assetManager, "mydream.m4a");
        }
        if (created) {
            isPlayingAsset = true;
            setPlayingAssetAudioPlayer(isPlayingAsset);
        }
    }

    public void clickpause(View view) {
        if (created) {
            isPlayingAsset = false;
            setPlayingAssetAudioPlayer(isPlayingAsset);
        }

    }
    public void clickstop(View view) {
        if(created){
            setStop();
        }

    }
    @Override
    protected void onPause()
    {
        isPlayingAsset = false;
        setPlayingAssetAudioPlayer(isPlayingAsset);
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        shutdown();
        super.onDestroy();
    }
}
