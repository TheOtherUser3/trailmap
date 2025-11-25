# Trail Mapper – Polylines, Polygons & Interactive Map Overlays

This project demonstrates how to build an **interactive Google Maps app** in  
**Jetpack Compose (Material 3)** using the official **Maps Compose** library.

The app draws a polyline representing a trail, a polygon highlighting a park or  
area of interest, and allows full customization of both through UI controls.  
Users can tap either overlay to view details about the trail or the park.

---

## Features

### **1. Google Map with Hiking Trail (Polyline)**
- A multi-segment polyline represents a sample hiking route.
- Fully customizable:
  - Line color
  - Line width
- Tapping the polyline displays trail information.

### **2. Polygon Highlighting a Park**
- A polygon marks a park or natural area.
- Customizable:
  - Fill color
  - Stroke width
- Tapping the polygon displays area information.

### **3. Overlay Interaction**
- Both polyline and polygon are clickable.
- Displays contextual info depending on which overlay the user taps.

---

implementation("com.google.maps.android:maps-compose:4.4.1")
implementation("com.google.android.gms:play-services-location:21.2.0")
