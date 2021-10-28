package tp.edu.moshout;

import androidx.appcompat.app.AppCompatActivity;



import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import tp.edu.moshout.apicalls.Call;
import tp.edu.moshout.apicalls.RetrofitInterface;
import tp.edu.moshout.jsonobject.Paging;
import tp.edu.moshout.jsonobject.Received;
import tp.edu.moshout.jsonobject.Track;
import tp.edu.moshout.song.Playlist;
import tp.edu.moshout.song.Song;
import tp.edu.moshout.AppUtil.*;

public class SearchActivity extends AppCompatActivity {

    //the user's query
    private String searchQuery;

    //the text in the searchbar
    private EditText queryText;

    //boolean will show whether user is currently using the search functionality,
    //false = not using, true = using
    private boolean searchBoolean = false;

    //button to initiate search
    private ImageButton buttonSearch;

    //track placeholder
    private static final String QUERYTYPE = "track";
    private static final String MARKET = null;
    private static final int LIMIT = 5;
    private static final int OFFSET = 0;

    //List of Track objects obtained from the Api Call
    private List<Track> trackList;
    //Playlist object of SearchActivity
    private Playlist playlist;

    //deals with the showing of keyboard when clicking EditText
    private InputMethodManager imm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initialization of the scene
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //setting the phone keyboard manager
        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);

        queryText = findViewById(R.id.queryText);
        buttonSearch = findViewById(R.id.buttonSearch);

        //Obtain the user's query
        retrieveSearchQuery();

        //focus on the current layout, as the focus on the EditText is carried over from the
        //MainActivity
        findViewById(R.id.mainLayout).requestFocus();

        //Do an api call to Spotify for the query
        querySearch(Call.getRetrofitQuery(), searchQuery, QUERYTYPE, MARKET, LIMIT, OFFSET,
                "textSearch", "imageSearch");

        //if the user changes their mind and wan to search for something else.
        queryText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    searchQuery = queryText.getText().toString();
                    //query for new song information
                    querySearch(Call.getRetrofitQuery(), searchQuery, QUERYTYPE, MARKET, LIMIT, OFFSET,
                            "textSearch", "imageSearch");
                    //search has been done, change it back to a state where the user is not searching
                    prepareSearch(buttonSearch);
                    return true;
                }
                return false;
            }
        });


    }

    //Querying for tracks to Spotify
    private void querySearch(RetrofitInterface retrofitInterface, String query, String queryType,
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

                        List<Track> receivedItems = received.getItemList();


                        if (receivedItems.size() >= 5)
                            trackList = received.getItemList();


                        handleList(receivedItems, textId, imageId);




                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    //obtain searchQuery from MainActivity
    private void retrieveSearchQuery() {

        Bundle queryPackage = this.getIntent().getExtras();

        //obtain query
        searchQuery = queryPackage.getString("query");

        //display query in searchbar
        queryText.setText(searchQuery);

        //Move the text cursor to the end of the EditText, as it is by default flickering
        //in front of the added text
        queryText.setSelection(queryText.getText().length());
        ;
    }

    //when the search button is clicked
    public void prepareSearch(View view) {
        //when the user initiates the search
        if (!searchBoolean) {

            //Sets search icon to a cross for the user to cancel when clicked again
            buttonSearch.setImageResource(R.drawable.cross);
            //automatically simulates a user tapping on the searchbar/EditText
            queryText.requestFocus();
            //Show keyboard
            imm.showSoftInput(queryText, InputMethodManager.SHOW_IMPLICIT);
            //show cursor flashing
            queryText.setCursorVisible(true);

            //search is initiated
            searchBoolean = true;

            /*when the user cancels the search*/
        } else {
            //Sets search icon to the searchButton
            buttonSearch.setImageResource(R.drawable.search);
            //Hide Keyboard
            imm.hideSoftInputFromWindow(queryText.getWindowToken(), 0);
            //remove focus on the EditText by focusing on the mainlayout
            findViewById(R.id.mainLayout).requestFocus();
            //disable cursor flashing
            queryText.setCursorVisible(false);

            //search is cancelled
            searchBoolean = false;

        }

    }

    //method that takes the data returned from Spotify Api to add them to
    // TextViews and ImageViews
    public void handleSelection(View view) {

        // 1. Get the ID of the selected song
        String resourceId = getResources().getResourceEntryName(view.getId());

        // 2. Position of song relative to List<Track>
        String position = resourceId.substring(resourceId.length() - 1);

        // 3. Obtain the song
        Track track = trackList.get(Integer.parseInt(position) - 1);

        //if a preview is not available, make a popup telling the user so
        if (track.getPreview_url() == null){
            Toast.makeText(this, "Not available for play", Toast.LENGTH_SHORT).show();
            return;

        }

        //4. Popup notifying the user that song is selected
        Toast.makeText(this, "Streaming song: " + track.getName(), Toast.LENGTH_SHORT).show();

        //send data to PlayMusic Activity for music to be played
        sendDataToActivity(Integer.parseInt(position)-1);


    }

    //method that takes the data returned from Spotify Api to add them to
    // TextViews and ImageViews
    private void handleList(List<Track> recommendations, String text, String image) {

        /*
        The thread that calls the handleList method is actually a background thread,
        functions such as the setting of the image sources for ImageViews and the text for
        TextViews require the main Thread/Ui Thread, thus it is called
        */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //if not enough tracks are returned to fill all 5 track slots, inform the user that
                //there was no result found to prevent crash
                if(recommendations.size() < 5){
                    Toast.makeText(getApplicationContext(), "No result found", Toast.LENGTH_SHORT).show();
                    return;
                }


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
                    String imageUrl = recommendations.get(i - 1).getAlbum().getImages().get(0).getUrl();

                    /*
                    Name of the song can be obtained from the specified Track object in the List,
                    through the getName method
                    */
                    textView.setText(recommendations.get(i - 1).getName());

                    //Picasso is a library that can load images from a url with a single line of code
                    Picasso.get().load(imageUrl).into(imageView);
                }

                //copied over from MainActivity, should refactor

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

                        song.setPosition(preview);
                        //set song object to position in the array
                        songPreviewArray[preview] = song;
                        preview += 1;
                    }

                }
                //Placeholder before major code refactoring

                List<Song> listSongArray = Arrays.asList(songArray);
                List<Song> listPreviewArray = Arrays.asList(songPreviewArray);

                playlist = new Playlist(listSongArray, listPreviewArray);
            }
        });

    }

    /*//Creation of the serializable data matrix to be sent to PlayMusic Activity to be played
    private String[][] prepareDataForTransfer() {

        //verify the number of preview urls available for streaming
        int count = 0;
        for (int number = 0; number < 5; number++){
            //if there is preview available
            if(trackList.get(number).getPreview_url()!=null){
                //add the length of the matrix
                count += 1;
            }
        }

        //creation of data matrix with the length of the number of tracks with previews
        String[][] dataPackage = new String[count][5];

        //Input required information into array
        for (int i = 0; i < count; i++) {
            //if preview is available
            if(trackList.get(i).getPreview_url()!=null){
                //store song name
                dataPackage[i][0] = trackList.get(i).getName();
                //store name of artist
                dataPackage[i][1] = trackList.get(i).getArtist().get(0).getName();
                //store url of song image
                dataPackage[i][2] = trackList.get(i).getAlbum().getImages().get(0).getUrl();
                //store song preview url
                dataPackage[i][3] = trackList.get(i).getPreview_url();
                //store song index
                dataPackage[i][4] = String.valueOf(i);
            }
        }

        return dataPackage;
    }*/

    private void sendDataToActivity(int position) {
        // Create a new Intent and specify the source and destination screen/activity.
        Intent intent = new Intent(this, PlayMusic.class);

        //get song position. copied form MainActivity needs refactoring
        for (Song song: playlist.getPlaylistWithPreview()){
            if(song.getPreviewUrl().equals(playlist.getPlaylist().get(position).getPreviewUrl())){
                position = song.getPosition();
            }

        }

        // Track's position number relative to the array of tracks
        intent.putExtra("position", position);
        
        // Create a new Bundle to store the song matrix
        Bundle bundle = new Bundle();
        //Serializing the matrix so that it can be sent over to the next activity
        bundle.putSerializable("playlist", playlist);

        //storing the bundle in the intent so that it will also be transferred to the next activity
        intent.putExtras(bundle);

        //Proceed to next Activity
        startActivity(intent);
    }

    public void goHome(View view){
        NavBar.goHome(view,this);
    }

    public void goPlaylist(View view){
        NavBar.goPlaylist(view, this);
    }

}
