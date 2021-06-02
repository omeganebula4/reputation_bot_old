package reputation_bot;

import java.util.List;

import lib.bot.cmd.AbstractCommand;
import lib.bot.cmd.annotation.Args;
import lib.bot.management.PermissionManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Remrep extends AbstractCommand {

    public Remrep() {
        super("remrep", "Removes specified number of reps from the specified user.");
    }

    @Args(min = 1, max = 2)
    Member name;
    @Override
    public boolean onCommand(MessageReceivedEvent event, String s, String rawArguments, List<String> list) {
    	if (event.getMessage().getMentionedMembers().isEmpty() == false) {
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
    		if (list.size() == 1) {
        		event.getChannel().sendTyping().queue();
    	        event.getChannel().sendMessage("Removed 1 rep from <@" + name.getId() + ">").queue();
    	        //remove 1 rep from user
        	}
        	else if (list.size() == 2){
        		String rep = list.get(1);
    			try {
    			    int repInt = Integer.parseInt(rep);
    			    if (repInt > 0) { 
    			    	event.getChannel().sendTyping().queue();
    	            	event.getChannel().sendMessage("Removed " + repInt + " rep from <@" + name.getId() + ">").queue();
    	            	//remove 'repInt' rep from user
    				} 
    			    else {
    			    	return true;
    			    }
    			}
    			catch (NumberFormatException e) {
    			    return true;
    			}
    		}
    	}
    	else if (event.getMessage().getMentionedMembers().isEmpty() == true) {
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
                "Removes 3 reputation points from the balance of User#1234 (default number is 1 if not specified)";
    }
}
