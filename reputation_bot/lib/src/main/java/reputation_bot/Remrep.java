package reputation_bot;

import java.util.List;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import lib.bot.cmd.AbstractCommand;
import lib.bot.cmd.annotation.Args;
import lib.bot.management.PermissionManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Remrep extends AbstractCommand {
	private ReputationDAO reputationDAO;
    public Remrep(ReputationDAO reputationDAO) {
        super("remrep", "Removes specified number of reps from the specified user in the specified database.");
        this.reputationDAO = reputationDAO;
    }

    @Args(min = 2, max = 3)
    
    @Override
    public boolean onCommand(MessageReceivedEvent event, String s, String rawArguments, List<String> list) {
    	Member name = null;
    	int replyInt = CommandDetectionUtil.ReplyDetection(event);
    	switch(replyInt) {
    	case 0:
    		return true;
		case 1:
    		name = event.getMessage().getMentionedMembers().get(0);
    		break;
    	case 2:
    		name = event.getMessage().getMentionedMembers().get(1);
    		break;
    	}
    	
    	int repInt = CommandDetectionUtil.RepDetection(name, event, list);
    	switch(repInt) {
    	case 0:
    		return true;
    	case 1:
    		reputationDAO.weeklyCollection.updateOne(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.inc("repAmount", -1));
			event.getChannel().sendTyping().queue();
	        event.getChannel().sendMessage("Removed 1 rep from <@" + name.getId() + "> (Weekly collection)").queue();
	        break;
    	case 2:
    		reputationDAO.monthlyCollection.updateOne(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.inc("repAmount", -1));
    		event.getChannel().sendTyping().queue();
    		event.getChannel().sendMessage("Removed 1 rep from <@" + name.getId() + "> (Monthly collection)").queue();
        	break;
    	case 3:
    		reputationDAO.alltimeCollection.updateOne(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.inc("repAmount", -1));
    		event.getChannel().sendTyping().queue();
    		event.getChannel().sendMessage("Removed 1 rep from <@" + name.getId() + "> (Alltime collection)").queue();
        	break;
    	case 4:
    		reputationDAO.weeklyCollection.updateOne(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.inc("repAmount", -Integer.parseInt(list.get(2))));
    		event.getChannel().sendTyping().queue();
    		event.getChannel().sendMessage("Removed " + list.get(2) + " rep from <@" + name.getId() + "> (Weekly collection)").queue();
        	break;
    	case 5:
    		reputationDAO.monthlyCollection.updateOne(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.inc("repAmount", -Integer.parseInt(list.get(2))));
    		event.getChannel().sendTyping().queue();
    		event.getChannel().sendMessage("Removed " + list.get(2) + " rep from <@" + name.getId() + "> (Monthly collection)").queue();
        	break;
    	case 6:
    		reputationDAO.alltimeCollection.updateOne(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.inc("repAmount", -Integer.parseInt(list.get(2))));
    		event.getChannel().sendTyping().queue();
    		event.getChannel().sendMessage("Removed " + list.get(2) + " rep from <@" + name.getId() + "> (Alltime collection)").queue();
        	break;
		}
    	return false;
    }

    @Override
    protected boolean hasPermission(PermissionManager pm, MessageReceivedEvent messageContext, List<String> args) {
        return super.hasPermission(pm, messageContext, args);
        //return pm.isBotAdmin(messageContext.getMember(), messageContext.getGuild());
    }

    @Override
    protected String usageMessage() {
        return "%c [@user] weekly/monthly/alltime (number of rep)";

    }

    @Override
    protected String examplesMessage() {
        return "%c @User#1234 weekly 3 \n" +
                "Removes 3 reputation points from the balance of User#1234 in the weekly database (default number is 1 if not specified)";
    }
}
