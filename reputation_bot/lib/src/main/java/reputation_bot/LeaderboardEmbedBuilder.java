package reputation_bot;

import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

public class LeaderboardEmbedBuilder{
	int numPerPage = 10;
	
	public EmbedBuilder EmbedBuild(Guild guild, MongoCollection<ReputationData> repCol, int currentPage, String leaderboardName) {
		EmbedBuilder leaderboard = new EmbedBuilder();
		leaderboard.setTitle(leaderboardName);
		
		String str = "```" + String.format("%-40s %9s %8s %n %n", "Name", "Rep", "Rank");
		for (ReputationData dataObj : CommandDetectionUtil.getReputationDetails(repCol, currentPage, numPerPage)) {
			str += String.format("%-40s %9d %8s %n", guild.getMemberById(dataObj.getMemberID()).getEffectiveName(), dataObj.getRepAmount(), "#" + Long.toString(dataObj.getRank(repCol)));
		}
		str += "```";
		
		leaderboard.addField("", str, false);
		leaderboard.setColor(0x4f068b);
		leaderboard.setFooter("Page " + Integer.toString(currentPage) + " of " + Integer.toString(CommandDetectionUtil.maxPages(repCol, numPerPage)));
		return leaderboard;
	}
}
