package ChatApp.android.Model;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private String senderID,receivedID,messageRequest;
    private  long timeSent;

    public Request(){

    }
    public Request(String senderID,String receivedID,String messageRequest,long timeSent){
        this.senderID=senderID;
        this.receivedID=receivedID;
        this.messageRequest=messageRequest;
        this.timeSent=timeSent;
    }
    public Map<String,Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("senderID",senderID);
        result.put("receivedID",receivedID);
        result.put("messageRequest",messageRequest);
        result.put("timeSent",timeSent);
        return  result;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceivedID() {
        return receivedID;
    }

    public void setReceivedID(String receivedID) {
        this.receivedID = receivedID;
    }

    public String getMessageRequest() {
        return messageRequest;
    }

    public void setMessageRequest(String messageRequest) {
        this.messageRequest = messageRequest;
    }

    public long getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(long timeSent) {
        this.timeSent = timeSent;
    }
}
