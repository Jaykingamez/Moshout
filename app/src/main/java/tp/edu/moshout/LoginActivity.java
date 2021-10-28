package tp.edu.moshout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import tp.edu.moshout.apicalls.Call;
import tp.edu.moshout.song.Account;

public class LoginActivity extends AppCompatActivity {

    private EditText username_text;
    private EditText password_text;
    private Button login;
    private Button signUp;

    private String username;
    private String password;

    private SharedPreferences loginDetails;
    private SharedPreferences.Editor loginEditor;

    //account in use
    private Account account;

    //To save our data into SharedPreferences, a global database in Android, we're going to have
    //to turn the ArrayList into json which can be stored. Google's Gson library is here to achieve
    // this task
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username_text = findViewById(R.id.username);
        password_text = findViewById(R.id.password);

        login = findViewById(R.id.login);
        signUp = findViewById(R.id.signUp);

        loginDetails = PreferenceManager.getDefaultSharedPreferences(this);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp();
            }
        });



    }

    private void checkLogin(){
         String username = username_text.getText().toString();
         String password = password_text.getText().toString();


        //get account json
        String returnedValue = loginDetails.getString(username, "");
        account = gson.fromJson(returnedValue,
                new TypeToken<Account>() {
                }.getType());



         if (returnedValue.isEmpty() || !account.getPassword().equals(password))
             incorrectLogin();
         else{
             loginEditor = loginDetails.edit();
             //remove current key as well as it values
             loginEditor.remove("username").commit();
             loginEditor.putString("username", username);
             loginEditor.commit();
             Intent intent = new Intent(this, MainActivity.class);

             startActivity(intent);
         }
             ;
    }

    private void SignUp(){
        Intent intent = new Intent(this, SignUp.class);

        startActivity(intent);
    }

    private void incorrectLogin(){
        Toast.makeText(getApplicationContext(), "Incorrect login details, please try again.", Toast.LENGTH_SHORT).show();
    }

   /* private void setLoginDetails(Class signUp, String[] signUpDetails){
        if(signUp.getName().equals("SignUp")){
            loginEditor.putString(signUpDetails[0],signUpDetails[1]);
            loginEditor.commit();
        }


    }*/



}
