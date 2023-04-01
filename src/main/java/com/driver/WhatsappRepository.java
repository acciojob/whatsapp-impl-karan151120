package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;

    private HashMap<Message, List<User>> messageListHashMap;
    private HashMap<Group, User> adminMap;
    private HashMap<String, User> user;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.user = new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) {
        if(user.containsKey(mobile)) return "";
        else {
            user.put(mobile,new User(name, mobile));
        }
        return "SUCCESS";
    }

    public Group creategroup(List<User> users) {
        Group group;
        if(users.size() == 2) {
            group = new Group(users.get(1).getName(),2);
            groupUserMap.put(group, users);

            adminMap.put(group, users.get(0));
        }

        else {
            customGroupCount++;
            String name = "Group" + " " + customGroupCount;
            group = new Group(name,users.size());

            groupUserMap.put(group, users);

            adminMap.put(group,users.get(0));
        }
        return group;
    }

    public int createMessage(String content) {
        this.messageId++;

        Message message = new Message(this.messageId, content);
        messageListHashMap.put(message, new ArrayList<>());

        return this.messageId;
    }

    public int sendMessage(Message message, User sender, Group group) {
        if(!groupUserMap.containsKey(group)) return -1;

        List<User> users = groupUserMap.get(group);

        boolean exist = false;
        for(User user1 : users) {
            if(user1.equals(sender)) {
                exist = true;
                break;
            }
        }

        if(!exist) return -2;

        if(groupMessageMap.containsKey(group)) {
            List<Message> l = groupMessageMap.get(group);
            l.add(message);
            groupMessageMap.put(group, l);

            senderMap.put(message,sender);
        }
        else {
            List<Message> l = new ArrayList<>();
            l.add(message);
            groupMessageMap.put(group, l);

            senderMap.put(message,sender);
        }

        return groupMessageMap.get(group).size();
    }

    public String changeAdmin(User approver, User user, Group group) {
        if (!groupUserMap.containsKey(group)) return "1";
        else if (!adminMap.get(group).getName().equals(approver.getName())) {
            return "2";
        }

        List<User> users = groupUserMap.get(group);
        int exist = 0;
        for(int i=0; i<users.size(); i++) {
            if(users.get(i).equals(user)) {
                exist = i;
                break;
            }
        }

        if(exist == -1) return "3";

        adminMap.put(group, users.get(exist));
        return "SUCCESS";
    }
}
