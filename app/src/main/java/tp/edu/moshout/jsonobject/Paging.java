package tp.edu.moshout.jsonobject;

import com.google.gson.annotations.SerializedName;

import java.util.List;

//According to Spotify Web Api Documentation
/*https://developer.spotify.com/documentation/web-api/reference/object-model/#:~:text=Reference%20in%20beta!-,Object%20Model,simplify%20retrieval%20of%20further%20objects.*/

public class Paging {
    //The Paging object contains keys to other objects

    //When creating the key, instead of "itemList" which there are no values attached to it
    //Gson will create it as "items", therefore, in the code, the variable "itemList" refers to "items"
    @SerializedName("items")
    private List<Track> itemList;

    private String href;

    private int limit;

    private String next;

    private int offset;

    private String previous;

    private String total;


    //Returns a List of Track objects
    public List<Track> getItemList() {
        return itemList;
    }

    public String getHref() {
        return href;
    }

    public int getLimit() {
        return limit;
    }

    public String getNext() {
        return next;
    }

    public int getOffset() {
        return offset;
    }

    public String getPrevious() {
        return previous;
    }

    public String getTotal() {
        return total;
    }

}
