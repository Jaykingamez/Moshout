package tp.edu.moshout.AppUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tp.edu.moshout.PlayMusic;
import tp.edu.moshout.PlaylistSongsActivity;
import tp.edu.moshout.R;
import tp.edu.moshout.song.Playlist;
import tp.edu.moshout.song.Song;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<Playlist> playlists;
    //refer to context where adapter was instantiated
    private Context mContext;
    //function that adapter was called to serve, hardcoded String value
    private String function;
    //If song was to be added, put here
    private Song song;
    //the position that the song was going to be set to.
    private int privateSongPosition;

    //To save our data into SharedPreferences, a global database in Android, we're going to have
    //to turn the ArrayList into json which can be stored. Google's Gson library is here to achieve
    // this task
    private Gson gson = new Gson();

    SharedPreferences playlistContainer;
    SharedPreferences.Editor editor;

    //key to get sharedPrefs list of selected songs
    private final String SELECTEDSONGS = "selectedSongs";
    //key to get sharedPrefs list of selected playlists
    private final String SELECTEDPLAYLISTS = "selectedPlaylists";

    //reference to trashCan object
    private ImageView trashCan;

    //constructor of recyclerViewAdapter that will help process the Playlist ArrayList
    public RecyclerViewAdapter(Context mContext, ArrayList<Playlist> playlists, String function, Song song, int position, ImageView trashCan) {
        this.playlists = playlists;
        this.mContext = mContext;
        this.function = function;
        this.song = song;
        this.privateSongPosition = position;
        this.trashCan = trashCan;
        playlistContainer = PreferenceManager.getDefaultSharedPreferences(mContext);

    }

    //on calling for the creation of the adapter, holders need to be created. They are xml templates for how
    //information ought to be displayed
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //if information is related to songs, use songcircle xml file, if its playlist information, use listitem xml
        if (function.equals("showSongs")) {
            //inflate fills the recyclerview with repeated copies of layout_listitem
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_songcircle, parent, false);
            //creation of the holder
            SongHolder holder = new SongHolder(view);
            return holder;
        } else {
            //inflate fills the recyclerview with repeated copies of layout_listitem
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
            //creation of the holder
            PlaylistHolder holder = new PlaylistHolder(view);
            return holder;
        }

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        //if it is about showing playlist information
        if (!function.equals("showSongs")) {
            //if playlist is empty, a playlist image cannot be set, thus only only its name is set,
            //if it is not empty, call function created in holder to set ImageView
            if (!playlists.get(position).getPlaylistWithPreview().isEmpty()) {
                //Load the image of the first song into the playlist as the playlist image
                ((PlaylistHolder) holder).setImageView(position);

                //Load the playlist name under the image
            }
            ((PlaylistHolder) holder).setTextView(position);

        }

        //if function is about allowing user to add song, go to holder and activate Listener function
        if (function.equals("addSong")) {
            ((PlaylistHolder) holder).setListener((PlaylistHolder) holder, position);
            //if in PlaylistActivity and the user clicks on the playlist to view its songs,
            //this function allows the songs to be displayed
        } else if (function.equals("showPlaylist")) {
            ((PlaylistHolder) holder).showSongs(position);
            ((PlaylistHolder) holder).deletePlaylist(position);
            //if songs are to be displayed, set ImageView and TextView, set function to allow it to be played,
            //also set function allowing it to be deleted
        } else if (function.equals("showSongs")) {
            ((SongHolder) holder).setUpSongs(position);
            ((SongHolder) holder).playSong(position);
            ((SongHolder) holder).deleteSong(position);

        }

    }

    //function decides how many times OnBindViewHolder is looped based on the int return
    @Override
    public int getItemCount() {
        //loop over the songs in the  playlistWithPreview
        if (function.equals("showSongs")) {
            return playlists.get(privateSongPosition).getPlaylistWithPreview().size();
        } else {
            //if not just loop over to show all of the playlists
            return playlists.size();
        }

    }

    //holder that handles all the playlists
    public class PlaylistHolder extends RecyclerView.ViewHolder {
        //meant for displaying image of the playlist
        ImageView playlistImage;
        //meant for displaying name of the playlist
        TextView playlistName;
        //meant handling the LinearLayout that encapsulates the entire view
        LinearLayout linearLayout;

        //reference to playlists that are selected
        List<Playlist> selectedPlaylists;

        public PlaylistHolder(@NonNull View itemView) {
            super(itemView);
            playlistImage = itemView.findViewById(R.id.playlistImage);
            playlistName = itemView.findViewById(R.id.confirmPlaylistName);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            if (trashCan != null)
                //trashCan assists in the deletion of playlists
                trashCan.setVisibility(View.INVISIBLE);


        }


        public void setTextView(int position) {
            playlistName.setText(playlists.get(position).getPlaylistName());

        }

        public void setImageView(int position) {
            Picasso.get().load(playlists.get(position).getPlaylistWithPreview().get(0).getImageUrl()).into(playlistImage);

        }

        //listener help to add songs to playlists, inform users when dong so
        public void setListener(PlaylistHolder holder, int position) {
            playlistImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, playlists.get(position).getPlaylistName() + "is selected", Toast.LENGTH_SHORT).show();
                    playlists.get(position).addSong(song);
                    //refreshes the adapter for user to see results of effort
                    onBindViewHolder(holder, position);

                }
            });

        }

        //when user want songs of playlist to be displayed
        public void showSongs(int position) {
            playlistImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //tell user playlist that is selected
                    Toast.makeText(mContext, playlists.get(position).getPlaylistName() + "is selected", Toast.LENGTH_SHORT).show();
                    //prepare Intent to go to another Activity
                    Intent intent = new Intent(mContext, PlaylistSongsActivity.class);
                    //add data about the playlist that is selected
                    intent.putExtra("playlistPosition", position);
                    //go to next Activity
                    mContext.startActivity(intent);
                }
            });
        }

        public void deletePlaylist(int position){
            //onLongClick listener is triggered when the user holds on song Image
            playlistImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //get selectedPlaylists json
                    String returnedValue = playlistContainer.getString(SELECTEDPLAYLISTS, "");

                    //if it's empty, create new ArrayList, if it's not, turn json into List<Song> for songs
                    //to be added to it
                    if (returnedValue.isEmpty()) {
                        selectedPlaylists = new ArrayList<>();
                    } else {
                        selectedPlaylists = gson.fromJson(returnedValue,
                                new TypeToken<List<Playlist>>() {
                                }.getType());
                    }

                    //add red border around selected song
                    playlistImage.setBackgroundResource(R.drawable.redborder);
                    //add selected playlist to selected playlist list
                    selectedPlaylists.add(playlists.get(position));


                    //commit to sharedPrefs database by preparing data to json
                    String json = gson.toJson(selectedPlaylists);
                    //prepares the SharedPrefs for editing
                    editor = playlistContainer.edit();
                    //remove current key as well as it values
                    editor.remove(SELECTEDPLAYLISTS).commit();
                    //set new data as value
                    editor.putString(SELECTEDPLAYLISTS, json);
                    editor.commit();

                    //it's set visible for user to confirm deletion
                    trashCan.setVisibility(View.VISIBLE);
                    //tags will be used to identify the drawable that the trashcan is currently using
                    trashCan.setTag("cross");

                    //get the view that the mContext belongs to
                    View rootView = ((Activity) mContext).getWindow().getDecorView().findViewById(android.R.id.content);
                    //get the reference of the cancel button by finding it through the rootView
                    ImageView playlistCross = rootView.findViewById(R.id.cancel);
                    playlistCross.setVisibility(View.VISIBLE);

                    return true;
                }
            });

        }




    }

    //holder that handles dealing wih songs
    public class SongHolder extends RecyclerView.ViewHolder {
        //display image of song
        ImageView playlistSongImage;
        //display song's name
        TextView playlistSongName;
        //reference to a CardView in songCircle xml, to help show user that song is selected for deletion
        CardView radialCircle;
        //reference to songs that are selected
        List<Song> selectedSongs;

        public SongHolder(@NonNull View itemView) {
            super(itemView);
            playlistSongImage = itemView.findViewById(R.id.playlistSongImage);
            playlistSongName = itemView.findViewById(R.id.playlistSongName);
            radialCircle = itemView.findViewById(R.id.radialCircle);

            selectedSongs = new ArrayList<>();


            //View rootView = ((Activity) mContext).getWindow().getDecorView().findViewById(android.R.id.content);
            //trashCan = rootView.findViewById(R.id.trashcan);
            if (trashCan != null)
                //do not show trashcan to user if user has not selected any song for deletion
                trashCan.setVisibility(View.INVISIBLE);
            //linearLayout = itemView.findViewById(R.id.linearLayout);


        }

        //set song information to the Views
        public void setUpSongs(int position) {
            Picasso.get().load(playlists.get(privateSongPosition).getPlaylistWithPreview().get(position).getImageUrl()).into(playlistSongImage);
            playlistSongName.setText(playlists.get(privateSongPosition).getPlaylistWithPreview().get(position).getName());
        }

        //allow user to play song when View is tapped on
        public void playSong(int position) {
            playlistSongImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, playlists.get(privateSongPosition).getPlaylistWithPreview().get(position).getName() + "is selected", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, PlayMusic.class);
                    //add position of song in playlist for reference
                    intent.putExtra("position", position);
                    //add position of playlist in global playlists for reference
                    intent.putExtra("playlist", playlists.get(privateSongPosition));
                    mContext.startActivity(intent);
                }

            });


        }

        //function that allows song to be deleted
        public void deleteSong(int position) {
            //onLongClick listener is triggered when the user holds on song Image
            playlistSongImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //get selectedSongs json
                    String returnedValue = playlistContainer.getString(SELECTEDSONGS, "");

                    //if it's empty, create new ArrayList, if it's not, turn json into List<Song> for songs
                    //to be added to it
                    if (returnedValue.isEmpty()) {
                        selectedSongs = new ArrayList<>();
                    } else {
                        selectedSongs = gson.fromJson(returnedValue,
                                new TypeToken<List<Song>>() {
                                }.getType());
                    }

                    //get red colour stored in colours.xml
                    int redColour = mContext.getResources().getColor(R.color.red);
                    //create red border around selected song
                    radialCircle.setCardBackgroundColor(redColour);
                    //add selected song to selected songs list
                    selectedSongs.add(playlists.get(privateSongPosition).getPlaylistWithPreview().get(position));


                    //commit to sharedPrefs database by preparing data to json
                    String json = gson.toJson(selectedSongs);
                    //prepares the SharedPrefs for editing
                    editor = playlistContainer.edit();
                    //remove current key as well as it values
                    editor.remove(SELECTEDSONGS).commit();
                    //set new data as value
                    editor.putString(SELECTEDSONGS, json);
                    editor.commit();

                    //it's set visible for user to confirm deletion
                    trashCan.setVisibility(View.VISIBLE);
                    //tags will be used to identify the drawable that the trashcan is currently using
                    trashCan.setTag("cross");

                    //get the view that the mContext belongs to
                    View rootView = ((Activity) mContext).getWindow().getDecorView().findViewById(android.R.id.content);
                    //get the reference of the backButton by finding it through the rootView
                    ImageView songBackCross = rootView.findViewById(R.id.songBackCross);
                    //change it to cross for user to cancel deletion
                    songBackCross.setImageResource(R.drawable.cross);

                    return true;
                }
            });
        }


    }
}
