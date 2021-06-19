package reputation_bot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RepDetect extends ListenerAdapter{
	
	volatile List<Long> prison = new ArrayList<Long>();
	ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	int tyTrue = 0;
	int thxTrue = 0;

	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String args = event.getMessage().getContentStripped();
		
		String[] argslist = event.getMessage().getContentRaw().replaceAll("\\.","").split("\\s+");
		List<String> list = Arrays.asList(argslist);
		
		List<Member> mentioned = event.getMessage().getMentionedMembers();
		LinkedHashSet<Member> linkedHashSet = new LinkedHashSet<Member>(mentioned);
		List<Member> mentioned_refined = new ArrayList<Member>(linkedHashSet);
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).contains("ty") | list.get(i).contains("Ty") | list.get(i).contains("TY")) {
				
				String[] tylist = list.get(i).toLowerCase().split("");
            	List<String> tyArraylist = new ArrayList<String>(Arrays.asList(tylist));
            	tyArraylist.remove(0);
            	LinkedHashSet<String> linkedHashSetForTy = new LinkedHashSet<String>(tyArraylist);
            	List<String> refinedTyArraylist = new ArrayList<String>(linkedHashSetForTy);
            	String tyString = String.join("", refinedTyArraylist);
            	if (tyString.contentEquals("y")) {
            		tyTrue = 1;
            	}
            	
			}
			
			else if (list.get(i).contains("thx") | list.get(i).contains("Thx") | list.get(i).contains("THX")) {
				
				String[] thxlist = list.get(i).toLowerCase().split("");
            	List<String> thxArraylist = new ArrayList<String>(Arrays.asList(thxlist));
            	thxArraylist.remove(0);
            	thxArraylist.remove(0);
            	LinkedHashSet<String> linkedHashSetForThx = new LinkedHashSet<String>(thxArraylist);
            	List<String> refinedThxArraylist = new ArrayList<String>(linkedHashSetForThx);
            	String thxString = String.join("", refinedThxArraylist);
            	if (thxString.contentEquals("x") | thxString.contentEquals("hx")) {
            		thxTrue = 1;
            	}
            	
			}
			
		}
		if (tyTrue == 1 | thxTrue == 1 | list.contains("tyty") | list.contains("Tyty") | args.contains("thank") | args.contains("Thank") | args.contains("Thanx") | args.contains("thanx") | args.contains("tysm") | args.contains("Tysm")) {
			
			tyTrue = 0;
			thxTrue = 0;
			
			for (int i = 0; i < mentioned_refined.size(); i++) {
				
				if (mentioned_refined.get(i).getIdLong() == event.getAuthor().getIdLong()) {
					
		        	mentioned_refined.remove(mentioned_refined.get(i));
		        	event.getChannel().sendTyping().queue();
				    event.getChannel().sendMessage("Beep Boop: Rep abuse detected. Don't try to give yourself rep... :rage:").queue();
		        
				}
		    
			}
			
			if (!mentioned_refined.isEmpty()) {
				
				String names = " ";
				
			    if (!event.getAuthor().isBot()) {
			    	
					if (prison.contains(event.getAuthor().getIdLong())) {
						
						event.getChannel().sendTyping().queue();
						event.getChannel().sendMessage("You're still on cooldown.").queue();
						
					}
					
					else {
						
						for (int i = 0; i < mentioned_refined.size(); i++) {
							DatabaseInit.alltimeCollection.updateOne(Filters.and(Filters.eq("memberID", mentioned_refined.get(i).getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.inc("repAmount", 1));
		        			DatabaseInit.monthlyCollection.updateOne(Filters.and(Filters.eq("memberID", mentioned_refined.get(i).getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.inc("repAmount", 1));
		        			DatabaseInit.weeklyCollection.updateOne(Filters.and(Filters.eq("memberID", mentioned_refined.get(i).getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.inc("repAmount", 1));
					        names = names + "<@" + mentioned_refined.get(i).getId() + "> ";
					    }
						
					    event.getChannel().sendTyping().queue();
					    event.getChannel().sendMessage("Added 1 rep to" + names).allowedMentions(new ArrayList<>()).queue();
					    
						prison.add(event.getAuthor().getIdLong());
						scheduler.schedule(() -> {
							prison.remove(event.getAuthor().getIdLong());
						}, 20, TimeUnit.SECONDS);
						
					}
					
				}
			    
			}
		}
	}
}
