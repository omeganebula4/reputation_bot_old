package reputation_bot;

import java.util.List;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import lib.bot.cmd.AbstractCommand;
import lib.bot.cmd.annotation.Args;
import lib.bot.management.PermissionManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Addrep extends AbstractCommand {

    public Addrep() {
        super("addrep", "Adds specified number of reps to the specified user in the specified collection (weekly/monthly/alltime)");
    }

    @Args(min = 2, max = 3)
    
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
    			if (list.size() == 2) {
    				if (list.get(1).equals("alltime")) {
    					DatabaseInit.alltimeCollection.updateOne(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.inc("repAmount", 1));
    					event.getChannel().sendTyping().queue();
            	        event.getChannel().sendMessage("Added 1 rep to <@" + name.getId() + "> (All-time collection)").queue();
    				}
    				else if (list.get(1).equals("monthly")) {
    					DatabaseInit.monthlyCollection.updateOne(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.inc("repAmount", 1));
    					event.getChannel().sendTyping().queue();
            	        event.getChannel().sendMessage("Added 1 rep to <@" + name.getId() + "> (Weekly collection)").queue();
    				}
    				else if (list.get(1).equals("weekly"))  {
    					DatabaseInit.weeklyCollection.updateOne(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.inc("repAmount", 1));
    					event.getChannel().sendTyping().queue();
            	        event.getChannel().sendMessage("Added 1 rep to <@" + name.getId() + "> (Weekly collection)").queue();
    				}
    				else {
    					return true;
    				}
            	}
    			
            	else if (list.size() == 3){
            		String rep = list.get(2);
            		if (list.get(1).equals("alltime")) {
            			try {
            			    int repInt = Integer.parseInt(rep);
            			    if (repInt > 0) { 
            			    	DatabaseInit.alltimeCollection.updateOne(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.inc("repAmount", repInt));
            			    	event.getChannel().sendTyping().queue();
            	            	event.getChannel().sendMessage("Added " + repInt + " rep to <@" + name.getId() + "> (All-time collection)").queue();
            				} 
            			    else {
            			    	return true;
            			    }
            			}
            			catch (NumberFormatException e) {
            			    return true;
            			}
    				}
    				else if (list.get(1).equals("monthly")) {
            			try {
            			    int repInt = Integer.parseInt(rep);
            			    if (repInt > 0) { 
            			    	DatabaseInit.monthlyCollection.updateOne(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.inc("repAmount", repInt));
            			    	event.getChannel().sendTyping().queue();
            	            	event.getChannel().sendMessage("Added " + repInt + " rep to <@" + name.getId() + "> (Monthly collection)").queue();
            				} 
            			    else {
            			    	return true;
            			    }
            			}
            			catch (NumberFormatException e) {
            			    return true;
            			}
    				}
    				else if (list.get(1).equals("weekly"))  {
            			try {
            			    int repInt = Integer.parseInt(rep);
            			    if (repInt > 0) { 
            			    	DatabaseInit.weeklyCollection.updateOne(Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)), Updates.inc("repAmount", repInt));
            			    	event.getChannel().sendTyping().queue();
            	            	event.getChannel().sendMessage("Added " + repInt + " rep to <@" + name.getId() + "> (Weekly collection)").queue();
            				} 
            			    else {
            			    	return true;
            			    }
            			}
            			catch (NumberFormatException e) {
            			    return true;
            			}
    				}
    				else {
    					return true;
    				}
            		
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
        return "%c [@user] (weekly/monthly/alltime) (number of rep)";

    }

    @Override
    protected String examplesMessage() {
        return "%c @User#1234 alltime 3 \n" +
                "Adds 3 reputation points in the alltime balance of User#1234 (default number is 1 if not specified)";
    }
}