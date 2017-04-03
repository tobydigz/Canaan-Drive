package xyz.digzdigital.canaandrive.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
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
import xyz.digzdigital.canaandrive.R;
import xyz.digzdigital.canaandrive.ui.user.MapsActivity;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, OnCompleteListener<AuthResult> {

    @BindView(R.id.sign_in_button) Button signInButton;
    @BindView(R.id.btn_reset_password) Button btnResetPassword;
    @BindView(R.id.sign_up_button) Button signUpButton;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.email) EditText email;
    @BindView(R.id.password) EditText password;
    @BindView(R.id.activity_sign_up)
    LinearLayout activitySignUp;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        btnResetPassword.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
    }

    private void switchActivity(Class classFile) {
        Intent intent = new Intent(this, classFile);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reset_password:
                switchActivity(ResetPasswordActivity.class);
                break;
            case R.id.sign_in_button:
                switchActivity(LoginActivity.class);
                break;
            case R.id.sign_up_button:
                signup();
                break;
        }
    }

    private void signup() {
        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString().trim();
        if (!validateEmail(email) || !validatePassword(password)) return;
        progressBar.setVisibility(View.VISIBLE);
        signUpButton.setEnabled(false);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, this);
    }


    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            Snackbar.make(activitySignUp, "Enter email address!", Snackbar.LENGTH_SHORT).show();
            this.email.setError("Enter email address!");
            return false;

        } else this.email.setError(null);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(activitySignUp, "Enter a valid email address!", Snackbar.LENGTH_SHORT).show();
            this.email.setError("Enter a valid email address!");
            return false;
        } else this.email.setError(null);

        return true;
    }

    private boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            Snackbar.make(activitySignUp, "Enter password", Snackbar.LENGTH_SHORT).show();
            this.password.setError("Enter password");
            return false;
        } else this.password.setError(null);

        if (password.length() < 6) {
            Snackbar.make(activitySignUp, "Password too short, enter minimum 6 characters!", Snackbar.LENGTH_SHORT).show();
            this.password.setError("Password too short");
            return false;
        } else this.password.setError(null);
        return true;
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        progressBar.setVisibility(View.GONE);

        if (!task.isSuccessful()) {
            Snackbar.make(activitySignUp, "Sign Up failed, try again", Snackbar.LENGTH_SHORT).show();
            signUpButton.setEnabled(true);
        } else switchActivity(MapsActivity.class);

    }
}