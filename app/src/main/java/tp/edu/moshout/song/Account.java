package tp.edu.moshout.song;

import java.util.List;

public class Account {
    //store information regarding the user
    private String fullName;
    private String email;
    private String password;
    private String birthDate;
    private String country;

    private List<Playlist> playlists;

    public Account(String fullName, String email, String password, String birthDate, String country){
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.country = country;
    }

    public List<Playlist> getPlaylists(){
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists){
        this.playlists = playlists;
    }

    public String getPassword(){
        return password;
    }

}
