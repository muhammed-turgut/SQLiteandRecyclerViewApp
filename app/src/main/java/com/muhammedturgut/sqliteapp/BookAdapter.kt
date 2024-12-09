package com.muhammedturgut.sqliteapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muhammedturgut.sqliteapp.databinding.RecyclerRowBinding

class BookAdapter(val bookListe:ArrayList<BookTime>):RecyclerView.Adapter<BookAdapter.BookHolder>() {
    class BookHolder(val binding:RecyclerRowBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder {
        val binding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BookHolder(binding)
    }

    override fun getItemCount(): Int {
        return bookListe.size
    }

    override fun onBindViewHolder(holder: BookHolder, position: Int) {
        holder.binding.bookNameTextRow.text=bookListe.get(position).nameBook
        holder.binding.bookAboutTextRow.text=bookListe.get(position).bookAbout

        holder.itemView.setOnClickListener {
            val intent=Intent(holder.itemView.context,DetailsActivtiy::class.java)
            intent.putExtra("info","old")
            intent.putExtra("id",bookListe.get(position).id)
            holder.itemView.context.startActivity(intent)
        }
    }
}