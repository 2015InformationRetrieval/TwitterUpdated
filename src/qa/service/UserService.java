package qa.service;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import qa.analysis.TextTokenizer;
import qa.connection.Parameter;
import qa.datahelper.UserHelper;
import twitter4j.IDs;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class UserService {
	private UserHelper userHelper = new UserHelper();
	private static Twitter twitter = null;
	
	//private static final String DB_PATH = "/Users/jiechen/Google Drive/Eclipse-Luna/neo4j-community-2.2.0-M02/ir";
	//GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
	
	public boolean isExist(User user) {
		// TODO Auto-generated method stub
		return userHelper.isExistByUserId(user.getId());
	}
	
	public boolean IsExistUserNetwork(User user) {
		// TODO Auto-generated method stub
		return userHelper.IsExistUserNetwork(user.getId());
	}
	
	public UserService(){
		if(twitter == null){
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true);
			cb.setOAuthConsumerKey(Parameter.CONSUMER_KEY);
		    cb.setOAuthConsumerSecret(Parameter.CONSUMER_KEY_SECRET);
		    cb.setOAuthAccessToken(Parameter.ACCESS_TOKEN);
		    cb.setOAuthAccessTokenSecret(Parameter.ACESS_TOKEN_SECRET);
		    twitter= new TwitterFactory(cb.build()).getInstance();
		} 
	}
	
	public void createIndex(User user) {
		// TODO Auto-generated method stub
		
		try {
			System.out.println("-----creatIndex Begin------");
			// construct the follower index
			userHelper.addUser(user.getId(), user.getName());
			IDs followerIter = null;
			followerIter = twitter.getFollowersIDs(user.getId(), -1);
			long[] followers = followerIter.getIDs();
			
			for(long id : followers){
				System.out.println("Follower: "+id+"  "+getScreenNameById(id));
				userHelper.addFollower(user.getId(),id,getScreenNameById(id));
				if(userHelper.isExistByUserId(id)){
					parseUser(id);
				}
			}
			
			userHelper.updateCF(user.getId());
			// construct the following index
			/*IDs following = null;
			following = twitter.getFriendsIDs(user.getId(), -1);
			long[] followings = following.getIDs();
			for(long id : followings){
				if(id == Long.parseLong(Parameter.USER_ID))
					continue;
				System.out.println("Following: "+id+"  "+getScreenNameById(id));
				userHelper.addFollowing(user.getId(), id, getScreenNameById(id));
				if(userHelper.isExistByUserId(id)){
					parseUser(id);
				}
			}*/
			System.out.println("-----creatIndex Stop------");
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void parseUser(long id){
		System.out.println("------------Parse" + id + " start--------------");
		try {
			
			ResponseList<Status> statusList= twitter.getUserTimeline(id);
			for(Status status : statusList){
				
				String text = status.getText();
				//System.out.println(text);
				TextTokenizer tokenizer = new TextTokenizer(text);
				String token = null;
				while((token = tokenizer.nextWord()) != null ){
					userHelper.addIndex(token, id);
				}
			}
			System.out.println("------------Parse" + id + " End--------------");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getScreenNameById(long id){
		String name = null;
		try {
			 name = twitter.showUser(id).getScreenName();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;  
		}
		return name;
	}
	
}
