package com.example.storyapps.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.example.storyapps.BuildConfig
import com.example.storyapps.databinding.ActivityAddStoryBinding
import com.example.storyapps.viewmodel.AddStoryViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var photoFile: File
    private val addStoryViewModel by viewModels<AddStoryViewModel>()

    private val startForResultGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImage: Uri? = data?.data
            selectedImage?.let {
                binding.imageView.setImageURI(it)
                photoFile = uriToFile(it)
            }
        }
    }

    private val startForResultCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val takenImage = BitmapFactory.decodeFile(photoFile.path)
            binding.imageView.setImageBitmap(takenImage)
        }
    }
    private val startForResultAddStory = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Refresh story list on success
            setResult(RESULT_OK)
            finish()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGallery.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startForResultGallery.launch(galleryIntent)
        }

        binding.btnCamera.setOnClickListener {
            if (hasCameraPermission()) {
                startCamera()
            } else {
                requestCameraPermission()
            }
        }

        binding.btnUpload.setOnClickListener {
            val description = binding.etDescription.text.toString()
            if (description.isEmpty()) {
                Toast.makeText(this, "Please add a description", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val token = getSharedPreferences("user_prefs", MODE_PRIVATE).getString("token", "") ?: ""
            val compressedFile = compressImage(photoFile)
            addStoryViewModel.addStory("Bearer $token", description, compressedFile)
        }

        addStoryViewModel.addStoryResult.observe(this, Observer { response ->
            if (response.error) {
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Story added successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
    }

    private fun startCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createImageFile()
        val photoURI = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", photoFile)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startForResultCamera.launch(cameraIntent)
    }

    // Constants for permission request
    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, start camera
                startCamera()
            } else {
                // Camera permission denied
                Toast.makeText(this, "Camera permission is required to take photos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createImageFile(): File {
        val fileName = "photo_${System.currentTimeMillis()}"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDir)
    }

    private fun uriToFile(uri: Uri): File {
        val contentResolver = contentResolver
        val myFile = File.createTempFile("temp_image", null, cacheDir)
        contentResolver.openInputStream(uri)?.use { inputStream ->
            myFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return myFile
    }

    private fun compressImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos)
        val compressedData = bos.toByteArray()

        val fos = FileOutputStream(file)
        fos.write(compressedData)
        fos.flush()
        fos.close()

        return file
    }
}
