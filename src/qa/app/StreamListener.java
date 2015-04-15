package qa.app;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import qa.app.Answer;
import qa.connection.Parameter;
import qa.datahelper.UserHelper;
import qa.service.UserService;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.StatusUpdate;

public class StreamListener implements StatusListener{
	
	//String Neo4j_Path="/Users/jiechen/git/TwitterQA/neo4j-community-2.2.0/ir";
	
	private UserService userService = new UserService();
	
	private UserHelper userHelper = new UserHelper();
	//GraphDatabaseService graphDataService=new GraphDatabaseFactory().newEmbeddedDatabase(Neo4j_Path);
	@Override
	public void onException(Exception arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrubGeo(long arg0, long arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStallWarning(StallWarning arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatus(Status status) {
		// TODO Auto-generated method stub
		System.out.println(status.getUser().getName() + " : " + status.getText());
		System.out.println(status.getUser().getName()+"  "+status.getUser().getId());
		
		if(status.getUser().getId() != Long.parseLong(Parameter.USER_ID )){
			System.out.println("--------TESTING-------");
			if(userService.IsExistUserNetwork(status.getUser())){
				System.out.println(status.getUser().getName()+" is in database");
				Answer.reply(status);
			}else{
				System.out.println("Need to create user"+  status.getUser().getName());
				userHelper.addUser(status.getUser().getId(), status.getUser().getName());
				userService.createIndex(status.getUser());
				System.out.println("SEEEEEEEEEEEEEEEEEEEE---------");
				Answer.reply(status);
			}
			
			
			
			
		}
			
	}
	

	@Override
	public void onTrackLimitationNotice(int arg0) {
		// TODO Auto-generated method stub
		
	}

	
	
}
