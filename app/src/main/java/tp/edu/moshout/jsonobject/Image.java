package tp.edu.moshout.jsonobject;

//According to Spotify Web Api Documentation
/*https://developer.spotify.com/documentation/web-api/reference/object-model/#:~:text=Reference%20in%20beta!-,Object%20Model,simplify%20retrieval%20of%20further%20objects.*/

public class Image {
    private String url;

    private Integer height;

    private Integer width;

    //Obtain the url where the image is stored
    public String getUrl() {
        return url;
    }

    //Obtain height of images in pixels
    public Integer getHeight() {
        return height;
    }

    //Obtain width of images in pixels
    public Integer getWidth() {
        return width;
    }
}
