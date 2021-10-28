These objects were created from the JSON converted into Java objects. While obtaining the objects through that method was workable,
many calls had to be created to get to certain information that was burried in multiple object classes. 

Thus these Java classes were created to help store the objects that the code uses frequently. Thus the code becomes more readable and concise.

Account class deals with Account creation, it is a non-static class. Many of these can be created. Each account class will be unique. It is created
when a user signs up. 

Playlist class deals with playlist management, it is also a non-static class. It is created when a user creates a new playlist.

Song class deals with song information, it is a non-static class. It is created when a call is made to Spotify Web Api and the code stores that information
into a Song class object for use. 