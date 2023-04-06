package com.example.memoriesinfotos

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memoriesinfotos.databinding.RecyclerRowBinding

class FotoAdapter (var fotoList: ArrayList<Foto>) : RecyclerView.Adapter<FotoAdapter.FotoHolder>(){
    class FotoHolder(val binding : RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FotoHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FotoHolder(binding)
    }
    fun filterList(filterlist:ArrayList<Foto>){
        fotoList = filterlist
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return fotoList.size
    }

    override fun onBindViewHolder(holder: FotoHolder, position: Int) {
        holder.binding.recyclerViewFotoView.setImageBitmap(fotoList.get(position).image)
        holder.itemView.setOnClickListener {
            val intentFoto = Intent(holder.itemView.context,FotoView::class.java)
            intentFoto.putExtra("id",fotoList.get(position).id)
            holder.itemView.context.startActivity(intentFoto)
        }
    }
}