package mediaplayer.util;

public class DurationUtil {
    
    public static String format(int inTime)
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
}
