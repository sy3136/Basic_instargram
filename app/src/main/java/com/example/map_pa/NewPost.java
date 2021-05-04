package com.example.map_pa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class NewPost extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseReference ref;
    StorageReference ref2;
    StorageReference ref3;
    StorageReference ref33;
    DatabaseReference ref4;
    DatabaseReference ref5;
    private static final int PICK_IMAGE = 777;
    private static final int PICK_IMAGE2 = 77;
    Uri currentImageUri;
    boolean check;
    boolean check2;
    private String userName;
    Uri imageUri = null;
    private Uri downloadUrl = null;
    private Uri profile_url = null;

    private post publicPost;
    private post personalPost;

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        check = false;
        check2 = false;
        publicPost = new post();
        personalPost = new post();

        Intent newPostIntent = getIntent();

        final String username = newPostIntent.getStringExtra("Username");

        userName = username;

        ref = FirebaseDatabase.getInstance().getReference().child("User");
        ref2 = FirebaseStorage.getInstance().getReference("Images");

        ref3 = FirebaseStorage.getInstance().getReference("Posts");
        ref4 = FirebaseDatabase.getInstance().getReference().child("publicPost");
        ref5 = FirebaseDatabase.getInstance().getReference().child("personalPost"+username);

        ImageButton imageContent = (ImageButton)findViewById(R.id.imageContent);
        imageContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery2,PICK_IMAGE2);
            }
        });
        final EditText postContent = (EditText)findViewById(R.id.postContent);
        final EditText postTag = (EditText)findViewById(R.id.postTags);
        final CheckBox publicCheck = (CheckBox)findViewById(R.id.publicCheck);


        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        final ImageButton image = (ImageButton)header.findViewById(R.id.drawer_image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery,PICK_IMAGE);
            }
        });
        final TextView text = (TextView)header.findViewById(R.id.drawer_username);
        Menu menu = navigationView.getMenu();
        final MenuItem fullname = menu.findItem(R.id.navigationFullname);
        final MenuItem birth = menu.findItem(R.id.navigationBirthday);
        final MenuItem email = menu.findItem(R.id.navigationEmail);
        ref.child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                fullname.setTitle(user.getFullname());
                birth.setTitle(user.getBirth());
                email.setTitle(user.getEmail());
                text.setText(user.getUserid());

                StorageReference islandRef = ref2.child("profileImage"+userName);

                final long ONE_MEGABYTE = 1024 * 1024;
                islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUr2 = uri;
                        profile_url = downloadUr2;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        image.setImageBitmap(bitmap);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, tb, R.string.app_name, R.string.app_name);
        drawerToggle.syncState();

        Button createPost = (Button)findViewById(R.id.createPost);
        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check2 = true;
                final String spostContent = postContent.getText().toString();
                final String spostTag = postTag.getText().toString();

                if(TextUtils.isEmpty(spostContent)) {
                    Toast.makeText(NewPost.this, "Please input contents", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(publicCheck.isChecked() == true){
                    ref4.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            publicPost.setUsername(userName);
                            publicPost.setContent(spostContent);
                            publicPost.setTag(spostTag);
                            if(profile_url != null){
                                publicPost.setProfile(profile_url.toString());
                            }
                            if(downloadUrl != null){
                                publicPost.setImage(downloadUrl.toString());
                            }
                            ref4.push().setValue(publicPost);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
                else{
                    ref5.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            personalPost.setUsername(userName);
                            personalPost.setContent(spostContent);
                            personalPost.setTag(spostTag);
                            if(profile_url != null){
                                personalPost.setProfile(profile_url.toString());
                            }
                            if(downloadUrl != null){
                                personalPost.setImage(downloadUrl.toString());
                            }
                            ref5.push().setValue(personalPost);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(NewPost.this,databaseError.toString(),Toast.LENGTH_LONG).show();
                            return;
                        }
                    });
                }
                Intent createPostIntent = new Intent(NewPost.this, postPage.class);
                createPostIntent.putExtra("Username",username);
                startActivity(createPostIntent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && data != null && data.getData() != null){
            ImageButton img = (ImageButton)findViewById(R.id.drawer_image);
            currentImageUri = data.getData();
            check = true;
            img.setImageURI(currentImageUri);
            if(check == true){
                StorageReference stref = ref2.child("profileImage"+userName);
                UploadTask uploadTask = stref.putFile(currentImageUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                });

                check = false;
            }
        }
        else if(requestCode == PICK_IMAGE2 && data != null && data.getData() != null ){
            ImageButton img2 = (ImageButton)findViewById(R.id.imageContent);
            imageUri = data.getData();
            check = true;
            img2.setImageURI(imageUri);
            if(check == true){
                final StorageReference stref = ref3.child(imageUri.getLastPathSegment());
                stref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        stref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUr = uri;
                                downloadUrl = downloadUr;
                            }
                        });
                    }
                });

                check = false;
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        closeDrawer();

        switch (item.getItemId()){
            case R.id.navigationBirthday:
                break;

            case R.id.navigationEmail:
                break;

            case R.id.navigationFullname:
                break;
        }


        return false;
    }

    private void closeDrawer(){
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void openDrawer(){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
        }
        super.onBackPressed();
    }
}
