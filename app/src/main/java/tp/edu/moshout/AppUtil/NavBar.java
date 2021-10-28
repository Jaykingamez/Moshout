package tp.edu.moshout.AppUtil;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import tp.edu.moshout.MainActivity;
import tp.edu.moshout.PlaylistActivity;
import tp.edu.moshout.song.Playlist;

public class NavBar {
    public static void goHome(View view, Context context){
        //create intent to return to homepage
        Intent intent = new Intent(context, MainActivity.class);
        //go to the destination intent set
        context.startActivity(intent);
    }

    public static void goPlaylist(View view, Context context) {
        //create intent to go to playlist page
        Intent intent = new Intent(context, PlaylistActivity.class);
        //go to the destination intent set
        context.startActivity(intent);
    }

}
