package ChatApp.android.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import ChatApp.android.R;
import ChatApp.android.Model.Message;
import ChatApp.android.databinding.RecivedChatComponentBinding;
import ChatApp.android.databinding.SentChatComponentBinding;

public class MessageAdapter  extends RecyclerView.Adapter {
    Context context;
    ArrayList<Message> messages;
    final  int USER_SENT=1;
    final  int USER_RECEIVE=2;
    String senderRoom;
    String receiverRoom;

    FirebaseRemoteConfig remoteConfig;
    public MessageAdapter(Context context, ArrayList<Message> messages, String senderRoom, String receiverRoom) {
        remoteConfig = FirebaseRemoteConfig.getInstance();
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == USER_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.sent_chat_component, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.recived_chat_component, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message=messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return USER_SENT;
        }else{
            return USER_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
    public class SentViewHolder extends RecyclerView.ViewHolder {

        SentChatComponentBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SentChatComponentBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        RecivedChatComponentBinding binding;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RecivedChatComponentBinding.bind(itemView);
        }
    }

}
