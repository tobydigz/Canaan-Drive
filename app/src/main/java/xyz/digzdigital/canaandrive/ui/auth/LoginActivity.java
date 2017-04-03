package xyz.digzdigital.canaandrive.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.digzdigital.canaandrive.ui.MainActivity;
import xyz.digzdigital.canaandrive.ui.user.MapsActivity;
import xyz.digzdigital.canaandrive.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, OnCompleteListener<AuthResult> {

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

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) switchActivity(MapsActivity.class);

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
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        auth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(this, this);
    }

    private boolean validate(String email, String password) {
        boolean state = true;

        if (TextUtils.isEmpty(email)) {
            Snackbar.make(activityLogin, "Enter email address!", Snackbar.LENGTH_SHORT).show();
            this.email.setError("Enter email address");
            state = false;
        } else this.email.setError(null);


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(activityLogin, "Enter a valid email address!", Snackbar.LENGTH_SHORT).show();
            this.email.setError("Enter a valid email address!");
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
            Snackbar.make(activityLogin, "Login failed", Snackbar.LENGTH_SHORT).show();
            this.btnLogin.setEnabled(true);
        } else {
            switchActivity(MainActivity.class);
        }
    }

    private void switchActivity(Class classFile) {
        Intent intent = new Intent(this, classFile);
        startActivity(intent);
    }
}