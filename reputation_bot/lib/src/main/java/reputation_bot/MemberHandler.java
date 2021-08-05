package reputation_bot;

import java.util.function.Consumer;

import com.mongodb.client.model.Filters;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

class MemberConsumer implements Consumer<Member> {
	private ReputationDAO reputationDAO;
	public MemberConsumer(ReputationDAO reputationDAO) {
		this.reputationDAO = reputationDAO;
	}
	
	public void accept(Member member) {
		if (!member.getUser().isBot()) {
			long count = reputationDAO.alltimeCollection.countDocuments(Filters.and(Filters.eq("memberID", member.getIdLong()), Filters.eq("guildID", Main.guildID)));
			
			if (count <= 0) {
				ReputationData memberObj = new ReputationData();
				
				memberObj.setGuildID(Main.guildID);
				memberObj.setMemberID(member.getIdLong());
				memberObj.setRepAmount(0);
				
				reputationDAO.alltimeCollection.insertOne(memberObj);
				reputationDAO.monthlyCollection.insertOne(memberObj);
				reputationDAO.weeklyCollection.insertOne(memberObj);
			}
		}
	}
}

public class MemberHandler extends ListenerAdapter{
	
	private ReputationDAO reputationDAO;
	public MemberHandler(ReputationDAO reputationDAO) {
		this.reputationDAO = reputationDAO;
	}
	
	@Override
	public void onReady(ReadyEvent event) {
		Consumer<Member> c = new MemberConsumer(reputationDAO);
		Guild guild = event.getJDA().getGuildById(Long.toString(Main.guildID));
		guild.loadMembers(c);
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		Member newMember = event.getMember();
		if (!newMember.getUser().isBot()) {
			long count = reputationDAO.alltimeCollection.countDocuments(Filters.and(Filters.eq("memberID", newMember.getIdLong()), Filters.eq("guildID", Main.guildID)));
			
			if (count <= 0) {
				System.out.println(newMember.getEffectiveName());
				ReputationData memberObj = new ReputationData();
				
				memberObj.setGuildID(Main.guildID);
				memberObj.setMemberID(newMember.getIdLong());
				memberObj.setRepAmount(0);
				
				reputationDAO.alltimeCollection.insertOne(memberObj);
				reputationDAO.monthlyCollection.insertOne(memberObj);
				reputationDAO.weeklyCollection.insertOne(memberObj);
			}
		}
	}
	
	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
		Member leftMember = event.getMember();
		if (!leftMember.getUser().isBot()) {
			long count = reputationDAO.alltimeCollection.countDocuments(Filters.and(Filters.eq("memberID", leftMember.getIdLong()), Filters.eq("guildID", Main.guildID), Filters.eq("repAmount", 0)));
			
			if (count > 0) {
				reputationDAO.alltimeCollection.deleteMany(Filters.and(Filters.eq("memberID", leftMember.getIdLong()), Filters.eq("guildID", Main.guildID), Filters.eq("repAmount", 0)));
				reputationDAO.monthlyCollection.deleteMany(Filters.and(Filters.eq("memberID", leftMember.getIdLong()), Filters.eq("guildID", Main.guildID), Filters.eq("repAmount", 0)));
				reputationDAO.weeklyCollection.deleteMany(Filters.and(Filters.eq("memberID", leftMember.getIdLong()), Filters.eq("guildID", Main.guildID), Filters.eq("repAmount", 0)));
			}
		}
	}
}
