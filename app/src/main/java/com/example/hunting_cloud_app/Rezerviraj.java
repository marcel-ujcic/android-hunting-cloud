package com.example.hunting_cloud_app;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalTime;

public class Rezerviraj {
    public String fullName, lokacija;
    public LocalDate datum;
    public String ura, opombe;

    //@RequiresApi(api = Build.VERSION_CODES.O)
    public Rezerviraj(String fullName, String lokacija, String ura, String opombe) {
        this.fullName = fullName;
        this.lokacija = lokacija;
        this.ura = ura;
        this.opombe = opombe;
        //this.datum = LocalDate.now();
        //this.ura = LocalTime.now();
    }
}
