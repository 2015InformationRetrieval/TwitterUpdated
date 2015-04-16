package qa.datahelper;
// package
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import twitter4j.User;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.ReadableIndex;
import org.neo4j.graphdb.index.UniqueFactory;
import org.neo4j.kernel.TopLevelTransaction;
import org.neo4j.tooling.GlobalGraphOperations;
import org.neo4j.graphdb.DynamicLabel; // add a label
import org.neo4j.graphdb.Label;

import static org.neo4j.kernel.impl.util.FileUtils.deleteRecursively;

public  class UserHelper { // UserHelper
	
	// there are two labels in this Neo4j. 
	// (1) User(Property:id)  (2) Index (Property: token)
	
	 ExecutionEngine engine;	
	 
	 private static String DB_PATH;
	 private static GraphDatabaseService db;
	 //private static final String DB_PATH = "ir";
	 //GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
	 
	 public UserHelper () {

		 DB_PATH = "neo4j-community-2.2.0/ir";
		 //db = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		 if (db == null) {
			 db = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		 }
	 }
	 
	 // two labels in neo4j, Index and User
	 org.neo4j.graphdb.Label Index = DynamicLabel.label("Index"); 
	 org.neo4j.graphdb.Label User = DynamicLabel.label("User");
	 
//	    private void clearDbPath()
//	    {
//	        try
//	        {
//	            deleteRecursively( new File( DB_PATH ) );
//	        }
//	        catch ( IOException e )
//	        {
//	            throw new RuntimeException( e );
//	        }
//	    }

	 public static enum RelTypes implements RelationshipType{
		 
		 Indexed,
		 // Following, // Following shows that a user is following other users
		 Followed   // Followed shows that a user is followed by other users
		 
	}
	 
	 /**
	  * check whether the network of userId is in neo4j or not
	  * @param id
	  * @return
	  */
	 public boolean IsExistUserNetwork(long id) {
		 
		 System.out.println("--------check IsExistUserNetwork--------");
		 Set<String> container = new HashSet<String>();
		
		 if ( isExistByUserId(id)) {
			 
			 try ( Transaction tx = db.beginTx()) {
				 
				 ResourceIterator<Node> iterator_user = db.findNodesByLabelAndProperty(User, "ID", id).iterator();
				 Node user_node = iterator_user.next();
				 Iterator<Relationship> rels = user_node.getRelationships(RelTypes.Indexed, Direction.OUTGOING).iterator(); 
				 
				 while ( rels.hasNext() ) {
					 Relationship rel = rels.next();
					 String name = rel.getType().name();
					 container.add(name);
				 }
				 
				 tx.success();
				 
			 } // end try 
			 
			 try ( Transaction tx = db.beginTx()) {
				 
				 ResourceIterator<Node> iterator_user = db.findNodesByLabelAndProperty(User, "ID", id).iterator();
				 Node user_node = iterator_user.next();
				 Iterator<Relationship> rels = user_node.getRelationships(RelTypes.Followed, Direction.INCOMING).iterator(); 
				 
				 while ( rels.hasNext() ) {
					 Relationship rel = rels.next();
					 String name = rel.getType().name();
					 container.add(name);
				 }
				 
				 tx.success();
				 
			 } // end try 
			 
			 
			 
		 } else {
			 System.out.println("IsExistUserNetwork ID: " + id + " is not in neo4j");
		 }
		 
		 if ( container.contains("Indexed") && container.contains("Followed") ) {
			 System.out.println("IsExistUserNetwork ID: " + id + " is true");
			 return true;
		 }
		 System.out.println();
		 return false;
		 
//		 if ( isExistByUserId (id)) {
//			try ( Transaction rx = db.beginTx() ) {
//				 ResourceIterator<Node> iterator_user = db.findNodesByLabelAndProperty(User, "ID", id).iterator();
//				 Node user_node = iterator_user.next();
//				 Relationship relation_indexed = user_node.getSingleRelationship(RelTypes.Indexed, Direction.OUTGOING);
//				 Relationship relation_followed = user_node.getSingleRelationship(RelTypes.Followed, Direction.INCOMING);
//				 rx.success();
//				 if (relation_indexed != null && relation_followed != null) {
//					 return true;
//				 }
//			 }
//		 } else {
//			 System.out.println("userID: " + id + " is not in neo4j. Thus no exsiting network");
//		 }
		 
		 
//		 //Set<RelationshipType> contain = new HashSet<RelationshipType>();
//		 try ( Transaction tx = db.beginTx()) {
//			 if ( isExistByUserId(id) ) {
//				 //Iterator<RelationshipType> itea_type = db.getRelationshipTypes();
//				 Iterable<RelationshipType> itea_type = db.getRelationshipTypes();
//				 Iterator<RelationshipType> itea = itea_type.iterator();
//				 while( itea.hasNext()) {
//					 String name = itea.next().name();
//					 container.add(name);
//				 }
//			 } else {
//	
//			 }
//		 } // end try

	 }
	 
	 /**
	  * at first, crawling data from twitter, just put the user who asks the question into neo4j
	  * @param userId
	  * @param Name
	  */
	 public void addUser(long userId, String Name) {
		 
		 if ( !isExistByUserId(userId) ) {
			 
			 try( Transaction tx =  db.beginTx()) {
				 int df = 0;
				 int cf = 0;
				 Node user = db.createNode();
				 user.addLabel(User);
				 user.setProperty("ID", userId);
				 user.setProperty("name", Name);
				 user.setProperty("DF", df);
				 user.setProperty("CF",cf);
				 System.out.println("addUser is done");
				 tx.success();
			 }
			 System.out.println();
		 } else {
			 System.out.println("errors: " + userId + "is already in neo4j");
		 }
		 
	 } // end addUser
	 
	 
	/**
	  * find user whether the user exist in the main user list
	  * @param id: the twitter_id of the user
	  * @return if there is the user exist in the database, return true.  Else return false 
	  */
	public boolean isExistByUserId(long id) {
		// TODO Auto-generated method stub
        // START SNIPPET: execute
		System.out.println("-------isExistByUserId---------");
		boolean result = false;
		try( Transaction tx =  db.beginTx()) {			
			ResourceIterator<Node> iterator1 = db.findNodesByLabelAndProperty(User, "ID", id).iterator();			
		    while (iterator1.hasNext()) {		    	
		    	result =  true;		    	
		    }		    
		    tx.success();		
		}
		System.out.println();
		return result;
	}
	
	/**
	  * find index whether the token exist in the main index list
	  * @param id: the token of index
	  * @return if there exists token in the database, return true.  Else return false 
	  */
	public boolean isExistByIndex(String token) {	
		System.out.println("-------isExistByIndex---------");
		boolean result = false;
		try( Transaction tx =  db.beginTx()) {		
			ResourceIterator<Node> iterator1 = db.findNodesByLabelAndProperty(Index, "token", token).iterator();			
		    while (iterator1.hasNext()) {	    
		    	result = true;			
		    }		    
		    tx.success();			
		}	    
		System.out.println();
	    return result;
	} 
	
	/**
	 * check if there exist relationship between token and id
	 * @param token
	 * @param id
	 * @return
	 */
	public boolean checkRelationShipToken(String token, long id) {
		
		 Boolean relation = false;
		 
		 try ( Transaction tx = db.beginTx() ) {
			 
			 ResourceIterator<Node> iterator_user = db.findNodesByLabelAndProperty(User, "ID", id).iterator();
			 Node user_node = iterator_user.next();
			 Iterator<Relationship> rels = user_node.getRelationships(RelTypes.Indexed, Direction.OUTGOING).iterator();
			 
			 while ( rels.hasNext() ) {
				 Relationship rel = rels.next();
				 Node index_node = rel.getOtherNode(user_node);
				 if ( index_node.getProperty("token").equals(token)) {
					 relation = true;
				 }
			 }
			 
			 tx.success();
		 }
		 System.out.println();
		return relation;
		
		/*
		ExecutionEngine engine = new ExecutionEngine(db);
		ExecutionResult result; 
		
		try ( Transaction tx = db.beginTx()) {
			
			String query = "MATCH (a:User) -[type:Indexed]-> (b:Index) where a.ID=" + id + " and b.token='" + token + "' return type" ;
			result = engine.execute(query);
			tx.success();
			if (result == null ) {
				return false;
			} else {
				return true;
			}

		} // end try
		*/
		
		
	} // end checkRelationShipToken
	
	/**
	 * check is there a relationship Followed from follower to user
	 * @param id1
	 * @param id2
	 * @return
	 */
	public boolean checkRelationShipUser(long user, long follower) {
		
		 Boolean relation = false;
		 
		 try ( Transaction tx = db.beginTx() ) {
			 
			 ResourceIterator<Node> iterator_user = db.findNodesByLabelAndProperty(User, "ID", user).iterator();
			 Node user_node = iterator_user.next();
			 Iterator<Relationship> rels = user_node.getRelationships(RelTypes.Followed, Direction.INCOMING).iterator();
			 
			 while ( rels.hasNext() ) {
				 Relationship rel = rels.next();
				 Node follower_node = rel.getOtherNode(user_node);
				 if ( (long)follower_node.getProperty("ID") == follower) {
					 relation = true;
				 }
 			 }
			 
			 tx.success();
		 }
		 System.out.println();
		return relation;
		
//		ExecutionEngine engine = new ExecutionEngine(db);
//		ExecutionResult result; 
//		
//		try ( Transaction tx = db.beginTx()) {
//			
//			String query = "MATCH (user:User) <-[type:Followed]- (follower:User) where user.ID=" + user + " and follower.ID=" + follower + " return type" ;
//			result = engine.execute(query);
//			tx.success();
//
//			if (result == null ) {
//				return false;
//			} else {
//				return true;
//			}
//
//		} // end try 
		
		
	} // end checkRelationShipUser
	
	/**
	 * on the condition that there is relationship between token and id
	 * get the term frequency of this token indexed by id
	 * @param token
	 * @param id
	 * @return
	 */
	public int getTF(String token, long id) {
		 System.out.println("--------getTF-------");
		 int TF = 0;
		 
		 try ( Transaction tx = db.beginTx() ) {
			 
			 ResourceIterator<Node> iterator_user = db.findNodesByLabelAndProperty(User, "ID", id).iterator();
			 Node user_node = iterator_user.next();
			 Iterator<Relationship> rels = user_node.getRelationships(RelTypes.Indexed, Direction.OUTGOING).iterator();
			 
			 while ( rels.hasNext() ) {
				 Relationship rel = rels.next();
				 Node index = rel.getOtherNode(user_node);
				 if (index.getProperty("token").equals(token)) {
					 TF = (int) rel.getProperty("TF");
				 }
			 }
			 
			 tx.success();
		 }
		 System.out.println("TF between " + token + " and " + id + " is " + TF);
		 System.out.println();
		 return TF;
		
		/*
		ExecutionEngine engine = new ExecutionEngine(db);
		ExecutionResult result; 

		int resultTF = 0;
		
		try ( Transaction tx = db.beginTx()) {
			
			String query = "MATCH (a:User) -[type:Indexed]-> (b:Index) where a.ID=" + id + " and b.token='" + token + "' return type" ;
			result = engine.execute(query);
			
			for (Map<String, Object> map : result) {
				
				Relationship rs = (Relationship)map.get("type");
				resultTF = (int) rs.getProperty("TF");
				tx.success();	
			} // end for
		} // end try

		return resultTF;
		*/

	} // end getTF
	
	/**
	 * update the whole collection frequency of this userID. 
	 * Only when we finished add index to each user, we can update its CF from its followers
	 * each follower would be viewed as a document, and the user asking questions would be viewed as colletion
	 * @param userID
	 */
	public int updateCF(long userID) {
		System.out.println("------update CF Begin ------");
		int userID_CF = 0;
		
		if ( isExistByUserId(userID) ) {
			
			 try ( Transaction tx =  db.beginTx() ) {
				 
				 ResourceIterator<Node> iterator_user = db.findNodesByLabelAndProperty(User, "ID", userID).iterator();
				 Node user_node = iterator_user.next();
				 Iterator<Relationship> rels = user_node.getRelationships(RelTypes.Followed, Direction.INCOMING).iterator();
				 int CF = (int) user_node.getProperty("CF");
				 
				 while (rels.hasNext()) {
					 
					 Relationship rel = rels.next();
					 Node follower = rel.getOtherNode(user_node);
					 int DF = (int) follower.getProperty("DF"); 
					 userID_CF = userID_CF + DF;
					 
				 } // end while
				 userID_CF = userID_CF + CF;
				 user_node.setProperty("CF", userID_CF);
				 tx.success();
			 } // end try
		
		} else {
			System.out.println("userID: " + userID + " is not in neo4j." );
		}
		
		System.out.println("the collection frequency of this userID: " + userID + " is " + userID_CF);
		
		System.out.println("------update CF End ------");
		System.out.println();
		return userID_CF;
		
	} // end updateCF
	
	
	/**
	  * add the a term under a specific user
	  * @param token: the term add to the index
 	  * @param id: the twitter_id of the user
	  */
	public void addIndex(String token, long id) {
		System.out.println("----------addIndex----------");
		if (isExistByUserId(id)) { // user id is in neo4j
			
			if (isExistByIndex(token)) { // token is already in neo4j
				 System.out.println("token: " + token + " is already in database" );
				 
				 if ( checkRelationShipToken(token, id) ) { // there is already relationship between token and id
					 
					 try ( Transaction tx = db.beginTx() ) {
						 
						 ResourceIterator<Node> iterator_user = db.findNodesByLabelAndProperty(User, "ID", id).iterator();
						 //ResourceIterator<Node> iterator_index= db.findNodesByLabelAndProperty(Index, "token", token).iterator();
						 
						 Node user_node = iterator_user.next();
						 int df = (int) user_node.getProperty("DF") + 1;
						 user_node.setProperty("DF", df);
						 
						 //Node index_node = iterator_index.next();
						 Iterator<Relationship> rels = user_node.getRelationships(RelTypes.Indexed, Direction.OUTGOING).iterator();
						 while (rels.hasNext()) {
							 Relationship rel = rels.next();
							 String rel_token = (String) rel.getOtherNode(user_node).getProperty("token");
							 if ( rel_token.equals(token) ) {
								 rel.setProperty("TF", getTF(token,id) + 1);
							 }
						 }
						 
						 System.out.println("the TF of " + token + " and " + id + " is " + getTF(token, id));
						 tx.success();
						 
					 }
					 
				 } else { // there is no relationship between token and id
					 
					 try( Transaction tx =  db.beginTx() ) {
						 ResourceIterator<Node> iterator_user = db.findNodesByLabelAndProperty(User, "ID", id).iterator();
						 ResourceIterator<Node> iterator_index= db.findNodesByLabelAndProperty(Index, "token", token).iterator();
						 
						 Node user_node = iterator_user.next();  // there is only one node in iterator1
						 int df = (int) user_node.getProperty("DF") + 1;
						 user_node.setProperty("DF", df);
						 
						 Node index_node = iterator_index.next();  // there is only one node in iterator2
						 
						 Relationship relationship = user_node.createRelationshipTo(index_node, RelTypes.Indexed);
						 relationship.setProperty("TF",1);
						 
						 System.out.println("index: " + token + " is already in neo4j. Add Relationship Indexed to " + id);
						 tx.success();
					 }
						 
				 }
				 

			} else { // create a node with property token: token
				
				 try( Transaction tx =  db.beginTx()) { // create relationship betwen token and id
					 
					 Node index_node = db.createNode();
					 index_node.addLabel(Index);
					 index_node.setProperty("token", token);
					 
					 ResourceIterator<Node> iterator_user = db.findNodesByLabelAndProperty(User, "ID", id).iterator();
					 Node user_node = iterator_user.next();
					 
					 // receive the current document frequency and add ones
					 int df = (int) user_node.getProperty("DF") + 1;
					 user_node.setProperty("DF", df);
					 
					 
					 Relationship relationship = user_node.createRelationshipTo(index_node, RelTypes.Indexed);
					 
					 // the term frequency of term is one
					 relationship.setProperty("TF",1);

					 System.out.println("Index: " + token + " is not in neo4j. First create a node then add relationship Indexed to " + id);
					 tx.success();
					 
				 }
		
			}		
			
		} else { // user id is not in neo4j, should send errors
			System.out.println("errors user: " + id + " is not in neo4j");
		}
		System.out.println();
		
	} // end addIndex
  

	/**
	  * add the follower of the user
	  * @param string 
	  * @param userId: the twitter_id of the user
	  * @param followerId: the twitter_id of the follower of the user
	  */
	public void addFollower(long userId, long followerId, String name) {
		// TODO Auto-generated method stub
		System.out.println("---------addFollower----------");
		if (isExistByUserId(userId)) { // check userId is in neo4j or not

			if ( isExistByUserId(followerId)) { // followedId is in neo4j, we only need to find this follower
				
				if ( checkRelationShipUser(userId, followerId) ) { // there is already some relationship between userId and followerId
					
					System.out.println("followerId: " + followerId + " already Followed " + "userId: " + userId);
					
				} else {  // there is no relationship between userId and followerId
					
					try ( Transaction tx = db.beginTx() ) {
						
						ResourceIterator<Node> itea_follower = db.findNodesByLabelAndProperty(User,"ID",followerId).iterator();
						Node follower_node = itea_follower.next();
							
						ResourceIterator<Node> iterator_user = db.findNodesByLabelAndProperty(User, "ID", userId).iterator();
						Node user_node = iterator_user.next();

						Relationship relationship1 = follower_node.createRelationshipTo(user_node, RelTypes.Followed);

						System.out.println("followerId: "+ followerId +" is in neo4j. AddFollower is done");	
						 
						tx.success();
						
					} // end try 
				}

			} else { // followerId is not in the neo4j
				try( Transaction tx =  db.beginTx()) {
					 
					 // create a follower node
					 Node follower_node = db.createNode();
					 follower_node.addLabel(User);
					 follower_node.setProperty("ID", followerId);
					 follower_node.setProperty("name", name);
					 follower_node.setProperty("CF", 0);
					 follower_node.setProperty("DF", 0);
					 
					 // create a user node
					 ResourceIterator<Node> iterator_user = db.findNodesByLabelAndProperty(User, "ID", userId).iterator();
					 Node user_node = iterator_user.next();
					 
					 Relationship relationship2 = follower_node.createRelationshipTo(user_node, RelTypes.Followed);
		 
					 System.out.println("followerId: "+ followerId +" is not in neo4j. AddFollower is done");	 
					 tx.success();
				 }
			}
			 
		} else { 
			// there is no user with userId
			System.out.println("errors userId" + userId + " is not in the database");
			System.out.println();
		}
		
	} // end addFollower

	/**
	  * find whether the query word is a index or not
	  * @param word: the query word 
	  * 
	  */
	public Node findIndex(String word){
	
		Node node = null;
		ExecutionEngine engine = new ExecutionEngine(db);
		ExecutionResult result;
		
		try(Transaction transction=db.beginTx()){
			//suppose it's called value
			result=engine.execute("MATCH (a) Where a.value='"+ word + "' RETURN  a");
			node=(Node) result.columnAs("a");
			transction.success();
		}
		
		 return node;	
		 
	}
	
	/**
	  * find the followers of the questioner who has the index terms
	  * @param index: the index node
	  * @param Uid: the questioner's id
	  */
	public Map<Long, String> findAnswerBL(String index,Long Uid){
		
		 //Set<String> users = new HashSet<>();
		 Map<Long, String> users =new HashMap<>();
		 ExecutionEngine engine = new ExecutionEngine(db);
		 ExecutionResult result;
		 
		 try(Transaction transction=db.beginTx()){
			  
			// result=engine.execute("MATCH (a)-[:`Followed`]->(b)-[:`Indexed`]-(c) where c.token='"+index+"' and b.ID="+Uid+" RETURN a");
			 result = engine.execute("MATCH (b)<-[:`Followed`]-(a)-[:`Indexed`]-(c) where c.token='"+index+"' and b.ID="+Uid+" RETURN a");
			//result=engine.execute("MATCH (b:`User`) where b.ID="+Uid+" RETURN b");
			 //System.out.println("Check Uid: "+Uid);
			 System.out.println("Check index: "+index);
			 //result=engine.execute("MATCH (a)-[:`Followed`]-(b) where a.ID="+Uid+" RETURN b");
			 //result=engine.execute("MATCH (a)-[:`Indexed`]-(b:`User`) where a.token='"+index+"' RETURN b");
			 //result=engine.execute("MATCH (a)-[:`Indexed`]-(b:`User`) where a.token='pittsburgh' RETURN b");
			 for(Map<String,Object> map : result){
				 Node temp=(Node) map.get("a");
				 String name=(String) temp.getProperty("name");
				 Long id=(Long)temp.getProperty("ID");
				 users.put(id, name);
			 } 
			 System.out.println("This is the partial results: "+users);
			 transction.success();
		 }
		 return users;
	}
	
	public Map<String, Float> findAnswerProb(Map<Long, String> answerer,List<String> query,Long Uid){
		
		 Set<String> users = new HashSet<>();
		 Map<String, Float> unsort = new HashMap<>();
		 ExecutionEngine engine = new ExecutionEngine(db);
		 ExecutionResult result;
		 
		 try(Transaction transction=db.beginTx()){
			  Iterator it = answerer.entrySet().iterator();
			  while(it.hasNext()){
				  Map.Entry pair= (Map.Entry) it.next();
				  String name = (String) pair.getValue();
				  Long id = (Long) pair.getKey();
				  float prob = (float) 1.0;
				  System.out.println("------Answerers' Name---:"+name);
				  result = engine.execute("MATCH (a) where a.ID="+id+" RETURN a");
				  for(Map<String,Object> map : result){
					     System.out.println("-----------Prob Result:-----"+map);
						 Node ans=(Node) map.get("a");
						 int D=(int) ans.getProperty("DF");
						 int V=(int) ans.getProperty("CF");
						 for(String word:query){
							 int tf=getTF(word,id);
							 prob*=(float) (tf+1)/(D+V);
							 System.out.println("DF: "+D+" CF: "+V+" tf+ "+tf);
							 System.out.println(prob);
							 
						 }
				  }
				  prob = (float) Math.log(prob);
				  System.out.println("---------The total probabality is : "+prob);
				  unsort.put(name,prob);
			  }
			  
			  System.out.println("findAnswerProb is done");
			  transction.success();
			  
		}
		 System.out.println("---------The unsort result is : "+unsort);
		 return unsort;
	}

	
	/**
	  * find the followers of the user
	  * @param id: the id of the user
	  * @param graphDataService: the address of database
	  */
	public Set<Node> findFollowed(Long id){
		
		Set<Node> users = new HashSet<>();
		ExecutionEngine engine = new ExecutionEngine(db);
		ExecutionResult result;
		
		try(Transaction transction=db.beginTx()){
			//suppose the relationship named 'FOLLOWED', and the property called id
			result=engine.execute("MATCH (a)-[:`FOLLOWED`]->(b) where b.id='"+id+"' RETURN a");
			 for(Map<String,Object> map : result){
				 Node temp=(Node) map.get("a");
				 users.add(temp);	 
			 }
			 transction.success();
		}	
		return users;
	}
	
	 // end addFollowing...
	
	/**
	 * shut down the database
	 * no parameter
	 * return none
	 */
    public void shutDown()
    {
        System.out.println();
        System.out.println( "Shutting down database ..." );
        // START SNIPPET: shutdownServer
        db.shutdown();
        // END SNIPPET: shutdownServer
    } // end shutdown
    
}