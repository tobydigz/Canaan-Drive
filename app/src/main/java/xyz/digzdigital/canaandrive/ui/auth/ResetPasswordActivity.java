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
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.digzdigital.canaandrive.R;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener, OnCompleteListener<Void> {
    @BindView(R.id.btn_back) Button btnBack;
    @BindView(R.id.btn_reset_password) Button btnResetPassword;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.email) EditText email;
    @BindView(R.id.activity_reset_password)
    LinearLayout activityResetPassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        btnResetPassword.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_reset_password:
                resetPassword();
                break;
            case R.id.btn_back:
                switchActivity(LoginActivity.class);
                break;
        }
    }

    private void resetPassword() {
        String email = this.email.getText().toString().trim();
        if (!validate(email))return;
        btnResetPassword.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(this);
    }

    private boolean validate(String email) {
        boolean state = true;
        if (TextUtils.isEmpty(email)) {
            Snackbar.make(activityResetPassword, "Enter email address!", Snackbar.LENGTH_SHORT).show();
            this.email.setError("Enter email address!");
            state =  false;
        } else this.email.setError(null);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar.make(activityResetPassword, "Enter a valid email address!", Snackbar.LENGTH_SHORT).show();
            this.email.setError("Enter a valid email address!");
            state = false;
        } else this.email.setError(null);

        return state;
    }

    private void switchActivity(Class classFile) {
        Intent intent = new Intent(this, classFile);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        progressBar.setVisibility(View.GONE);
        if (task.isSuccessful()){
            Snackbar.make(activityResetPassword, "Instructions have been sent to your email", Snackbar.LENGTH_SHORT).show();
            switchActivity(LoginActivity.class);
        }else {
            btnResetPassword.setEnabled(true);
            Snackbar.make(activityResetPassword, "Failed to send reset instructions", Snackbar.LENGTH_SHORT).show();
        }
    }
}
