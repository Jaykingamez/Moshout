package tp.edu.moshout;

import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import tp.edu.moshout.AppUtil.RecyclerViewAdapter;
import tp.edu.moshout.song.Account;
import tp.edu.moshout.song.Playlist;
import tp.edu.moshout.song.Song;

public class PlayMusic extends AppCompatActivity {

    private String PLAYLISTKEY = "keyToPlaylist";

    //This is the built-in MediaPlayer object that we will use
    //to play the music
    private MediaPlayer player = null;

    //This is the position of the song in playback.
    //We set it to 0 here so that it starts at the beginning.
    private int musicPosition = 0;

    //This Button variable is created to link to the Play button
    // at the playback screen. We need to do this because it will act
    //as both Play and Pause button.
    private ImageView btnPlayPause = null;

    //Matrix that store the songs
    private String[][] songMatrix;

    //playlist that store song objects
    private Playlist playlist;

    //position of the song in the positionArray (uses Java indexes)
    private int position;
    //position of the song in the shuffledPositionArray (uses Java indexes)
    private int shuffledPosition;
    //stores the positions of songs
    private Integer[] positionArray;
    //stores the shuffled positions of song
    private Integer[] shuffledPositionArray;

    //Song Data
    private String songName;
    private String artistName;
    private String imageUrl;
    private String previewUrl;

    //Music Player UI
    private ImageView songArt;
    private TextView songText;
    private TextView artistText;
    private ImageView forwardPlay;
    private ImageView backwardPlay;
    private ImageView shuffle;
    private ImageView loop;

    private RecyclerView showPlaylist;

    //false means no loop, true means do loop
    private boolean isLoop;
    //false means no shuffle, true means shuffle
    private boolean isShuffle;

    //seekbar is the music player bar that shows how far the song has played through 30 seconds
    private SeekBar seekBar;
    //the handler handles moving the seekbar to the right position at the right time
    private Handler seekBarHandler;

    private ArrayList<Playlist> playlists;
    //To save our data into SharedPreferences, a global database in Android, we're going to have
    //to turn the ArrayList into json which can be stored. Google's Gson library is here to achieve
    // this task
    private Gson gson = new Gson();

    SharedPreferences playlistContainer;
    SharedPreferences.Editor editor;

    //username of account
    private String username;
    //account in use
    private Account account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initialization of the scene
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        songArt = findViewById(R.id.songImage);
        songText = findViewById(R.id.songTitle);
        artistText = findViewById(R.id.artist);

        btnPlayPause = findViewById(R.id.btnPlayPause);
        forwardPlay = findViewById(R.id.forwardPlay);
        backwardPlay = findViewById(R.id.backPlay);
        shuffle = findViewById(R.id.shuffle);
        loop = findViewById(R.id.loop);

        showPlaylist = findViewById(R.id.showPlaylist);

        //do not loop
        isLoop = false;
        //do not shuffle
        isShuffle = false;


        seekBar = findViewById(R.id.seekBar);
        seekBarHandler = new Handler();

        //get SharedPrefs which store data
        playlistContainer = PreferenceManager.getDefaultSharedPreferences(this);
        username = playlistContainer.getString("username","");
        //get json String
        String returnedValue = playlistContainer.getString(username,"");

        //if it's empty, create new ArrayList, if it's not, turn json into List<Playlist> to displayed
        //playlists in it
        if (returnedValue.isEmpty()){
            playlists = new ArrayList<>();
        }
        else{
            //playlists = new ArrayList<>();
            account= gson.fromJson(returnedValue,
                    new TypeToken<Account>(){}.getType());
            playlists = (ArrayList<Playlist>) account.getPlaylists();
            if (playlists==null){
                playlists = new ArrayList<>();
            }
        }

