package ChatApp.android.Services;

import android.animation.ValueAnimator;
import android.app.Service;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;

import android.os.CountDownTimer;

import android.os.Handler;

import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.Toast;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Date;

import ChatApp.android.GlobalStuff;
import ChatApp.android.R;
import de.hdodenhof.circleimageview.CircleImageView;


public class FloatingWidgetService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingView;
    private Point szWindow = new Point();
    private WindowManager.LayoutParams params;

    public boolean isLeft = true;
    public CircleImageView BubbleImage;
    public DisplayMetrics metrics;
    public int windowWidth;
    public ConstraintLayout constraintLayout;
    public ConstraintSet constraintSet = new ConstraintSet();
    public String receive_text;
    public TextView txtBubbleText;
    long oldtime;
    long newtime;
    public Bitmap bitmap;
    public FirebaseDatabase database;
    String senderRoom;
    String receiverUid;
    String data;


    public FloatingWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return (IBinder) intent;
    }


    //create floating bubble
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.bubble, null);
        //FrameLayout bubble = mFloatingView.findViewById(R.id.bubble_frame);
        BubbleImage = mFloatingView.findViewById(R.id.bubble_img);
        txtBubbleText = mFloatingView.findViewById(R.id.txtchatpop);
        txtBubbleText.setVisibility(View.GONE);

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            mWindowManager.getDefaultDisplay().getSize(szWindow);
        } else {
            DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            szWindow.set(width, height);
        }

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //
        resetMessagePosition( params.x);
        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        //Set the close button
        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the service and remove the from from the window
                stopSelf();
            }
        });

        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int windowWidth = metrics.widthPixels;
            int windowHeight = metrics.heightPixels;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:

                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        //
                        if (Math.abs(Xdiff) < 3 && Math.abs(Ydiff) < 3) {
                            Activity currentactivity = GlobalStuff.getCurrentActivity();
                            Intent intent = new Intent(getApplicationContext(), currentactivity.getClass());
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("fromwhere","ser");
                            PendingIntent pendingIntent =
                                    PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                            try {
                                pendingIntent.send();
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        }

                        if (Xdiff != 0 || Ydiff !=0 ) {
                            resetPosition((int) event.getRawX());
                        }

                        return true;
                    case MotionEvent.ACTION_MOVE:

                        //Calculate the X and Y coordinates of the view.

                        Xdiff = (int) (event.getRawX() - initialTouchX);
                        resetMessagePosition((int) event.getRawX());
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        if(Xdiff < 0)
                        {
                            params.x -= 180;
                        }
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
            byte[] imgbyte = intent.getByteArrayExtra("byteArray");
            bitmap = BitmapFactory.decodeByteArray(imgbyte, 0, imgbyte.length);
            BubbleImage.setImageBitmap(bitmap);
            senderRoom = intent.getStringExtra("sender_room");
            receiverUid = intent.getStringExtra("receiver_uid");
            ReceiveMessageListen();
        return START_NOT_STICKY;
    }

    private void startMyOwnForeground() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String NOTIFICATION_CHANNEL_ID = "ChatApp.android";
            String channelName = "DunnoChat Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setContentTitle("App is running in background")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(2, notification);
        }
    }

    //move floating bubble to left side
    private void moveToLeft() {
                ValueAnimator va = ValueAnimator.ofFloat(params.x, 0);
                int mDuration = 180;
                va.setDuration(mDuration);
                va.addUpdateListener(animation -> {
                    params.x = Math.round((Float) animation.getAnimatedValue());
                    if(mFloatingView.getWindowToken() != null)
                    mWindowManager.updateViewLayout(mFloatingView, params);
                });
                va.start();
    }

    //move floating bubble to right side
    private void moveToRight() {
        metrics = getResources().getDisplayMetrics();
        windowWidth = metrics.widthPixels;
        ValueAnimator va = ValueAnimator.ofFloat(params.x, windowWidth);
        int mDuration = 180;
        va.setDuration(mDuration);
        va.addUpdateListener(animation -> {
            params.x = Math.round((Float) animation.getAnimatedValue());
            mWindowManager.updateViewLayout(mFloatingView, params);
        });
        va.start();
    }


    //if move to middle then set back to left or right depoend on current position
    private void resetPosition(int x_cord_now) {
        metrics = getResources().getDisplayMetrics();
        windowWidth = metrics.widthPixels;
        if (x_cord_now <= windowWidth / 2) {
                isLeft = true;
                moveToLeft();
            } else {
                isLeft = false;
                moveToRight();
            }

    }

    //change message position
    private void resetMessagePosition(int x_cord_now) {
        metrics = getResources().getDisplayMetrics();
        constraintLayout = mFloatingView.findViewById(R.id.root_container);
        constraintSet.clone(constraintLayout);
        windowWidth = metrics.widthPixels;
        constraintSet.clear(R.id.txtchatpop,ConstraintSet.START);
        constraintSet.clear(R.id.txtchatpop,ConstraintSet.END);
        constraintSet.clear(R.id.bubble_img,ConstraintSet.END);
        if (x_cord_now <= windowWidth / 2) {
            isLeft = true;
            constraintSet.connect(R.id.txtchatpop,ConstraintSet.START,R.id.bubble_img,ConstraintSet.END,5);
        } else {
            isLeft = false;
            constraintSet.connect(R.id.txtchatpop,ConstraintSet.END,R.id.bubble_img,ConstraintSet.START,5);
            constraintSet.connect(R.id.bubble_img,ConstraintSet.END,R.id.txtchatpop,ConstraintSet.START,5);
        }
        constraintSet.applyTo(constraintLayout);
    }

    //receive and display message next to bubble
    private void ReceiveMessageListen()
    {
        database = FirebaseDatabase.getInstance();
        Query query =  database.getReference().child("chats")
                .child(senderRoom).child("messages");
        query.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.child("senderId").getValue().toString().equals(receiverUid)) {
                    Date date = new Date();
                    newtime = date.getTime();
                    oldtime = Long.parseLong(snapshot.child("timestamp").getValue().toString());
                    if(newtime - oldtime <= 980)
                    {
                        data = snapshot.child("message").getValue().toString();
                        txtBubbleText = mFloatingView.findViewById(R.id.txtchatpop);
                        txtBubbleText.setText(data);
                        txtBubbleText.setVisibility(View.VISIBLE);
                        AlphaAnimation alphaAnim = new AlphaAnimation(1.0f,0.0f);
                        alphaAnim.setStartOffset(500);
                        alphaAnim.setDuration(800);
                        alphaAnim.setAnimationListener(new Animation.AnimationListener()
                        {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            public void onAnimationEnd(Animation animation)
                            {
                                txtBubbleText.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        txtBubbleText.setAnimation(alphaAnim);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }


}
