package tp.edu.moshout.jsonobject;

//According to Spotify Web Api Documentation
/*https://developer.spotify.com/documentation/web-api/reference/object-model/#:~:text=Reference%20in%20beta!-,Object%20Model,simplify%20retrieval%20of%20further%20objects.*/

public class Received {
    //Making a search query would return this object, where the key "tracks" must be used to obtain Root object
    private Paging tracks;

    public Paging getTracks() {
        return tracks;
    }
}
