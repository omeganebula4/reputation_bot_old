package reputation_bot;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

class MemberConsumer implements Consumer<Member> {
	public void accept(Member member) {
		if (!member.getUser().isBot()) {
			long count = DatabaseInit.alltimeCollection.countDocuments(Filters.and(Filters.eq("memberID", member.getIdLong()), Filters.eq("guildID", Main.guildID)));
			
			if (count <= 0) {
				SetObj memberObj = new SetObj();
				
				memberObj.setGuildID(Main.guildID);
				memberObj.setMemberID(member.getIdLong());
				memberObj.setRepAmount(0);
				memberObj.setName(member.getEffectiveName());
				
				DatabaseInit.alltimeCollection.insertOne(memberObj);
				DatabaseInit.monthlyCollection.insertOne(memberObj);
				DatabaseInit.weeklyCollection.insertOne(memberObj);
			}
		}
	}
}

public class DatabaseInit extends ListenerAdapter{
	
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
	static MongoCollection<SetObj> alltimeCollection;
	static MongoCollection<SetObj> monthlyCollection;
	static MongoCollection<SetObj> weeklyCollection;
	
	Timer weeklyTimer = new Timer();
	TimerTask weeklyTimerTask = new TimerTask() {
		public void run() {
			FindIterable<SetObj> fi = weeklyCollection.find();
		    MongoCursor<SetObj> cursor = fi.iterator();
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
    	FindIterable<SetObj> fi = monthlyCollection.find();
	    MongoCursor<SetObj> cursor = fi.iterator();
	    try {
	        while(cursor.hasNext()) {               
	        	monthlyCollection.updateOne(Filters.and(Filters.eq("memberID", cursor.next().getMemberID()), Filters.eq("guildID", Main.guildID)), Updates.set("repAmount", 0));
	        }
	    } finally {
	        cursor.close();
	    }
    });
	
	@Override
	public void onReady(ReadyEvent event) {
        System.out.println("API is ready!");
        Guild guild = event.getJDA().getGuildById(Long.toString(Main.guildID));
        
        test = mongoClient.getDatabase("test");
        alltimeCollection = test.getCollection("alltimeCollection", SetObj.class);
        monthlyCollection = test.getCollection("monthlyCollection", SetObj.class);
        weeklyCollection = test.getCollection("weeklyCollection", SetObj.class);
		
        timer.start();
        weeklyTimer.scheduleAtFixedRate(weeklyTimerTask, 604800000, 604800000);
        
        Consumer<Member> c = new MemberConsumer();
        guild.loadMembers(c);
    }
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		Member newMember = event.getMember();
		if (!newMember.getUser().isBot()) {
			long count = alltimeCollection.countDocuments(Filters.and(Filters.eq("memberID", newMember.getIdLong()), Filters.eq("guildID", Main.guildID)));
			
			if (count <= 0) {
				System.out.println(newMember.getEffectiveName());
				SetObj memberObj = new SetObj();
				
				memberObj.setGuildID(Main.guildID);
				memberObj.setMemberID(newMember.getIdLong());
				memberObj.setRepAmount(0);
				memberObj.setName(newMember.getEffectiveName());
				
				alltimeCollection.insertOne(memberObj);
				monthlyCollection.insertOne(memberObj);
				weeklyCollection.insertOne(memberObj);
			}
		}
	}
	
	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
		Member leftMember = event.getMember();
		if (!leftMember.getUser().isBot()) {
			long count = alltimeCollection.countDocuments(Filters.and(Filters.eq("memberID", leftMember.getIdLong()), Filters.eq("guildID", Main.guildID), Filters.eq("repAmount", 0)));
			
			if (count > 0) {
				alltimeCollection.deleteMany(Filters.and(Filters.eq("memberID", leftMember.getIdLong()), Filters.eq("guildID", Main.guildID), Filters.eq("repAmount", 0)));
				monthlyCollection.deleteMany(Filters.and(Filters.eq("memberID", leftMember.getIdLong()), Filters.eq("guildID", Main.guildID), Filters.eq("repAmount", 0)));
				weeklyCollection.deleteMany(Filters.and(Filters.eq("memberID", leftMember.getIdLong()), Filters.eq("guildID", Main.guildID), Filters.eq("repAmount", 0)));
			}
		}
	}
}
