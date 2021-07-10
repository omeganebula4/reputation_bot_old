package reputation_bot;

import org.bson.codecs.pojo.annotations.BsonId;

import net.dv8tion.jda.api.entities.User;

public class UserData {
	@BsonId
    private long userId;
    private String username;
    private String discriminator;

    public UserData() {
    }

    public UserData(long userId, String username, String discriminator) {
        this.userId = userId;
        this.username = username;
        this.discriminator = discriminator;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }
    public String getFullName(){
        return username + "#" + discriminator;
    }
    public boolean equalName(User u){
        return username.equalsIgnoreCase(u.getName()) && discriminator.equalsIgnoreCase(u.getDiscriminator());
    }
}
