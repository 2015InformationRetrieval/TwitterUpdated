package qa.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/** 
 * This is for INFSCI 2140 in 2015
 * 
 */
public class StopwordsRemover {
	
	private static Map<String, Boolean> map = null;
	// YOU MUST IMPLEMENT THIS METHOD
	public static void init( FileInputStream instream ) {
		// load and store the stop words from the fileinputstream with appropriate data structure
		// that you believe is suitable for matching stop words.
		  //initialzie fileinputstream and bufferedreader 
        if(map != null){
        	return;
        }
        map = new HashMap<String, Boolean>();
        BufferedReader reader = null;        
         
        //read sample.txt
      
        reader = new BufferedReader(new InputStreamReader(instream));
     
        //Reading File line by line using BufferedReader
        //You can get next line using reader.readLine() method.
        String line;
		try {
			line = reader.readLine();
			 while(line != null){
		        	map.put(line.trim(), true);
		            line = reader.readLine();
		          }
		        
		          reader.close();
		          instream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	// YOU MUST IMPLEMENT THIS METHOD
	public static boolean isStopword( String s ) {
		// return true if the input word is a stopword, or false if not
		if(map == null){
			try {
				init(new FileInputStream(new File("stoplist")));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		return map.containsKey(s);
	}
	
}
