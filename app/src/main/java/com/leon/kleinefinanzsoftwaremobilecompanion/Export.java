package com.leon.kleinefinanzsoftwaremobilecompanion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Export implements Serializable {
    public List<ImportEntry> data = new ArrayList<>();
}
