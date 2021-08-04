package reputation_bot;

import java.util.List;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import lib.bot.cmd.AbstractCommand;
import lib.bot.cmd.annotation.Args;
import lib.bot.management.PermissionManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Setrep extends AbstractCommand {
	
	private ReputationDAO reputationDAO;
    public Setrep(ReputationDAO reputationDAO) {
        super("setrep", "Sets the number of rep of the specified user to the specified number in the specified database.");
        this.reputationDAO = reputationDAO;
    }

    @Args(min = 3, max = 3)
    
    @Override
    public boolean onCommand(MessageReceivedEvent event, String s, String rawArguments, List<String> list) {
    	if (event.getGuild().getIdLong() == Main.guildID) {
	    	Member name = null;
	    	
	    	int replyInt = CommandDetectionUtil.ReplyDetection(event, list, reputationDAO.alltimeCollection);
	    	switch(replyInt) {
	    	case 0:
	    		return true;
			case 1:
	    		name = event.getMessage().getMentionedMembers().get(0);
	    		break;
	    	case 2:
	    		name = event.getMessage().getMentionedMembers().get(1);
	    		break;
	    	case 3:
	    		name = event.getGuild().getMemberById(Long.parseLong(list.get(0)));
	    		break;
	    	}
	    	
	    	int repInt = CommandDetectionUtil.RepDetectionForSetrep(name, event, list);
	    	switch(repInt) {
	    	case 0:
	    		return true;
	    	case 1:
	    		reputationDAO.weeklyCollection.updateOne(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.set("repAmount", Integer.parseInt(list.get(2))));
	    		event.getChannel().sendTyping().queue();
	    		event.getChannel().sendMessage("Set <@" + name.getId() + ">'s weekly rep balance to " + list.get(2)).queue();
	        	break;
	    	case 2:
	    		reputationDAO.monthlyCollection.updateOne(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.set("repAmount", Integer.parseInt(list.get(2))));
	    		event.getChannel().sendTyping().queue();
	    		event.getChannel().sendMessage("Set <@" + name.getId() + ">'s monthly rep balance to " + list.get(2)).queue();
	        	break;
	    	case 3:
	    		reputationDAO.alltimeCollection.updateOne(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.set("repAmount", Integer.parseInt(list.get(2))));
	    		event.getChannel().sendTyping().queue();
	    		event.getChannel().sendMessage("Set <@" + name.getId() + ">'s alltime rep balance to " + list.get(2)).queue();
	        	break;
	    	}
    	}
    	else {
    		event.getChannel().sendTyping().queue();
    		event.getChannel().sendMessage("ReputationBot is only available in one guild.").queue();
    	}
		return false;
    }

    @Override
    protected boolean hasPermission(PermissionManager pm, MessageReceivedEvent messageContext, List<String> args) {
        return super.hasPermission(pm, messageContext, args);
        //return CommandDetectionUtil.hasPerms(messageContext.getMember(), messageContext.getGuild());
    }

    @Override
    protected String usageMessage() {
        return "%c [@user] (weekly/monthly/alltime) (number of rep) \n" +
        		"All parameters are required.";
    }

    @Override
    protected String examplesMessage() {
        return "%c @User#1234 monthly 3 \n" +
                "Sets the number of rep of @User#1234 in the monthly database to 3.";
    }
}
