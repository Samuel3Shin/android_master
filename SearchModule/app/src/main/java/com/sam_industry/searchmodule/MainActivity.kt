package com.sam_industry.searchmodule

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.search_bar.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class MainActivity : AppCompatActivity() {
    var schools = JSONArray()
    val schoolCodeMap = mutableMapOf<String, String>()
    val districtCodeMap = mutableMapOf<String, String>()

    val districtCodeKey = "district"
    val schoolCodeKey = "schoolName"
    val preference by lazy {getSharedPreferences("mainActivity", Context.MODE_PRIVATE)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        val file: File = File()
        val rows: List<List<String>> = csvReader().readAll(getAssets().open("school_info.csv"))

        Log.d("tkandpf", rows.size.toString())
        Log.d("tkandpf", rows.toString())

        for (i in 0 until rows.size) {
            var str: String = rows.get(i).toString()
            str = str.replace("[","")
            str = str.replace("]","")

            var strLst = str.split(",")


            schoolCodeMap.put(strLst.get(2).trim(), strLst.get(1).trim())
            districtCodeMap.put(strLst.get(2).trim(), strLst.get(0).trim())

//            Log.d("tkandpf", str.split(",")[0].trim())
//            Log.d("tkandpf", str.split(",")[1].trim())
//            schoolCodeMap.put(rows.get(1).toString(), rows.get(0).toString())
        }

        searchBar.imageView.setOnClickListener {
            val keyword = searchBar.autoCompleteTextView.text.toString()
            Toast.makeText(this@MainActivity, keyword, Toast.LENGTH_SHORT).show()
            Toast.makeText(this@MainActivity, schoolCodeMap.size.toString(), Toast.LENGTH_SHORT).show()
            Toast.makeText(this@MainActivity, districtCodeMap.size.toString(), Toast.LENGTH_SHORT).show()


            preference.edit().putString(schoolCodeKey, schoolCodeMap.get(keyword)).apply()
            preference.edit().putString(districtCodeKey, districtCodeMap.get(keyword)).apply()


            Log.d("tkandpf", schoolCodeMap.get(keyword).toString())

            Log.d("tkandpf", districtCodeMap.get(keyword).toString())
            Log.d("tkandpf", preference.getString(districtCodeKey, "").toString())
            Log.d("tkandpf", preference.getString(schoolCodeKey, "").toString())

            }
            searchBar.autoCompleteTextView.setText("")

    }



}
