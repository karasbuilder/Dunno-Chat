package ChatApp.android.Model;

import java.util.ArrayList;

public class Group {



    private String groupID;
    private String groupName;
    private String groupDescription;
    private String adminID;
    private String groupIcon;
    private long createAt;


    public Group(){

    }
    public Group(String groupID,String groupName,String groupDescription,String adminID,long createAt,String icon){
        this.groupID=groupID;
        this.groupName=groupName;
        this.groupDescription=groupDescription;
        this.groupIcon=icon;
        this.adminID=adminID;
        this.createAt=createAt;
        

    }
    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }


    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }



    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }


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








}
