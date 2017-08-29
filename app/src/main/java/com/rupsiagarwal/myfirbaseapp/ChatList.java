package com.rupsiagarwal.myfirbaseapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class ChatList extends AppCompatActivity {


    // [START define_database_reference]
   // private DatabaseReference mDatabase;
    // [END define_database_reference]
    ArrayList<HashMap<String, String>> list;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    public static String DATE = "date";
    public static String NAME = "name";
    public static String MESSAGE = "message";
    public static String FID = "myid";
    public static String TOID = "uid";
    public static String IMAGE = "img";
    //private FirebaseRecyclerAdapter<Tasks, TasksViewHolder> mAdapter;
    ChatAdapter mAdapter;
    String name;
    Firebase reference1, reference2;
    public static final int MEDIA_TYPE_IMAGE = 1;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Uri fileUri; // file url to store image/video
    String imgString = null;
    Bitmap bitmap;
    private static final String IMAGE_DIRECTORY_NAME = "Firebase chat";
    //private FirebaseStorage storageimg;
    //private StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        // mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorsimple)));
        //mActionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title);
        ImageView logout = (ImageView) mCustomView.findViewById(R.id.imgLogout);
        ImageView back = (ImageView) mCustomView.findViewById(R.id.imgBack);
        // back.setVisibility(View.GONE);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                File sharedPreferenceFile = new File("/data/data/com.rupsiagarwal.myfirbaseapp/shared_prefs/");
                File[] listFiles = sharedPreferenceFile.listFiles();
                for (File filelist : listFiles) {
                    filelist.delete();
                }
                FirebaseAuth.getInstance().signOut();
                Intent logout = new Intent(ChatList.this, SignInActivity.class);
                logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                logout.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                ChatList.this.startActivity(logout);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(ChatList.this, Home.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                ChatList.this.startActivity(i);
                ChatList.this.overridePendingTransition(0, 0);
            }
        });
        Bundle extras = getIntent().getExtras();
        //toid = extras.getString("uid");
        name = extras.getString("to_name");
        if (null != mTitleTextView) {
            mTitleTextView.setText(name);
            mActionBar.setCustomView(mCustomView);
            mActionBar.setDisplayShowCustomEnabled(true);
        }
       // storageimg = FirebaseStorage.getInstance();
        //get intent from previous activity

        // [START create_database_reference]
        //mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);

        // Set up FirebaseRecyclerAdapter with the Query
       // DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        //DatabaseReference ref = database.child("messages");
        SharedPreferences t = getSharedPreferences("info", MODE_PRIVATE);
        final String myname = t.getString("name", "");
        list = new ArrayList<HashMap<String, String>>();
        Firebase.setAndroidContext(this);
        //reference = new Firebase("https://myfirebaseapp-d7ee6.firebaseio.com/messages/");
        //firebase url path for get and send data
        reference1 = new Firebase("https://myfirebaseapp-d7ee6.firebaseio.com/messages/" + myname + "_" + name);
        reference2 = new Firebase("https://myfirebaseapp-d7ee6.firebaseio.com/messages/" + name + "_" + myname);
//

        final EditText chattext = (EditText) findViewById(R.id.chatText);

        ImageView imgSend = (ImageView) findViewById(R.id.imgSend);
        ImageView imgAttach = (ImageView) findViewById(R.id.imgAttach);
        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SharedPreferences t = getSharedPreferences("info", MODE_PRIVATE);
                String fromname = t.getString("name", "");

                final String chat = chattext.getText().toString();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                final String strDate = sdf.format(c.getTime());

                if (chat.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Empty message can't be send !", Toast.LENGTH_LONG).show();

                } else {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", chat);
                    map.put("from", fromname);
                    map.put("to", name);
                    map.put("datetime", strDate);
                    map.put("img", "");
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    chattext.setText("");
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        imgAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                selectImage();
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String datetime = map.get("datetime").toString();
                String from = map.get("from").toString();
                String to = map.get("to").toString();
                String img = map.get("img").toString();
                HashMap<String, String> map1 = new HashMap<String, String>();
                map1.put(DATE, datetime);
                //map1.put(NAME, msg.name);
                map1.put(MESSAGE, message);
                map1.put(FID, from);
                map1.put(TOID, to);
                map1.put(IMAGE, img);
                //map.put(KEY, key);
                list.add(map1);
                mAdapter = new ChatAdapter(list, ChatList.this);
                mRecycler.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                mRecycler.getLayoutManager().scrollToPosition(list.size() - 1);
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        reference2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ArrayList<HashMap<String, String>> list2 = new ArrayList<HashMap<String, String>>();
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String datetime = map.get("datetime").toString();
                String from = map.get("from").toString();
                String to = map.get("to").toString();
                String img = map.get("img").toString();
                HashMap<String, String> map1 = new HashMap<String, String>();
                map1.put(DATE, datetime);
                //map1.put(NAME, msg.name);
                map1.put(MESSAGE, message);
                map1.put(FID, from);
                map1.put(TOID, to);
                map1.put(IMAGE, img);
                //map.put(KEY, key);
                list2.add(map1);
                list2.addAll(list);
                list2.remove(0);
                mAdapter = new ChatAdapter(list2, ChatList.this);
                mRecycler.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                mRecycler.getLayoutManager().scrollToPosition(list2.size() - 1);
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatList.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    String[] galleryPermissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (EasyPermissions.hasPermissions(ChatList.this, galleryPermissions)) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    } else {
                        EasyPermissions.requestPermissions(this, "Access for storage",
                                101, galleryPermissions);
                    }

                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                previewCapturedImage();
            //onCaptureImageResult(data);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }


    @SuppressWarnings("unused")
    private void onCaptureImageResult(Intent data) {

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".png");

        FileOutputStream fo;
        //profilePic.setImageBitmap(thumbnail);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        thumbnail.compress(Bitmap.CompressFormat.PNG, 80, bytes);
        byte[] imageBytes = bytes.toByteArray();
        imgString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        uploadFile(imgString);
//        Uri selectedImageUri = data.getData();
//        uploadFile(selectedImageUri);
        //imgLoader.DisplayImage(imageBytes, profilePic);
//        Glide.with(this)
//                .load(imageBytes)
//                .asBitmap()
//                .error(R.drawable.user)
//                .into(profilePic);
    }


    private void previewCapturedImage() {
        try {


            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);

//            profilePic.setImageBitmap(bitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
            byte[] imageBytes = baos.toByteArray();
            imgString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            String i = imgString;
            uploadFile(imgString);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;

    }


    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(selectedImagePath, options);

//        profilePic.setImageBitmap(bitmap);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        imgString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        uploadFile(imgString);

    }


    //this method will upload the file
    private void uploadFile(String img) {
        //if there is a file to upload
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            SharedPreferences t = getSharedPreferences("info", MODE_PRIVATE);
            String fromname = t.getString("name", "");
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            final String strDate = sdf.format(c.getTime());
            Map<String, String> map = new HashMap<String, String>();
            map.put("message", "");
            map.put("img", img);
            map.put("from", fromname);
            map.put("to", name);
            map.put("datetime", strDate);
            reference1.push().setValue(map);
            reference2.push().setValue(map);
            progressDialog.dismiss();
    }
}
