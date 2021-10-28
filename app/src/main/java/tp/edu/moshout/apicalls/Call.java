package tp.edu.moshout.apicalls;

import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import tp.edu.moshout.jsonobject.*;


import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tp.edu.moshout.jsonobject.Received;

public class Call {
    //Spotify Api authentication parameters
    private static final String CLIENTID = "c1e378ffd8fa4162b7f941fd06269507";
    private static final String CLIENTSECRET = "ceacbb263bd444f3a0ce1e1f7cffd381";
    private static final String GRANT_TYPE = "client_credentials";

    private static OkHttpClient okHttpClient;
    private static Gson gson;


    //Constants to store authentication token
    private static final String PREFNAME = "sharedPref";
    private static final String TOKEN_KEY_NAME = "AuthToken";

    private static String access_Token;
    private static String queryHeader;

    //private TextView textViewResult;
    //private ImageView imageView;
    private static  RetrofitInterface retrofitInterface;
    private static RetrofitInterface searchInterface;

    //Constants to Android Studio's Shared Preferences to store the authentication token
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor editor;

    static {
        //Gson is the class that will parse JSON objects to java objects and vice versa.
        gson = new GsonBuilder().create();


        //Logging interceptor is created to keep track of the http api calls
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //Integrate the logging interceptor into OkHttpClient, a http client
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

    }



    public static String authorizationHeader(){

        //According to Spotify Web Api documentation, an Authorization Header is needed where it contains
        //Basic (clientid:secret key).encodedInBase64
        String unencodedString = "Basic ";
        String clientCombined = CLIENTID + ":" + CLIENTSECRET;

        //Encoding the Keys in Base64, NO_WRAP would remove line terminators from the encoded object
        //ensuring no formatting issues
        byte [] encodedValue = Base64.encode(clientCombined.getBytes(), Base64.NO_WRAP);
        return unencodedString + new String(encodedValue);
    }

    public static RetrofitInterface getRetrofitToken(){

        //creating a Retrofit object that integrates the gson and okhttp, to make calls to http api for authentication
        Retrofit authRetrofit = new Retrofit.Builder()
                .baseUrl("https://accounts.spotify.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        //creating the methods stated in RetrofitInterface.java to make api calls
        retrofitInterface = authRetrofit.create(RetrofitInterface.class);

        return retrofitInterface;


    }

    public static RetrofitInterface getRetrofitQuery(){

        //creating a Retrofit object that integrates the gson and okhttp, to make calls to http api for querying
        Retrofit Retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        //creating the methods stated in RetrofitInterface.java to make api calls
        searchInterface = Retrofit.create(RetrofitInterface.class);

        //According to Spotify Web Api documentation, making a query requires the header of
        // Authorization:Bearer AuthenticationToken
        queryHeader = "Bearer " + access_Token;

        return searchInterface;
    }

    public static void setAccess_Token(String token) {access_Token = token;}

    public static String getGrantType() {return GRANT_TYPE; }

    public static String getQueryHeader(){ return queryHeader; }
}
