package com.sam_industry.searchmodule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        val file: File = File()
        val rows: List<List<String>> = csvReader().readAll(getAssets().open("school_info.csv"))

        Log.d("tkandpf", rows.size.toString())
        Log.d("tkandpf", rows.toString())

    }

    var schools = JSONArray()
    val itemMap = mutableMapOf<JSONObject, String>()

}