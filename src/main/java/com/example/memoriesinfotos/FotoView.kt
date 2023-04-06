package com.example.memoriesinfotos

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.memoriesinfotos.databinding.ActivityFotoViewBinding

class FotoView : AppCompatActivity() {
    var selectedId : Int? = null
    private lateinit var database : SQLiteDatabase
    private lateinit var binding : ActivityFotoViewBinding
    private lateinit var fotoAdapter : FotoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFotoViewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        fotoAdapter = FotoAdapter(ArrayList<Foto>())
        database = this.openOrCreateDatabase("Fotos", MODE_PRIVATE,null)

        val idIntent = intent
        selectedId = idIntent.getIntExtra("id",1)


        val cursor = database.rawQuery("SELECT * FROM fotos WHERE id = ?", arrayOf(selectedId.toString()))
        val imageIx = cursor.getColumnIndex("image")
        while (cursor.moveToNext()){
            val byteArray = cursor.getBlob(imageIx)
            val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
            binding.imageView.setImageBitmap(bitmap)
        }
        cursor.close()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.nav_details){
            val detailIntent = Intent(this@FotoView,FotoDetails::class.java)
            detailIntent.putExtra("id",selectedId)
            startActivity(detailIntent)
        }else{
            val alert = AlertDialog.Builder(this@FotoView)
            alert.setTitle("Alert !")
            alert.setMessage("Are you sure you want to delete the photo ?")
            alert.setPositiveButton("No"){dialog, which ->
                Toast.makeText(this@FotoView,"Not deleted",Toast.LENGTH_SHORT).show()
            }
            alert.setNegativeButton("Yes"){dialog, which ->
                val intent = intent
                val selectedId = intent.getIntExtra("id",1)
                database.delete("Fotos","id = ?", arrayOf(selectedId.toString()))
                val intentBack = Intent(this@FotoView,FotoList::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intentBack)
                fotoAdapter.notifyDataSetChanged()
                Toast.makeText(this@FotoView,"Deleted",Toast.LENGTH_SHORT).show()
            }
            alert.show()
        }
        return super.onOptionsItemSelected(item)
    }
}