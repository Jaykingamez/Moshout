package tp.edu.moshout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import tp.edu.moshout.song.Account;

public class SignUp extends AppCompatActivity {

    private EditText fullName;
    private EditText email;
    private EditText password;
    private EditText birthDate;
    private EditText country;

    //To save our data into SharedPreferences, a global database in Android, we're going to have
    //to turn the ArrayList into json which can be stored. Google's Gson library is here to achieve
    // this task
    private Gson gson = new Gson();

    private SharedPreferences loginDetails;
    private SharedPreferences.Editor loginEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        birthDate = findViewById(R.id.birthDate);
        country = findViewById(R.id.country);
    }

    public void goBack(View view){
        //simulate back button on phone
        onBackPressed();
    }

    public void createAccount(View view){
        if(fullName.getText().toString().isEmpty() || email.getText().toString().isEmpty() ||
        password.getText().toString().isEmpty() || birthDate.getText().toString().isEmpty() ||
        country.getText().toString().isEmpty()){
            Toast.makeText(this, "Some paramters have been left empty, sorry :(", Toast.LENGTH_SHORT).show();
            return;
        }

        Account account = new Account(fullName.getText().toString(), email.getText().toString(),
                password.getText().toString(), birthDate.getText().toString(),
                country.getText().toString());

        loginDetails =  PreferenceManager.getDefaultSharedPreferences(this);


        //convert to json string for storage
        String json = gson.toJson(account);

        loginEditor = loginDetails.edit();
        //remove current key as well as it values
        loginEditor.remove(email.getText().toString()).commit();
        loginEditor.putString(email.getText().toString(), json);
        loginEditor.commit();

        //force user to go back to previous Activity to input their login details
        onBackPressed();

    }
}
