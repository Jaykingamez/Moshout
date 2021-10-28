package tp.edu.moshout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tp.edu.moshout.AppUtil.RecyclerViewAdapter;
import tp.edu.moshout.song.Account;
import tp.edu.moshout.song.Playlist;
import tp.edu.moshout.song.Song;

public class PlaylistSongsActivity extends AppCompatActivity {
    //position of playlist in playlists
    private int playlistPosition;
    //key to get playlists
    private String PLAYLISTKEY = "keyToPlaylist";
    //key to get songs selected for deletion
    private final String SELECTEDSONGS = "selectedSongs";

    private ArrayList<Playlist> playlists;
    //To save our data into SharedPreferences, a global database in Android, we're going to have
    //to turn the ArrayList into json which can be stored. Google's Gson library is here to achieve
    // this task
    private Gson gson = new Gson();

    SharedPreferences playlistContainer;
    SharedPreferences.Editor editor;

    //reference to RecylerView displaying songs
    private RecyclerView songPlaylist;

    //reference to trash icon to delete songs
    private ImageView songTrash;
    //reference to back button/cross button when going back to previous Activity or cancel deletion of songs
    private ImageView songBackCross;

    //name of the playlist that the user selected
    private TextView playlistName;
    //layout of the display that shows the songs to be deleted
    private LinearLayout deletedLayout;
    //layout to confirm deletion of songs
    private ConstraintLayout confirmDelete;

