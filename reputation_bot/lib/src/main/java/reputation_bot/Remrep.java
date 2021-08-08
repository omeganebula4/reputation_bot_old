package reputation_bot;

import java.util.List;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import lib.bot.cmd.AbstractCommand;
import lib.bot.cmd.annotation.Args;
import lib.bot.management.PermissionManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Remrep extends AbstractCommand {
	private ReputationDAO reputationDAO;

	public Remrep(ReputationDAO reputationDAO) {
		super("remrep", "Removes specified number of reps from the specified user in the specified database.");
		this.reputationDAO = reputationDAO;
	}

	@Args(min = 2, max = 3)

	@Override
	public boolean onCommand(MessageReceivedEvent event, String s, String rawArguments, List<String> list) {
		if (event.getGuild().getIdLong() == Main.guildID) {
			Member name = null;
			int replyInt = CommandDetectionUtil.ReplyDetection(event, list, reputationDAO.alltimeCollection);
			switch (replyInt) {
			case 0:
				return true;
			case 1:
				name = event.getMessage().getMentionedMembers().get(0);
				break;
			case 2:
				name = event.getMessage().getMentionedMembers().get(1);
				break;
			case 3:
				name = event.getGuild().getMemberById(Long.parseLong(list.get(0)));
				break;
			}

			int repInt = CommandDetectionUtil.RepDetection(name, event, list);
			switch (repInt) {
			case 0:
				return true;
			case 1:
				reputationDAO.weeklyCollection.updateOne(
						Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)),
						Updates.inc("repAmount", -1));
				event.getChannel().sendTyping().queue();
				event.getChannel().sendMessage("Removed 1 rep from <@" + name.getId() + "> (Weekly collection)")
						.queue();
				break;
			case 2:
				reputationDAO.monthlyCollection.updateOne(
						Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)),
						Updates.inc("repAmount", -1));
				event.getChannel().sendTyping().queue();
				event.getChannel().sendMessage("Removed 1 rep from <@" + name.getId() + "> (Monthly collection)")
						.queue();
				break;
			case 3:
				reputationDAO.alltimeCollection.updateOne(
						Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)),
						Updates.inc("repAmount", -1));
				event.getChannel().sendTyping().queue();
				event.getChannel().sendMessage("Removed 1 rep from <@" + name.getId() + "> (Alltime collection)")
						.queue();
				break;
			case 4:
				reputationDAO.weeklyCollection.updateOne(
						Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)),
						Updates.inc("repAmount", -1));
				reputationDAO.monthlyCollection.updateOne(
						Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)),
						Updates.inc("repAmount", -1));
				reputationDAO.alltimeCollection.updateOne(
						Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)),
						Updates.inc("repAmount", -1));
				event.getChannel().sendTyping().queue();
				event.getChannel().sendMessage("Removed 1 rep from <@" + name.getId() + "> (All collections)").queue();
				break;
			case 5:
				reputationDAO.weeklyCollection.updateOne(
						Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)),
						Updates.inc("repAmount", -Integer.parseInt(list.get(2))));
				event.getChannel().sendTyping().queue();
				event.getChannel()
						.sendMessage("Removed " + list.get(2) + " rep from <@" + name.getId() + "> (Weekly collection)")
						.queue();
				break;
			case 6:
				reputationDAO.monthlyCollection.updateOne(
						Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)),
						Updates.inc("repAmount", -Integer.parseInt(list.get(2))));
				event.getChannel().sendTyping().queue();
				event.getChannel()
						.sendMessage(
								"Removed " + list.get(2) + " rep from <@" + name.getId() + "> (Monthly collection)")
						.queue();
				break;
			case 7:
				reputationDAO.alltimeCollection.updateOne(
						Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)),
						Updates.inc("repAmount", -Integer.parseInt(list.get(2))));
				event.getChannel().sendTyping().queue();
				event.getChannel()
						.sendMessage(
								"Removed " + list.get(2) + " rep from <@" + name.getId() + "> (Alltime collection)")
						.queue();
				break;
			case 8:
				reputationDAO.weeklyCollection.updateOne(
						Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)),
						Updates.inc("repAmount", -Integer.parseInt(list.get(2))));
				reputationDAO.monthlyCollection.updateOne(
						Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)),
						Updates.inc("repAmount", -Integer.parseInt(list.get(2))));
				reputationDAO.alltimeCollection.updateOne(
						Filters.and(Filters.eq("memberID", name.getIdLong()), Filters.eq("guildID", Main.guildID)),
						Updates.inc("repAmount", -Integer.parseInt(list.get(2))));
				event.getChannel().sendTyping().queue();
				event.getChannel()
						.sendMessage("Removed " + list.get(2) + " rep from <@" + name.getId() + "> (All collections)")
						.queue();
				break;
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
	protected String usageMessage() {
		return "%c [@user] weekly/monthly/alltime/all (number of rep) \n"
				+ "(number of rep) is an optional parameter. If not specified, default is 1.";
	}

	@Override
	protected String examplesMessage() {
		return "%c @User#1234 alltime \n" + "Removes 1 reputation point from the all-time balance of User#1234."
				+ "%c @User#1234 all 3 \n"
				+ "Removes 3 reputation points from the all balances of User#1234 in every leaderboard.";
	}
}
