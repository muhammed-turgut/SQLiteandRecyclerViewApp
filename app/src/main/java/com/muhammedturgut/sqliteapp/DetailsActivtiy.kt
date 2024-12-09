package com.muhammedturgut.sqliteapp

import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.muhammedturgut.sqliteapp.databinding.ActivityDetailsActivtiyBinding

class DetailsActivtiy : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsActivtiyBinding
    private lateinit var database: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDetailsActivtiyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database=this.openOrCreateDatabase("Book", MODE_PRIVATE,null)
        val intent=intent
        val info=intent.getStringExtra("info")
        if(info.equals("old")){
            val selectedId=intent.getIntExtra("id",1)
            val cursor=database.rawQuery("SELECT * FROM book WHERE id=?", arrayOf(selectedId.toString()))
            val bookNameIx=cursor.getColumnIndex("bookName")
            val bookAboutIx=cursor.getColumnIndex("bookAbout")
            val imageIx=cursor.getColumnIndex("image")

            while(cursor.moveToNext()){
                binding.BookNameTextDetails.setText(cursor.getString(bookNameIx))
                binding.BookAboutTextDetails.setText(cursor.getString(bookAboutIx))
                val byteArray=cursor.getBlob(imageIx)
                val bitmap=BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                binding.imageView.setImageBitmap(bitmap)

            }
            cursor.close()
        }


    }
}