package co.edu.intecap.boundservice.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

public class BoundMusicService extends Service {

    private final IBinder localBinder = new LocalBinder();
    private MediaPlayer mediaPlayer;
    private int currentPosition;

    @Override
    public void onCreate() {
        super.onCreate();


    }

    public boolean isPlaying() {
       boolean isPlaying = false;
       try{
           isPlaying =  mediaPlayer.isPlaying();
       }catch (Exception e){
           isPlaying = false;
       }

       return isPlaying;
    }

    public class LocalBinder extends Binder {
        public BoundMusicService getService() {
            // Return this instance of BoundMusicService so clients can call public methods
            return BoundMusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }


    public void start(String musicPath){

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(musicPath);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        try {
            if(mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }catch (Exception e){
            e.getMessage();
        }

    }

    public void pause(){
        mediaPlayer.pause();
        currentPosition = mediaPlayer.getCurrentPosition();
    }


    public void resume(){
        mediaPlayer.seekTo(currentPosition);
        mediaPlayer.start();
    }

    @Override
    public void onDestroy() {
       stop();
    }
}
