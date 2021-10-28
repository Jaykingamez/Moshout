package tp.edu.moshout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tp.edu.moshout.song.Account;
import tp.edu.moshout.song.Playlist;
import tp.edu.moshout.AppUtil.*;
import tp.edu.moshout.song.Song;

public class PlaylistActivity extends AppCompatActivity {

     private String PLAYLISTKEY = "keyToPlaylist";

    private final String SELECTEDPLAYLISTS = "selectedPlaylists";

     private ArrayList<Playlist> playlists;
     //To save our data into SharedPreferences, a global database in Android, we're going to have
     //to turn the ArrayList into json which can be stored. Google's Gson library is here to achieve
     // this task
     private Gson gson = new Gson();

    SharedPreferences playlistContainer;
    SharedPreferences.Editor editor;

    //reference to the root layout of the activity
    private LinearLayout playlistActivity;

    //show this when naming the new playlist
    private ConstraintLayout nameNewPlaylist;
    //show this when name of the new playlist is confirmed
    private ConstraintLayout createNewPlaylist;

    //use this to extract the name of new playlist from user
    private EditText newPlaylistName;
    private String stringNewPlaylistName;

    //show user that the creation of the new playlist is confirmed
    private TextView confirmPlaylistName;

    //trashcan to confirm delete
    private ImageView trashCan;

    //cancel Image
    private ImageView cancel;

    //layout that shows playlists to be deleted
    private LinearLayout deletedPlaylist;

    private ConstraintLayout deletePlaylist;

    //username of account
    private String username;
    //account in use
    private Account account;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        playlistActivity = findViewById(R.id.playlistActivity);
        nameNewPlaylist = findViewById(R.id.nameNewPlaylist);
        createNewPlaylist = findViewById(R.id.createNewPlaylist);
        newPlaylistName = findViewById(R.id.newPlaylistName);
        confirmPlaylistName = findViewById(R.id.confirmPlaylistName);
        trashCan = findViewById(R.id.trashcan);
        cancel = findViewById(R.id.cancel);
        deletedPlaylist = findViewById(R.id.deletedPlaylist);
        deletePlaylist = findViewById(R.id.deletePlaylist);

        retrievePlaylist();

