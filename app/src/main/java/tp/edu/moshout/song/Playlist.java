package tp.edu.moshout.song;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Playlist implements Serializable {

    //store information regarding the playlist

    private String playlistName;

    private String playlistImage;

    private List<Song> playlist;

    private int playlistPosition;

    private List<Song> playlistWithPreview;

    private static Song potentialNewSong;



    public Playlist(List<Song> playlist , List<Song> playlistWithPreview){
        this.playlist = playlist;
        this.playlistWithPreview = playlistWithPreview;
        //playlistImage = playlistWithPreview.get(0).getImageUrl();
    }

    public void addSong(Song song){
        this.playlistWithPreview.add(song);
        Log.i("Errorleh", this.playlistWithPreview.get(0).getName());
    }

    public void removeSong(int position){
        this.playlistWithPreview.remove(position);
        updateSongPosition();
        Log.i("Errorleh", this.playlistWithPreview.get(0).getName());

    }

    private void updateSongPosition(){
        int counter = 0;
        for(Song song: this.playlistWithPreview) {
            song.setPosition(counter);
            counter += 1;
        }
    }

    public List<Song> getPlaylist(){
        return playlist;
    }

    public List<Song> getPlaylistWithPreview(){
        return playlistWithPreview;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public void setPlaylistPosition(int position){
        playlistPosition = position;
    }

    public int getPlaylistPosition(){
        return playlistPosition;
    }




}
