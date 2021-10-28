package tp.edu.moshout.jsonobject;

import com.google.gson.annotations.SerializedName;

//According to Spotify Web Api Documentation
/*https://developer.spotify.com/documentation/web-api/reference/object-model/#:~:text=Reference%20in%20beta!-,Object%20Model,simplify%20retrieval%20of%20further%20objects.*/

public class AuthToken {

    //The key to the authentication token
    @SerializedName("access_token")
    private String access_Token;

    //The key to the type of authentication token
    @SerializedName("token_type")
    private String token_Type;

    //How long the token will last before expiring
    @SerializedName("expires_in")
    private String expires_In;

    //Access that the token gransts
    private String scope;

    public String getAccess_Token() {
        return access_Token;
    }

    public String getToken_Type() {
        return token_Type;
    }

    public String getExpires_In() {
        return expires_In;
    }

    public String getScope() {
        return scope;
    }
}
