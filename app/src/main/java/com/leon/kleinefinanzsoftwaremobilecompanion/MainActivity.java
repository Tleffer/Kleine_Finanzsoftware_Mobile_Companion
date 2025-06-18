package com.leon.kleinefinanzsoftwaremobilecompanion;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class MainActivity extends AppCompatActivity {
    Import impo;
    ActivityResultLauncher<Intent> launcher;
    int[] pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        ListView financeList = findViewById(R.id.finance_list);
        TextView NothingToSee = findViewById(R.id.empty_view);
        Button cancel_bt = findViewById(R.id.cancel_button);
        Button confirm_button = findViewById(R.id.confirm_button);
        Button add_bt = findViewById(R.id.add_button);
        Button cancel_select_bt = findViewById(R.id.cancel_select_button);
        ImageView delete_select_bt = findViewById(R.id.delete_select);
        CheckBox checkall = findViewById(R.id.check_all_box);
        ImageButton exp_button = findViewById(R.id.export_button);

        NothingToSee.setVisibility(View.INVISIBLE);
        cancel_bt.setVisibility(View.INVISIBLE);
        confirm_button.setVisibility(View.INVISIBLE);
        cancel_bt.setEnabled(false);
        confirm_button.setEnabled(false);

        cancel_select_bt.setEnabled(false);
        cancel_select_bt.setVisibility(View.INVISIBLE);
        delete_select_bt.setEnabled(false);
        delete_select_bt.setVisibility(View.INVISIBLE);
        exp_button.setEnabled(false);
        exp_button.setVisibility(View.INVISIBLE);

        cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_cancel();
            }
        });
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_confirm(pos);
            }
        });
        add_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });
        checkall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkedall();
            }
        });
        cancel_select_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel_select();
            }
        });
        delete_select_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_select_bt_click();
            }
        });
        exp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    export();
                } catch (JsonProcessingException e) {
                    Log.d("export-error", "error while exporting");
                }
            }
        });

        refresh();
        //uncheck when loaded
        for(int i = 0; i < impo.data.size(); i++) {
            impo.data.get(i).check = false;
        }
        save(impo);
        refresh();

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ImportEntry entry = (ImportEntry) result.getData().getSerializableExtra("entry");

                        Log.d("hinzu", "joa");

                        impo.data.add(entry);
                        save(impo);
                        refresh();
                    }
                }
        );
    }
    public void delete(int[] index) {
        Button cancel_bt = findViewById(R.id.cancel_button);
        Button confirm_button = findViewById(R.id.confirm_button);

        pos = new int[index.length];
        pos = index.clone();

        cancel_bt.setVisibility(View.VISIBLE);
        confirm_button.setVisibility(View.VISIBLE);
        cancel_bt.setEnabled(true);
        confirm_button.setEnabled(true);
        cancel_bt.bringToFront();
        confirm_button.bringToFront();
    }
    private void delete_confirm(int[] index) {

        for(int i = index.length - 1; i >= 0; i--) {
            if(index[i] != -1) {
                impo.data.remove(i);
            }
        }


        Button cancel_bt = findViewById(R.id.cancel_button);
        Button confirm_button = findViewById(R.id.confirm_button);
        ListView financeList = findViewById(R.id.finance_list);

        financeList.bringToFront();
        cancel_bt.setVisibility(View.INVISIBLE);
        confirm_button.setVisibility(View.INVISIBLE);
        cancel_bt.setEnabled(false);
        confirm_button.setEnabled(false);

        cancel_select();
    }
    private void delete_cancel() {
        Button cancel_bt = findViewById(R.id.cancel_button);
        Button confirm_button = findViewById(R.id.confirm_button);
        ListView financeList = findViewById(R.id.finance_list);

        financeList.bringToFront();
        cancel_bt.setVisibility(View.INVISIBLE);
        confirm_button.setVisibility(View.INVISIBLE);
        cancel_bt.setEnabled(false);
        confirm_button.setEnabled(false);

        cancel_select();
    }
    public void abgebucht(int index) {
        impo.data.get(index).gebucht = !impo.data.get(index).gebucht;
        save(impo);
        refresh();
    }
    private void add() {

        Intent intent = new Intent(MainActivity.this, HinzuActivity.class);
        launcher.launch(intent);
    }
    public void checked(int index, boolean c) {
        Button cancel_select_bt = findViewById(R.id.cancel_select_button);
        ImageView delete_select_bt = findViewById(R.id.delete_select);
        CheckBox checkall = findViewById(R.id.check_all_box);
        ImageButton exp_button = findViewById(R.id.export_button);
        impo.data.get(index).check = c;
        if(c) {
            cancel_select_bt.setEnabled(true);
            cancel_select_bt.setVisibility(View.VISIBLE);
            delete_select_bt.setEnabled(true);
            delete_select_bt.setVisibility(View.VISIBLE);
            exp_button.setEnabled(true);
            exp_button.setVisibility(View.VISIBLE);
            int tmp = 0;
            for (int i = 0; i < impo.data.size(); i++) {
                if(impo.data.get(i).check) {
                    tmp++;
                }
            }
            if(tmp == impo.data.size()) {
                checkall.setChecked(true);
            }
        } else {
            checkall.setChecked(false);
            int tmp = 0;
            for (int i = 0; i < impo.data.size(); i++) {
                if(impo.data.get(i).check) {
                    tmp++;
                }
            }
            if(tmp == 0) {
                cancel_select_bt.setEnabled(false);
                cancel_select_bt.setVisibility(View.INVISIBLE);
                delete_select_bt.setEnabled(false);
                delete_select_bt.setVisibility(View.INVISIBLE);
                exp_button.setEnabled(false);
                exp_button.setVisibility(View.INVISIBLE);
            }
        }
        save(impo);
        refresh();
    }
    private void checkedall() {
        Button cancel_select_bt = findViewById(R.id.cancel_select_button);
        ImageView delete_select_bt = findViewById(R.id.delete_select);
        CheckBox checkall = findViewById(R.id.check_all_box);
        ImageButton exp_button = findViewById(R.id.export_button);

        if(checkall.isChecked()) {
            for (int i = 0; i < impo.data.size(); i++) {
                impo.data.get(i). check = true;
            }
            cancel_select_bt.setEnabled(true);
            cancel_select_bt.setVisibility(View.VISIBLE);
            delete_select_bt.setEnabled(true);
            delete_select_bt.setVisibility(View.VISIBLE);
            exp_button.setEnabled(true);
            exp_button.setVisibility(View.VISIBLE);
        } else {
            int tmp = 0;
            for (int i = 0; i < impo.data.size(); i++) {
                if(impo.data.get(i).check) {
                    tmp++;
                }
            }
            if(tmp == impo.data.size()) {
                for (int i = 0; i < impo.data.size(); i++) {
                    impo.data.get(i). check = false;
                }
                cancel_select_bt.setEnabled(false);
                cancel_select_bt.setVisibility(View.INVISIBLE);
                delete_select_bt.setEnabled(false);
                delete_select_bt.setVisibility(View.INVISIBLE);
                exp_button.setEnabled(false);
                exp_button.setVisibility(View.INVISIBLE);
            }
        }
        save(impo);
        refresh();
    }
    public void save(Import data) {
        try {
            FileOutputStream fos = openFileOutput("save.data", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(data);
            os.close();
            fos.close();
        } catch (IOException e) {
            Log.d("data-error", "IO error");
            e.printStackTrace();
        }
    }
    public void refresh() {
        ListView financeList = findViewById(R.id.finance_list);
        TextView NothingToSee = findViewById(R.id.empty_view);

        NothingToSee.setVisibility(View.INVISIBLE);
        financeList.setAdapter(null);

        try {
            FileInputStream fis = openFileInput("save.data");
            ObjectInputStream is = new ObjectInputStream(fis);
            impo = (Import) is.readObject();
            is.close();
            fis.close();
            //Log.d("data", data.dataEntryList.get(0).kilometres);
        } catch (IOException | ClassNotFoundException e) {
            impo = new Import();
            //throw new RuntimeException(e);
            Log.d("data-error", "not found");
        }



        if(!impo.data.isEmpty()) {
            String[] vals = new String[impo.data.size()];
            for (int i = 0; i < vals.length; i++) {
                vals[i] = impo.data.get(i).usage;
            }

            //creating the listView
            financeListAdapter adapter = new financeListAdapter(this, vals, impo, this);
            financeList.setAdapter(adapter);
        } else {
            NothingToSee.setVisibility(View.VISIBLE);
        }
    }
    private void cancel_select() {
        Button cancel_select_bt = findViewById(R.id.cancel_select_button);
        ImageView delete_select_bt = findViewById(R.id.delete_select);
        CheckBox checkall = findViewById(R.id.check_all_box);
        ImageButton exp_button = findViewById(R.id.export_button);
        checkall.setChecked(false);
        for (int i = 0; i < impo.data.size(); i++) {
            impo.data.get(i). check = false;
        }
        cancel_select_bt.setEnabled(false);
        cancel_select_bt.setVisibility(View.INVISIBLE);
        delete_select_bt.setEnabled(false);
        delete_select_bt.setVisibility(View.INVISIBLE);
        exp_button.setEnabled(false);
        exp_button.setVisibility(View.INVISIBLE);
        save(impo);
        refresh();
    }
    private void delete_select_bt_click() {
        int[] tmparray = new int[impo.data.size()];
        for(int i = 0; i< impo.data.size(); i++) {
            if(impo.data.get(i).check) {
                tmparray[i] = i;
            } else {
                tmparray[i] = -1;
            }
        }
        delete(tmparray);
    }
    private void export() throws JsonProcessingException {
        Export expo = new Export();
        for(int i = 0; i < impo.data.size(); i++) {
            if(impo.data.get(i).check) {
                expo.data.add(impo.data.get(i));
            }
        }

        XmlMapper mapper = new XmlMapper();
        String xml = mapper.writeValueAsString(expo);
        Calendar c = Calendar.getInstance();
        Date datevar;
        String timevar = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
        DateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            datevar = sdf.parse(timevar);

        } catch (ParseException e) {
            //throw new RuntimeException(e);
            datevar = new Date();
            Log.d("parse-eror", e.toString());
        }
        exportAndShareXmlFile("export " + c.get(Calendar.YEAR) + "-"+ c.get(Calendar.MONTH) + "-"+ c.get(Calendar.DAY_OF_MONTH) + " " + datevar.toString().substring(11,16) + ".finentry", xml);

        Log.d("data-confirm", "Export confirm+");
        Toast save_conf_toast = Toast.makeText(this, "Datei erforlgreich in Downloads gespeichert", Toast.LENGTH_SHORT);
        save_conf_toast.show();

    }

    private void exportAndShareXmlFile(String fileName, String xmlContent) {
        ContentResolver resolver = getContentResolver();
        Uri fileUri = null;

            // Android 10+ (Scoped Storage)
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            //values.put(MediaStore.Downloads.MIME_TYPE, "application/xml");
            values.put(MediaStore.Downloads.IS_PENDING, 1);

            Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            fileUri = resolver.insert(collection, values);

            if (fileUri != null) {
                try (OutputStream out = resolver.openOutputStream(fileUri)) {
                    if (out != null) {
                        out.write(xmlContent.getBytes());
                    }
                    values.clear();
                    values.put(MediaStore.Downloads.IS_PENDING, 0);
                    resolver.update(fileUri, values, null, null);
                } catch (IOException e) {
                    Log.e("share-error", "Write failed", e);
                    return;
                }
            }

        if (fileUri != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/xml");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Share exported XML"));
        }
    }
}