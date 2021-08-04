package reputation_bot;

import java.util.List;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandDetectionUtil {
	
    public static boolean hasPerms(Member member, Guild guild){
        if(member==null) return false;
        if(member.getIdLong()==791217248693780501L) return true;
        if(guild==null) return false;
        return member.hasPermission(Permission.ADMINISTRATOR);
    }
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}
	
	public static boolean isLong(String s) {
	    try {
	        Long.parseLong(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}
	
	private static boolean MemberExist(MongoCollection<ReputationData> collection, long memberID) {
    	long count = collection.countDocuments(Filters.and(Filters.eq("memberID", memberID), Filters.eq("guildID", Main.guildID)));
    	if (count >= 1) {return true;}
    	else {return false;}
    }
	
    public static int ReplyDetection(MessageReceivedEvent event, List<String> list, MongoCollection<ReputationData> alltimecoll) {
        int returnint = 0;
        if (!event.getMessage().getMentionedMembers().isEmpty()) {
            if (event.getMessage().getReferencedMessage() == null & event.getMessage().getMentionedMembers().size() == 1) {	//no reply, message mention
                returnint = 1;
            }
            else if (event.getMessage().getReferencedMessage() != null){
                if (event.getMessage().getMentionedMembers().size() == 1) { 	//reply without ping, message mention
                    returnint = 1;
                }
                else if (event.getMessage().getMentionedMembers().size() == 2) {	//reply with ping, message mention
                    returnint = 2;
                }
                else {
                    returnint = 0;
                }
            }
            else {
            	returnint = 0;
            }
        }
        else  if (isLong(list.get(0))) {
        	long memberID = Long.parseLong(list.get(0)); 
			if (MemberExist(alltimecoll, memberID)) {
				returnint = 3;
			}
			else {
				returnint = 0;
			}
        }
        else {
        	returnint = 0;
        }
        return returnint;
    }
    
    public static int RepDetection(Member member, MessageReceivedEvent event, List<String> list) {
    	int returnint = 0;
    	if (!member.getUser().isBot() & !member.equals(null)) {
    		if (list.size() == 2) {
    			if (list.get(1).equals("weekly")) {
    				returnint = 1; //weekly, rep = 1
    			}
    			else if (list.get(1).equals("monthly")) {
    				returnint = 2; //monthly, rep = 1
    			}
    			else if (list.get(1).equals("alltime")) {
    				returnint = 3; //alltime, rep = 1
    			}
    			else {
    				returnint = 0;
    			}
    		}
    		
    		else if (list.size() == 3) {
    			String rep = list.get(2);
    			if (isInteger(rep)) {
    				if (Integer.parseInt(rep) > 0) {
    					if (list.get(1).equals("weekly")) {
            				returnint = 4; //weekly, rep = list.get(2)
            			}
            			else if (list.get(1).equals("monthly")) {
            				returnint = 5; //monthly, rep = list.get(2)
            			}
            			else if (list.get(1).equals("alltime")) {
            				returnint = 6; //alltime, rep = list.get(2)
            			}
            			else {
            				returnint = 0;
            			}
    				}
    				else {
    					returnint = 0;
    				}
    			}
    			else if (!isInteger(rep)) {
    				returnint = 0;
    			}
    		}
    		
    		else {
    			returnint = 0;
    		}
    	}
		return returnint;
    }
    
    public static int RepDetectionForSetrep(Member member, MessageReceivedEvent event, List<String> list) {
    	int returnint = 0;
    	if (!member.getUser().isBot() & !member.equals(null)) {
    		if (list.size() == 3) {
    			String rep = list.get(2);
    			if (isInteger(rep)) {
    				if (list.get(1).equals("weekly")) {
           				returnint = 1; //weekly, rep = list.get(2)
           			}
           			else if (list.get(1).equals("monthly")) {
           				returnint = 2; //monthly, rep = list.get(2)
           			}
            		else if (list.get(1).equals("alltime")) {
            			returnint = 3; //alltime, rep = list.get(2)
            		}
            		else {
            			returnint = 0;
            		}
    			}
    			else if (!isInteger(rep)) {
    				returnint = 0;
    			}
    		}
    		
    		else {
    			returnint = 0;
    		}
    	}
		return returnint;
    }
    
    public static int ReplyDetectionForCheckrep(MessageReceivedEvent event, List<String> list, MongoCollection<ReputationData> alltimecoll) {
    	int returnint = 0;
    	if (list.size() == 0) {
    		returnint = 1; //check for author
    	}
    	else if (list.size() == 1) {
    		if (!event.getMessage().getMentionedMembers().isEmpty()) {
                if (event.getMessage().getReferencedMessage() == null & event.getMessage().getMentionedMembers().size() == 1) {	//no reply, message mention
                    returnint = 2;
                }
                else if (event.getMessage().getReferencedMessage() != null){
                    if (event.getMessage().getMentionedMembers().size() == 1) { 	//reply without ping, message mention
                        returnint = 2;
                    }
                    else if (event.getMessage().getMentionedMembers().size() == 2) {	//reply with ping, message mention
                        returnint = 3;
                    }
                    else {
                        returnint = 0;
                    }
                }
                else {
                	returnint = 0;
                }
            }
            else  if (isLong(list.get(0))) {
            	long memberID = Long.parseLong(list.get(0)); 
				if (MemberExist(alltimecoll, memberID)) {
					returnint = 4;
				}
				else {
					returnint = 0;
				}
            }
            else {
            	returnint = 0;
            }
    	}
        return returnint;
    }
}