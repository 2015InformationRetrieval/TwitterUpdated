package qa.datahelper;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class test {
	
	//private static final String DB_PATH = "/Users/jiechen/Google Drive/Eclipse-Luna/neo4j-community-2.2.0-M02/test";
	//static GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
	//static Transaction tx=db.beginTx();
	
	public static void main(String[] args) {
		
			UserHelper uh = new UserHelper();

//			uh.addUser(30,"Chongqing");
//			uh.addFollower(30, 25, "Chengdu");
//			uh.addIndex("Hot",25);
//			uh.addIndex("HotPot",30);
			
			//System.out.println(uh.updateCF(30));

			//uh.get
//			uh.addUser(30,"Chongqing");
//			uh.addIndex("HotPot",30);
//			boolean result = uh.checkRelationShipToken("HotPot",30);
//			System.out.println(result);
			//uh.addFollower(30, 25, "Chengdu");
			//uh.addUser(25,"Fu_Jun");
			//uh.addIndex("Sichuan", 25);
			//uh.addIndex("ChengDu", 25);
			//uh.addFollower(25, 24, "Cheng_Ang");
			//tx.success();
			//tx.close();
			uh.shutDown();
		
		
		
	}
	
}