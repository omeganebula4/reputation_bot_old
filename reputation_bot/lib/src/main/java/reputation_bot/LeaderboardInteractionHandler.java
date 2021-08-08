package reputation_bot;

import com.mongodb.client.MongoCollection;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class LeaderboardInteractionHandler extends ListenerAdapter {

	private ReputationDAO reputationDAO;

	public LeaderboardInteractionHandler(ReputationDAO reputationDAO) {
		this.reputationDAO = reputationDAO;
	}

	@Override
	public void onReady(ReadyEvent event) {
		Guild guild = event.getJDA().getGuildById(Main.guildID);
		OptionData option = new OptionData(OptionType.STRING, "leaderboard-type", "Which leaderboard to display.", true)
				.addChoice("All-time", "alltime").addChoice("Monthly", "monthly").addChoice("Weekly", "weekly");
		guild.upsertCommand("show-leaderboard", "Shows the leaderboard specified.").addOptions(option).queue();
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
	public void onSlashCommand(SlashCommandEvent event) {
		if (event.getName().equals("show-leaderboard")) {
			if (event.getGuild().getIdLong() == Main.guildID) {
				if (event.getOption("leaderboard-type") != null) {
					MongoCollection<ReputationData> repCol = null;
					String leaderboardName = null;
					switch (event.getOption("leaderboard-type").getAsString()) {
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
					LeaderboardEmbedManagerSlash embedManager = new LeaderboardEmbedManagerSlash(repCol,
							leaderboardName);
					embedManager.sendEmbed(event);
				}
			} else {
				event.reply("ReputationBot is only available in one guild.").setEphemeral(true).queue();
			}
		}
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		if (LeaderboardCmd.author != null) {
			if (LeaderboardCmd.author == event.getMember()) {
				if (event.getComponentId().equals("left-toprep")) {
					LeaderboardEmbedManager.editEmbedLeft(event);
				} else if (event.getComponentId().equals("right-toprep")) {
					LeaderboardEmbedManager.editEmbedRight(event);
				}

				if (CommandDetectionUtil.hasPerms(event.getMember(), event.getGuild())
						|| LeaderboardCmd.author == event.getMember()) {
					if (event.getComponentId().equals("delete-toprep")) {
						event.deferEdit().queue();
						event.getMessage().delete().queue();
					}
				}
			}
		}

		if (event.getComponentId().equals("left-toprep-slash")) {
			LeaderboardEmbedManagerSlash.editEmbedLeft(event);
		} else if (event.getComponentId().equals("right-toprep-slash")) {
			LeaderboardEmbedManagerSlash.editEmbedRight(event);
		}
	}
}