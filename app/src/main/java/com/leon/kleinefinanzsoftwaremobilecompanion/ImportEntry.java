package com.leon.kleinefinanzsoftwaremobilecompanion;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class ImportEntry implements Serializable {
    public double amount;
    public String usage;
    public boolean gebucht;
    public boolean einnahme;
    public Date date;
    public boolean check = false;
    public ImportEntry(double amount, String usage, boolean gebucht, boolean einnahme, Date date) {
        this.usage = usage;
        this.amount = amount;
        this.gebucht = gebucht;
        this.einnahme = einnahme;
        this.date = date;
    }
}
