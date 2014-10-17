package mdproto;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class Db {
	private static DB getDB() {
		MongoClient client;
		try {
			client = new MongoClient();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		return client.getDB("mdproto");
	}
	static DB db = getDB();
	static DBCollection publications = db.getCollection("publications");

}
