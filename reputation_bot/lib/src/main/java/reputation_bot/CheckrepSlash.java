package reputation_bot;

import com.mongodb.client.model.Filters;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class CheckrepSlash extends ListenerAdapter {
	private ReputationDAO reputationDAO;

	public CheckrepSlash(ReputationDAO reputationDAO) {
		this.reputationDAO = reputationDAO;
	}

	@Override
	public void onReady(ReadyEvent event) {
		Guild guild = event.getJDA().getGuildById(Main.guildID);
		guild.upsertCommand("checkrep", "Shows the reputation details of member specified/author.")
				.addOption(OptionType.USER, "member", "Mention the member whose reputation details need to be shown.",
						false)
				.queue();
	}

	@Override
	public void onSlashCommand(SlashCommandEvent event) {
		Member member = null;
		if (event.getName().equals("checkrep")) {
			if (event.getGuild().getIdLong() == Main.guildID) {
				if (event.getOption("member") != null) {
					member = event.getOption("member").getAsMember();
				} else {
					member = event.getMember();
				}

				long repNumAlltime, alltimeRank, repNumWeekly, weeklyRank, repNumMonthly, monthlyRank;
				if (member != null & !member.getUser().isBot()) {
					ReputationData nameAlltime = reputationDAO.alltimeCollection.find(Filters
							.and(Filters.eq("memberID", member.getIdLong()), Filters.eq("guildID", Main.guildID)))
							.first();
					ReputationData nameMonthly = reputationDAO.monthlyCollection.find(Filters
							.and(Filters.eq("memberID", member.getIdLong()), Filters.eq("guildID", Main.guildID)))
							.first();
					ReputationData nameWeekly = reputationDAO.weeklyCollection.find(Filters
							.and(Filters.eq("memberID", member.getIdLong()), Filters.eq("guildID", Main.guildID)))
							.first();

					repNumAlltime = nameAlltime.getRepAmount();
					alltimeRank = nameAlltime.getRank(reputationDAO.alltimeCollection);
					repNumWeekly = nameWeekly.getRepAmount();
					repNumMonthly = nameMonthly.getRepAmount();
					monthlyRank = nameMonthly.getRank(reputationDAO.monthlyCollection);
					weeklyRank = nameWeekly.getRank(reputationDAO.weeklyCollection);

					EmbedBuilder authorRank = new EmbedBuilder();
					authorRank.setTitle(member.getEffectiveName() + "'s Reputation Information");
					authorRank.addField("All-time Leaderboard",
							"Rank: #" + Long.toString(alltimeRank) + "\n" + "Rep: " + Long.toString(repNumAlltime),
							false);
					authorRank.addField("Monthly Leaderboard",
							"Rank: #" + Long.toString(monthlyRank) + "\n" + "Rep: " + Long.toString(repNumMonthly),
							false);
					authorRank.addField("Weekly Leaderboard",
							"Rank: #" + Long.toString(weeklyRank) + "\n" + "Rep: " + Long.toString(repNumWeekly),
							false);
					authorRank.setColor(0x4f068b);
					event.replyEmbeds(authorRank.build()).setEphemeral(true).queue();
					authorRank.clear();
				} else {
					event.reply("Invalid member chosen.").setEphemeral(true).queue();
				}
			} else {
				event.reply("ReputationBot is only available in one guild.").setEphemeral(true).queue();
			}
		}
	}
}
