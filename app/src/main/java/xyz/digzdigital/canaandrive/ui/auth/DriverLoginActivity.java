package xyz.digzdigital.canaandrive.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.digzdigital.canaandrive.R;
import xyz.digzdigital.canaandrive.ui.driver.DriverActivity;
import xyz.digzdigital.canaandrive.ui.user.MapsActivity;

public class DriverLoginActivity extends AppCompatActivity implements View.OnClickListener, OnCompleteListener<AuthResult> {

    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_reset_password)
    Button btnResetPassword;
    @BindView(R.id.btn_signup)
    Button btnSignup;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.activity_login)
    LinearLayout activityLogin;
    private ProgressDialog progressDialog;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        auth.signInAnonymously();

            btnResetPassword.setVisibility(View.GONE);
            btnSignup.setVisibility(View.GONE);

        btnLogin.setOnClickListener(this);
        btnResetPassword.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_reset_password:
                switchActivity(ResetPasswordActivity.class);
                break;
            case R.id.btn_signup:
                switchActivity(RegisterActivity.class);
                break;
        }
    }

    private void login() {
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        if (!validate(emailText, passwordText)) return;
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Please wait, signing you in");
        progressDialog.show();
        btnLogin.setEnabled(false);
        signIn(emailText, passwordText);
        // auth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(this, this);
    }

    private void signIn(String emailText, final String passwordText) {
        FirebaseDatabase.getInstance().getReference().child("auth").child(emailText).child("password").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (((String) dataSnapshot.getValue()).equals(passwordText)){
                    switchActivity(DriverActivity.class);
                }else{
                    Snackbar.make(activityLogin, "Login failed", Snackbar.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean validate(String email, String password) {
        boolean state = true;

        if (TextUtils.isEmpty(email)) {
            Snackbar.make(activityLogin, "Enter username", Snackbar.LENGTH_SHORT).show();
            this.email.setError("Enter username");
            state = false;
        } else this.email.setError(null);


        if (TextUtils.isEmpty(password)) {
            Snackbar.make(activityLogin, "Enter password", Snackbar.LENGTH_SHORT).show();
            this.password.setError("Enter password");
            state = false;
        } else this.password.setError(null);


        if (password.length() < 6) {
            Snackbar.make(activityLogin, "Password should be at least six characters", Snackbar.LENGTH_SHORT).show();
            this.password.setError("Password too short");
            state = false;
        } else this.password.setError(null);

        return state;
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        progressBar.setVisibility(View.GONE);
        if (!task.isSuccessful()) {

        } else {

        }
    }

    private void switchActivity(Class classFile) {
        Intent intent = new Intent(this, classFile);
        intent.putExtra("driverid", email.getText().toString());
        startActivity(intent);
    }
}