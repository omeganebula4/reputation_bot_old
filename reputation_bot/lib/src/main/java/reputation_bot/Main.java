package reputation_bot;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import lib.bot.cmd.CommandHandler;
import lib.bot.management.PermissionManager;
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
	public static long guildID = 831532447631278146L;
	
	public static void main(String[] args) throws LoginException, IOException, InterruptedException {
		IBotConfig internalConfig = new VolatileBotConfig();
		InternalBotConfig internalBotConfig = new InternalBotConfig(internalConfig);
		PermissionManager permissionManager = new PermissionManager(internalBotConfig);
		CommandHandler commandManager = new CommandHandler(permissionManager, ".r", "ReputationBot");
		JDA jdaBuilder = JDABuilder.createDefault("ODMxNTI3NjI0NjA1Njk2MDcy.YHWicg.Z7byFNOYO3mehevrS829xfTgFGQ").enableIntents(GatewayIntent.GUILD_MEMBERS).setMemberCachePolicy(MemberCachePolicy.ALL).setActivity(Activity.watching("The Rep Economy Grow")).build();
		jdaBuilder.addEventListener(commandManager);
		jdaBuilder.addEventListener(new RepDetect());
		jdaBuilder.addEventListener(new DatabaseInit());
		commandManager.addCommandToRoot(new Addrep());
		commandManager.addCommandToRoot(new Remrep());
		commandManager.addCommandToRoot(new Setrep());
	}
}
