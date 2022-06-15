package ChatApp.android.Model;

import java.util.ArrayList;

public class Group {



    private String groupID,groupName,adminID,adminName;
    private  long createAt;
    private ArrayList<User> members;

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }



    public ArrayList<User> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<User> members) {
        this.members = members;
    }



}
