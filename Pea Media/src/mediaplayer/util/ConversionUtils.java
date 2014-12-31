package mediaplayer.util;

/**
 * Provides conversion tools.
 * 
 * @author Alex Hage
 */
public class ConversionUtils {

	/**
	 * Formats time in seconds to a string of format hh:mm:ss.
	 * 
	 * @param inTime
	 *            the time in seconds to format.
	 * @return outTime formatted time as String.
	 */
    public static String formatTimeInSeconds(int inTime)
    {
    	String outTime = "";
    	
    	int hours = (inTime / 3600) % 24;
    	int minutes = (inTime / 60) % 60;
    	int seconds = inTime % 60;
    	
		outTime += (hours < 10 ? "0" + hours : hours) + ":"
				+ (minutes < 10 ? "0" + minutes : minutes) + ":"
				+ (seconds < 10 ? "0" + seconds : seconds);
		
		return outTime;
    }
    
    /**
     * Removes the file extension from the filename.
     * @param s
     * @return
     */
	public static String removeFileExtension(String oldName) {

	    String separator = System.getProperty("file.separator");
	    String newName;

	    // Remove everything before the last separator. Effectively removes path/URL
	    int lastSeparatorIndex = oldName.lastIndexOf(separator);
	    if (lastSeparatorIndex == -1) 
	    {
	        newName = oldName;
	    } 
	    else 
	    {
	        newName = oldName.substring(lastSeparatorIndex + 1);
	    }
	    int extensionIndex = newName.lastIndexOf(".");
	    //No extension
	    if (extensionIndex == -1)
	    {
	        return newName;
	    }
	    //Remove extension.
	    return newName.substring(0, extensionIndex);
	}
}
