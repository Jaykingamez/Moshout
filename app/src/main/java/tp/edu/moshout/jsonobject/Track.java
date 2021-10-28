package tp.edu.moshout.jsonobject;


import java.util.List;

//According to Spotify Web Api Documentation
/*https://developer.spotify.com/documentation/web-api/reference/object-model/#:~:text=Reference%20in%20beta!-,Object%20Model,simplify%20retrieval%20of%20further%20objects.*/

public class Track {
    //The Track object contains keys to other objects
    private String name;

    private int popularity;

    private String preview_url;

    //The object type: “track”.
    private String type;

    private Album album;

    private List<Artist> artists;

    //Gets the name of the track
    public String getName() {
        return name;
    }

    //Gets the track's popularity on Spotify
    public int getPopularity() {
        return popularity;
    }

    //This allows one to obtain a 30 seconds preview of the track on Spotify
    public String getPreview_url() {
        return preview_url;
    }

    public String getType() {
        return type;
    }

    public Album getAlbum() {
        return album;
    }

    //Returns a list of Artist objects
    public List<Artist> getArtist(){
        return artists;
    }

}
