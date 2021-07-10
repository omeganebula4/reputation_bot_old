package reputation_bot;

import net.dv8tion.jda.api.entities.User;

public class UsernameCache {
    private final UsernameHandler usernameHandler;

    public UsernameCache(UsernameHandler usernameHandler) {
        this.usernameHandler = usernameHandler;
    }

    public String searchForUserById(long userId){
        return usernameHandler.getUser(userId);
    }

    public void manuallyAddUserToCache(User user){
        usernameHandler.addUserToCache(user);
    }
}
