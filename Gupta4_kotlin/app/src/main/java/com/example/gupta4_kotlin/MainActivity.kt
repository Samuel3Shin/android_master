package com.example.gupta4_kotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader

class MainActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    var serviceUrl: String = "https://open.neis.go.kr/hub/mealServiceDietInfo"
    var serviceKey: String = "ce674eea5a53470680157d24c26d07a4"

    val preference by lazy {getSharedPreferences("mainActivity", Context.MODE_PRIVATE)}

    var district_code = "J10"
    var school_code = "7530184"


    var strUrl = ""
    var result = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // shared preference에서 교육청 코드와 학교 코드 불러오는데, 만약에 없으면 그냥 위의 default값(안산동산고등학교 코드) 내뱉음.
        district_code = preference.getString("districtCode", district_code).toString()
        school_code = preference.getString("schoolCode", school_code).toString()

        Log.d("tkandpf", district_code)
        Log.d("tkandpf", school_code)

        buttonUpper.setOnClickListener {
            val popup = PopupMenu(this@MainActivity, it)
            popup.setOnMenuItemClickListener(this@MainActivity)
            popup.inflate(R.menu.main)
            popup.show()
        }

        calendarView.setOnDateChangeListener { calendarView, i, i1, i2 ->

            var date = i.toString() + "/" + (i1 + 1) + "/" + i2
            var ii1 = i.toString()
            var ii2 = (i1 + 1).toString()
            var ii3 = i2.toString()
            if (i1 + 1 < 10) {
                ii2 = "0$ii2"
            }
            if (i2 < 10) {
                ii3 = "0$ii3"
            }
            var date3 = ii1 + ii2 + ii3

            textView.setText(date)

            // 아침, 점심, 저녁 따로 호출하려면 아래의 코드를 추가해야함.
            // String which_meal_code = "1";
            val date_code = date3
            strUrl = "$serviceUrl?KEY=$serviceKey&ATPT_OFCDC_SC_CODE=$district_code&SD_SCHUL_CODE=$school_code&MMEAL_SC_CODE=&MLSV_YMD=$date_code"
            run()
            parse(result)

        }
    }

    val client = OkHttpClient()

    fun run() {
        val request = Request.Builder()
                .url(strUrl)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }

                    var resStr = response.body!!.string()

                    result = resStr
                }
            }
        })
    }

    fun parse(result: String) {
        var school = ""
        var date = ""
        var bld = ""
        var dish = ""
        var meal_school = false
        var meal_date = false
        var meal_bld = false
        var meal_dish = false

        schoolName.setText("")
        mealInfo.setText("")

        val factory = XmlPullParserFactory.newInstance()
        val xmlpp = factory.newPullParser()

        xmlpp.setInput(StringReader(result))


        var eventType = xmlpp.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
            } else if (eventType == XmlPullParser.START_TAG) {
                val tag_name = xmlpp.name
                when (tag_name) {
                    "SCHUL_NM" -> meal_school = true
                    "MLSV_YMD" -> {
                    }
                    "MMEAL_SC_NM" -> meal_bld = true
                    "DDISH_NM" -> meal_dish = true
                }
            } else if (eventType == XmlPullParser.TEXT) {
                if (meal_school) {
                    school = xmlpp.text
                    schoolName.setText(school + "\n")
                    meal_school = false
                }
                if (meal_date) {
                    date = xmlpp.text
                    mealInfo.append("날짜: $date\n")
                    meal_date = false
                }
                if (meal_bld) {
                    bld = xmlpp.text
                    mealInfo.append(bld + "\n")
                    meal_bld = false
                }
                if (meal_dish) {
                    dish = xmlpp.text
                    mealInfo.append("급식: $dish\n")
                    meal_dish = false
                    mealInfo.append("---------------------------------\n\n")
                }
            } else if (eventType == XmlPullParser.END_TAG) {

            }
            eventType = xmlpp.next()
        }

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_mealInfo ->  {
                Toast.makeText(this@MainActivity, "급식메뉴!", Toast.LENGTH_SHORT).show()
                return true
            }

            R.id.menu_board ->  {
                Toast.makeText(this@MainActivity, "게시판!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, CommunityActivity::class.java)
                startActivity(intent)

                return true
            }

            R.id.menu_myPage ->  {
                Toast.makeText(this@MainActivity, "마이 페이지!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MySettingActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item!!)
    }

}


