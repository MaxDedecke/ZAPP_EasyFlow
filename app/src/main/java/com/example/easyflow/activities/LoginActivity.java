package com.example.easyflow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easyflow.R;
import com.example.easyflow.interfaces.NotifyEventHandlerBoolean;
import com.example.easyflow.utils.FirebaseHelper;
import com.example.easyflow.utils.GlobalApplication;
import com.example.easyflow.models.User;

public class LoginActivity extends AppCompatActivity implements NotifyEventHandlerBoolean {
    private EditText mEmail;
    private EditText mPassword;
    private TextView mAnmelden;
    private TextView mFrage;
    private Button mLoginButton;
    private boolean mUserHasAccount = true;
    private FirebaseHelper mFirebaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mLoginButton = findViewById(R.id.login_button);
        mFrage = findViewById(R.id.textview_kein_konto_frage);
        mAnmelden = findViewById(R.id.textview_anmelden);
        mEmail = findViewById(R.id.email_address_edittext);
        mPassword = findViewById(R.id.password_edittext);


        mFirebaseHelper = FirebaseHelper.getInstance();
        mFirebaseHelper.setListenerBoolean(this);
    }

    public void login(View view) {
        mEmail.setError(null);
        mPassword.setError(null);

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if (!GlobalApplication.isValidEmail(email)) {
            //Toast.makeText(this, getString(R.string.error_no_valid_email), Toast.LENGTH_SHORT).show();
            mEmail.setError(getString(R.string.error_no_valid_email));
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 5) {
            mPassword.setError("Passwort muss mindestens 6 Zeichen haben!");
            return;
        }


        mFirebaseHelper.validateUserCredentials(email, password);

        //todo weiter in eventhandler


    }

    public void addNewMember(View view) {
        mUserHasAccount = !mUserHasAccount;

        if (mUserHasAccount) {
            mFrage.setText(R.string.login_frage_kein_konto);
            mAnmelden.setText(R.string.login_registrieren);
            mLoginButton.setText(R.string.login_anmelden);
        } else {
            mFrage.setText(R.string.login_frage_ein_konto);
            mAnmelden.setText(R.string.login_anmelden);
            mLoginButton.setText(R.string.login_registrieren);
        }

    }

    @Override
    public void Notify(boolean accountExists, boolean passwordCorrect) {
        if (!mUserHasAccount) {
            // Create a new user.
            if (accountExists) {
                mEmail.setError("Es existiert bereits ein Konto mit dieser Email.");
                return;
            }
            User user = mFirebaseHelper.registerUser(mEmail.getText().toString(), mPassword.getText().toString());
            startApplicationForUser(user);

        } else {
            // Check credentials for user.
            if (accountExists && passwordCorrect) {
                User user = FirebaseHelper.mCurrentUser;
                startApplicationForUser(user);
            }

            if (!accountExists) {
                mEmail.setError("Es existiert kein Nutzer mit dieser Email.");
            } else if (!passwordCorrect) {
                mPassword.setError("Das Passwort ist falsch.");
            }


        }
    }

    private void startApplicationForUser(User user) {
        Toast.makeText(this, this.getString(R.string.login_successfull), Toast.LENGTH_SHORT).show();

        // Set activity_executed true.
        // Set user object to current user.
        GlobalApplication.saveStartUpInSharedPreferences(user, true);


        // Show Main Activity.
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

