package ChatApp.android.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ChatApp.android.Activities.GroupChatScreen;
import ChatApp.android.Model.Message;
import ChatApp.android.R;
import ChatApp.android.databinding.RecivedChatComponentBinding;
import ChatApp.android.databinding.SentChatComponentBinding;
import ChatApp.android.databinding.SentGroupchatComponentBinding;
import ChatApp.android.databinding.ReceviedGroupchatComponentBinding;

public class GroupMessageAdapter extends RecyclerView.Adapter {
    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;



    Context context;
    ArrayList<Message> messages;


    public GroupMessageAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.sent_groupchat_component, parent, false);
            return new GroupMessageAdapter.SentViewGroupHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.recevied_groupchat_component, parent, false);
            return new GroupMessageAdapter.ReceiverViewGroupHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        //setting prepare format datetime and convert from time stamp to date

        int reactions[] = new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();


        long time=message.getTimestamp();
        if (holder.getClass() == SentViewGroupHolder.class) {
            SentViewGroupHolder viewGroupHolder = (SentViewGroupHolder) holder;

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            ref.orderByChild("uid").equalTo(message.getSenderId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String name=""+dataSnapshot.child("name").getValue();
                        viewGroupHolder.binding.nameTv.setText(name);
                        viewGroupHolder.binding.messageTv.setText(message.getMessage());
                        viewGroupHolder.binding.timeStamp.setText(dateFormat.format(new Date(time)));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        else{
           ReceiverViewGroupHolder viewGroupHolder = (ReceiverViewGroupHolder)  holder;

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            ref.orderByChild("uid").equalTo(message.getSenderId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String name=""+dataSnapshot.child("name").getValue();
                        viewGroupHolder.binding.nameTv.setText(name);
                        viewGroupHolder.binding.messageTv.setText(message.getMessage());
                        viewGroupHolder.binding.timeStamp.setText(dateFormat.format(new Date(time)));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }



    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public class SentViewGroupHolder extends RecyclerView.ViewHolder {

       SentGroupchatComponentBinding  binding;
        public SentViewGroupHolder(@NonNull View itemView) {
            super(itemView);
            binding = SentGroupchatComponentBinding.bind(itemView);
        }
    }

    public class ReceiverViewGroupHolder extends RecyclerView.ViewHolder {

        private  ReceviedGroupchatComponentBinding binding;

        public ReceiverViewGroupHolder(@NonNull View itemView) {
            super(itemView);
            binding=ReceviedGroupchatComponentBinding.bind(itemView);

        }
    }
}
