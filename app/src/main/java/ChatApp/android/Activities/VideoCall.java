package ChatApp.android.Activities;

import static android.content.ContentValues.TAG;
import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ChatApp.android.Adapters.MessageAdapter;
import ChatApp.android.Constant;
import ChatApp.android.GridVideoViewContainer;
import ChatApp.android.Model.MessageBean;
import ChatApp.android.Model.User;
import ChatApp.android.Model.UserStatusData;
import ChatApp.android.Model.VUser;
import ChatApp.android.R;
import ChatApp.android.RecyclerItemClickListener;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.Constants;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.RtmStatusCode;

public class VideoCall extends AppCompatActivity {

    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
    private static final int LAYOUT_TYPE_DEFAULT = 0;
    private final HashMap<Integer, SurfaceView> mUidsList = new HashMap<>();
    private ImageView mCallBtn, mMuteBtn, mSwitchCameraBtn;
    private GridVideoViewContainer mGridVideoViewContainer;
    private boolean isCalling = true;
    private boolean isMuted = false;
    private boolean mIsLandscape = false;
    private boolean isAddingFriend = false;
    private boolean isShowingFriend = false;
    private boolean isLocalCall = true;
    public int mLayoutType = LAYOUT_TYPE_DEFAULT;
    String sendcallUser, sendcallUser_token;
    String receivecallUSer, receivecallUser_token;
//    boolean isVideo = true;
//    boolean isAudio = true;
//    ImageView acceptCallBtn;
//    ImageView endCallBtn;
//    ImageView toggleAudioBtn;
//    ImageView toggleVideoBtn;
    private WebView webView;
    VUser user;
    String appID = "b21cfdd91e834a3ebc8d7a365318840f";
    private RtcEngine mRtcEngine;
    private String mPeerId = "";
    private RecyclerView mRecyclerView;
    private MessageAdapter mMessageAdapter;
    private String localState = Constant.USER_STATE_OPEN;
    private List<String> DBFriend = new ArrayList<>();
    private TextView mTitleTextView;
    private String channelName;
    private RtmClient mRtmClient;
    private RtmClientListener mClientListener;

