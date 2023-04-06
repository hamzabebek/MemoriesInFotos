package com.example.memoriesinfotos

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.memoriesinfotos.databinding.ActivityFotoDetailsBinding

class FotoDetails : AppCompatActivity() {
    private lateinit var binding: ActivityFotoDetailsBinding
    private lateinit var database: SQLiteDatabase
    private lateinit var fotoAdapter : FotoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFotoDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        fotoAdapter = FotoAdapter(ArrayList<Foto>())
        database = this.openOrCreateDatabase("Fotos", MODE_PRIVATE,null)

        val intent = intent
        val selectedId = intent.getIntExtra("id",1)

        val cursor = database.rawQuery("SELECT * FROM fotos WHERE id = ?",arrayOf(selectedId.toString()))
        val fotoKeyIx = cursor.getColumnIndex("fotokey")
        val fotoDetailIx = cursor.getColumnIndex("fotodetail")

        while (cursor.moveToNext()){
            binding.detailText.setText(cursor.getString(fotoDetailIx).toString())
            binding.keyText.setText(cursor.getString(fotoKeyIx).toString())
            fotoAdapter.notifyDataSetChanged()


        }
        cursor.close()
    }
}