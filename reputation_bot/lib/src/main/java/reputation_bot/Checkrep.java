package reputation_bot;

import java.util.Collections;
import java.util.List;

import com.mongodb.client.model.Filters;

import lib.bot.cmd.AbstractCommand;
import lib.bot.cmd.annotation.Args;
import lib.bot.management.PermissionManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Checkrep extends AbstractCommand {

	private ReputationDAO reputationDAO;

	public Checkrep(ReputationDAO reputationDAO) {
		super("checkrep", "Displays the rank and rep information about the user specified or the author.");
		this.reputationDAO = reputationDAO;
	}

	@Args(min = 0, max = 1)

	@Override
	public boolean onCommand(MessageReceivedEvent event, String s, String rawArguments, List<String> list) {
		if (event.getGuild().getIdLong() == Main.guildID) {
			Member name = null;
			long repNumAlltime, alltimeRank, repNumWeekly, weeklyRank, repNumMonthly, monthlyRank;

			int replyInt = CommandDetectionUtil.ReplyDetectionForCheckrep(event, list, reputationDAO.alltimeCollection);
			switch (replyInt) {
			case 0:
				return true;
			case 1:
				name = event.getMessage().getMember();
				break;
			case 2:
				name = event.getMessage().getMentionedMembers().get(0);
				break;
			case 3:
				name = event.getMessage().getMentionedMembers().get(1);
				break;
			case 4:
				name = event.getGuild().getMemberById(Long.parseLong(list.get(0)));
				break;
			}

			if (name != null & !name.getUser().isBot()) {
				ReputationData nameAlltime = reputationDAO.alltimeCollection.find(
						Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)))
						.first();
				ReputationData nameMonthly = reputationDAO.monthlyCollection.find(
						Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)))
						.first();
				ReputationData nameWeekly = reputationDAO.weeklyCollection.find(
						Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)))
						.first();

				repNumAlltime = nameAlltime.getRepAmount();
				alltimeRank = nameAlltime.getRank(reputationDAO.alltimeCollection);
				repNumWeekly = nameWeekly.getRepAmount();
				repNumMonthly = nameMonthly.getRepAmount();
				monthlyRank = nameMonthly.getRank(reputationDAO.monthlyCollection);
				weeklyRank = nameWeekly.getRank(reputationDAO.weeklyCollection);

				EmbedBuilder authorRank = new EmbedBuilder();
				authorRank.setTitle(name.getEffectiveName() + "'s Reputation Information");
				authorRank.addField("All-time Leaderboard",
						"Rank: #" + Long.toString(alltimeRank) + "\n" + "Rep: " + Long.toString(repNumAlltime), false);
				authorRank.addField("Monthly Leaderboard",
						"Rank: #" + Long.toString(monthlyRank) + "\n" + "Rep: " + Long.toString(repNumMonthly), false);
				authorRank.addField("Weekly Leaderboard",
						"Rank: #" + Long.toString(weeklyRank) + "\n" + "Rep: " + Long.toString(repNumWeekly), false);
				authorRank.setColor(0x4f068b);
				event.getChannel().sendTyping().queue();
				event.getChannel().sendMessageEmbeds(authorRank.build()).queue();
				authorRank.clear();
			} else {
				return true;
			}
		} else {
			event.getChannel().sendTyping().queue();
			event.getChannel().sendMessage("ReputationBot is only available in one guild.").queue();
		}
		return false;
	}

	@Override
	protected boolean hasPermission(PermissionManager pm, MessageReceivedEvent messageContext, List<String> args) {
		return super.hasPermission(pm, messageContext, args);
		// return CommandDetectionUtil.hasPerms(messageContext.getMember(),
		// messageContext.getGuild());
	}

	@Override
	public List<String> getAliases() {
		return Collections.singletonList("rep");
	}

	@Override
	protected String usageMessage() {
		return "%c [@user]";
	}

	@Override
	protected String examplesMessage() {
		return "%c @User#1234 \n" + "Shows the reputation points and rank of User#1234 on all of the leaderboards.";
	}

}
