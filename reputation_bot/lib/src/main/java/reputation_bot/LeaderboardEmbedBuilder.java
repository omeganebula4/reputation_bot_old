package reputation_bot;

import net.dv8tion.jda.api.EmbedBuilder;

public class LeaderboardEmbedBuilder {
	public static EmbedBuilder EmbedBuild(String LeaderboardName) {
		EmbedBuilder leaderboard = new EmbedBuilder();
		leaderboard.setTitle(LeaderboardName);
		leaderboard.addField("a", "b", false);
		leaderboard.setColor(0x4f068b);
		return leaderboard;
	}
}
