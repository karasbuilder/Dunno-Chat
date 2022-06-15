package ChatApp.android.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ChatApp.android.Activities.ChatUserScreen;
import ChatApp.android.Activities.ProfileContactUser;
import ChatApp.android.Model.User;
import ChatApp.android.R;
import ChatApp.android.databinding.ItemStatusBinding;
import ChatApp.android.databinding.ItemUserBinding;
import ChatApp.android.databinding.RowConversationChatComponentBinding;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    Context context;
    ArrayList<User> users;



    public FriendsAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);

        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        User user = users.get(position);
        String visitorID = user.getUid();

       holder.binding.namUserContact.setText(user.getName());
       holder.binding.addressUserContact.setText(user.getAddressUser());

        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.imageUserContact);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileContactUser.class);
               intent.putExtra("visitID",visitorID);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {

            return users.size();

    }
    public class FriendViewHolder extends RecyclerView.ViewHolder{

      ItemStatusBinding binding;
        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
           binding=ItemStatusBinding.bind(itemView);
        }
    }

}
