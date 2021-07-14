package reputation_bot;

import java.util.Collections;
import java.util.List;

import com.mongodb.client.model.Filters;

import lib.bot.cmd.AbstractCommand;
import lib.bot.cmd.annotation.Args;
import lib.bot.management.PermissionManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Checkrep extends AbstractCommand {
	public Checkrep() {
        super("checkrep", "Displays the rank and rep information about the user specified or the author.");
        //String[] aliases = {"rep"};
        //List<String> aliasList = new ArrayList<>(Arrays.asList(aliases));
	}

    @Args(min = 0, max = 1)
    
    @Override
    public boolean onCommand(MessageReceivedEvent event, String s, String rawArguments, List<String> list) {
    	User name = null;
    	long repNumAlltime;
		long repNumWeekly;
		long repNumMonthly;
    	long alltimeRank, weeklyRank, monthlyRank;
    	if (list.size() == 0) {
    		name = event.getMessage().getMember().getUser();
    	}
    	else if (list.size() == 1) {
    		if (!event.getMessage().getMentionedMembers().isEmpty()) {
    			if (event.getMessage().getReferencedMessage() == null) {
    				name = event.getMessage().getMentionedMembers().get(0).getUser();
    			}
    			else if (event.getMessage().getReferencedMessage() != null){
    				if (event.getMessage().getMentionedMembers().size() == 2) {
    					name = event.getMessage().getMentionedMembers().get(1).getUser();
    				}
    				else if (event.getMessage().getMentionedMembers().size() == 1) {
    					name = event.getMessage().getMentionedMembers().get(0).getUser();
    				}
    				else {
    					return true;
    				}
    			}
    		}
    		else if (event.getMessage().getMentionedMembers().isEmpty()) {
    			Guild guild = event.getGuild();
    			try{ 
    				Long.parseLong(list.get(0)); 
    				if (guild.isMember(event.getJDA().getUserById(list.get(0)))) {
    					name = event.getJDA().getUserById(list.get(0));
    					System.out.println(name);
    				}
    				else {
        				return true;
        			}
    			}
    			
    			catch(Exception e){
    				return true;
    			}
    		}
    	}
    	else {
    		return true;
    	}
    	
    	if (name != null & !name.isBot()) {
    		repNumAlltime = ReputationDAO.alltimeCollection.find(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID))).first().getRepAmount();
    		alltimeRank = ReputationDAO.alltimeCollection.countDocuments(Filters.and(Filters.eq("guildID", Main.guildID), Filters.gt("repAmount", repNumAlltime))) + 1;
    		repNumWeekly = ReputationDAO.weeklyCollection.find(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID))).first().getRepAmount();
    		weeklyRank = ReputationDAO.weeklyCollection.countDocuments(Filters.and(Filters.eq("guildID", Main.guildID), Filters.gt("repAmount", repNumWeekly))) + 1;
    		repNumMonthly = ReputationDAO.monthlyCollection.find(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID))).first().getRepAmount();
    		monthlyRank = ReputationDAO.monthlyCollection.countDocuments(Filters.and(Filters.eq("guildID", Main.guildID), Filters.gt("repAmount", repNumMonthly))) + 1;
    		
    		EmbedBuilder authorRank = new EmbedBuilder();
    		authorRank.setTitle(name.getName() + "'s Reputation Information");
    		authorRank.addField("All-time Leaderboard", "Rank: " + Long.toString(alltimeRank) + "\n" + "Rep: " + Long.toString(repNumAlltime), false);
    		authorRank.addField("Weekly Leaderboard", "Rank: " + Long.toString(weeklyRank) + "\n" + "Rep: " + Long.toString(repNumWeekly), false);
    		authorRank.addField("Monthly Leaderboard", "Rank: " + Long.toString(monthlyRank) + "\n" + "Rep: " + Long.toString(repNumMonthly), false);
    		authorRank.setColor(0x4f068b);
    		event.getChannel().sendTyping().queue();
    		event.getChannel().sendMessage(authorRank.build()).queue();
    		authorRank.clear();
    	}
    	else {
    		return true;
    	}
    	return false;
    }

    @Override
    protected boolean hasPermission(PermissionManager pm, MessageReceivedEvent messageContext, List<String> args) {
        return super.hasPermission(pm, messageContext, args);
        //return pm.isBotAdmin(messageContext.getMember(), messageContext.getGuild());
    }

    @Override
    public List<String> getAliases() {
    	return Collections.singletonList("rep");
    }

    @Override
    protected String usageMessage() {
        return "%c [@user]";
    }

    @Override
    protected String examplesMessage() {
        return "%c @User#1234 \n" +
                "Shows the reputation points and rank of User#1234 on all of the leaderboards.";
    }
    
}
