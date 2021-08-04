package reputation_bot;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import lib.bot.cmd.CommandHandler;
import lib.bot.management.ExpiringReactionMenuHandler;
import lib.bot.management.PermissionManager;
import lib.bot.management.ReactionManager;
import lib.bot.persistence.IBotConfig;
import lib.bot.persistence.InternalBotConfig;
import lib.bot.persistence.VolatileBotConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;


public class Main {
	
	public static JDA jda;
	public static long guildID = 725268887238606871L;
	
	public static void main(String[] args) throws LoginException, IOException, InterruptedException {
		ReputationDAO reputationDAO = new ReputationDAO();
		reputationDAO.MainInit();
		
		UsernameHandler usernameHandler = new UsernameHandler(ReputationDAO.test);
		@SuppressWarnings("unused")
		UsernameCache usernameCache = new UsernameCache(usernameHandler);
		
		IBotConfig internalConfig = new VolatileBotConfig();
		InternalBotConfig internalBotConfig = new InternalBotConfig(internalConfig);
		PermissionManager permissionManager = new PermissionManager(internalBotConfig);
		
		ReactionManager reactionManager = new ReactionManager();
		ExpiringReactionMenuHandler expiringReactionMenuHandler = new ExpiringReactionMenuHandler(reactionManager);
		CommandHandler commandManager = new CommandHandler(permissionManager, ".r", "ReputationBot", reactionManager, expiringReactionMenuHandler);
		
		JDA jdaBuilder = JDABuilder.createDefault("ODMxNTI3NjI0NjA1Njk2MDcy.YHWicg.Z7byFNOYO3mehevrS829xfTgFGQ").enableIntents(GatewayIntent.GUILD_MEMBERS).setMemberCachePolicy(MemberCachePolicy.ALL).setActivity(Activity.watching("The Rep Economy Grow")).build();
		jdaBuilder.addEventListener(commandManager);
		jdaBuilder.addEventListener(new RepDetect(reputationDAO));
		jdaBuilder.addEventListener(new MemberHandler(reputationDAO));
		jdaBuilder.addEventListener(new SelectMenuLeaderboard());
		commandManager.addCommandToRoot(new Addrep(reputationDAO));
		commandManager.addCommandToRoot(new Remrep(reputationDAO));
		commandManager.addCommandToRoot(new Setrep(reputationDAO));
		commandManager.addCommandToRoot(new Checkrep(reputationDAO));
		commandManager.addCommandToRoot(new LeaderboardCmd(reputationDAO));
	}
}
