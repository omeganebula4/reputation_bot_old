package reputation_bot;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RepDetect extends ListenerAdapter {

	private ReputationDAO reputationDAO;

	public RepDetect(ReputationDAO reputationDAO) {
		this.reputationDAO = reputationDAO;
	}

	ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	volatile List<Long> prison = new ArrayList<Long>();

	private static final Pattern urlPattern = Pattern.compile(
			"(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)" + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
					+ "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

	private static final Pattern thanksPattern = Pattern.compile(
			"\\W*([Tt]hn?x+|THN?X+|[tT]y+|TY+|[tT]hanks+|THANKS+|[tT]hanx+|THANX+|[tT]hank+|THANK+|[tT]ysm+|TYSM+|[tT]yvm+|TYVM+|(ty){1,6}|Ty(ty){0,5}|(TY){1,6})(\\W*)(<@![0-9]{18}>)*(\\W*)");

	private static List<String> getLinks(String str) {
		List<String> returnList = new ArrayList<String>();
		String[] parts = str.split("\\s+");
		for (String item : parts) {
			if (urlPattern.matcher(item).matches()) {
				returnList.add(item);
			}
		}
		return returnList;
	}

	public static boolean hasThanks(String str) {
		String[] parts = str.split("\\s+");
		for (String item : parts) {
			if (thanksPattern.matcher(item).matches()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (!event.getAuthor().isBot()) {
			if (event.getGuild().getIdLong() == Main.guildID) {
				String message = event.getMessage().getContentRaw();
				if (!getLinks(message).isEmpty()) {
					for (String item : getLinks(message)) {
						System.out.println(message.contains(item));
						message = message.replace(item, "");
					}
				}

				List<Member> memberList = event.getMessage().getMentionedMembers();
				LinkedHashSet<Member> refinedMemberSet = new LinkedHashSet<Member>(memberList);
				List<Member> refinedMemberList = new ArrayList<Member>(refinedMemberSet);

				List<Member> removalList = new ArrayList<Member>();
				if (hasThanks(message)) {
					if (!refinedMemberList.isEmpty()) {
						if (CommandDetectionUtil.selfRep(refinedMemberList, event.getMember())) {
							removalList.add(event.getMember());
							event.getChannel().sendTyping().queue();
							event.getChannel()
									.sendMessage("Beep Boop: Rep abuse detected. Don't try to give yourself rep... :rage:")
									.queue();
						}

						if (!CommandDetectionUtil.botList(refinedMemberList).isEmpty()) {
							removalList.addAll(CommandDetectionUtil.botList(refinedMemberList));
						}
					}

					if (!removalList.isEmpty()) {
						refinedMemberList.removeAll(removalList);
					}

					if (!refinedMemberList.isEmpty()) {

						String names = " ";

						if (prison.contains(event.getAuthor().getIdLong())) {

							event.getChannel().sendTyping().queue();
							event.getChannel().sendMessage("You're still on cooldown.").queue();

						}

						else {

							for (Member member : refinedMemberList) {
								reputationDAO.alltimeCollection
										.updateOne(
												Filters.and(Filters.eq("memberID", member.getIdLong()),
														Filters.eq("guildID", Main.guildID)),
												Updates.inc("repAmount", 1));
								reputationDAO.monthlyCollection
										.updateOne(
												Filters.and(Filters.eq("memberID", member.getIdLong()),
														Filters.eq("guildID", Main.guildID)),
												Updates.inc("repAmount", 1));
								reputationDAO.weeklyCollection
										.updateOne(
												Filters.and(Filters.eq("memberID", member.getIdLong()),
														Filters.eq("guildID", Main.guildID)),
												Updates.inc("repAmount", 1));
								names = names + "<@" + member.getId() + "> ";
							}

							event.getChannel().sendTyping().queue();
							event.getChannel().sendMessage("Added 1 rep to" + names).allowedMentions(new ArrayList<>())
									.queue();

							prison.add(event.getAuthor().getIdLong());
							scheduler.schedule(() -> {
								prison.remove(event.getAuthor().getIdLong());
							}, 20, TimeUnit.SECONDS);

						}

					}

				}

			}

		}
	}
}
