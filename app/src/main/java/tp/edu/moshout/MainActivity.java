package tp.edu.moshout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import tp.edu.moshout.apicalls.RetrofitInterface;
import tp.edu.moshout.jsonobject.*;

import tp.edu.moshout.apicalls.Call;
import tp.edu.moshout.song.*;
import tp.edu.moshout.AppUtil.*;
import tp.edu.moshout.AppUtil.*;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //PlaceHolder for Homepage songs
    private final static String GENRE = "Papa Roach";
    private static final String QUERYTYPE = "track";
    private static final String MARKET = null;
    private static final int LIMIT = 5;
    private static final int OFFSET = 0;


    //List of Track objects obtained from the Api Call
    private static List<Track> trackList;

    private ImageButton searchButton;
    private TextView homepage;
    private EditText searchBar;

    private LinearLayout navbar;
    private LinearLayout createdPlaylists;

    //boolean will show whether user is currently using the search functionality,
    //false = not using, true = using
    private boolean searchBoolean = false;

    //Playlists at homepage
    Playlist likeThis;
    Playlist listenAgain;
    Playlist trending;

    private ArrayList<Playlist> playlists;

    SharedPreferences playlistContainer;
    SharedPreferences.Editor editor;

    //To save our data into SharedPreferences, a global database in Android, we're going to have
    //to turn the ArrayList into json which can be stored. Google's Gson library is here to achieve
    // this task
    private Gson gson = new Gson();

    //username of account
    private String username;
    //account in use
    private Account account;


    //deals with the showing of keyboard when clicking EditText
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initialization of the scene
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting the phone keyboard manager
        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);

        searchButton = findViewById(R.id.searchButton);
        searchBar = findViewById(R.id.searchText);
        homepage = findViewById(R.id.homepage);
        navbar = findViewById(R.id.navbar);
        createdPlaylists = findViewById(R.id.createdPlaylists);


        //searchbar set to not be visible on start
        searchBar.setVisibility(View.INVISIBLE);

        //Call for Api Token
        getToken(Call.getRetrofitToken());

        //when the user decides to press the search button
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //remove the focus on the searchbar so the flickering icon wouldn't appear
                //in the searchbar
                //searchBar.clearFocus();

                prepareSearch(v);

                //remove Android keyboard from screen
                //imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);

                //send query to SearchActivity to carry out query
                sendDataToSearchActivity(searchBar.getText().toString());



                return true;
            }
            return false;
        });

        setUpPlaylist();
    }

    //method to get Spotify Api Token
    private void getToken(RetrofitInterface retrofitInterface) {

        /*
        Headers are built in Call.java under the apicalls package.

        To make an api call to Spotify, an access level must be specified. Which is under
        Call.getGrantType()

        Using RxJava's Observable objects to provide asynchronous functionality,
        so that whole app wouldn't lock up when an api call is made. The object works like this.
        A background thread subscribes to the result of the observable object, so it will be
        wait for a result to be returned. When that happens, the result can be processed, with the
        various functions at one's disposal.

        */
        retrofitInterface.getToken(Call.authorizationHeader(), Call.getGrantType())
                .toObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<AuthToken>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    //On receiving the results, the Access Token is set within Call.java,
                    //to be used in future calls
                    @Override
                    public void onNext(AuthToken value) {
                        Call.setAccess_Token(value.getAccess_Token());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("Errorleh", e.toString());

                    }

                    //As the name suggests, once the results are obtained. The subscription is over,
                    //thus the results can be sent be further processing.
                    @Override
                    public void onComplete() {
                        //Initializing mainActivity screen with songs, using the token obtained
                        querySearch("likeThis", Call.getRetrofitQuery(), GENRE, QUERYTYPE, MARKET, LIMIT,
                                OFFSET, "textLike", "imageLike");
                        querySearch("listenAgain", Call.getRetrofitQuery(), GENRE, QUERYTYPE, MARKET, LIMIT,
                                OFFSET, "textListen", "imageListen");
                        /*querySearch(Call.getRetrofitQuery(), GENRE, QUERYTYPE, MARKET, LIMIT,
                                OFFSET, "textPlaylist", "imagePlaylist");*/
                        querySearch("trending", Call.getRetrofitQuery(), GENRE, QUERYTYPE, MARKET, LIMIT,
                                OFFSET, "textTrending", "imageTrending");


                    }
                });
    }

    //queries to spotify for track information
    private void querySearch(String playlist, RetrofitInterface retrofitInterface, String query, String queryType,
                             String market, int limit, int offset, String textId, String imageId) {
        retrofitInterface.getSongs(Call.getQueryHeader(), query, queryType, market, limit, offset)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Received>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    //The Received object received from the Api Call is processed here
                    @Override
                    public void onNext(Received value) {
                        //The Received object is processed into the Paging object with the use of
                        // the key in the Received object
                        Paging received = value.getTracks();

                        /*The key in the Paging object returns a List of Track Objects, which will be
                        handled with under the handleList method, which requires the textId and
                        ImageId of the TextView and ImageView objects respectively
                        that the data will be used for.*/
                        trackList = received.getItemList();
                        handleList(playlist, trackList, textId, imageId);


                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    //method that takes the data returned from Spotify Api to add them to
    // TextViews and ImageViews
    private void handleList(String playlist, List<Track> trackList, String text, String image) {

        /*
        The thread that calls the handleList method is actually a background thread,
        functions such as the setting of the image sources for ImageViews and the text for
        TextViews require the main Thread/Ui Thread, thus it is called
        */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 5; i++) {
                    String textId = text + i;
                    String imageId = image + i;


                    //Get TextView object with its name in String
                    TextView textView = findViewById(getResources().getIdentifier(textId, "id", getPackageName()));
                    //Get ImageView object with its name in String
                    ImageView imageView = findViewById(getResources().getIdentifier(imageId, "id", getPackageName()));

               /*Image Url for the song can be obtained through the Album object, through the
                 List of Image object, get the first element in the list,
                 which stores the largest image possible
                 Thus using getUrl, to obtain the image url from the Image Object*/
                    String imageUrl = trackList.get(i - 1).getAlbum().getImages().get(0).getUrl();

                /*
                Name of the song can be obtained from the specified Track object in the List,
                through the getName method
                */
                    textView.setText(trackList.get(i - 1).getName());

                    //Picasso is a library that can load images from a url with a single line of code
                    Picasso.get().load(imageUrl).into(imageView);
                }


                int songsWithPreview = 0;

                //determine the number of songs with previews
                for (int preview = 0; preview < trackList.size(); preview++){
                    if(trackList.get(preview).getPreview_url() != null){
                        songsWithPreview+=1;
                    }
                }
                //create an array with the length of the number of songs with a preview
                Song[] songArray = new Song[trackList.size()];
                Song[] songPreviewArray = new Song[songsWithPreview];

                //used to determine the position/index of the song in the array
                int preview = 0;

                for (int pos = 0; pos < trackList.size() ; pos++) {
                    //Create Song object
                    Song song = new Song(trackList.get(pos).getName(), trackList.get(pos).getArtist().get(0).getName(),
                            trackList.get(pos).getAlbum().getImages().get(0).getUrl(), trackList.get(pos).getPreview_url(),
                            pos);
                    songArray[pos] = song;

                    //if track has a preview
                    if (trackList.get(pos).getPreview_url() != null) {
                        //set song object to position in the array
                        songPreviewArray[preview] = song;
                        preview += 1;
                    }

                }

                List<Song> listSongArray = Arrays.asList(songArray);
                List<Song> listPreviewArray = Arrays.asList(songPreviewArray);

                if ("likeThis".equals(playlist)) {
                    likeThis = new Playlist(listSongArray, listPreviewArray);
                } else if ("listenAgain".equals(playlist)) {
                    listenAgain = new Playlist(listSongArray, listPreviewArray);
                } else if ("trending".equals(playlist)) {
                    trending = new Playlist(listSongArray, listPreviewArray);
                }
            }


        });

    }

    //when the search button is clicked
    public void prepareSearch(View view) {
        //when the user cancels the search
        if (searchBoolean) {
            //Sets search icon to the searchButton
            searchButton.setImageResource(R.drawable.search);
            //Shows the "Hompage" Text
            homepage.setVisibility(View.VISIBLE);
            //Remove the bar allocated for searching
            searchBar.setVisibility(View.GONE);

            //Remove Android Keyboard that still stays open when search is cancelled
            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);

            //user is not searching
            searchBoolean = false;

            //when the user initiates a search
        } else {
            //Sets search icon to a cross for the user to cancel when clicked again
            searchButton.setImageResource(R.drawable.cross);
            //Don't show the "Hompage" Text
            homepage.setVisibility(View.INVISIBLE);
            //Show the bar allocated for searching
            searchBar.setVisibility(View.VISIBLE);

            //Focuses on EditText, allowing user to type
            searchBar.requestFocus();

            //Shows keyboard when clicking search button
            imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);

            //user is searching
            searchBoolean = true;
        }

    }

    //send data to searchActivity, where the query will be done
    private void sendDataToSearchActivity(String query) {
        //1. Create a new Intent and specify the source and destination screen/activity.
        Intent intent = new Intent(this, SearchActivity.class);

        //2. Store the song information into the Intent object to be
        //sent over to the destination screen.
        intent.putExtra("query", query);


        //Go to SearchActivity
        startActivity(intent);

    }

    private void sendDataToPlayMusic(Playlist playlist, int position) {
        // Create a new Intent and specify the source and destination screen/activity.
        Intent intent = new Intent(this, PlayMusic.class);

        // Track's position number relative to the array of tracks
        intent.putExtra("playlist", playlist);

        int songPos = 0;

        //get song position
        for (Song song: playlist.getPlaylistWithPreview()){
            if(song.getName().equals(playlist.getPlaylist().get(position).getName())){
                position = songPos;
            }
            songPos += 1;

        }

        //send song position with playlist
        intent.putExtra("position", position );

        startActivity(intent);
    }

    public void likeThisSend(View view) {
        // 1. Get the ID of the selected song
        String resourceId = getResources().getResourceEntryName(view.getId());

        // 2. Position of song relative to List<Track>
        String position = resourceId.substring(resourceId.length() - 1);

        // 3. Obtain the song
        Song song = likeThis.getPlaylist().get(Integer.parseInt(position) - 1);

        //if a preview is not available, make a popup telling the user so
        if (song.getPreviewUrl() == null) {
            Toast.makeText(this, "Not available for play", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("Errorleh", "Clicked like this");
        sendDataToPlayMusic(likeThis, Integer.parseInt(position) - 1);
    }

    public void listenAgainSend(View view) {
        // 1. Get the ID of the selected song
        String resourceId = getResources().getResourceEntryName(view.getId());

        // 2. Position of song relative to List<Track>
        String position = resourceId.substring(resourceId.length() - 1);

        // 3. Obtain the song
        Song song = listenAgain.getPlaylist().get(Integer.parseInt(position) - 1);

        //if a preview is not available, make a popup telling the user so
        if (song.getPreviewUrl() == null) {
            Toast.makeText(this, "Not available for play", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("Errorleh", "Clicked listen Again");
        sendDataToPlayMusic(listenAgain, Integer.parseInt(position) - 1);
    }

    public void trendingSend(View view) {
        // 1. Get the ID of the selected song
        String resourceId = getResources().getResourceEntryName(view.getId());

        // 2. Position of song relative to List<Track>
        String position = resourceId.substring(resourceId.length() - 1);

        // 3. Obtain the song
        Song song = trending.getPlaylist().get(Integer.parseInt(position) - 1);

        //if a preview is not available, make a popup telling the user so
        if (song.getPreviewUrl() == null) {
            Toast.makeText(this, "Not available for play", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("Errorleh", "Clicked trending");
        sendDataToPlayMusic(trending, Integer.parseInt(position) - 1);

    }

    //set up playlists to be displayed on MainActivity
    public void setUpPlaylist(){
        createdPlaylists.removeAllViews();
        retrievePlaylist();
        if(playlists.size() > 0){
            //loop through the playlist
            for(int element = 0; element < playlists.size(); element++){
                //inflate Layout with playlists that were created
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                //use listitem xml as template
                View myView = inflater.inflate(R.layout.layout_listitem, null);

                //get playlist's ImageView
                ImageView playlistImage = myView.findViewById(R.id.playlistImage);
                //get playlist's TextView
                TextView playlistName = myView.findViewById(R.id.confirmPlaylistName);

                //if playlist is not empty
                if(playlists.get(element).getPlaylistWithPreview().size() > 0){
                    //set deleted playlist's ImageView
                    Picasso.get().load(playlists.get(element).getPlaylistWithPreview().get(0).getImageUrl()).into(playlistImage);
                }
                //else just give playlist its default view

                //get playlist's TextView
                playlistName.setText(playlists.get(element).getPlaylistName());

                putListenerOnPlaylist(playlistImage, element);

                //add the playlists to be displayed
                createdPlaylists.addView(myView);
            }

        }else{
            TextView textView = new TextView(this);
            textView.setText("Sorry it seems you have not created any playlists");
            createdPlaylists.addView(textView);
        }

    }

    private void putListenerOnPlaylist(View view, int element){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tell user playlist that is selected
                Toast.makeText(getApplicationContext(), playlists.get(element).getPlaylistName() + "is selected", Toast.LENGTH_SHORT).show();
                //prepare Intent to go to another Activity
                Intent intent = new Intent(getApplicationContext(), PlaylistSongsActivity.class);
                //add data about the playlist that is selected
                intent.putExtra("playlistPosition", element);
                //go to next Activity
                getApplicationContext().startActivity(intent);
            }
        });
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



    public void goPlaylist(View view){
        NavBar.goPlaylist(view, this);
    }



}
