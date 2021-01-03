package com.example.hunting_cloud_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    private EditText emailEditText;
    private Button resetPasswordButton;
    private ProgressBar progressBar1;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = (EditText) findViewById(R.id.vnesiEnaslov);
        resetPasswordButton = (Button) findViewById(R.id.resetPassword);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Obvezno polje!");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Prosim vnesite veljaven e-naslov!");
            emailEditText.requestFocus();
            return;
        }

        //progressBar1.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPassword.this, "Preverite e-naslov za ponastavitev gesla!", Toast.LENGTH_LONG).show();
                    //progressBar.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(ForgotPassword.this, "Napaka! Poskusite ponovno!", Toast.LENGTH_LONG).show();
                    //progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}