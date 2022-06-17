package ChatApp.android.Adapters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ChatApp.android.Activities.ConfirmPhoneOTP;
import ChatApp.android.Model.Request;
import ChatApp.android.Model.User;
import ChatApp.android.R;
import ChatApp.android.databinding.ItemUserBinding;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    Context context;
    ArrayList<User> users;
    ArrayList<Request> requests;

    private DatabaseReference   chatRequestRef, chatSentRef,useRef;

    String currentID;
    public RequestAdapter (Context context,  ArrayList<Request> requests) {
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.item_user,parent,false);

        currentID=FirebaseAuth.getInstance().getCurrentUser().getUid();
        useRef=FirebaseDatabase.getInstance().getReference().child("users");

        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend Requests");
        chatSentRef = FirebaseDatabase.getInstance().getReference().child("Friend Received");
       return new RequestViewHolder(view);
    }
    public void addFriendsSuccess(String newFriends){


               useRef.child(currentID)
                .child("friends").setValue(newFriends)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Add Friends Sucess", Toast.LENGTH_SHORT).show();
                            removeRequest(newFriends,currentID);
                        }
                    }
                });

    }
    public void removeRequest(String senderUserId,String receiverUserId){

        chatRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            chatSentRef.child(receiverUserId).child(senderUserId)
                                    .removeValue();
                        }
                    }
                });

    }
    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
       Request request=requests.get(position);

       /*FirebaseDatabase.getInstance().getReference()
               .child("users")
               .child(request.getSenderID()).addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       User user=snapshot.getValue(User.class);
                       String visitorID=user.getUid();
                       holder.binding.displayName.setText(user.getName());
                       Glide.with(context).load(user.getProfileImage())
                               .placeholder(R.drawable.avatar)
                               .into(holder.binding.imageView2);
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });*/

        String senderID=request.getSenderID();
        Toast.makeText(context, "Send =>"+senderID, Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(senderID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);

                        holder.binding.displayName.setText(user.getName());



                        Glide.with(context).load(user.getProfileImage())
                                .placeholder(R.drawable.avatar)
                                .into(holder.binding.imageView2);

                        holder.binding.acceptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //base on current uid => add new child friends => add new ID
                                addFriendsSuccess(senderID);

                            }
                        });

                        holder.binding.declineBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                              removeRequest(senderID,currentID);

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    @Override
    public int getItemCount() {
       if(requests!=null){
           return  requests.size();
        }
        return 0;
    }

    public  class RequestViewHolder extends RecyclerView.ViewHolder{
        ItemUserBinding binding;
        public RequestViewHolder(@NonNull View itemView){
            super(itemView);
            binding=ItemUserBinding.bind(itemView);

        }
    }
}
