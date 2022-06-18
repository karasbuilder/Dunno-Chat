package ChatApp.android.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import ChatApp.android.Model.User;
import ChatApp.android.databinding.ItemParticipantAddBinding;
import ChatApp.android.R;

public class ParticipantsAddAdapter extends RecyclerView.Adapter<ParticipantsAddAdapter.HolderParticipantsAdd> {


    private Context context;
    private ArrayList<User> userArrayList;
    private String groupID,myGroupRole;
    public ParticipantsAddAdapter(Context context, ArrayList<User> userArrayList,String groupID,String myGroupRole) {
        this.context = context;
        this.userArrayList = userArrayList;
        this.groupID=groupID;
        this.myGroupRole=myGroupRole;
    }


    @NonNull
    @Override
    public HolderParticipantsAdd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_participant_add,parent,false);
        return new HolderParticipantsAdd(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderParticipantsAdd holder, int position) {
        //get data
        User user=userArrayList.get(position);
        String name=user.getName();
        String email=user.getEmail();
        String image=user.getProfileImage();
        String uid=user.getUid();
        //set data to ui
        holder.binding.nameParticipants.setText(name);
        holder.binding.emailParticipants.setText(email);
        Glide.with(context.getApplicationContext()).load(image)
                .placeholder(R.drawable.avatar)
                .into(holder.binding.imageUserParticipant);



        checkAlreadyInGroup(user,holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
                ref.child(groupID).child("Participants").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String hisPerviousRole=""+snapshot.child("role").getValue();
                            String [] options;
                            AlertDialog.Builder builder=new AlertDialog.Builder(context);
                            builder.setTitle("Choose Options");
                            if (myGroupRole.equals("creator")){
                                if(hisPerviousRole.equals("admin")){
                                    options=new String[]{"Remove Admin","Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which==0){
                                                removeAdmin(user);

                                            }else{
                                                removeParticipant(user);

                                            }
                                        }
                                    }).show();
                                }
                                else if(hisPerviousRole.equals("participant")){
                                    options=new String[]{"Make Admin","Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which ==0){
                                                makeAdminUser(user);

                                            }
                                            else {
                                                removeParticipant(user);
                                            }
                                        }
                                    }).show();


                                }

                            }
                            else if(myGroupRole.equals("admin")){
                                if(hisPerviousRole.equals("creator")){
                                    Toast.makeText(context, "Creator of this Group", Toast.LENGTH_SHORT).show();
                                }
                                else if (hisPerviousRole.equals("admin")){
                                    options=new String[]{"Remove Admin","Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which==0){
                                                removeAdmin(user);

                                            }else{
                                                removeParticipant(user);

                                            }
                                        }
                                    }).show();

                                }
                                else if(hisPerviousRole.equals("participant")){
                                    options=new String[]{"Make Admin","Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which ==0){
                                                makeAdminUser(user);

                                            }
                                            else {
                                                removeParticipant(user);
                                            }
                                        }
                                    }).show();
                                }


                            }


                        }
                        else  {
                            //when user not exist
                            AlertDialog.Builder builder=new AlertDialog.Builder(context);
                            builder.setTitle("Add Participant")
                                    .setMessage("Add this user in this group !")
                                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            addParticipantUser(user);

                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                        }
                                    }).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    private void addParticipantUser(User user) {
        String timeStamp=""+System.currentTimeMillis();
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("uid",user.getUid());
        hashMap.put("role","participant");
        hashMap.put("timeStamp",timeStamp);
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupID).child("Participants").child(user.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Add Participant Success", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Add Participant Failure", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void makeAdminUser(User user) {
        //setup admin

        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("role","admin");

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupID).child("Participants").child(user.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "This User is now Admin", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void removeAdmin(User user) {
        //after remove admin role it will be participant user
        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("role","participant");

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupID).child("Participants").child(user.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "This User is now Participant", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void   removeParticipant(User user){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupID).child("Participants").child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Remove Successful User", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkAlreadyInGroup(User user,HolderParticipantsAdd holder){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupID).child("Participants")
                .child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String hisRole=""+snapshot.child("role").getValue();
                            holder.binding.statusParticipants.setText(hisRole);

                        }
                        else{
                            holder.binding.statusParticipants.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class HolderParticipantsAdd extends RecyclerView.ViewHolder{
        private ItemParticipantAddBinding binding;
        public HolderParticipantsAdd(@NonNull View itemView) {
            super(itemView);
            binding=ItemParticipantAddBinding.bind(itemView);
        }
    }
}
