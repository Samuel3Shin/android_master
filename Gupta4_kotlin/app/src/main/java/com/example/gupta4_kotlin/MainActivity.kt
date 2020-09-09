package com.example.gupta4_kotlin

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader

class MainActivity : AppCompatActivity() {

    var serviceUrl: String = "https://open.neis.go.kr/hub/mealServiceDietInfo"
    var serviceKey: String = "ce674eea5a53470680157d24c26d07a4"

    var district_code = "J10"
    var school_code = "7530184"

    var strUrl = ""
    var result = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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


}


