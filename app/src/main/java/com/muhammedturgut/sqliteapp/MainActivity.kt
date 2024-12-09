package com.muhammedturgut.sqliteapp

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.muhammedturgut.sqliteapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bookList:ArrayList<BookTime>
    private lateinit var bookAdapter: BookAdapter
    private lateinit var database: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       bookList=ArrayList<BookTime>()
        bookAdapter= BookAdapter(bookList)
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        binding.recyclerView.adapter=bookAdapter

        try {

            database=this.openOrCreateDatabase("Book", MODE_PRIVATE,null)
            val cursor=database.rawQuery("SELECT *FROM book",null)
            val bookNameIx=cursor.getColumnIndex("bookName")
            val booKAboutIx=cursor.getColumnIndex("bookAbout")
            val idIx=cursor.getColumnIndex("id")

            while(cursor.moveToNext()){
                val bookName=cursor.getString(bookNameIx)
                val bookAbout=cursor.getString(booKAboutIx)
                val id=cursor.getInt(idIx)
                val bookTime=BookTime(bookName,bookAbout,id)
                bookList.add(bookTime)
            }
            bookAdapter.notifyDataSetChanged()
            cursor.close()

        }catch (e:Exception){
            e.printStackTrace()
        }



    }
    fun addButton(view: View){
        val intent=Intent(this@MainActivity,BookActivity::class.java)
        intent.putExtra("info","detay")
        startActivity(intent)
    }


}