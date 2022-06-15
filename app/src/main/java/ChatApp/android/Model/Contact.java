package ChatApp.android.Model;

public class Contact {
    public String name,profileImage,addressUser;
    public Contact(){

    }
    public Contact(String name,String profileImage,String addressUser){
        this.name=name;
        this.profileImage=profileImage;
        this.addressUser=addressUser;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getAddressUser() {
        return addressUser;
    }

    public void setAddressUser(String addressUser) {
        this.addressUser = addressUser;
    }



}
