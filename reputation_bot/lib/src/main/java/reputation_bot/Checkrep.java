package reputation_bot;

import java.util.Collections;
import java.util.List;

import com.mongodb.client.model.Filters;

import lib.bot.cmd.AbstractCommand;
import lib.bot.cmd.annotation.Args;
import lib.bot.management.PermissionManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Checkrep extends AbstractCommand {
	public Checkrep() {
        super("checkrep", "Displays the rank of the user specified or the author of the message in the leaderboard mentioned (default is alltime leaderboard)");
        //String[] aliases = {"rep"};
        //List<String> aliasList = new ArrayList<>(Arrays.asList(aliases));
        super.getAliases();
	}

    @Args(min = 0, max = 1)
    
    @Override
    public boolean onCommand(MessageReceivedEvent event, String s, String rawArguments, List<String> list) {
    	Member name = null;
    	int repNumAlltime, repNumWeekly, repNumMonthly;
    	long alltimeRank, weeklyRank, monthlyRank;
    	if (list.size() == 0) {
    		name = event.getMessage().getMember();
    		repNumAlltime = DatabaseInit.alltimeCollection.find(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID))).first().getRepAmount();
    		alltimeRank = DatabaseInit.alltimeCollection.countDocuments(Filters.and(Filters.eq("guildID", Main.guildID), Filters.gt("repAmount", repNumAlltime))) + 1;
    		repNumWeekly = DatabaseInit.weeklyCollection.find(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID))).first().getRepAmount();
    		weeklyRank = DatabaseInit.weeklyCollection.countDocuments(Filters.and(Filters.eq("guildID", Main.guildID), Filters.gt("repAmount", repNumAlltime))) + 1;
    		repNumMonthly = DatabaseInit.monthlyCollection.find(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID))).first().getRepAmount();
    		monthlyRank = DatabaseInit.monthlyCollection.countDocuments(Filters.and(Filters.eq("guildID", Main.guildID), Filters.gt("repAmount", repNumAlltime))) + 1;
    		
    		EmbedBuilder authorRank = new EmbedBuilder();
    		authorRank.setTitle(event.getMember().getEffectiveName() + "'s Reputation Information");
    		authorRank.addField("All-time Leaderboard", "Rank: " + Long.toString(alltimeRank) + "\n" + "Rep: " + Integer.toString(repNumAlltime), false);
    		authorRank.addField("Weekly Leaderboard", "Rank: " + Long.toString(weeklyRank) + "\n" + "Rep: " + Integer.toString(repNumWeekly), false);
    		authorRank.addField("Monthly Leaderboard", "Rank: " + Long.toString(monthlyRank) + "\n" + "Rep: " + Integer.toString(repNumMonthly), false);
    		authorRank.setColor(0x4f068b);
    		event.getChannel().sendTyping().queue();
	        event.getChannel().sendMessage(authorRank.build()).queue();
	        authorRank.clear();
    	}
    	else if (list.size() == 1) {
    		if (event.getMessage().getReferencedMessage() == null) {
    			name = event.getMessage().getMentionedMembers().get(0);
    		}
    		else if (event.getMessage().getReferencedMessage() != null){
    			if (event.getMessage().getMentionedMembers().size() == 2) {
    				name = event.getMessage().getMentionedMembers().get(1);
    			}
    			else if (event.getMessage().getMentionedMembers().size() == 1) {
    				name = event.getMessage().getMentionedMembers().get(0);
    			}
    			else {
    				return true;
    			}
    		}
    		//this is not complete yet
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
        return "%c [@user] (number of rep)";
    }

    @Override
    protected String examplesMessage() {
        return "%c @User#1234 3 \n" +
                "Adds 3 reputation points in the balance of User#1234 (default number is 1 if not specified)";
    }
    
}
