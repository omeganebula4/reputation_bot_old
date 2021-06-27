package reputation_bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.mongodb.BasicDBObject;

import lib.bot.management.ExpiringReactionMenuHandler;
import lib.bot.management.ReactionManager;
import lib.bot.management.prompt.EmbedMenu;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class ReputationLeaderboardMenu extends EmbedMenu{
	private int page;
    private int mode;
    private int perPage = 5;
    private final ReputationDAO reputationDAO;
    private final UsernameCache cache;

    public ReputationLeaderboardMenu(ReactionManager reactionManager, ExpiringReactionMenuHandler handler, Member owner, TextChannel where, ReputationDAO reputationDAO, UsernameCache cache) {
        super(reactionManager, handler, owner, where, System.currentTimeMillis()+ TimeUnit.MINUTES.toMillis(2));
        this.reputationDAO = reputationDAO;
        this.cache = cache;
            create();

            System.out.println("creating");
            addButton("\u2B05\uFE0F", s -> navigatePage(s,-1));
            addButton("\u27A1\uFE0F", s -> navigatePage(s,1));

            addButton("\ud83c\uddfc", s -> switchMode(s,0));
            addButton("\ud83c\uddf2", s -> switchMode(s,1));
            addButton("\ud83c\udde6", s -> switchMode(s,2));

    }

    private void navigatePage(String s, int factor){
        page+=factor;
        update();
    }

    private void switchMode(String s, int val){
        if(mode==val) return;
        mode = val;
        update();
    }
    public EmbedBuilder getCurrentEmbed(){
        EmbedBuilder eb = new EmbedBuilder();
        String title;
        eb.setTitle("Leaderboard Page " + page);

        String basic = page + " + " + page + " = " + (page+page);
        List<SetObj> modeVal;
        switch (mode){
            case 0:
                title = "Weekly";
                modeVal = DatabaseInit.weeklyCollection.find().sort(new BasicDBObject("repAmount",-1)).skip((page-1)*perPage).limit(perPage).into(new ArrayList<SetObj>());//get the leaderboard data for the current page here
                break;
            case 1:
                title = "Monthly";
                modeVal = DatabaseInit.monthlyCollection.find().sort(new BasicDBObject("repAmount",-1)).skip((page-1)*perPage).limit(perPage).into(new ArrayList<SetObj>());
                break;
            default:
                title = "All time";
                modeVal = DatabaseInit.alltimeCollection.find().sort(new BasicDBObject("repAmount",-1)).skip((page-1)*perPage).limit(perPage).into(new ArrayList<SetObj>());
                break;
        }
        title+=" Leaderboard";
        eb.setTitle(title);

        if(modeVal.isEmpty()) return eb;
        final String pointsStr = " Points";
        int rankWidth = (modeVal.size()>1) ? getRankWidth(modeVal.get(0).getRank(), modeVal.get(1).getRank()) : (modeVal.get(0).getRank()+"").length();
        int pointsWidth = (modeVal.size()>1) ? getRankWidth(modeVal.get(0).getRepAmount(), modeVal.get(1).getRepAmount()) : (modeVal.get(0).getRepAmount()+"").length();
        if(pointsWidth<pointsStr.length()) pointsWidth = pointsStr.length();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(3x `)
                .append("#".repeat(rankWidth+2))
        .append(pointsStr)
        .append(" - User").append("\n");
        for (SetObj reputationData : modeVal) {
            stringBuilder.append(
                    formatEntry(
                            reputationData,
                            rankWidth,
                            pointsWidth,
                            getFormattedUsers(modeVal.stream().map(SetObj::getMemberID).collect(Collectors.toList()))
                    )
            ).append("\n");
        }
        stringBuilder.append("```");

        eb.addField(" ", stringBuilder.toString(), false);

        return eb;
    }
    private int getRankWidth(Long minRank, Long maxRank){
        int minl = minRank.toString().length();
        int maxl = maxRank.toString().length();
        return Math.max(minl,maxl);
    }
    private Map<Long, String> getFormattedUsers(List<Long> ids){
        Map<Long, String> found = new HashMap<>();

        for (Long id : ids) {
            String name = cache.searchForUserById(id);
            if(name!=null) {
                found.put(id, name);
                continue;
            }
            found.put(id, id.toString());
        }

        return found;
    }
    private String formatEntry(SetObj data, int rankWidth, int pointsWidth, Map<Long, String> idToFormat){
        return formatNumber(data.getRank(), rankWidth) + formatRight(data.getRepAmount(), pointsWidth) + " - " + idToFormat.get(data.getMemberID());
    }
    private static String formatNumber(Long rank, int rankWidth){
        int l = rank.toString().length();
        int addedZeros = rankWidth-l;
        String num = null;
        if(addedZeros>0){
            if(rank<0){
                num = String.format("%0" + (addedZeros-1) + "d", rank);
            }else{
                num = String.format("%0" + (addedZeros) + "d", rank);
            }
        }else{
            num = rank.toString();
        }
        return "#" + num + ":";
    }

    private static String formatRight(Object val, int width){
        String s = val.toString();
        if(s.length()>width){
            s = s.substring(0,width);
        }
        int leftGap = width-s.length();
        return " ".repeat(leftGap) + s;
    }
}
