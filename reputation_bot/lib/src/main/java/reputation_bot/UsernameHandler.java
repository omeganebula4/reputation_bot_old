package reputation_bot;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.user.GenericUserEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class UsernameHandler extends ListenerAdapter {

    private final MongoCollection<UserData> userDataMongoCollection;

    //cache to only access the database on real username change events
    private final Map<Long, String> internalCache = new HashMap<>();

    /**
     * 0 - 15:34 - 414
     * 3 - 15:37 - 448
     * 6 - 15:40 - 486
     * 9 - 15:43 - 527
     *
     *
     * @param database
     */
    public UsernameHandler(MongoDatabase database) {
        this.userDataMongoCollection = database.getCollection("usernameCache", UserData.class);
    }
    String getUser(long userId){
        if(internalCache.containsKey(userId)) return internalCache.get(userId);
        UserData data = getDataForUser(userId);
        if(data==null) return null;
        return data.getFullName();
    }

    private UserData getDataForUser(long id){
        for (UserData ud : userDataMongoCollection.find(eq("_id", id))) {
            return ud;
        }
        return null;
    }

    private void updateInternalCache(User u){
        internalCache.put(u.getIdLong(), u.getName()+"#"+u.getDiscriminator());
    }
    private void updateDatabase(User u){
        userDataMongoCollection.insertOne(new UserData(u.getIdLong(), u.getName(), u.getDiscriminator()));
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        addUserToCache(event.getUser());

    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        //remove user from cache/database
        //even if user is in other server of bot
        //the user will be reregistered on any later update call
        internalCache.remove(event.getUser().getIdLong());
        userDataMongoCollection.deleteOne(eq("_id", event.getUser().getIdLong()));
    }

    @Override
    public void onGenericUser(@NotNull GenericUserEvent event) {
        addUserToCache(event.getUser());
    }

    void addUserToCache(User user){
        System.out.println("update user " + user);
        User u = user;


        //check if user is in cache
        if(!internalCache.containsKey(u.getIdLong())){
            //not in cache
            //this means the cache should be updated
            updateInternalCache(u);

            System.out.println("user not in cache " + u);

            //is the username in the database?
            //or
            //username is in database
            //check if username differs
            UserData data = getDataForUser(u.getIdLong());
            if(data==null || !data.equalName(u)){
                //user not in database nor in cache
                //add to both
                updateDatabase(u);
                System.out.println("user not in database or name change " + u);
                return;
            }
            //username now in cache and in database if not there or the name changed
            System.out.println("user in database and no name change " + u);
            return;
        }

        //user is in cache
        //check if cache name differs from current name
        String name = internalCache.get(u.getIdLong());

        System.out.println("user in cache " + u);

        //same name as in cache -> done nothing to do
        if(name.equalsIgnoreCase(u.getName() + "#" + u.getDiscriminator())) return;

        //cache name differs
        //update cache and database (the db might have the correct username but this point
        //will be reached rarely so an extra check is useless)
        updateInternalCache(u);
        updateDatabase(u);

        System.out.println("a name change. cache size " + internalCache.size());

    }
}
