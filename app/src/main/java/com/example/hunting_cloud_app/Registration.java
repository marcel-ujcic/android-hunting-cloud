package com.example.hunting_cloud_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Registration extends AppCompatActivity implements View.OnClickListener {

    private ImageView slika;
    private TextView ime, registerUser;
    private EditText editTextFullName, editTextLovska, editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private Spinner spinnerProperty;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fDatabaseRoot = database.getReference();

        slika = (ImageView) findViewById(R.id.slika);
        slika.setOnClickListener(this);

        ime = (TextView) findViewById(R.id.ime);
        ime.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.registration);
        registerUser.setOnClickListener(this);

        editTextFullName = (EditText) findViewById(R.id.fullName);
        //editTextLovska = (EditText) findViewById(R.id.lovskaDruzina);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        fDatabaseRoot.child("Druzine").child("ime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> propertyAddressList = new ArrayList<String>();

                for (DataSnapshot addressSnapshot: dataSnapshot.getChildren()) {
                    String propertyAddress = addressSnapshot.getKey().toString();
                    if (propertyAddress!=null) {
                        propertyAddressList.add(propertyAddress);
                    }
                }

                spinnerProperty = (Spinner) findViewById(R.id.lovskaDruzina);
                ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(Registration.this, android.R.layout.simple_spinner_item, propertyAddressList);
                addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerProperty.setAdapter(addressAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.slika:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.ime:
                startActivity(new Intent(this, LoginActivity.class));
                break;

            case R.id.registration:
                registerUser();
                break;
        }

    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String lovskaDruzina = spinnerProperty.getSelectedItem().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();

        if (fullName.isEmpty()) {
            editTextFullName.setError("Obvezen vnos polnega imena!");
            editTextFullName.requestFocus();
            return;
        }

        if (lovskaDruzina.isEmpty()) {
            editTextLovska.setError("Obvezen vnos lovske družine!");
            editTextLovska.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Obvezno polje!");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Geslo mora vsebovati vsaj 6 znakov");
            editTextPassword.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Obvezen vnos e-naslova!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("E-naslov ni veljaven!");
            editTextEmail.requestFocus();
            return;
        }

        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    User user = snapshot1.getValue(User.class);
                    assert user != null;

                    if (user.email.equals(email)) {
                        editTextEmail.setError("E-naslov je že uporabljen!");
                        editTextEmail.requestFocus();
                        //return;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            User user = new User(fullName, lovskaDruzina, email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(Registration.this, "Uporabnik je uspešno registriran", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(Registration.this, "Uporabnik ni bil uspešno registriran! Poskusi ponovno", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {

                            Toast.makeText(Registration.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }
}