    //private RtmClient mRtmClient;
    //private RtmClientListener mClientListener;
    private EditText mMsgEditText;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mRef;
    private ChildEventListener childEventListener;
    private ChildEventListener joinFriendChildEventListener;
    private ChildEventListener chatSearchChildEventListener;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.hide();
        }

        getExtras();
        initUI();

        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
            initEngineAndJoinChannel();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQ_ID: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    break;
                }
                initEngineAndJoinChannel();
                break;
            }
        }
    }

    private void getExtras() {
        sendcallUser = getIntent().getExtras().getString("sendcallUser");
        channelName = sendcallUser;
        user = new VUser();
    }

    private void initUI() {
        mCallBtn = findViewById(R.id.start_call_end_call_btn);
        mMuteBtn = findViewById(R.id.audio_mute_audio_unmute_btn);
        mSwitchCameraBtn = findViewById(R.id.switch_camera_btn);
        mGridVideoViewContainer = findViewById(R.id.grid_video_view_container);

        mGridVideoViewContainer.setItemEventHandler(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //can add single click listener logic
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //can add long click listener logic
            }

            @Override
            public void onItemDoubleClick(View view, int position) {
                //onBigVideoViewDoubleClicked(view, position);
            }

        });
    }

    private void joinChannel() {
        // Join a channel with a token, token can be null.
        mRtcEngine.joinChannel(null, channelName, "Extra Optional Data", 0);
    }

    private final io.agora.rtc2.IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the onJoinChannelSuccess callback.
        // This callback occurs when the local user successfully joins the channel.
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("User: " + uid + " join!");
                    user.setAgoraUid(uid);
                    SurfaceView localView = mUidsList.remove(0);
                    mUidsList.put(uid, localView);
                    //mRef.child(getUserName()).setValue(new User(getUserName(), user.getUid(), localState, DBFriend));
                }
            });
        }
        @Override
        // Listen for the onFirstRemoteVideoDecoded callback.
        // This callback occurs when the first video frame of a remote user is received and decoded after the remote user successfully joins the channel.
        // You can call the setupRemoteVideo method in this callback to set up the remote video view.
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        // Listen for the onUserOffline callback.
        // This callback occurs when the remote user leaves the channel or drops offline.
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("User: " + uid + " left the room.");
                    onRemoteUserLeft(uid);
                }
            });
        }
    };

    private void initEngineAndJoinChannel() {
        initializeEngine();
        setupLocalVideo();
        joinChannel();
    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), appID, mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

        private void setupLocalVideo() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRtcEngine.enableVideo();
                    mRtcEngine.enableInEarMonitoring(true);
                    mRtcEngine.setInEarMonitoringVolume(80);

                    SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
                    mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
                    surfaceView.setZOrderOnTop(false);
                    surfaceView.setZOrderMediaOverlay(false);

                    mUidsList.put(0, surfaceView);

                    mGridVideoViewContainer.initViewContainer(VideoCall.this, 0, mUidsList, mIsLandscape);
                }
            });
        }

        private boolean checkSelfPermission(String permission, int requestCode) {
            if (ContextCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
                return false;
            }
            return true;
        }

        private void onBigVideoViewDoubleClicked(View view, int position) {
            if (mUidsList.size() < 2) {
                return;
            }

            final UserStatusData user = mGridVideoViewContainer.getItem(position);

            if (user.mUid != this.user.getAgoraUid()) {

                chatSearchChildEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        User result = dataSnapshot.getValue(User.class);
                        //mRef.orderByChild("uid").startAt(user.mUid).endAt(user.mUid + "\uf8ff").removeEventListener(chatSearchChildEventListener);

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };

                mRef.orderByChild("uid").startAt(user.mUid).endAt(user.mUid + "\uf8ff").addChildEventListener(chatSearchChildEventListener);
            }
        }


        private void onRemoteUserLeft(int uid) {
            removeRemoteVideo(uid);
        }

        private void removeRemoteVideo(final int uid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Object target = mUidsList.remove(uid);
                    if (target == null) {
                        return;
                    }
                    switchToDefaultVideoView();
                }
            });

        }

        private void setupRemoteVideo(final int uid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SurfaceView mRemoteView = RtcEngine.CreateRendererView(getApplicationContext());

                    mUidsList.put(uid, mRemoteView);
                    mRemoteView.setZOrderOnTop(true);
                    mRemoteView.setZOrderMediaOverlay(true);
                    mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));

                    switchToDefaultVideoView();
                }
            });
        }

        private void switchToDefaultVideoView() {

            mGridVideoViewContainer.initViewContainer(VideoCall.this, (user.getAgoraUid()), mUidsList, mIsLandscape);

            boolean setRemoteUserPriorityFlag = false;

            mLayoutType = LAYOUT_TYPE_DEFAULT;

            int sizeLimit = mUidsList.size();
            if (sizeLimit > 5) {
                sizeLimit = 5;
            }

            for (int i = 0; i < sizeLimit; i++) {
                int uid = mGridVideoViewContainer.getItem(i).mUid;
                if (user.getAgoraUid() != uid) {
                    if (!setRemoteUserPriorityFlag) {
                        setRemoteUserPriorityFlag = true;
                        mRtcEngine.setRemoteUserPriority(uid, Constants.USER_PRIORITY_HIGH);
                    } else {
                        mRtcEngine.setRemoteUserPriority(uid, Constants.USER_PRIORITY_NORANL);
                    }
                }
            }
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (isCalling) {
                leaveChannel();
            }
            RtcEngine.destroy();
        }

        private void leaveChannel() {
            // Leave the current channel.
            mRtcEngine.leaveChannel();
        }

        private void finishCalling() {
            leaveChannel();
            mUidsList.clear();
        }

        private void startCalling() {
            setupLocalVideo();
            joinChannel();
        }

        private String getUserName() {
            return this.sendcallUser;
        }

        public void onSwitchCameraClicked(View view) {
            mRtcEngine.switchCamera();
        }

        public void onLocalAudioMuteClicked(View view) {
            isMuted = !isMuted;
            mRtcEngine.muteLocalAudioStream(isMuted);
            int res = isMuted ? R.drawable.btn_mute : R.drawable.btn_unmute;
            mMuteBtn.setImageResource(res);
        }


        public void onChatCloseClicked(View view) {
            mSwitchCameraBtn.setVisibility(View.VISIBLE);
            mCallBtn.setVisibility(View.VISIBLE);
            mMuteBtn.setVisibility(View.VISIBLE);
        }


        private void showToast(final String text) {
            Toast.makeText(VideoCall.this, text, Toast.LENGTH_SHORT).show();
        }
}
