package ChatApp.android.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String uid, name, phoneNumber, profileImage,token,passwordUser,coverImage,addressUser;
    private String gender;
    private String email;


    public User() {

    }


    public User(String uid, String name, String phoneNumber,String email,String passwordUser ,String profileImage,String coverImage,String addressUser,String gender) {
        this.uid = uid;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email=email;
        this.coverImage=coverImage;
        this.profileImage = profileImage;
        this.passwordUser=passwordUser;
        this.addressUser=addressUser;
        this.gender=gender;
    }
    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid",uid);
        result.put("name",name);
        result.put("phoneNumber",phoneNumber);
        result.put("email",email);
        result.put("coverImage",coverImage);
        result.put("profileImage",profileImage);
        result.put("passwordUser",passwordUser);
        result.put("addressUser",addressUser);
        result.put("gender",gender);

        return result;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPasswordUser() {
        return passwordUser;
    }

    public void setPasswordUser(String passwordUser) {
        this.passwordUser = passwordUser;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddressUser() {
        return addressUser;
    }

    public void setAddressUser(String addressUser) {
        this.addressUser = addressUser;
    }

}
