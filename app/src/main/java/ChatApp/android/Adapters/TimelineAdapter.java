package ChatApp.android.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ChatApp.android.Model.Post;
import ChatApp.android.R;
import ChatApp.android.databinding.RowConversationChatComponentBinding;
import ChatApp.android.databinding.TimelinePostComponentBinding;
import de.hdodenhof.circleimageview.CircleImageView;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder>{

    Context context;
    ArrayList<Post> posts;
    boolean check = true;

    public TimelineAdapter(Context context, ArrayList<Post> posts)
    {
        this.context = context;
        this.posts = posts;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileimage;
        TextView name;
        TextView timestamp;
        TextView content;
        TextView likes;
        ImageView likeicon;
        AppCompatButton likebutton;
        AppCompatButton commentbutton;
        TimelinePostComponentBinding binding;

        public ViewHolder(@NonNull View itemview)
        {
            super(itemview);
            profileimage = itemview.findViewById(R.id.post_profileimage);
            name = itemview.findViewById(R.id.post_name);
            likes = itemview.findViewById(R.id.post_likenum);
            timestamp = itemview.findViewById(R.id.post_timestamp);
            content = itemview.findViewById(R.id.post_content);
            likeicon = itemview.findViewById(R.id.post_likeicon);
            likebutton = itemview.findViewById(R.id.post_likebutton);
            commentbutton = itemview.findViewById(R.id.post_commentbutton);
            binding = TimelinePostComponentBinding.bind(itemView);
        }
    }


    @NonNull
    @Override
    public TimelineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.timeline_post_component, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");

        String current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String uid = post.getUid();

        FirebaseDatabase.getInstance().getReference("users").child(post.getUid()).child("profileImage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profile_image = null;
                if(snapshot.exists()) {
                    profile_image = snapshot.getValue().toString();
                }
                Glide.with(context.getApplicationContext()).load(profile_image)
                        .placeholder(R.drawable.avatar)
                        .into(holder.profileimage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Load the image using Glide

        long time=post.getTimestamp();
        holder.timestamp.setText(dateFormat.format(new Date(time)));

        int num = post.getLikesAmount();
        if(num == 0)
        {
            holder.likes.setVisibility(View.GONE);
        }
        else {
            holder.likes.setVisibility(View.VISIBLE);
            holder.likes.setText("Likes: " +num);
        }

        if(post.isLiked())
        {
            holder.likeicon.setImageResource(R.drawable.like_ic);
            holder.likebutton.setTextColor(Color.parseColor("#00A8C1"));
        }
        else
        {
            holder.likeicon.setImageResource(R.drawable.notlike_ic);
            holder.likebutton.setTextColor(Color.parseColor("#000000"));
        }

        FirebaseDatabase.getInstance().getReference("users").child(uid).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.name.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.content.setText(post.getContent());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("timeline").child(post.getUid()).child(post.getPostid());
        holder.likebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = true;
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(check && snapshot.child("likes").child(current_uid).exists())
                            {
                                ref.child("likes").child(current_uid).removeValue();
                                check = false;
                            }
                            else if(check)
                            {
                                ref.child("likes").child(current_uid).setValue("");
                                check = false;
                                //Log.d("status", "liked");

                            }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
