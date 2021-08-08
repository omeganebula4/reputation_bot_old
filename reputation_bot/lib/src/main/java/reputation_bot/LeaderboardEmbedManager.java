package reputation_bot;

import com.mongodb.client.MongoCollection;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public class LeaderboardEmbedManager {

	private static MongoCollection<ReputationData> repCol;
	private static String leaderboardName;

	public LeaderboardEmbedManager(MongoCollection<ReputationData> repCol, String leaderboardName) {
		LeaderboardEmbedManager.repCol = repCol;
		LeaderboardEmbedManager.leaderboardName = leaderboardName;
	}

	private static int currentPage = 1;

	public void sendEmbed(MessageChannel channel) {
		LeaderboardEmbedBuilder embedBuilder = new LeaderboardEmbedBuilder();
		boolean leftDisable = true, rightDisable = true;

		if (embedBuilder.maxPages(repCol, embedBuilder.numPerPage) == 1) {
			leftDisable = true;
			rightDisable = true;
		} else if (embedBuilder.maxPages(repCol, embedBuilder.numPerPage) > 1) {
			leftDisable = true;
			rightDisable = false;

		}
		channel.sendMessageEmbeds(embedBuilder
				.EmbedBuild(channel.getJDA().getGuildById(Main.guildID), repCol, currentPage, leaderboardName).build())
				.setActionRow(Button.primary("left-toprep", Emoji.fromUnicode("U+2B05")).withDisabled(leftDisable),
						Button.primary("right-toprep", Emoji.fromUnicode("U+27A1")).withDisabled(rightDisable),
						Button.danger("delete-toprep", Emoji.fromUnicode("U+1F6AE")))
				.queue();
	}

	public static void editEmbedLeft(ButtonClickEvent event) {
		currentPage -= 1;
		editEmbedSend(event);
	}

	public static void editEmbedRight(ButtonClickEvent event) {
		currentPage += 1;
		editEmbedSend(event);
	}

	public static void editEmbedSend(ButtonClickEvent event) {
		LeaderboardEmbedBuilder embedBuilder = new LeaderboardEmbedBuilder();
		int maxPages = embedBuilder.maxPages(repCol, embedBuilder.numPerPage);
		boolean leftDisable = true, rightDisable = true;
		if (maxPages == 1) {
			leftDisable = true;
			rightDisable = true;
		} else if (maxPages > 1) {
			if (currentPage <= 1) {
				leftDisable = true;
				rightDisable = false;
			} else if (currentPage >= maxPages) {
				leftDisable = false;
				rightDisable = true;
			} else {
				leftDisable = false;
				rightDisable = false;
			}
		}
		event.editMessageEmbeds(embedBuilder.EmbedBuild(event.getGuild(), repCol, currentPage, leaderboardName).build())
				.setActionRow(Button.primary("left-toprep", Emoji.fromUnicode("U+2B05")).withDisabled(leftDisable),
						Button.primary("right-toprep", Emoji.fromUnicode("U+27A1")).withDisabled(rightDisable),
						Button.danger("delete-toprep", Emoji.fromUnicode("U+1F6AE")))
				.queue();
	}
}