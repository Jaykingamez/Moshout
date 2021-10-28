The objects here all work similarily.

It works based on the Object Model as stated Spotify Web Api Documentation.

//According to Spotify Web Api Documentation
/*https://developer.spotify.com/documentation/web-api/reference/object-model/#:~:text=Reference%20in%20beta!-,Object%20Model,simplify%20retrieval%20of%20further%20objects.*/

The names of the variables are the keys to unlock information in the objects,
some of the variables are serialized to different names as keys.

This is to improve comprehension as some of the name of the keys are not descriptive enough.

Doing an Api Call returns a JSON , gson, a Java library, helps converts the JSON 
into Java objects that the code can interact in. However it needs a framework to convert the object into,
thus these objects are created for gson to convert the JSON into Java objects for the code to interact with.


