package qa.util;

import qa.connection.Parameter;

public class QuestionUtil {
	
	public static String removeUserName(String q){
		return q.replaceAll(Parameter.USER_NAME, "");
	}
	
	public static void main(String args[]){
		System.out.println(QuestionUtil.removeUserName("what are the color of water? @Dr_Answerer"));
	}
}
