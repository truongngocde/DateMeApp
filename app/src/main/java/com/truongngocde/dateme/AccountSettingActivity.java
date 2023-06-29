package com.truongngocde.dateme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingActivity extends AppCompatActivity {
    private CircleImageView UserImage;
    private TextView UserName;
    private TextView UserStatus;
    private Button StatusBtn;
    private Button ImageBtn;
    private Toolbar mToolBar;
    private ProgressDialog mDProgressDialog;
    private static final int GALARY_PICK=1;
    private String CurrentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        mAuth=FirebaseAuth.getInstance();
        mStorageRef= FirebaseStorage.getInstance().getReference();

        //tool bar
        mToolBar=(Toolbar)findViewById(R.id.AccountSetting_ToolBar);
        //setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Account Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //define xml components
        UserImage=(CircleImageView)findViewById(R.id.UserImage);
        UserName=(TextView)findViewById(R.id.UserName);
        UserStatus=(TextView)findViewById(R.id.UserStatus);
        StatusBtn=(Button)findViewById(R.id.changeStatusBtn);
        ImageBtn=(Button)findViewById(R.id.changeImageBtn);

        //firebase
        FirebaseUser currentUser= mAuth.getCurrentUser();
        CurrentUserId = currentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId);

        //offline capability
        mUserDatabase.keepSynced(true);

        //display name and status and image of the user
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Name = snapshot.child("Name").getValue().toString();
                final String Image = snapshot.child("Image").getValue().toString();
                String Status = snapshot.child("Status").getValue().toString();

                UserName.setText(Name);
                UserStatus.setText(Status);
                if(!Image.equals("default")) {
                    //in case of offline image load quickly
                    Picasso.get().load(Image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.user).into(UserImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                }
                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(Image).placeholder(R.drawable.user).into(UserImage);
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        StatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountSettingActivity.this,AccountStatusActivity.class);
                startActivity(intent);
            }
        });

        ImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"SELECT IMAGE"),GALARY_PICK);
            }
        });

        UserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountSettingActivity.this,DisplayUserImageActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALARY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            ProgressDialog progressDialog = new ProgressDialog(AccountSettingActivity.this);
            progressDialog.setTitle("\nTải lên hình ảnh");
            progressDialog.setMessage("Vui lòng đợi trong khi chúng tôi tải lên hình ảnh của bạn");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            // Upload ảnh gốc lên cơ sở dữ liệu lưu trữ
            UploadImageInStorageDataBase(imageUri, progressDialog);
        }
    }

    private void UploadImageInStorageDataBase(Uri imageUri, ProgressDialog progressDialog) {
        // Upload ảnh gốc lên cơ sở dữ liệu lưu trữ
        final StorageReference filePath = mStorageRef.child("profile_images").child(CurrentUserId + ".jpg");
        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Lưu đường dẫn tải xuống ảnh vào cơ sở dữ liệu
                        mUserDatabase.child("Image").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(AccountSettingActivity.this, "\n" +
                                            "hình ảnh của bạn đã được tải lên thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AccountSettingActivity.this, "\n" +
                                            "Lỗi khi tải lên", Toast.LENGTH_SHORT).show();
                                    progressDialog.hide();
                                }
                            }
                        });

                    }
                });

            }
        });
    }

}