    //username of account
    private String username;
    //account in use
    private Account account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_songs);

        songPlaylist = findViewById(R.id.songPlaylist);
        songTrash = findViewById(R.id.songTrash);
        songBackCross = findViewById(R.id.songBackCross);
        playlistName = findViewById(R.id.playlistName);
        deletedLayout = findViewById(R.id.deletedLayout);

        confirmDelete = findViewById(R.id.confirmDelete);

        confirmDelete.setVisibility(View.INVISIBLE);
        songTrash.setVisibility(View.INVISIBLE);


        //obtain playlists from SharedPrefs
        retrievePlaylist();
        //display songs in recyclerView
        initRecyclerView();

    }

    private void retrievePlaylist() {

        playlistContainer = PreferenceManager.getDefaultSharedPreferences(this);
        username = playlistContainer.getString("username","");
        //get account json
        String returnedValue = playlistContainer.getString(username, "");

        //initialize playlists lists
        if (returnedValue.isEmpty()) {
            playlists = new ArrayList<>();
        } else {
            account = gson.fromJson(returnedValue,
                    new TypeToken<Account>() {
                    }.getType());
            playlists = (ArrayList<Playlist>) account.getPlaylists();
            if (playlists==null){
                playlists = new ArrayList<>();
            }
        }

        //get playlist that songs are in
        Bundle bundle = getIntent().getExtras();
        playlistPosition = bundle.getInt("playlistPosition");

        //set playlist name for display
        playlistName.setText(playlists.get(playlistPosition).getPlaylistName());

        //set positions of the songs in playlists
        for(int i = 0; i < playlists.get(playlistPosition).getPlaylistWithPreview().size() ; i++){
            playlists.get(playlistPosition).getPlaylistWithPreview().get(i).setPosition(i);
        }

        //Log.i("Help", playlists.get(playlistPosition).getPlaylistWithPreview().get(1).getPosition()+"");


    }

    private void initRecyclerView() {
        //create recycleViewAdapter to adapt the playlists
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, playlists, "showSongs", null, playlistPosition, songTrash);
        songPlaylist.setAdapter(adapter);
        //creation of a grid based layout for the playlists with a column count of 3
        songPlaylist.setLayoutManager(new GridLayoutManager(this, 3));

    }

    private void commitPlaylist() {
        account.setPlaylists(playlists);
        //convert to json string for storage
        String json = gson.toJson(account);

        editor = playlistContainer.edit();

        //remove current key as well as it values
        editor.remove(username).commit();
        editor.putString(username, json);
        editor.commit();
    }

    //when the user clicks button to go back or cancel deletion of song
    public void goBack(View view) {
        // if the song is tagged with cross if deletion is ongoing, cancel deletion
        if (String.valueOf(songTrash.getTag()).equals("cross")) {
            songTrash.setTag("");
            //show backward arrow for user to be able to click to go back to previous activity
            songBackCross.setImageResource(R.drawable.backward_arrow);
            //refresh recycler view to remove all evidence of deletion process
            initRecyclerView();
            editor = playlistContainer.edit();
            //remove selection of selected songs
            editor.remove(SELECTEDSONGS).commit();
        } else {
            //go back to previous activity if deletion process is not ongoing
            //simulate pressing the back button on Android phone
            onBackPressed();
        }

    }
    //when user click the trash icon
    public void removeTrash(View view) {
        List<Song> removedSongs;
        String returnedValue = playlistContainer.getString(SELECTEDSONGS, "");

        //get songs they are to be removed
        if (returnedValue.isEmpty()) {
            return;
        } else {
            removedSongs = gson.fromJson(returnedValue,
                    new TypeToken<List<Song>>() {
                    }.getType());
        }


        //help count iterations
        int counter = 1;
        for (Song song : removedSongs) {
            /* Logic behind it
            For example, we have 5 songs in a list. We delete the 3rd song in the list. The list now
            has 4 songs, however, the position that the songs have in the list is unchanged. The 4th Song
            would still be set as 4th position, and the 5th song would be set as 5th position. Thus songs
            with positions higher than the one deleted need to be adjusted to fit this change. Thus the old 4th position
            song will become the new 3rd position, and the old 5th position song will become the new 4th position
            */


            //before song is removed, readjust all song positions
            for(int i = counter; i < removedSongs.size(); i++){
                //if another song position in removedSongs is more than the deleted song position
                if(removedSongs.get(i).getPosition() > song.getPosition()){
                    //reduce that song's position by one
                    removedSongs.get(i).setPosition(removedSongs.get(i).getPosition() - 1);
                }
            }
            //before song is removed, readjust song positions in playlist
            for(int i = song.getPosition(); i < playlists.get(playlistPosition).getPlaylistWithPreview().size(); i++){
                //if another song's position in the playlist is more than the deleted song position
                if(playlists.get(playlistPosition).getPlaylistWithPreview().get(i).getPosition() > song.getPosition()){
                    //reduce that song's position by one
                    playlists.get(playlistPosition).getPlaylistWithPreview().get(i).setPosition(song.getPosition()-1);
                }
            }
            //finally delete song after adjustments were made
            playlists.get(playlistPosition).getPlaylistWithPreview().remove(song.getPosition());

            //inflate deleted Layout with songs that were selected
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            //use songcircle xml as template
            View myView = inflater.inflate(R.layout.layout_songcircle, null);

            //get deleted song's ImageView
            ImageView playlistSongImage = myView.findViewById(R.id.playlistSongImage);
            //get deleted song's TextView
            TextView playlistSongName = myView.findViewById(R.id.playlistSongName);

            //set deleted song's ImageView
            Picasso.get().load(song.getImageUrl()).into(playlistSongImage);
            //get deleted song's TextView
            playlistSongName.setText(song.getName());

            //add the song to be deleted
            deletedLayout.addView(myView);

        }
        //show the new layout layered on top of overall layout and wait for user to click
        confirmDelete.setVisibility(View.VISIBLE);
        editor = playlistContainer.edit();
        //since all selected songs have been prepared for deletion, clear all information in SharedPrefs
        //about the selected songs
        editor.remove(SELECTEDSONGS).commit();
    }

    //do not delete songs
    public void noDelete(View view){
        //refresh playlist with original songs
        retrievePlaylist();
        //refresh recycler views
        initRecyclerView();
        //hide delete UI
        confirmDelete.setVisibility(View.INVISIBLE);
        //hide trash icon
        songTrash.setVisibility(View.INVISIBLE);
        //remove views added in deletedLayout
        deletedLayout.removeAllViews();
        goBack(view);

    }
    //delete songs
    public void yesDelete(View view){
        //enforce changes made to the new playlist to the deletes made
        commitPlaylist();
        //hide trash icon
        songTrash.setVisibility(View.INVISIBLE);
        //get and set new playlist
        retrievePlaylist();
        //update recyclerview accordingly
        initRecyclerView();
        //hide delete UI
        confirmDelete.setVisibility(View.INVISIBLE);
        //remove views added in deletedLayout
        deletedLayout.removeAllViews();
        goBack(view);
    }



}