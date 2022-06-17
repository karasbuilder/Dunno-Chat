package ChatApp.android.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ChatApp.android.Activities.GroupChatScreen;
import ChatApp.android.Model.Group;
import ChatApp.android.Model.Message;
import ChatApp.android.R;
import ChatApp.android.databinding.ItemGroupBinding;

public class GroupChatAdapter extends  RecyclerView.Adapter<GroupChatAdapter.HolderGroupChat> {
    private Context context;
    private ArrayList<Group> listGroup;
    public GroupChatAdapter(Context context,ArrayList<Group> listGroup){
        this.listGroup=listGroup;
        this.context=context;
    }

    @NonNull
    @Override
    public HolderGroupChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_group,parent,false);


        return  new HolderGroupChat(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChat holder, int position) {
        Group group=listGroup.get(position);
        String groupID=group.getGroupID();
        String groupIcon=group.getGroupIcon();
        String groupTitle=group.getGroupName();


        Glide.with(context).load(groupIcon)
                .placeholder(R.drawable.avatar)
                .into(holder.binding.groupIcon);
        holder.binding.groupTitle.setText(groupTitle);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, GroupChatScreen.class);
                intent.putExtra("groupID",groupID);
                context.startActivity(intent);

            }
        });
        loadLastMessage(groupID,holder);
    }

    private void loadLastMessage(String groupID,HolderGroupChat holderGroupChat){
        //get the last message by group id by choose last item of message
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupID).child("Messages").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                           Message mess=ds.getValue(Message.class);
                           holderGroupChat.binding.messPaShow.setText(mess.getMessage());

                           //convert time stamp from firebase

                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                            long time=mess.getTimestamp();
                           holderGroupChat.binding.timePaShow.setText(dateFormat.format(new Date(time)));

                            ///get information of sender last id
                            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("users");
                            ref.orderByChild("uid").equalTo(mess.getSenderId())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds:snapshot.getChildren()){
                                                String name=""+ds.child("name").getValue();
                                                holderGroupChat.binding.namePaShow.setText(name);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    @Override
    public int getItemCount() {
        if(listGroup!=null){
            return listGroup.size();
        }
       return  0;
    }


    //Ui views
    class  HolderGroupChat extends RecyclerView.ViewHolder{
        private ItemGroupBinding binding;
        public HolderGroupChat(@NonNull View itemView) {
            super(itemView);
            binding=ItemGroupBinding.bind(itemView);
        }
    }
}
