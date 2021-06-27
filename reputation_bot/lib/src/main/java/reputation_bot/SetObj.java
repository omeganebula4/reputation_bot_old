package reputation_bot;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class SetObj {

    private long memberID;
    private long guildID;
    private long repAmount;
    private String name;

    public long getMemberID() {
        return memberID;
    }

    public void setMemberID(long memberID) {
        this.memberID = memberID;
    }

    public long getGuildID() {
        return guildID;
    }

    public void setGuildID(long guildID) {
        this.guildID = guildID;
    }

    public long getRepAmount() {
        return repAmount;
    }

    public void setRepAmount(long repAmount) {
        this.repAmount = repAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public long getRank(MongoCollection<SetObj> repCol) {
    	long monthlyRank = repCol.countDocuments(Filters.and(Filters.eq("guildID", Main.guildID), Filters.gt("repAmount", this.getRepAmount()))) + 1;
    	return monthlyRank;
    }
}
