package qa.analysis;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
/**
 * This is for INFSCI 2140 in 2015
 * 
 * TextTokenizer can split a sequence of text into individual word tokens.
 */
public class TextTokenizer {
	
	//declare a LinkedList to store each token of char[] array data type
	//because LinkedList use pointer, when add each token to LinkedList, it is a little faster than ArrayList
 	List<char[]> str = new ArrayList<char[]>();
 	
 	// declare the iterator
	Iterator<char[]> itea;
	
	String words = null;	
	
	// punctuation is the token delimiter
   
	// YOU MUST IMPLEMENT THIS METHOD
	public TextTokenizer( String s ) {
	// this constructor will tokenize the input texts (usually it is a char array for a whole document)
		char[] texts = s.toLowerCase().toCharArray();
		for(int i = 0;i < texts.length;i++){
			if(texts[i] < 'a' || texts[i] > 'z'){
				texts[i] = ' ';
			}
		}
		words = new String(texts);
		
		// StringTokenizer is a class in util package, it can tokenize a large String by into several small Strings
		// we can set up the token sign by ourselves;
		StringTokenizer st = new StringTokenizer(words);
		
		String token = null;
		
		// the hasMoreTokens return a boolean data type, if is has more tokens, it returns true
		while (st.hasMoreTokens()) {
			
			 //Returns the next token from this string tokenizer
			 token = st.nextToken();
			 if(!StopwordsRemover.isStopword(token) && token.length() > 1){
				 //add each tokens into LinkedList
				 str.add(token.toCharArray());
			 }
				 
			
			 
		}
		
		// initialize the iterator after LinkedList store all the tokens
		itea = str.iterator();
		
	}
	
	// YOU MUST IMPLEMENT THIS METHOD
	public String nextWord() {
    // read and return the next word of the document; or return null if it is the end of the document
		
		if (itea.hasNext()) {
			
			char[] wordtoken = itea.next();
			return new String(wordtoken);
			
		}
		
		return null;
	}
	public static void main(String args[]){
		TextTokenizer t = new TextTokenizer("  ~~What's pitts chis, sio?");
		System.out.println(t);
		String a;
		while((a = t.nextWord()) != null){
			System.out.println(a);
		}
	}
}
	
	

