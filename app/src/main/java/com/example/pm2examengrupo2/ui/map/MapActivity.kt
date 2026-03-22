package com.example.pm2examengrupo2.ui.map

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pm2examengrupo2.R
import com.example.pm2examengrupo2.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapBinding
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private var nombre: String = "Contacto"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperar datos
        try {
            latitud = intent.getStringExtra("latitud")?.toDoubleOrNull() ?: 
                     intent.getDoubleExtra("latitud", 0.0)
            longitud = intent.getStringExtra("longitud")?.toDoubleOrNull() ?: 
                      intent.getDoubleExtra("longitud", 0.0)
            nombre = intent.getStringExtra("nombre") ?: "Ubicación"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (latitud == 0.0 && longitud == 0.0) {
            Toast.makeText(this, "Ubicación inválida", Toast.LENGTH_SHORT).show()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.fabNavigate.setOnClickListener {
            abrirNavegacion()
        }
    }

    private fun abrirNavegacion() {
        if (latitud != 0.0 && longitud != 0.0) {
            val gmmIntentUri = Uri.parse("google.navigation:q=$latitud,$longitud")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                val intentGenerico = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$latitud,$longitud?q=$latitud,$longitud($nombre)"))
                startActivity(intentGenerico)
            }
        } else {
            Toast.makeText(this, "Sin coordenadas", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        val location = LatLng(latitud, longitud)
        
        mMap.addMarker(MarkerOptions()
            .position(location)
            .title(nombre))
            
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}
