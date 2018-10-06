package co.edu.intecap.boundservice.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import co.edu.intecap.boundservice.R;
import co.edu.intecap.boundservice.model.Song;
import co.edu.intecap.boundservice.service.BoundMusicService;

public class MainActivity extends AppCompatActivity {

    private ListView listview;
    private Button btnPlayStop;
    private TextView txtSongName;
    private CardView cardView;
    private ArrayList<Song> listOfContents;
    private AdapterClass adapter;
    private String path;
    static String songName;
    public static boolean playing = false;
    private BoundMusicService mService;
    private boolean mBound;
    private String songPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If Android Marshmello or above, then check if permission is granted
        if (Build.VERSION.SDK_INT >= 23){
            checkPermission();
        } else{
            initViews();
        }
    }



    void initViews() {
        //initializing views
        btnPlayStop = (Button) findViewById(R.id.btnPlayStop);
        txtSongName = (TextView) findViewById(R.id.txtSongName);
        cardView = (CardView) findViewById(R.id.cardView);
        listview = (ListView) findViewById(R.id.listView);
        listOfContents = new ArrayList<>();

        //If music is playing already on opening starting the app, player should be visible with Stop button
        if (playing) {
            txtSongName.setText(songName);
            cardView.setVisibility(View.VISIBLE);
            btnPlayStop.setText("Stop");
        }

        //Gives you the full path of phone memory
        path = Environment.getExternalStorageDirectory().getAbsolutePath();

        //Calling the function which fetches the list of music files
        initList(path);

        //initializing the adapter and passing the context, list item and list of references of SongObject
        adapter = new AdapterClass(this, R.layout.list_item, listOfContents);
        listview.setAdapter(adapter);

        //handling events when user clicks on any music file in list view
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //player is visible
                cardView.setVisibility(View.VISIBLE);

                //getting absolute path of selected song from bean class 'SongObject'
                Song song = listOfContents.get(position);
                songPath = song.getAbsolutePath();
                //Get and set the name of song in the player
                songName = listOfContents.get(position).getFileName();
                txtSongName.setText(songName);

               if(mService != null){
                   mService.stop();
                   mService.start(song.getAbsolutePath());
                   btnPlayStop.setText("Stop");
               }
            }

        });

        //Handling events when button Play/Stop is clicked in the player
        btnPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService.isPlaying()) {
                    //If song is playing and user clicks on Stop button
                    //Stop the song by calling stopService() and change boolean value
                    //text on button should be changed to 'Play'
                    playing = false;
                    btnPlayStop.setText("Play");
                    mService.stop();
                } else if (!playing) {
                    //If song is not playing and user clicks on Play button
                    //Start the song by calling startService() and change boolean value
                    //text on button should be changed to 'Stop'
                    playing = true;
                    btnPlayStop.setText("Stop");
                    mService.start(songPath);
                }
            }
        });

    }

    //Fetching .mp3 and .mp4 files from phone storage
    void initList(String path) {
        try {
            File file = new File(path);
            File[] filesArray = file.listFiles();
            String fileName;
            for (File file1 : filesArray) {
                if (file1.isDirectory()) {
                    initList(file1.getAbsolutePath());
                } else {
                    fileName = file1.getName();
                    if ((fileName.endsWith(".mp3"))) {
                        listOfContents.add(new Song(file1.getName(), file1.getAbsolutePath()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Handling permissions for Android Marshmallow and above
    void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //if permission granted, initialize the views
            initViews();
        } else {
            //show the dialog requesting to grant permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initViews();
                } else {
                    //permission is denied (this is the first time, when "never ask again" is not checked)
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        finish();
                    }
                    //permission is denied (and never ask again is  checked)
                    else {
                        //shows the dialog describing the importance of permission, so that user should grant
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("You have forcefully denied Read storage permission.\n\nThis is necessary for the working of app." + "\n\n" + "Click on 'Grant' to grant permission")
                                //This will open app information where user can manually grant requested permission
                                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                })
                                //close the app
                                .setNegativeButton("Don't", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                });
                        builder.setCancelable(false);
                        builder.create().show();
                    }
                }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, BoundMusicService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        unbindService(mConnection);
    }



    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            BoundMusicService.LocalBinder binder = (BoundMusicService.LocalBinder) iBinder;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

}
