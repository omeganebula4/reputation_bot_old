package reputation_bot;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

public class LeaderboardEmbedBuilder {
	int numPerPage = 10;

	public EmbedBuilder EmbedBuild(Guild guild, MongoCollection<ReputationData> repCol, int currentPage,
			String leaderboardName) {
		EmbedBuilder leaderboard = new EmbedBuilder();
		leaderboard.setTitle(leaderboardName);

		if (currentPage < 1 || currentPage > maxPages(repCol, numPerPage)) {
			leaderboard.addField("How did you get here?", "You're not supposed to be here. Get out.", false);
			leaderboard.setColor(0x4f068b);
		} else {
			String str = "```" + String.format("%6s %8s  %-48s %n", "Rank", "Rep", "Name");
			for (ReputationData dataObj : getReputationDetails(repCol, currentPage, numPerPage)) {
				str += String.format("%6s %8d  %-48s %n", "#" + Long.toString(dataObj.getRank(repCol)),
						dataObj.getRepAmount(), guild.getMemberById(dataObj.getMemberID()).getEffectiveName());
			}
			str += "```";

			leaderboard.addField("", str, false);
			leaderboard.setColor(0x4f068b);
			leaderboard.setFooter(
					"Page " + Integer.toString(currentPage) + " of " + Integer.toString(maxPages(repCol, numPerPage)));
		}
		return leaderboard;
	}

	public int maxPages(MongoCollection<ReputationData> leaderboardCol, int numPerPage) {
		double numOfDocs = (double) leaderboardCol.countDocuments();
		return (int) Math.ceil(((numOfDocs) / (numPerPage)));
	}

	public List<ReputationData> getReputationDetails(MongoCollection<ReputationData> leaderboardCol, int currentPage,
			int numPerPage) {
		return leaderboardCol.find().sort(Sorts.descending("repAmount")).limit(numPerPage)
				.skip((currentPage - 1) * numPerPage).into(new ArrayList<ReputationData>());
	}
}
