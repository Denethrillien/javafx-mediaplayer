package mediaplayer.model;

import java.net.URI;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Stores and retrieves the media item associated data. Contains the information
 * necessary for playback and displaying of media info.
 * 
 * @author Alex Hage
 *
 */
public class MediaItem 
{	
	/**
	 * The observable name value of the media file. TODO: Get title from
	 * metadata.
	 */
	private final StringProperty title;
	
	/**
	 * The observable URI object of the media file.
	 */
	private ObjectProperty<URI> uri;
	
	/**
	 * Default constructor
	 */
	public MediaItem()
	{
		this(null);
	} //end dctor

	/**
	 * Constructs a new MediaItem with the corresponding path value.
	 * 
	 * @param path
	 *            the String representation of the absolute path to media file.
	 */
	public MediaItem(URI uri) 
	{	
		this.title = new SimpleStringProperty("");
		this.uri = new SimpleObjectProperty<URI>(uri);
	} //end ctor
	
	/**
	 * Returns the title String of the MediaItem.
	 * 
	 * @return title String.
	 */
	public String getTitle()
	{
		return title.get();
	}
	
	/**
	 * Sets the title of the MediaItem.
	 * 
	 * @param title
	 *            the String representation of MediaItem's title.
	 */
	public void setTitle(String title)
	{
		this.title.set(title);
	}
	
	/**
	 * Returns the title StringProperty of the MediaItem.
	 * 
	 * @return title StringProperty.
	 */
	public StringProperty getTitleProperty()
	{
		return title;
	}
	
	/**
	 * Returns the URI of the media file.
	 * 
	 * @return uri URI.
	 */
	public URI getURI()
	{
		return uri.get();
	}
	
	/**
	 * Sets the URI of the MediaItem.
	 * 
	 * @param uri
	 *            the URI of the media file.
	 */
	public void setURI(URI uri)
	{
		this.uri.set(uri);
	}
	
	/**
	 * Returns the URI ObjectProperty of the MediaItem.
	 * 
	 * @return uri {@code ObjectProperty<URI>}.
	 */
	public ObjectProperty<URI> getURIProperty()
	{
		return uri;
	}
}
