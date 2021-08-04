package reputation_bot;

import org.bson.codecs.pojo.annotations.BsonIgnore;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class ReputationData {

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
    
    @BsonIgnore
    public long getRank(MongoCollection<ReputationData> rankCol) {
    	return (rankCol.countDocuments(Filters.and(Filters.eq("guildID", Main.guildID), Filters.gt("repAmount", this.repAmount))) + 1);
    }
}
