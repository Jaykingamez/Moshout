package tp.edu.moshout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    //private final int SPLASH_DISPLAY_TIME = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash_screen);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();





    }
}
