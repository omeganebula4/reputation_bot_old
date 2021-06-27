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

    public Setrep() {
        super("setrep", "Sets the number of rep of the specified user to the specified number.");
    }

    @Args(min = 3, max = 3)
    
    @Override
    public boolean onCommand(MessageReceivedEvent event, String s, String rawArguments, List<String> list) {
    	Member name = null;
    	if (!event.getMessage().getMentionedMembers().isEmpty()) {
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
    		if (!name.getUser().isBot()) {
    			String rep = list.get(2);
        		try {
        			int repInt = Integer.parseInt(rep);
        			if (list.get(1).equals("alltime")) {
            			DatabaseInit.alltimeCollection.updateOne(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.set("repAmount", repInt));
    					event.getChannel().sendTyping().queue();
    					event.getChannel().sendMessage("Set <@" + name.getId() + ">'s rep to " + rep + " (All-time collection)").queue();
    				}
    				else if (list.get(1).equals("monthly")) {
    					DatabaseInit.monthlyCollection.updateOne(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.set("repAmount", repInt));
    					event.getChannel().sendTyping().queue();
    					event.getChannel().sendMessage("Set <@" + name.getId() + ">'s rep to " + rep + " (Monthly collection)").queue();
    				}
    				else if (list.get(1).equals("weekly"))  {
    					DatabaseInit.weeklyCollection.updateOne(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.set("repAmount", repInt));
    					event.getChannel().sendTyping().queue();
    					event.getChannel().sendMessage("Set <@" + name.getId() + ">'s rep to " + rep + " (Weekly collection)").queue();
    				}
    				else {
    					return true;
    				}
        		}
        		catch (NumberFormatException e) {
        			return true;
        		}
    		}
    		else if (name.getUser().isBot()) {
    			return true;
    		}
    	}
    	else if (event.getMessage().getMentionedMembers().isEmpty()) {
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
    protected String usageMessage() {
        return "%c [@user] (number of rep)";

    }

    @Override
    protected String examplesMessage() {
        return "%c @User#1234 3 \n" +
                "Sets the number of rep of @User#1234 to 3.";
    }
}