        //initialize RecyclerView to display playlist
        initRecyclerView();


    }

    private void retrievePlaylist(){
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



        //set positions of the playlist in playlists
        for(int i = 0; i < playlists.size() ; i++){
            playlists.get(i).setPlaylistPosition(i);
        }
    }

    //commit data to database
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

    //initialize view to display playlists
    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclePlaylist);
        //create recycleViewAdapter to fill view with playlist information
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this,  playlists, "showPlaylist",null, 0, trashCan );
        recyclerView.setAdapter(adapter);
        //creation of a grid based layout for the playlists with a column count of 3
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

    }

    //if user want to create new playlist, set a layout to be visible for user to do so
    public void addNewPlaylist(View view){
        nameNewPlaylist.setVisibility(View.VISIBLE);
    }

    //whe user's click button to confirm creation of New Playlist
    public void confirmAdditionOfNewPlaylist(View view){
        //set naming UI invisible
        nameNewPlaylist.setVisibility(View.INVISIBLE);
        stringNewPlaylistName = newPlaylistName.getText().toString();
        if(stringNewPlaylistName.isEmpty()){
            Toast.makeText(this, "Playlist name can't be empty, sorry (シ_ _)シ", Toast.LENGTH_SHORT).show();
            return;
        }
        //display user the name of playlist they've created in another UI
        confirmPlaylistName.setText(stringNewPlaylistName);
        //set that another UI visible for user to confirm creation of playlist
        createNewPlaylist.setVisibility(View.VISIBLE);
    }

    public void createNewPlaylist(View view){
        //set naming playlist UI invisble
        createNewPlaylist.setVisibility(View.INVISIBLE);
        //create new playlist
        Playlist newPlaylist = new Playlist(null, new ArrayList<Song>());
        //set new playlist's name
        newPlaylist.setPlaylistName(stringNewPlaylistName);
        //add new playlist to global list of playlists
        playlists.add(newPlaylist);
        //commit changes
        commitPlaylist();
        //update current playlists
        retrievePlaylist();
        //reinitialize recyclerView to display changes made
        initRecyclerView();
    }

    public void removeTrash(View view){
        List<Playlist> removedPlaylist;
        String returnedValue = playlistContainer.getString(SELECTEDPLAYLISTS, "");

        //get songs they are to be removed
        if (returnedValue.isEmpty()) {
            return;
        } else {
            removedPlaylist = gson.fromJson(returnedValue,
                    new TypeToken<List<Playlist>>() {
                    }.getType());
        }


        //help count iterations
        int counter = 1;
        for (Playlist playlist : removedPlaylist) {
            /* Logic behind it
            For example, we have 5 playlist in a list. We delete the 3rd playlist in the list. The list now
            has 4 playlists, however, the position that the playlists have in the list is unchanged. The 4th playlist
            would still be set as 4th position, and the 5th playlist would be set as 5th position. Thus playlists
            with positions higher than the one deleted need to be adjusted to fit this change. Thus the old 4th position
            playlist will become the new 3rd position, and the old 5th position playlist will become the new 4th position
            */


            //before playlist is removed, readjust all song positions
            for(int i = counter; i < removedPlaylist.size(); i++){
                //if another song position in removedSongs is more than the deleted song position
                if(removedPlaylist.get(i).getPlaylistPosition() > playlist.getPlaylistPosition()){
                    //reduce that song's position by one
                    removedPlaylist.get(i).setPlaylistPosition(removedPlaylist.get(i).getPlaylistPosition() - 1);
                }
            }
            //before playlist is removed, readjust song positions in playlist
            for(int i = playlist.getPlaylistPosition(); i < playlists.size(); i++){
                //if another song's position in the playlist is more than the deleted song position
                if(playlists.get(i).getPlaylistPosition() > playlist.getPlaylistPosition()){
                    //reduce that song's position by one
                    playlists.get(i).setPlaylistPosition(playlists.get(i).getPlaylistPosition()-1);
                }
            }
            //finally delete playlist after adjustments were made
            playlists.remove(playlist.getPlaylistPosition());
            Log.i("Toxicity",playlist.getPlaylistPosition()+playlist.getPlaylistName());

            //inflate deleted Layout with playlists that were selected
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            //use listitem xml as template
            View myView = inflater.inflate(R.layout.layout_listitem, null);

            //get deleted playlist's ImageView
            ImageView playlistImage = myView.findViewById(R.id.playlistImage);
            //get deleted playlist's TextView
            TextView playlistName = myView.findViewById(R.id.confirmPlaylistName);

            //if playlist is not empty
            if(playlist.getPlaylistWithPreview().size() > 0){
                //set deleted playlist's ImageView
                Picasso.get().load(playlist.getPlaylistWithPreview().get(0).getImageUrl()).into(playlistImage);
            }
            //else just give playlist its default view

            //get deleted playlist's TextView
            playlistName.setText(playlist.getPlaylistName());

            //add the song to be deleted
            deletedPlaylist.addView(myView);

        }
        //show the new layout layered on top of overall layout and wait for user to click
        deletePlaylist.setVisibility(View.VISIBLE);
        editor = playlistContainer.edit();
        //since all selected songs have been prepared for deletion, clear all information in SharedPrefs
        //about the selected songs
        editor.remove(SELECTEDPLAYLISTS).commit();
    }

    public void cancelDeletion(View view){
        //refresh recycler view to remove all evidence of deletion process
        initRecyclerView();
        editor = playlistContainer.edit();
        //remove selection of selected songs
        editor.remove(SELECTEDPLAYLISTS).commit();
        cancel.setVisibility(View.INVISIBLE);
    }

    //do not delete playlists
    public void noDelete(View view){
        //refresh playlist with original songs
        retrievePlaylist();
        //refresh recycler views
        initRecyclerView();
        //hide delete UI
        deletePlaylist.setVisibility(View.INVISIBLE);
        //hide trash icon
        trashCan.setVisibility(View.INVISIBLE);
        //remove views added in deletedPlaylist
        deletedPlaylist.removeAllViews();
        //hide cancel ImageView
        cancel.setVisibility(View.INVISIBLE);
    }
    //delete playlists
    public void yesDelete(View view){
        //enforce changes made to the new playlist to the deletes made
        commitPlaylist();
        //hide trash icon
        trashCan.setVisibility(View.INVISIBLE);
        //get and set new playlist
        retrievePlaylist();
        //update recyclerview accordingly
        initRecyclerView();
        //hide delete UI
        deletePlaylist.setVisibility(View.INVISIBLE);
        //remove views added in deletedPlaylist
        deletedPlaylist.removeAllViews();
        //hide cancel ImageView
        cancel.setVisibility(View.INVISIBLE);
    }

    //go back to homepage
    public void goHome(View view){
        NavBar.goHome(view,this);
    }
    //go to playlist page
    public void goPlaylist(View view){
        NavBar.goPlaylist(view, this);
    }




}