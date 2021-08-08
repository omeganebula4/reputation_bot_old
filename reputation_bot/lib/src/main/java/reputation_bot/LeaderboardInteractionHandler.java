package reputation_bot;

import com.mongodb.client.MongoCollection;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LeaderboardInteractionHandler extends ListenerAdapter {

	private ReputationDAO reputationDAO;

	public LeaderboardInteractionHandler(ReputationDAO reputationDAO) {
		this.reputationDAO = reputationDAO;
	}

	@Override
	public void onSelectionMenu(SelectionMenuEvent event) {
		if (event.getComponentId().equals("leaderboard-selection")) {
			if (LeaderboardCmd.author != null && LeaderboardCmd.author == event.getMember()) {
				MongoCollection<ReputationData> repCol = null;
				String leaderboardName = null;

				event.deferEdit().queue();
				event.getMessage().delete().queue();
				event.getChannel().sendMessage("You selected " + event.getValues().get(0)).queue();
				switch (event.getValues().get(0)) {
				case "weekly":
					repCol = reputationDAO.weeklyCollection;
					leaderboardName = "Weekly Leaderboard";
					break;
				case "monthly":
					repCol = reputationDAO.monthlyCollection;
					leaderboardName = "Monthly Leaderboard";
					break;
				case "alltime":
					repCol = reputationDAO.alltimeCollection;
					leaderboardName = "All-time Leaderboard";
					break;
				}

				LeaderboardEmbedManager embedManager = new LeaderboardEmbedManager(repCol, leaderboardName);
				embedManager.sendEmbed(event.getChannel());
			}
		}
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		if (LeaderboardCmd.author != null && LeaderboardCmd.author == event.getMember()) {
			if (event.getComponentId().equals("left-toprep")) {
				LeaderboardEmbedManager.editEmbedLeft(event);
			} else if (event.getComponentId().equals("right-toprep")) {
				LeaderboardEmbedManager.editEmbedRight(event);
			} else if (event.getComponentId().equals("delete-toprep")) {
				event.deferEdit().queue();
				event.getMessage().delete().queue();
			}
		}
	}
}
