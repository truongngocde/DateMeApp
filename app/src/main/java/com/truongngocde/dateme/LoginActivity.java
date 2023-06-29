package com.truongngocde.dateme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.truongngocde.dateme.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private Toolbar mToolBar;
    private TextInputEditText emailText;
    private TextInputEditText passwordText;
    private Button LoginBtn;
    private Button RBtn;
    private TextView ResetPassword;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        //define xml components
        emailText=(TextInputEditText)findViewById(R.id.LoginEmailEdit);
        passwordText=(TextInputEditText)findViewById(R.id.LoginPasswordEdit);
        LoginBtn=(Button)findViewById(R.id.LoginBtn);
        RBtn=(Button)findViewById(R.id.RBtn);
        ResetPassword= (TextView)findViewById(R.id.ResetPassword);

        ResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(LoginActivity.this,ResetPasswordActivity.class);
                startActivity(intent);
            }
        });


        RBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        addEvents();
    }
    private void addEvents() {
        binding.LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String userEmail = binding.LoginEmailEdit.getText().toString();
        String userPassword = binding.LoginPasswordEdit.getText().toString();

        auth.signInWithEmailAndPassword(userEmail,userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,"Đăng nhập thành công",Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            startActivity(new Intent(LoginActivity.this,AllUsersActivity.class));
                        }else {
                            Toast.makeText(LoginActivity.this,"Email hoặc mật khẩu không đúng",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}