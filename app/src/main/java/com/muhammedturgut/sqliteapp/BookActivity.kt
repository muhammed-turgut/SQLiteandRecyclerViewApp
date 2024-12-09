package com.muhammedturgut.sqliteapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.SnackbarContentLayout
import com.muhammedturgut.sqliteapp.databinding.ActivityBookBinding
import java.io.ByteArrayOutputStream

class BookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookBinding
    private lateinit var activityResultLanchur:ActivityResultLauncher<Intent>
    private lateinit var permissonLanchur:ActivityResultLauncher<String>
    private var selectedBitmap:Bitmap?=null
    private lateinit var database: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database=this.openOrCreateDatabase("Book", MODE_PRIVATE,null)
        registerLanchuer()

        val intent=intent
        val info=intent.getStringExtra("info")
        if(info.equals("new")){
           binding.BookNameText.setText("")
            binding.BookAboutText.setText("")
            binding.uploadImageView.setImageResource(R.drawable.ic_launcher_background)
        }
    }

    fun saveOnClick(view :View){
        val bookName=binding.BookNameText.text.toString()
        val bookAbout=binding.BookAboutText.text.toString()

        if(selectedBitmap != null){
            val smallBitmap=makeSmallBitmap(selectedBitmap!!,300)
            val outputStrem=ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStrem)
            val byteArray=outputStrem.toByteArray()

            try{

                database.execSQL("CREATE TABLE IF NOT EXISTS book (id INTEGER PRIMARY KEY,bookName VARCHAR,bookAbout VARCHAR,image BLOB) ")
                val sqlString="INSERT INTO book (bookName,bookAbout,image) VALUES(?,?,?)"
                val statement=database.compileStatement(sqlString)
                statement.bindString(1,bookName)
                statement.bindString(2,bookAbout)
                statement.bindBlob(3,byteArray)
                statement.execute()

            }catch (e:Exception){
                e.printStackTrace()
            }
            val intent=Intent(this@BookActivity,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

    }

    fun selectdOnClick(view:View){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED ){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_LONG).setAction("Give Permission",View.OnClickListener {
                        permissonLanchur.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }).show()
                }else{
                    permissonLanchur.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }else{
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLanchur.launch(intentToGallery)
            }
        }
        else{
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                    Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_LONG).setAction("Give Permission",View.OnClickListener {
                        permissonLanchur.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
                }else{
                    permissonLanchur.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }else{
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLanchur.launch(intentToGallery)
            }
        }

    }


    //  Bitmap küçültme fonksiyonu
    private fun makeSmallBitmap(image:Bitmap,maximumSize: Int) : Bitmap{

        var width=image.width
        var height=image.height
        val bitmapRtio:Double =width.toDouble()/height.toDouble()

        if(bitmapRtio>1){
            width=maximumSize
            val scaledHeight=width/bitmapRtio
            height=scaledHeight.toInt()
        }
        else{
            height=maximumSize
            val scalaWith=height*bitmapRtio
            width=scalaWith.toInt()
        }
        return Bitmap.createScaledBitmap(image,100,100,true)

    }
    private fun registerLanchuer() {
        activityResultLanchur=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode== RESULT_OK){
                val intentFromResult=result.data
                if (intentFromResult != null){
                    val imageData=intentFromResult.data

                    if(imageData != null){
                        try{

                            if(Build.VERSION.SDK_INT>=20) {
                                val source = ImageDecoder.createSource(this@BookActivity.contentResolver, imageData)
                                selectedBitmap=ImageDecoder.decodeBitmap(source)
                                binding.uploadImageView.setImageBitmap(selectedBitmap)
                            }else{
                                selectedBitmap=MediaStore.Images.Media.getBitmap(contentResolver,imageData)
                                binding.uploadImageView.setImageBitmap(selectedBitmap)
                            }

                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
        permissonLanchur=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if(result){
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLanchur.launch(intentToGallery)
            }
            else{
                Toast.makeText(this@BookActivity,"Permission needed",Toast.LENGTH_LONG).show()
            }
        }
    }
}