package tp.edu.moshout.song;

import java.io.Serializable;

public class Song implements Serializable {
    private String name;

    private String artist;

    private String imageUrl;

    private String previewUrl;

    private int position;

    public Song(String name, String artist, String imageUrl, String previewUrl, int position){
        this.name = name;
        this.artist = artist;
        this.imageUrl = imageUrl;
        this.previewUrl = previewUrl;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {this.position = position;}

}
