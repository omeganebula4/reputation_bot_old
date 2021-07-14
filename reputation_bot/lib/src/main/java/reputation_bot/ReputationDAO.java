package reputation_bot;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.Timer;
import java.util.TimerTask;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import lib.bot.utils.MonthlyTimer;


public class ReputationDAO{
	
	static CodecRegistry pojoCodecRegistry = fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(
                    PojoCodecProvider.builder().automatic(true).build()
            )
    );

    static MongoClientSettings settings = MongoClientSettings.builder()
            .codecRegistry(pojoCodecRegistry)
            .build();
    
	static MongoClient mongoClient = MongoClients.create(settings);
	static MongoDatabase test;
	MongoCollection<ReputationData> alltimeCollection;
	MongoCollection<ReputationData> monthlyCollection;
	MongoCollection<ReputationData> weeklyCollection;
	
	Timer weeklyTimer = new Timer();
	TimerTask weeklyTimerTask = new TimerTask() {
		public void run() {
			FindIterable<ReputationData> fi = weeklyCollection.find();
		    MongoCursor<ReputationData> cursor = fi.iterator();
		    try {
		        while(cursor.hasNext()) {               
		        	weeklyCollection.updateOne(Filters.and(Filters.eq("memberID", cursor.next().getMemberID()), Filters.eq("guildID", Main.guildID)), Updates.set("repAmount", 0));
		        }
		    } finally {
		        cursor.close();
		    }
		}
	};
	
	MonthlyTimer timer = new MonthlyTimer(1, () -> {
    	FindIterable<ReputationData> fi = monthlyCollection.find();
	    MongoCursor<ReputationData> cursor = fi.iterator();
	    try {
	        while(cursor.hasNext()) {               
	        	monthlyCollection.updateOne(Filters.and(Filters.eq("memberID", cursor.next().getMemberID()), Filters.eq("guildID", Main.guildID)), Updates.set("repAmount", 0));
	        }
	    } finally {
	        cursor.close();
	    }
    });

	public void MainInit() {
        test = mongoClient.getDatabase("test");
        alltimeCollection = test.getCollection("alltimeCollection", ReputationData.class);
        monthlyCollection = test.getCollection("monthlyCollection", ReputationData.class);
        weeklyCollection = test.getCollection("weeklyCollection", ReputationData.class);
		
        timer.start();
        weeklyTimer.scheduleAtFixedRate(weeklyTimerTask, 604800000, 604800000);
    }
	
}
