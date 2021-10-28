package tp.edu.moshout.jsonobject;

import java.util.List;

//According to Spotify Web Api Documentation
/*https://developer.spotify.com/documentation/web-api/reference/object-model/#:~:text=Reference%20in%20beta!-,Object%20Model,simplify%20retrieval%20of%20further%20objects.*/

public class Album {
    //private ExternalUrl external_url;

    private List<Image> images;

    //Return a List of Image objects
    public List<Image> getImages() {
        return images;
    }
}
