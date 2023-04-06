package com.example.memoriesinfotos

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.memoriesinfotos.databinding.ActivityFotoAddBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream

class FotoAdd : AppCompatActivity() {
    private lateinit var binding: ActivityFotoAddBinding
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    var selectedBitmap : Bitmap? = null
    private lateinit var database : SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFotoAddBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        database = this.openOrCreateDatabase("Fotos", MODE_PRIVATE,null)

        registerLauncher()
    }


    fun savebutton(view: View){
        val fotoKey = binding.fotoKeys.text.toString()
        val fotoDetail = binding.detailsText.text.toString()
        if (selectedBitmap != null){
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!,500)

            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream)
            val byteArray = outputStream.toByteArray()

        try {
            database.execSQL("CREATE TABLE IF NOT EXISTS fotos(id INTEGER PRIMARY KEY, fotokey VARCHAR, fotodetail VARCHAR,image BLOB)")

            val sqlString = "INSERT INTO fotos (fotokey, fotodetail,image) VALUES (?, ?, ?)"
            val statement = database.compileStatement(sqlString)

            statement.bindString(1,fotoKey)
            statement.bindString(2,fotoDetail)
            statement.bindBlob(3,byteArray)
            statement.execute()

        } catch (e:Exception){
            e.printStackTrace()
        }
        }
        val intentBack = Intent(this@FotoAdd,FotoList::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intentBack)
    }

    private fun makeSmallerBitmap (image : Bitmap,maximumSize : Int) : Bitmap{
        var width = image.width
        var height = image.height
        val bitmapRadio : Double = width.toDouble() / height.toDouble()

        if(bitmapRadio > 1){
            width = maximumSize
            val scaledHeight = width / bitmapRadio
            height = scaledHeight.toInt()
        } else {
            height = maximumSize
            val scaledWidth = height * bitmapRadio
            width = scaledWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image,width,height,true)
    }

    fun selectImage(view : View){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }).show()
            }else{
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }

        }else{
            val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }
    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if(result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult != null){
                    val imageData = intentFromResult.data
                    if(imageData != null){
                        try {
                            if (Build.VERSION.SDK_INT >= 28){
                                val source = ImageDecoder.createSource(this@FotoAdd.contentResolver,imageData)
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView3.setImageBitmap(selectedBitmap)
                            }else{
                                selectedBitmap = MediaStore.Images.Media.getBitmap(this@FotoAdd.contentResolver,imageData)
                                binding.imageView3.setImageBitmap(selectedBitmap)
                            }
                        } catch (e : Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if(result){
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else{
                Toast.makeText(this@FotoAdd,"Permission Needed !!",Toast.LENGTH_SHORT).show()
            }
        }
    }
}