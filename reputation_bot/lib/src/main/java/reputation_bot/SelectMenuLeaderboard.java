package reputation_bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SelectMenuLeaderboard extends ListenerAdapter {
    @Override
    public void onSelectionMenu(SelectionMenuEvent event) {
    	EmbedBuilder embedBuilder = null;
    	if (event.getComponentId().equals("leaderboard-selection")) {
    		event.deferEdit().queue();
    		event.getMessage().delete().queue();
    		event.getChannel().sendMessage("You selected " + event.getValues().get(0)).queue();
    		switch(event.getValues().get(0)) {
    		case "weekly":
    			embedBuilder = LeaderboardEmbedBuilder.EmbedBuild("Weekly Leaderboard");
    			break;
    		case "monthly":
    			embedBuilder = LeaderboardEmbedBuilder.EmbedBuild("Monthly Leaderboard");
    			break;
    		case "alltime":
    			embedBuilder = LeaderboardEmbedBuilder.EmbedBuild("All-time Leaderboard");
    			break;
    		}
    		event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();;
    		embedBuilder.clear();
    	}
    }
}
