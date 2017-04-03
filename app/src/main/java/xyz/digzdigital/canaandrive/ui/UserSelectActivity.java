package xyz.digzdigital.canaandrive.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.digzdigital.canaandrive.R;
import xyz.digzdigital.canaandrive.ui.auth.DriverLoginActivity;
import xyz.digzdigital.canaandrive.ui.auth.LoginActivity;

public class UserSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select);
        ButterKnife.bind(this);
        FirebaseAuth.getInstance();
    }

    @OnClick(R.id.findRide)
    public void openUserActivity() {
        switchActivity(LoginActivity.class);
    }

    @OnClick(R.id.giveRide)
    public void openDriverActivity() {
        switchActivity(DriverLoginActivity.class);
    }

    private void switchActivity(Class classFile) {
        Intent intent = new Intent(this, classFile);
        startActivity(intent);
    }
}