        //obtain song information
        retrieveData();
        //update song information to UI
        setSongData();
        //prepare music player for playback
        preparePlayer();
        //update seekbar if musicPosition changes
        updateSeekBar();
        //allow user to change move the seekbar to change musicPosition
        userUpdateSeekBar();
    }

    private void retrieveData() {

        Bundle bundle = getIntent().getExtras();

        //Get song matrix
        //songMatrix = (String[][]) bundle.getSerializable("songPackage");
        //Get song position within the matrix
        position = bundle.getInt("position");

        //Get playlist
        playlist = (Playlist) bundle.getSerializable("playlist");

        Log.i("Errorleh", playlist.getPlaylistWithPreview().get(0).getName());

        /*//set the length of the positionArray based on the number of songs
        positionArray = new Integer[songMatrix.length];



        //creation of the positionArray
        for (int len = 0; len < songMatrix.length; len++) {
            positionArray[len] = len;
            }
*/
        positionArray = new Integer[playlist.getPlaylistWithPreview().size()];
        for (int len = 0; len < positionArray.length; len++) {
            positionArray[len] = len;
        }


    }

    //cast as List, but operations such as remove can't be done on it,
    //so one must cast it as a LinkedList to do operations on it
        /*List<Integer> intList = new LinkedList<>(Arrays.asList(notNullArray));

        for (int pos = 0; pos < 5; pos++) {
            if (songMatrix[pos][3] != null) {
                intList.add(pos + 1);
            }
        }*/
    /* notNullArray = intList.toArray(notNullArray);*/

    //position -= 5 - notNullArray.length;

    private void setSongData() {
        /*//if the user hasn't shuffled the songs
        if (!isShuffle) {
            songName = songMatrix[position][0];
            artistName = songMatrix[position][1];
            imageUrl = songMatrix[position][2];
            previewUrl = songMatrix[position][3];
        } else {
            //if the user wants the songs shuffled
            //obtain the position within the songMatrix by getting the position information
            //through the shuffledPositionArray
            songName = songMatrix[shuffledPositionArray[shuffledPosition]][0];
            artistName = songMatrix[shuffledPositionArray[shuffledPosition]][1];
            imageUrl = songMatrix[shuffledPositionArray[shuffledPosition]][2];
            previewUrl = songMatrix[shuffledPositionArray[shuffledPosition]][3];
        }
*/
        if (!isShuffle) {
            songName = playlist.getPlaylistWithPreview().get(position).getName();
            artistName = playlist.getPlaylistWithPreview().get(position).getArtist();
            imageUrl = playlist.getPlaylistWithPreview().get(position).getImageUrl();
            previewUrl = playlist.getPlaylistWithPreview().get(position).getPreviewUrl();
        } else {
            songName = playlist.getPlaylistWithPreview().get(shuffledPositionArray[shuffledPosition]).getName();
            artistName = playlist.getPlaylistWithPreview().get(shuffledPositionArray[shuffledPosition]).getArtist();
            imageUrl = playlist.getPlaylistWithPreview().get(shuffledPositionArray[shuffledPosition]).getImageUrl();
            previewUrl = playlist.getPlaylistWithPreview().get(shuffledPositionArray[shuffledPosition]).getPreviewUrl();
        }
        /*playlist.getPlaylistWithPreview()[position].getName();
        playlist.getPlaylistWithPreview()[position].getArtist();
        playlist.getPlaylistWithPreview()[position].getImageUrl();
        playlist.getPlaylistWithPreview()[position].getPreviewUrl();

        playlist.getPlaylistWithPreview()[shuffledPositionArray[shuffledPosition]].getName();
        playlist.getPlaylistWithPreview()[shuffledPositionArray[shuffledPosition]].getArtist();
        playlist.getPlaylistWithPreview()[shuffledPositionArray[shuffledPosition]].getImageUrl();
        playlist.getPlaylistWithPreview()[shuffledPositionArray[shuffledPosition].getPreviewUrl();
        */


        //load image into the ImageView
        Picasso.get().load(imageUrl).into(songArt);
        //set song name
        songText.setText(songName);
        //set artist name
        artistText.setText(artistName);

    }


    private void preparePlayer() {
        // 1. Create a new MediaPlayer.
        player = new MediaPlayer();

        //The try and catch codes are required by the prepare  method.
        //It is to catch any error that may occur and to handle the error.
        //You don't have to worry about this for now.
        //Right here, the error is printed out to the console using the
        // method printStackTrace().
        try {
            //2. This sets the Audio Stream Type to music streaming.
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);

            //3. Set the source of the music.
            //For example, the url for Billie Jean will look like:
            player.setDataSource(previewUrl);

            //4. Prepare the player for playback.
            player.prepare();

            //set the Seekbar maximum duration, 30s
            seekBar.setMax(player.getDuration() / 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playOrPauseMusic(View view) {
        // 1. If no MediaPlayer object is created, call
        //preparePlayer method to create it.
        if (player == null)
            preparePlayer();
        //2. If the player is not playing
        if (!player.isPlaying()) {
            //1. If the position of the music is greater than 0
            if (musicPosition > 0) {
                // 1. Get the player to go to the music position
                player.seekTo(musicPosition);
            }

            //2. Start the player
            player.start();

            //3. Set the text of the play button to "PAUSE"
            btnPlayPause.setImageResource(R.drawable.pause);

            //4. When the music ends, stop the player.
            gracefullyStopWhenMusicEnds();
        } else {
            //3.Pause the music.
            pauseMusic();
        }

    }

    private void pauseMusic() {
        //1. Pause the player.
        player.pause();

        // 2. set the image to a play button for user to click if they want to play
        btnPlayPause.setImageResource(R.drawable.playbutton);

        // 3. Get the current position of the music that is playing.
        musicPosition = player.getCurrentPosition();

    }

    private void gracefullyStopWhenMusicEnds() {

        player.setOnCompletionListener(mediaPlayer -> {
            //if the user wants the song to loop
            if (isLoop) {
                //go back to the start of the song
                player.seekTo(0);
                //and play the song
                player.start();
                return;
            }
            //else just stop the song
            stopActivities();
        });

    }

    private void stopActivities() {
        //if the player does exist
        if (player != null) {
            //set music position
            musicPosition = 0;
            //stop the music player
            player.stop();
            //release resources used on musicPlayer
            player.release();
            //since the musicPlayer's resources are released, there is no musicPlayer
            player = null;
            //set the image resource back to a play button
            btnPlayPause.setImageResource(R.drawable.playbutton);

        }

    }

    //method that updates seek bar
    private void updateSeekBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //if music player does exist
                if (player != null) {
                    //get current position of the musicPlayer
                    musicPosition = player.getCurrentPosition();
                    //change the position in milliseconds to seconds
                    int positionSeconds = musicPosition / 1000;
                    //set the seekbar to the musicPosition
                    seekBar.setProgress(positionSeconds);
                }
                //after every second, update seekbar
                seekBarHandler.postDelayed(this, 1000);
            }
        });

    }

    //function that allows the user to update the seekbar
    private void userUpdateSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            //if the user moves the seekbar
            public void onProgressChanged(SeekBar seekBar, int progress, boolean userInput) {
                //if player exists and there is input from the user
                if (player != null && userInput) {
                    //change musicPlayer to the position the user designated
                    //(progress is in seconds, changing to milliseconds which the player understands)
                    player.seekTo(progress * 1000);
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    // if the user wants the next Track to be played
    public void nextTrack(View view) {
        //if the user wants the song list to be shuffled
        if (isShuffle) {
            //as long as the shuffledPosition is less than the length of the positionArray
            if (shuffledPosition < positionArray.length - 1) {
                shuffledPosition += 1;
                //stop current musicPlayer Activities
                stopActivities();
                //update UI with the next song information
                setSongData();
                //play the song
                playOrPauseMusic(view);
            }

        } else {
            //if the user does not want the song list to be shuffled
            //as long as the position is less than the length of the positionArray
            if (position < positionArray.length - 1) {
                position += 1;
                //stop current musicPlayer Activities
                stopActivities();
                //update UI with the next song information
                setSongData();
                //play the song
                playOrPauseMusic(view);
            }
        }
    }

    // if the user wants the next Track to be played
    public void previousTrack(View view) {
        //if the user wants the trackList to be shuffled
        if (isShuffle) {
            //as long as shuffledPosition is more than 0
            if (shuffledPosition > 0) {
                shuffledPosition -= 1;
                //stop current musicPlayer Activities
                stopActivities();
                //update UI with the next song information
                setSongData();
                //play the song
                playOrPauseMusic(view);
            }
        } else {
            //if the user does not want the trackList to be shuffled
            //as long as position is more than 0
            if (position > 0) {
                position -= 1;
                //stop current musicPlayer Activities
                stopActivities();
                //update UI with the next song information
                setSongData();
                //play the song
                playOrPauseMusic(view);
            }
        }
    }

    //function where the Looping Ui is done
    public void doLoop(View view) {
        //if the user does not want the song to be looped
        if (isLoop) {
            //show an image indicating that the user does not want the song to be looped
            loop.setImageResource(R.drawable.noloop);
            //song is not be looped
            isLoop = false;
        } else {
            //if the user wants the song to be looped
            //show an image indicating that the user wants the song to be looped
            loop.setImageResource(R.drawable.yesloop);
            //song is to be looped
            isLoop = true;
        }
    }

    //function that shuffles the song list
    public void shuffleArray(View view) {
        //if the user wants the song to be shuffled
        if (!isShuffle) {
            //the shuffled array is set as the current position array
            shuffledPositionArray = positionArray;

            //cast as List, but operations such as remove can't be done on it,
            //so one must cast it as a LinkedList to do operations on it
            List<Integer> intList = new LinkedList<>(Arrays.asList(shuffledPositionArray));

            //remove current position to be shifted to the front of the shuffled list
            intList.remove(Integer.valueOf(position));

            //debugging purposes
            //intList.toArray(shuffledPositionArray);
            //Log.i("Errorleh", "removed shuffle: " + Arrays.toString(shuffledPositionArray));

            //Through the Collections class, use the method shuffle to shuffle the list
            Collections.shuffle(intList);

            //push other items of the list to the right by adding the removed position back to the
            //front of the list
            intList.add(0, position);

            //set the shuffledPositionArray which will be iterated in place of the positionArray
            shuffledPositionArray = intList.toArray(shuffledPositionArray);

            //debugging purposes
            Log.i("Errorleh", "Shuffled array: " + Arrays.toString(shuffledPositionArray));

            //set the position of the song which the user has clicked shuffled on to be zero
            shuffledPosition = 0;

            //display to the user that the song list has been shuffled
            shuffle.setImageResource(R.drawable.yesshuffle);

            //song list is shuffled
            isShuffle = true;
        } else {
            //display to the user that the song list is not shuffled
            shuffle.setImageResource(R.drawable.noshuffle);

            //set position to the current song position
            position = shuffledPositionArray[shuffledPosition];

            //create the original positionArray
            //positionArray = new Integer[songMatrix.length];

            /*for (int len = 0; len < songMatrix.length; len++) {
                positionArray[len] = len;
                if (positionArray[len] == shuffledPositionArray[shuffledPosition]) {
                    position = len;

                    Log.i("Errorleh", String.valueOf(position));
                }

            }*/

            //song list is not shuffled
            isShuffle = false;
        }

    }

    //for the back button that allows user to go back to previous Activity
    public void previousActivity(View view) {
        //Log.d("Errorleh", playlists.get(0).getPlaylistWithPreview().get(0).getName());
        commitPlaylist();
        //stop the music player and set it null
        stopActivities();
        //simulate pressing the back button on Android phone
        onBackPressed();

    }

    public void showPlaylist(View view){
        showPlaylist.setVisibility(View.VISIBLE);
        initRecyclerView();

    }

    private void commitPlaylist(){
        account.setPlaylists(playlists);
        //convert to json string for storage
        String json = gson.toJson(account);

        editor = playlistContainer.edit();
        //remove current key as well as it values
        editor.remove(username).commit();
        editor.putString(username, json);
        editor.commit();
    }

    private void initRecyclerView(){
        Song song;
        if (!isShuffle) {
            song = playlist.getPlaylistWithPreview().get(position);
        } else {
            song = playlist.getPlaylistWithPreview().get(shuffledPositionArray[shuffledPosition]);
        }
        //RecyclerView recyclerView = findViewById(R.id.showPlaylist);
        //create recycleViewAdapter to adapt the playlists
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, playlists, "addSong", song, 0, null );
        showPlaylist.setAdapter(adapter);
        //creation of a grid based layout for the playlists with a column count of 3
        showPlaylist.setLayoutManager(new GridLayoutManager(this, 3));
    }
}