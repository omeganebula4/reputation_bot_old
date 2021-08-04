package reputation_bot;

import java.util.Collections;
import java.util.List;

import lib.bot.cmd.AbstractCommand;
import lib.bot.cmd.annotation.Args;
import lib.bot.management.PermissionManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;

public class LeaderboardCmd extends AbstractCommand {
	private ReputationDAO reputationDAO;
    public LeaderboardCmd(ReputationDAO reputationDAO) {
        super("leaderboard", "Shows the reputation leaderboard rankings.");
        this.reputationDAO = reputationDAO;
    }

    @Args(min = 0, max = 1)
    
    @Override
    public boolean onCommand(MessageReceivedEvent event, String s, String rawArguments, List<String> list) {
    	EmbedBuilder embedBuilder = null;
    	if (event.getGuild().getIdLong() == Main.guildID) {
	    	if (list.size() == 0) {
	    		SelectionMenu menu = SelectionMenu.create("leaderboard-selection")
	    			     .setPlaceholder("Choose the leaderboard.") // shows the placeholder indicating what this menu is for
	    			     .setRequiredRange(1, 1) // only one can be selected
	    			     .addOption("Weekly Leaderboard", "weekly")
	    			     .addOption("Monthly Leaderboard", "monthly")
	    			     .addOption("All-time Leaderboard", "alltime")
	    			     .build();
	    		event.getChannel().sendMessage("Select the leaderboard to be displayed.")
	    			.setActionRow(menu).queue();
	    	}
	    	else if (list.size() == 1) {
	    		switch(list.get(0)) {
	    		case "weekly":
	    			embedBuilder = LeaderboardEmbedBuilder.EmbedBuild("Weekly Leaderboard");
	    			break;
	    		case "monthly":
	    			embedBuilder = LeaderboardEmbedBuilder.EmbedBuild("Monthly Leaderboard");
	    			break;
	    		case "alltime":
	    			embedBuilder = LeaderboardEmbedBuilder.EmbedBuild("All-time Leaderboard");
	    			break;
	    		default:
	    			return true;
	    		}
	    		event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
	    		embedBuilder.clear();
	    	}
	    	else {
	    		return true;
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
        return "%c weekly/monthly/alltime \n" +
        		"(weekly/monthly/alltime) is an optional parameter. A select menu will be displayed if it is not specified.";
    }

    @Override
    protected String examplesMessage() {
        return "%c \n" +
                "Shows a select menu where you can select which leaderboard to display and then displays that leaderboard. \n" +
        		"%c alltime \n" +
        		"Shows the all-time leaderboard." ;
    }
    
    @Override
    public List<String> getAliases() {
    	return Collections.singletonList("toprep");
    }
}
