package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import ChatApp.android.Button.HomeWatcher;
import ChatApp.android.Services.FloatingWidgetService;
import ChatApp.android.GlobalStuff;

import ChatApp.android.R;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import ChatApp.android.Adapters.MessageAdapter;
import ChatApp.android.Model.Message;
import ChatApp.android.databinding.ActivityChatUserScreenBinding;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUserScreen extends AppCompatActivity {
    ActivityChatUserScreenBinding binding;

    MessageAdapter adapter;
    ArrayList<Message> messages;

    boolean isBack = false;

    String senderRoom, receiverRoom;
    CircleImageView imageView;
    FirebaseDatabase database;
    FirebaseStorage storage;

    ProgressDialog dialog;
    String senderUid;
    String receiverUid;
    String token;
    String profile;
    String name;

    public boolean recentAppClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatUserScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading image...");
        dialog.setCancelable(false);

        messages = new ArrayList<>();

        if (GlobalStuff.getIsBackground() == true) {
            final SharedPreferences sp = getSharedPreferences("sdata", MODE_PRIVATE);
            receiverUid = sp.getString("rUID",null);
            senderUid = sp.getString("sUID",null);
            name = sp.getString("name",null);
            profile = sp.getString("profile",null);
            token = sp.getString("token",null);
            GlobalStuff.setIsBackground(false);
        }
        else
        {
            receiverUid = getIntent().getStringExtra("uid");
            senderUid = FirebaseAuth.getInstance().getUid();
            name = getIntent().getStringExtra("name");
            profile = getIntent().getStringExtra("image");
            token = getIntent().getStringExtra("token");
        }

        //Toast.makeText(this, token, Toast.LENGTH_SHORT).show();

        binding.name.setText(name);
        Glide.with(ChatUserScreen.this).load(profile)
                .placeholder(R.drawable.avatar)
                .into(binding.profile);

        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        database.getReference().child("presence").child(receiverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    if(!status.isEmpty()) {
                        if(status.equals("Offline")) {
                            binding.status.setVisibility(View.GONE);
                        } else {
                            binding.status.setText(status);
                            binding.status.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        adapter = new MessageAdapter(this, messages, senderRoom, receiverRoom,binding.profile);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Message message = snapshot1.getValue(Message.class);
                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt = binding.messageBox.getText().toString();

                Date date = new Date();
                Message message = new Message(messageTxt, senderUid, date.getTime());
                binding.messageBox.setText("");

                String randomKey = database.getReference().push().getKey();

                HashMap<String, Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastMsg", message.getMessage());
                lastMsgObj.put("lastMsgTime", date.getTime());

                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .child("messages")
                                        .child(randomKey)
                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                /*sendNotification(name, message.getMessage(), token);*/
                                            }
                                        });
                            }
                        });

            }
        });

        binding.attachmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivity(intent);
            }
        });

        final Handler handler = new Handler();
        binding.messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                database.getReference().child("presence").child(senderUid).setValue("typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedTyping,1000);
            }

            Runnable userStoppedTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderUid).setValue("Online");
                }
            };
        });

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        GlobalStuff.setCurrentActivity(this);
        onHomeButton();
        createVideoCallRoomIfNotExist();
        checkCallInvitation();
//        getSupportActionBar().setTitle(name);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 25) {

            if(data != null) {
                if(data.getData() != null) {
                    Uri selectedImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                    dialog.show();
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if(task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filePath = uri.toString();

                                        String messageTxt = binding.messageBox.getText().toString();

                                        Date date = new Date();
                                        Message message = new Message(messageTxt, senderUid, date.getTime());
                                        message.setMessage("photo");
                                        message.setImageUrl(filePath);
                                        binding.messageBox.setText("");

                                        String randomKey = database.getReference().push().getKey();

                                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMsg", message.getMessage());
                                        lastMsgObj.put("lastMsgTime", date.getTime());

                                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                        database.getReference().child("chats")
                                                .child(senderRoom)
                                                .child("messages")
                                                .child(randomKey)
                                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        database.getReference().child("chats")
                                                                .child(receiverRoom)
                                                                .child("messages")
                                                                .child(randomKey)
                                                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                    }
                                                                });
                                                    }
                                                });

                                        //Toast.makeText(ChatActivity.this, filePath, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
        GlobalStuff.setIsBackground(false);
        stopService(new Intent(this, FloatingWidgetService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        menu.findItem(R.id.call).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(ChatUserScreen.this, VideoCallOut.class);
                intent.putExtra("callsender_room", senderRoom);
                intent.putExtra("callreceiver_room", receiverRoom);
                intent.putExtra("callreceiver_token", token);
                intent.putExtra("callreceiver_name", name);
                intent.putExtra("callreceiver_uid",receiverUid);
                startActivity(intent);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    //Check if someone video called
    private void checkCallInvitation()
    {
        FirebaseDatabase.getInstance().getReference("videochat").child(receiverRoom).child("res").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if (snapshot.getValue().toString().equals("true")) {
                        Intent intent = new Intent(ChatUserScreen.this, VideoCallIn.class);
                        intent.putExtra("callsender_room", receiverRoom);
                        intent.putExtra("callreceiver_room", senderRoom);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    protected void SaveData() {
        final SharedPreferences sp = getSharedPreferences("sdata", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("rUID", receiverUid);
        editor.putString("sUID", senderUid);
        editor.putString("name", name);
        editor.putString("profile", profile);
        editor.putString("token", token);
        GlobalStuff.setIsBackground(true);
        editor.commit();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        isBack = true;
    }

    private void onHomeButton()
    {
        recentAppClicked = false;
        HomeWatcher mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if(recentAppClicked == false) {
                    SaveData();
                    RetrieveData();
                    GlobalStuff.setIsBackground(true);
                    //finish();
                }
                recentAppClicked = false;
            }
            @Override
            public void onHomeLongPressed() {
                if(isMyServiceRunning(FloatingWidgetService.class)){
                    GlobalStuff.setIsBackground(false);
                    stopService(new Intent(ChatUserScreen.this, FloatingWidgetService.class));
                }
                recentAppClicked = true;
            }
        });
        mHomeWatcher.startWatch();
    }

    private void RetrieveData()
    {
        imageView = binding.profile;
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        Intent i = new Intent(ChatUserScreen.this, FloatingWidgetService.class);
        bitmap.compress(Bitmap.CompressFormat.PNG, 60, bs);
        i.putExtra(android.content.Intent.EXTRA_TEXT,senderRoom);
        i.putExtra("byteArray", bs.toByteArray());
        i.putExtra("sender_room", senderRoom);
        i.putExtra("receiver_uid", receiverUid);
        startService(i);

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void createVideoCallRoomIfNotExist()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("videochat");
        FirebaseDatabase.getInstance().getReference("videochat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(senderRoom).exists() && snapshot.child(receiverRoom).exists())
                {
                    Log.d("EXIST?:", "yes");
                }
                else {
                    String vroom;
                    String vuid;
                    for(int i = 0; i < 2;i++) {
                        if(i == 0)
                        {
                            vroom = senderRoom;
                            vuid = senderUid;
                        }
                        else
                        {
                            vroom = receiverRoom;
                            vuid = receiverUid;
                        }
                        reference.child(vroom).setValue("res");
                        reference.child(vroom).setValue("key");
                        reference.child(vroom).child("res").setValue("null");
                        reference.child(vroom).child("key").setValue(vuid);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}