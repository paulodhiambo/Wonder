package com.odhiambopaul.wonder.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.odhiambopaul.wonder.App
import com.odhiambopaul.wonder.R
import com.odhiambopaul.wonder.data.entity.User
import com.odhiambopaul.wonder.di.factory.HomeViewModelFactory
import com.odhiambopaul.wonder.ui.users.UserListActivity
import kotlinx.android.synthetic.main.activity_home.*
import mumayank.com.airlocationlibrary.AirLocation
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.round


class HomeActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    private val lat = MutableLiveData<String>()
    private val long = MutableLiveData<String>()
    private val selectedGender = MutableLiveData<String>()
    private val name = MutableLiveData<String>()
    private val photo = MutableLiveData<String>()
    private var photoFile: File? = null

    private val gender =
        listOf<String>(
            "Male",
            "Female",
            "Other"
        )
    @Inject
    lateinit var homeviewmodelFactory: HomeViewModelFactory
    private var locationPermissionGranted = false
    private lateinit var homeViewModel: HomeViewModel

    private val airLocation = AirLocation(this, object : AirLocation.Callback {
        override fun onSuccess(locations: ArrayList<Location>) {
            lat.value = locations[0].latitude.round(3).toString()
            long.value = locations[0].longitude.round(3).toString()
            val pos = locations[0].latitude
            Log.d("Location::=======>", pos.toString())
        }

        override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {}
    }, true)

    private fun injectDagger() {
        App.instance.applicationComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        injectDagger()
        airLocation.start()
        homeViewModel = ViewModelProvider(this, homeviewmodelFactory)[HomeViewModel::class.java]
        init(homeViewModel, savedInstanceState)
    }

    private fun init(
        homeViewModel: HomeViewModel,
        savedInstanceState: Bundle?
    ) {
        _dynamic.onItemSelectedListener = this
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gender)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        _dynamic.adapter = adapter
        getLocationPermission()
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            // Add a marker in Nairobi and move the camera
            val sydney = LatLng(-1.2810386570537482, 36.815142296254635)
            lat.value = sydney.latitude.round(3).toString()
            long.value = sydney.longitude.round(3).toString()
            it.addMarker(MarkerOptions().position(sydney).title("My Location").draggable(true))
            //it.moveCamera(CameraUpdateFactory.newLatLng(sydney))
            it.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f))
            it.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
                override fun onMarkerDragEnd(p0: Marker?) {
                    Log.d("Drag started at ", "")
                    lat.value = p0?.position?.latitude?.round(3).toString()
                    long.value = p0?.position?.longitude?.round(3).toString()
                }

                override fun onMarkerDragStart(p0: Marker?) {
                    Log.d("Drag started at ", "")
                }

                override fun onMarkerDrag(p0: Marker?) {
                    it.animateCamera(CameraUpdateFactory.newLatLng(p0?.position))
                }

            })
            it.setOnCameraMoveListener { }
            it.setOnCameraIdleListener { }
        }
        btn_capture_location.setOnClickListener {
            name.value = name_text.text.toString()
            Log.d(
                "Location::::",
                "Location: ${lat.value} ${long.value} Gender: ${selectedGender.value} Name: ${name.value}"
            )

        }
        btn_take_photo.setOnClickListener {
            captureImage()
        }
        btn_save_local_to_db.setOnClickListener {
            if (validateData()) {
                val user =
                    User(
                        name_text.text.toString(),
                        selectedGender.value!!,
                        lat.value!!,
                        long.value!!,
                        photo.value!!
                    )
                saveUser(homeViewModel, user)
            } else {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_LONG).show()
            }

        }
        btn_user_list.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    UserListActivity::class.java
                )
            )
        }
        btn_upload_users.setOnClickListener { saveUserOnline(homeViewModel) }
    }

    //extension function to round lat and long
    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }

    private fun validateData(): Boolean {
        name.value = name_text.text.toString()
        return !(name.value == null || lat.value == null || long.value == null || photo.value == null)
    }

    private fun saveUserOnline(homeViewModel: HomeViewModel) {
        val loadingBar = ProgressDialog(this)
        loadingBar.setTitle("Uploading User")
        loadingBar.setMessage("Please Wait...")
        if (validateData()) {
            homeViewModel.status.observe(this, androidx.lifecycle.Observer {
                loadingBar.show()
                if (it.toString() == "success") {
                    loadingBar.hide()
                    Toast.makeText(this, "Uploaded Successfully", Toast.LENGTH_LONG).show()
                } else if (it.toString() == "fail") {
                    loadingBar.hide()
                    Toast.makeText(this, "An error occurred", Toast.LENGTH_LONG).show()
                }
            })
            val file = File(photo.value!!);
            val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull());
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
            val fName =
                name_text.text.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val gender =
                "${selectedGender.value}".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val latitude = "${lat.value}".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val longitude = "${long.value}".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            homeViewModel.uploadUsers(fName, gender, latitude, longitude, body)
        } else {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveUser(homeViewModel: HomeViewModel, user: User) {
        if (validateData()) {
            homeViewModel.saveUserLocal(user)
            homeViewModel.status.observe(this, androidx.lifecycle.Observer {
                if (it.toString() == "success") {
                    Toast.makeText(this, "Saved Successfully", Toast.LENGTH_LONG).show()
                } else if (it.toString() == "fail") {
                    Toast.makeText(this, "User already Exist", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_LONG).show()
        }

    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
        //
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedGender.value = gender[position]
    }

    private fun captureImage() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0
            )
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                //create the file where the photo should go
                try {
                    photoFile = createImageFile()
                    Log.d("Paul", photoFile!!.absolutePath)
                    if (photoFile != null) {
                        val photoUri = FileProvider.getUriForFile(
                            this,
                            "com.odhiambopaul.wonder.fileprovider",
                            photoFile!!
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                } catch (ex: Exception) {
                    Log.e("Error::", ex.localizedMessage!!)
                }
            } else {
                Log.d(".......", baseContext.toString())
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        airLocation.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //val imageBitmap = data?.extras?.get("data") as Bitmap
            // photo.value = imageBitmap.convertToByteArray()
            val bitmap = BitmapFactory.decodeFile(photoFile!!.absolutePath)
            picture_preview.setImageBitmap(bitmap)

        } else {
            Toast.makeText(this, "Request Cancelled", Toast.LENGTH_LONG).show()
        }
    }


    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
        photo.value = image.absoluteFile.toString()
        return image
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
        }
    }
}
