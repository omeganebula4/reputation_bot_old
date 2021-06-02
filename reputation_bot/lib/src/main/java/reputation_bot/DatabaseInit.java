package reputation_bot;

import java.util.function.Consumer;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

class MemberConsumer implements Consumer<Member> {
	public void accept(Member member) {
		if (!member.getUser().isBot()) {
			long count = DatabaseInit.collection.countDocuments(Filters.and(Filters.eq("memberID", member.getIdLong()), Filters.eq("guildID", Main.guildID)));
			
			if (count <= 0) {
				System.out.println(member.getEffectiveName());
				SetObj memberObj = new SetObj();
				
				memberObj.setGuildID(Main.guildID);
				memberObj.setMemberID(member.getIdLong());
				memberObj.setRepAmount(0);
				memberObj.setName(member.getEffectiveName());
				
				DatabaseInit.collection.insertOne(memberObj);
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
	static MongoCollection<SetObj> collection;
	
	@Override
	public void onReady(ReadyEvent event) {
        System.out.println("API is ready!");
        Guild guild = event.getJDA().getGuildById(Long.toString(Main.guildID));
        
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        test = mongoClient.getDatabase("test");
        collection = test.getCollection("repCollection", SetObj.class);
        
        Consumer<Member> c = new MemberConsumer();
        guild.loadMembers(c);
    }
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		Member newMember = event.getMember();
		if (!newMember.getUser().isBot()) {
			long count = collection.countDocuments(Filters.and(Filters.eq("nemberID", newMember.getIdLong()), Filters.eq("guildID", Main.guildID)));
			
			if (count <= 0) {
				System.out.println(newMember.getEffectiveName());
				SetObj memberObj = new SetObj();
				
				memberObj.setGuildID(Main.guildID);
				memberObj.setMemberID(newMember.getIdLong());
				memberObj.setRepAmount(0);
				memberObj.setName(newMember.getEffectiveName());
				
				collection.insertOne(memberObj);
			}
		}
	}
	
	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
		Member leftMember = event.getMember();
		if (!leftMember.getUser().isBot()) {
			long count = collection.countDocuments(Filters.and(Filters.eq("memberID", leftMember.getIdLong()), Filters.eq("guildID", Main.guildID), Filters.eq("repAmount", 0)));
			
			if (count > 0) {
				collection.deleteMany(Filters.and(Filters.eq("memberID", leftMember.getIdLong()), Filters.eq("guildID", Main.guildID), Filters.eq("repAmount", 0)));
			}
		}
	}
}
