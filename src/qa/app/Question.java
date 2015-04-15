package qa.app;


import qa.connection.Parameter;
import twitter4j.FilterQuery;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Question {
	TwitterStream twitterStream = null;
	
	public Question(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey(Parameter.CONSUMER_KEY);
	    cb.setOAuthConsumerSecret(Parameter.CONSUMER_KEY_SECRET);
	    cb.setOAuthAccessToken(Parameter.ACCESS_TOKEN);
	    cb.setOAuthAccessTokenSecret(Parameter.ACESS_TOKEN_SECRET);
	    twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
	    twitterStream.addListener(new StreamListener());
	    twitterStream.user();
	}
	
	
	public static void  main(String args[]){
		Question q = new Question();   
	}
}
