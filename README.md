# Hybrid Geofencing and Time-Based Reminder App
Android | Java | Geofencing | Google Maps SDK | AlarmManager
## Overview
This project is an Android application that provides reminders based on both **location** and **time**. The system combines **geofencing technology** with **time scheduling** to ensure that reminders are triggered when either the user reaches a specific location or the scheduled time occurs.

The application improves traditional reminder systems by introducing a **dual-trigger mechanism**, making task notifications more reliable and context-aware.

---

## Features
- Set reminders based on **specific location**
- Set reminders based on **scheduled time**
- **Hybrid trigger system** (location OR time)
- Interactive **map-based location selection**
- **High-priority notifications**
- Persistent reminder storage using **SQLite**
- Simple and user-friendly interface

---

## Technologies Used
- **Android Studio**
- **Java**
- **Google Maps SDK**
- **Google Play Services Geofencing API**
- **Android AlarmManager**
- **SQLite Database**
- **Android Notification Manager**

---

## System Workflow
1. User creates a reminder.
2. User selects a location using Google Maps.
3. User sets a reminder time.
4. The system registers:
   - A **Geofence** for location detection
   - A **Time alarm** using AlarmManager
5. The reminder notification is triggered when:
   - The user enters the selected location, OR
   - The scheduled time is reached.

---

## Project Structure
```
app/
 ├── activities/
 ├── database/
 ├── geofence/
 ├── notifications/
 └── utils/
```

---

## How to Run the Project
1. Clone the repository
2. Open the project in **Android Studio**
3. Sync Gradle files
4. Add your **Google Maps API key**
5. Run the application on an **Android device or emulator**

---

## Future Improvements
- Edit and update options for existing reminders
- Customizable geofence radius
- Cloud backup for reminders

---

## Author
**Gangamithra R**

---

## License
This project is developed for educational and learning purposes.
