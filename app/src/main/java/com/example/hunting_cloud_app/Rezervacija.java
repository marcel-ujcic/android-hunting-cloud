package com.example.hunting_cloud_app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import java.util.Date;

public class Rezervacija extends AppCompatActivity {
    EditText ura,opombe;
    Spinner mesta;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID, LD, fulNejm1;

    private String location;

    private ImageView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rezervacija);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fDatabaseRoot = database.getReference();
        ImageView MenuBtn = (ImageView) findViewById(R.id.meni);

        ImageView ObvBtn = (ImageView) findViewById(R.id.Obvestilo);

        EditText editText = (EditText) findViewById(R.id.ura_enter);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String fulNejm = user.getDisplayName();

        //System.out.println(user.getUid());

        fDatabaseRoot.child("Users").orderByKey().equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot addressSnapshot: snapshot.getChildren()) {
                    //System.out.println(addressSnapshot.child("lovskaDruzina").getValue().toString());
                    LD = addressSnapshot.child("lovskaDruzina").getValue().toString();
                    fulNejm1 = addressSnapshot.child("fullName").getValue().toString();
                }

                final List<String> RezervLocations = new ArrayList<String>();

                fDatabaseRoot.child("Druzine").child("ime").child(LD.trim()).child("Rezervacije").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot addressSnapshot: dataSnapshot.getChildren()) {
                            String propertyAddress = addressSnapshot.getKey().toString();
                            if (propertyAddress!=null) {
                                RezervLocations.add(propertyAddress);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                fDatabaseRoot.child("Druzine").child("ime").child(LD.trim()).child("Lokacije").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final List<String> propertyAddressList = new ArrayList<String>();

                        for (DataSnapshot addressSnapshot: dataSnapshot.getChildren()) {
                            String propertyAddress = addressSnapshot.getValue().toString();
                            if (propertyAddress!=null && !RezervLocations.contains(propertyAddress)) {
                                propertyAddressList.add(propertyAddress);
                            }
                        }

                        mesta = (Spinner) findViewById(R.id.dropdown_menu_preze);
                        ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(Rezervacija.this, android.R.layout.simple_spinner_item, propertyAddressList);
                        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mesta.setAdapter(addressAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    handled = true;
                }
                return handled;
            }
        });
        Button mButton = (Button) findViewById(R.id.bt_rezervacija);
        mButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {  // za klikn na potrditev
                ura = findViewById(R.id.ura_enter);
                opombe=findViewById(R.id.Opombe);
                if (ura.getText().toString().contains(":")){
                    String[] ura_min=ura.getText().toString().split(":");
                    if (Integer.parseInt(ura_min[0])>24 || Integer.parseInt(ura_min[0])<0){
                        Toast.makeText(getApplicationContext(), "Vnesi pravilno uro", Toast.LENGTH_SHORT).show();
                    }
                    else if (Integer.parseInt(ura_min[1])>59 || Integer.parseInt(ura_min[1])<0){
                        Toast.makeText(getApplicationContext(), "Vnesi pravilno uro", Toast.LENGTH_SHORT).show();
                    } else {

                        int min_ure = Integer.parseInt(ura_min[0]) * 60;
                        int min_min = Integer.parseInt((ura_min[1]));

                        int koncne = min_ure + min_min;

                        System.out.println(min_ure);

                        LocalDateTime now = LocalDateTime.now();

                        int hour = now.getHour();
                        int minute = now.getMinute();

                        int maxDovoljeno = ((hour + 1) * 60) + minute + 60;

                        System.out.println(maxDovoljeno);

                        if (koncne <= maxDovoljeno) {

                            location = mesta.getSelectedItem().toString().trim();

                            Rezerviraj rezerve = new Rezerviraj(fulNejm1, location, ura.getText().toString().trim(), opombe.getText().toString().trim());

                            FirebaseDatabase.getInstance().getReference("Druzine").child("ime").child(LD).child("Rezervacije").child(location).push().setValue(rezerve).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Rezervacija.this, "Rezervacija je uspela!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(Rezervacija.this, "Rezervacija ni uspela, poskusi ponovno!", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }else {
                            Toast.makeText(getApplicationContext(), "Vnesi pravilno uro!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else Toast.makeText(getApplicationContext(), "Vnesi pravilni format", Toast.LENGTH_SHORT).show();
            }
        });

        menu = (ImageView) findViewById(R.id.meni);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Rezervacija.this, ProfileActivity.class));
            }
        });

    }
}