package com.example.memoriesinfotos

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.collections.ArrayList
import com.example.memoriesinfotos.databinding.ActivityFotoListBinding
import java.util.*

class FotoList : AppCompatActivity() {
    private lateinit var binding: ActivityFotoListBinding
    private lateinit var fotoList : ArrayList<Foto>
    private lateinit var fotoAdapter : FotoAdapter
    private lateinit var swipeRefreshLayout : SwipeRefreshLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFotoListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        swipeRefreshLayout = binding.container
        fotoList = ArrayList()
        fotoAdapter = FotoAdapter(fotoList)

        binding.recyclerView.layoutManager = GridLayoutManager(this,2)
        binding.recyclerView.adapter = fotoAdapter

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false

            Collections.shuffle(fotoList, Random(System.currentTimeMillis()))

            fotoAdapter.notifyDataSetChanged()
        }

        try {
            val database = this.openOrCreateDatabase("Fotos", MODE_PRIVATE,null)

            val cursor = database.rawQuery("SELECT * FROM fotos",null)
            val fotoKeyIx = cursor.getColumnIndex("fotokey")
            val idIx = cursor.getColumnIndex("id")
            val imageIx = cursor.getColumnIndex("image")

            while (cursor.moveToNext()){
                val fotoKey = cursor.getString(fotoKeyIx)
                val id = cursor.getInt(idIx)
                val byteArray = cursor.getBlob(imageIx)
                val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                val foto = Foto(id,fotoKey,bitmap)
                fotoList.add(foto)
            }
            fotoAdapter.notifyDataSetChanged()

            cursor.close()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    fun addButton(view: View){
        val intentAdd = Intent(this@FotoList,FotoAdd::class.java)
        startActivity(intentAdd)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.search,menu)
        val searchItem : MenuItem = menu?.findItem(R.id.nav_search)!!
        val searchView : SearchView = searchItem.getActionView() as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener, android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }
    private fun filter(text : String){
        val filteredList : ArrayList<Foto> = ArrayList()

        for (item in fotoList){
            if(item.fotoKey.lowercase().contains(text.lowercase())){
                filteredList.add(item)
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(this,"No data found..",Toast.LENGTH_SHORT).show()
        }else{
            fotoAdapter.filterList(filteredList)
        }
    }
}