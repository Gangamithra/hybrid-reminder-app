package com.example.locationreminder;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;private ReminderAdapter adapter;
    private DatabaseHelper db;
    private List<ReminderModel> reminderList; // <--- ADD THIS LINE
    private GeofencingClient geofencingClient;
    private double selectedLat = 0.0, selectedLon = 0.0;
    // ... rest of code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        recyclerView = findViewById(R.id.reminderRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkExactAlarmPermission();
        requestPermissions();
        refreshList();

        findViewById(R.id.fabAdd).setOnClickListener(v -> showAddReminderDialog());
    }

    private void checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                startActivity(new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
            }
        }
    }

    private void requestPermissions() {
        String[] perms = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ?
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.ACCESS_BACKGROUND_LOCATION} :
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        ActivityCompat.requestPermissions(this, perms, 101);
    }

    // Update your refreshList method in MainActivity.java
    // Inside your refreshList method in MainActivity.java
    private void refreshList() {
        reminderList = db.getAllReminders();
        adapter = new ReminderAdapter(reminderList, reminder -> {
            // 1. Remove from Database
            db.deleteReminder(reminder.getTitle());

            // 2. Remove Geofence trigger
            geofencingClient.removeGeofences(java.util.Collections.singletonList(reminder.getTitle()))
                    .addOnSuccessListener(aVoid -> android.util.Log.d("MainActivity", "Geofence Removed"))
                    .addOnFailureListener(e -> android.util.Log.e("MainActivity", "Failed to remove geofence"));

            // 3. Refresh the UI list
            refreshList(); // This refreshes the screen after deletion

            Toast.makeText(this, "Reminder Turned Off", Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);
    }

    private void showAddReminderDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_reminder, null);
        EditText etTitle = view.findViewById(R.id.etTitle);
        Button btnDate = view.findViewById(R.id.btnDate);
        Button btnTime = view.findViewById(R.id.btnTime);

        btnDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (v1, y, m, d) -> btnDate.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y)),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (v1, h, m) -> btnTime.setText(String.format(Locale.getDefault(), "%02d:%02d", h, m)),
                    c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });

        view.findViewById(R.id.btnMapPick).setOnClickListener(v -> startActivityForResult(new Intent(this, MapPickerActivity.class), 101));

        new MaterialAlertDialogBuilder(this).setTitle("New Hybrid Reminder").setView(view)
                .setPositiveButton("Save", (d, w) -> {
                    String title = etTitle.getText().toString();
                    if (title.isEmpty() || selectedLat == 0.0) return;
                    db.insertData(title, selectedLat, selectedLon, 1000, btnDate.getText().toString(), btnTime.getText().toString());
                    scheduleAlarm(title, btnDate.getText().toString(), btnTime.getText().toString());
                    addGeofence(title, selectedLat, selectedLon, 1000);
                    refreshList();
                }).show();
    }

    // Inside MainActivity class
    private void scheduleAlarm(String title, String dStr, String tStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date date = sdf.parse(dStr + " " + tStr);
            if (date == null || date.before(new Date())) return;

            Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
            intent.setAction("com.example.locationreminder.ACTION_TIME_ALARM");
            intent.putExtra("isTimeBased", true);
            intent.putExtra("title", title);

            // Unique ID per alarm
            int requestCode = (int) System.currentTimeMillis();
            PendingIntent pi = PendingIntent.getBroadcast(this, requestCode, intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (am != null) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date.getTime(), pi);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void addGeofence(String title, double lat, double lon, float radius) {
        Geofence geofence = new Geofence.Builder().setRequestId(title).setCircularRegion(lat, lon, radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE).setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER).build();
        GeofencingRequest req = new GeofencingRequest.Builder().setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).addGeofence(geofence).build();
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, title.hashCode(), intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient.addGeofences(req, pi);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            selectedLat = data.getDoubleExtra("lat", 0.0);
            selectedLon = data.getDoubleExtra("lon", 0.0);
        }
    }
}