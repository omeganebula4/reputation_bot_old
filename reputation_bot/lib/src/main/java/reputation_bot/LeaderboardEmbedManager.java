package reputation_bot;

import com.mongodb.client.MongoCollection;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public class LeaderboardEmbedManager {
	
	private static MongoCollection<ReputationData> repCol;
	private static String leaderboardName;
	
	public LeaderboardEmbedManager (MongoCollection<ReputationData> repCol, String leaderboardName) {
		LeaderboardEmbedManager.repCol = repCol;
		LeaderboardEmbedManager.leaderboardName = leaderboardName;
	}
	
	private static int currentPage = 1;
	
	public void sendEmbed(MessageChannel channel) {
		LeaderboardEmbedBuilder embedBuilder = new LeaderboardEmbedBuilder();
		if (CommandDetectionUtil.maxPages(repCol, embedBuilder.numPerPage) == 1) {
			channel.sendMessageEmbeds(embedBuilder.EmbedBuild(channel.getJDA().getGuildById(Main.guildID), repCol, currentPage, leaderboardName).build())
				.setActionRow(Button.primary("left", Emoji.fromUnicode("U+2B05")).asDisabled(), Button.primary("right", Emoji.fromUnicode("U+27A1")).asDisabled())
					.queue();
		}
		else if (CommandDetectionUtil.maxPages(repCol, embedBuilder.numPerPage) > 1) {
			channel.sendMessageEmbeds(embedBuilder.EmbedBuild(channel.getJDA().getGuildById(Main.guildID), repCol, currentPage, leaderboardName).build())
				.setActionRow(Button.primary("left", Emoji.fromUnicode("U+2B05")).asDisabled(), Button.primary("right", Emoji.fromUnicode("U+27A1")))
					.queue();
		}
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
		int caseNum = CommandDetectionUtil.embedPageDetection(repCol, embedBuilder.numPerPage, currentPage);
		switch(caseNum) {
		case 0:
			event.editMessageEmbeds(embedBuilder.EmbedBuild(event.getGuild(), repCol, currentPage, leaderboardName).build())
			.setActionRow(Button.primary("left", Emoji.fromUnicode("U+2B05")).asDisabled(), Button.primary("right", Emoji.fromUnicode("U+27A1")).asDisabled())
				.queue();
		case 1:
			event.editMessageEmbeds(embedBuilder.EmbedBuild(event.getGuild(), repCol, currentPage, leaderboardName).build())
			.setActionRow(Button.primary("left", Emoji.fromUnicode("U+2B05")).asDisabled(), Button.primary("right", Emoji.fromUnicode("U+27A1")))
				.queue();
		case 2:
			event.editMessageEmbeds(embedBuilder.EmbedBuild(event.getGuild(), repCol, currentPage, leaderboardName).build())
			.setActionRow(Button.primary("left", Emoji.fromUnicode("U+2B05")), Button.primary("right", Emoji.fromUnicode("U+27A1")).asDisabled())
				.queue();
		case 3:
			event.editMessageEmbeds(embedBuilder.EmbedBuild(event.getGuild(), repCol, currentPage, leaderboardName).build())
			.setActionRow(Button.primary("left", Emoji.fromUnicode("U+2B05")), Button.primary("right", Emoji.fromUnicode("U+27A1")))
				.queue();
		}
	}
}