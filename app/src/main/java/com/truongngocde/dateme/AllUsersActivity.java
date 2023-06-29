package com.truongngocde.dateme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.TooltipCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.truongngocde.adapter.UsersAdapter;
import com.truongngocde.models.Users;

import java.util.ArrayList;

public class AllUsersActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    private ListView usersListView;
    private ArrayList <String>UsersId;
    private ArrayList <Users>UsersArrayList;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    boolean firstStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        final String UId=currentUser.getUid();

        //Tool bar
        //mToolBar=(Toolbar)findViewById(R.id.AllUsers_Toolbar);
        //setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //define xml components
        usersListView= (ListView)findViewById(R.id.AllUsers_ListView_id);

        UsersId=new ArrayList<>();
        UsersArrayList=new ArrayList<>();

        final UsersAdapter adapter=new UsersAdapter(AllUsersActivity.this,UsersArrayList);
        usersListView.setAdapter(adapter);


        //save users IDs in Users ArrayList to enable me to take the user's data to display them
        DatabaseReference root=FirebaseDatabase.getInstance().getReference();
        DatabaseReference m=root.child("users");
        ValueEventListener eventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    UsersId.clear();
                    for( DataSnapshot Snapshot: dataSnapshot.getChildren()){
                        //to not add my account to all users
                        if(!Snapshot.getKey().equals(UId)) UsersId.add(Snapshot.getKey().toString());
                    }
                    sentUserDataToArrayAdapter();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        m.addListenerForSingleValueEvent(eventListener);


        //if the user click to any user contact
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Users User= UsersArrayList.get(i);
                Intent intent = new Intent(AllUsersActivity.this,UserProfileActivity.class);
                intent.putExtra("User Id",User.getUserId());
                intent.putExtra("User Name",User.getUserName());
                intent.putExtra("User Status",User.getUserStatus());
                intent.putExtra("User Image",User.getUserImage());
                startActivity(intent);
            }
        });
        View profile_view = findViewById(R.id.ic_profile);
        View matches_view = findViewById(R.id.ic_matched);
        View home_view = findViewById(R.id.main_activity_icon);

        profile_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllUsersActivity.this, AccountSettingActivity.class);
                startActivity(intent);
            }
        });
        matches_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllUsersActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        home_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllUsersActivity.this, WelcomeActivity.class);
                startActivity(intent);
            }
        });

    }


    private void sentUserDataToArrayAdapter(){
        UsersArrayList.clear();
        final UsersAdapter adapter=new UsersAdapter(AllUsersActivity.this,UsersArrayList);

        for(int i=0;i<UsersId.size();i++){
            DatabaseReference root=FirebaseDatabase.getInstance().getReference();
            DatabaseReference m=root.child("users").child(UsersId.get(i));
            ValueEventListener eventListener= new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String name = dataSnapshot.child("Name").getValue().toString();
                        String status = dataSnapshot.child("Status").getValue().toString();
                        String image = dataSnapshot.child("Image").getValue().toString();

                        UsersArrayList.add(new Users(name,status,image,dataSnapshot.getKey()));
                        adapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            };
            m.addListenerForSingleValueEvent(eventListener);

        }
        usersListView.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_options,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        if(id == R.id.AccountSetting_id){
            Intent intent = new Intent(AllUsersActivity.this,AccountSettingActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.DeleteAccount_id){
            Intent intent = new Intent(AllUsersActivity.this,DeleteAccountActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.Logout_id){
            CheckIfLogOutOrNot();
        }
        return super.onOptionsItemSelected(item);
    }
    private void CheckIfLogOutOrNot(){
        //create the AlertDialog then check the user choose yes or no
        AlertDialog.Builder checkAlert = new AlertDialog.Builder(AllUsersActivity.this);
        checkAlert.setMessage("Bạn có muốn Đăng xuất không?")
                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        String CurrentUID = currentUser.getUid();
                        FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUID).child("Online").setValue(ServerValue.TIMESTAMP);
                        FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUID).child("Seen").setValue("offline");

                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(AllUsersActivity.this,WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = checkAlert.create();
        alert.setTitle("Log Out");
        alert.show();

    }
}