package com.example.trailmap

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocation: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocation = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            PolylinePolygonMapApp(fusedLocation)
        }
    }
}

@Composable
fun PolylinePolygonMapApp(
    fusedLocation: FusedLocationProviderClient
) {
    val context = LocalContext.current

    var hasPermission by remember { mutableStateOf(false) }
    var userLocation by remember { mutableStateOf<Location?>(null) }

    // Customization state
    var polyColor by remember { mutableStateOf(Color(0xFF00E676)) }
    var polyWidth by remember { mutableStateOf(10f) }

    var polygonColor by remember { mutableStateOf(Color(0x440064FF)) }
    var polygonStroke by remember { mutableStateOf(4f) }

    // Trail clicked?
    var trailInfo by remember { mutableStateOf<String?>(null) }
    // Park clicked?
    var parkInfo by remember { mutableStateOf<String?>(null) }

    // Request permission
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted -> hasPermission = granted }
        )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Get user location
    LaunchedEffect(hasPermission) {
        if (hasPermission &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocation.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) userLocation = loc
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
    ) {
        Text(
            "TRAIL & PARK MAP",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(12.dp)
        )

        if (!hasPermission) {
            Text(
                "Waiting for location permission...",
                color = Color(0xFFFFC107),
                modifier = Modifier.padding(16.dp)
            )
            return@Column
        }

        val mapProperties = MapProperties(isMyLocationEnabled = true)
        val uiSettings = MapUiSettings(zoomControlsEnabled = true)

        // Start view at the polyline for ease of demonstration
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                LatLng(37.423, -122.084),
                16f
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = uiSettings
            ) {

                // EXAMPLE HIKING TRAIL (Polyline)
                val trailPoints = listOf(
                    LatLng(37.4220, -122.0841),
                    LatLng(37.4232, -122.0832),
                    LatLng(37.4245, -122.0827),
                    LatLng(37.4253, -122.0838),
                    LatLng(37.4260, -122.0849)
                )

                Polyline(
                    points = trailPoints,
                    color = polyColor,
                    width = polyWidth,
                    clickable = true,
                    onClick = {
                        trailInfo = "Hiking Trail: 0.7 km scenic route"
                        parkInfo = null
                    }
                )

                // POLYGON (Park)
                val parkCoords = listOf(
                    LatLng(37.4273, -122.0850),
                    LatLng(37.4268, -122.0830),
                    LatLng(37.4255, -122.0835),
                    LatLng(37.4260, -122.0852)
                )

                Polygon(
                    points = parkCoords,
                    fillColor = polygonColor,
                    strokeColor = Color(0xFF2196F3),
                    strokeWidth = polygonStroke,
                    clickable = true,
                    onClick = {
                        parkInfo = "Mountain View Park – Picnic area, hiking, wildlife."
                        trailInfo = null
                    }
                )

                // Optional: Marker at user's location
                userLocation?.let {
                    Marker(
                        state = MarkerState(
                            position = LatLng(it.latitude, it.longitude)
                        ),
                        title = "You Are Here"
                    )
                }
            }
        }

        // Customization UI
        Column(Modifier.padding(12.dp)) {
            Text("Polyline color", color = Color.White)
            Row {
                ColorBox(Color.Green) { polyColor = Color.Green }
                ColorBox(Color.Red) { polyColor = Color.Red }
                ColorBox(Color.Cyan) { polyColor = Color.Cyan }
                ColorBox(Color.Yellow) { polyColor = Color.Yellow }
            }

            Text("Polyline width", color = Color.White, modifier = Modifier.padding(top = 6.dp))
            Slider(
                value = polyWidth,
                onValueChange = { polyWidth = it },
                valueRange = 5f..25f
            )

            Text("Polygon fill color", color = Color.White, modifier = Modifier.padding(top = 6.dp))
            Row {
                ColorBox(Color(0x5500FF00)) { polygonColor = it }
                ColorBox(Color(0x55FF0000)) { polygonColor = it }
                ColorBox(Color(0x550000FF)) { polygonColor = it }
                ColorBox(Color(0x55FFFF00)) { polygonColor = it }
            }

            Text("Polygon border width", color = Color.White, modifier = Modifier.padding(top = 6.dp))
            Slider(
                value = polygonStroke,
                onValueChange = { polygonStroke = it },
                valueRange = 2f..20f
            )
        }

        // Info outbox
        Text(
            text = when {
                trailInfo != null -> trailInfo!!
                parkInfo != null -> parkInfo!!
                else -> "Tap a trail or area for details"
            },
            color = Color.White,
            modifier = Modifier
                .padding(12.dp)
        )
    }
}

// Helper: Colored selectable box
@Composable
fun ColorBox(color: Color, onPick: (Color) -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .padding(4.dp)
            .background(color)
            .clickable { onPick(color) }
    )
}
