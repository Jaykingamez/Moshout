package tp.edu.moshout.apicalls;

import io.reactivex.Flowable;
import tp.edu.moshout.jsonobject.AuthToken;
import tp.edu.moshout.jsonobject.Received;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitInterface {
    //An Interface for Retrofit to create a method that would make a POST request to Spotify Web Api
    //to get the Authentication Token
    @FormUrlEncoded
    @POST("api/token")
    Flowable<AuthToken> getToken(
            @Header("Authorization") String header,
            @Field("grant_type") String grantType
    );

    //An Interface for Retrofit to create a method that would make a GET request to Spotify Web Api
    //to get a number of songs specified in the int variable limit
    @GET("v1/search")
    Flowable<Received> getSongs(
            @Header("Authorization") String header,
            @Query("q") String searchTerm,
            @Query("type") String type,
            @Query("market") String market,
            @Query("limit") int limit,
            @Query("offset") int offset
    );
}
