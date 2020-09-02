package com.example.gupta4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    TextView text1;
    String date="";
    String ii1, ii2, ii3, date3;

    static TextView schoolName;
    static TextView mealInfo;
    static String getData;

    private static final String TAG = "tkandpf";
    private CalendarView mCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        schoolName = (TextView) findViewById(R.id.school_name);
        mealInfo = (TextView) findViewById(R.id.meal_info);

        text1 = (TextView) findViewById(R.id.textView);

        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                date = i + "/" + (i1+1) + "/" + i2;
                ii1=String.valueOf(i);
                ii2=String.valueOf(i1+1);
                ii3=String.valueOf(i2);
                if(i1+1 < 10) {
                    ii2 = "0" + ii2;
                }
                if(i2 < 10) {
                    ii3 = "0" + ii3;
                }
                date3=ii1+ii2+ii3;

                text1.setText(date);

                String serviceUrl = "https://open.neis.go.kr/hub/mealServiceDietInfo";
                String serviceKey = "ce674eea5a53470680157d24c26d07a4";

                String district_code = "J10";
                String school_code = "7530184";

                // 아침, 점심, 저녁 따로 호출하려면 아래의 코드를 추가해야함.
//                String which_meal_code = "1";
                String date_code = date3;

                String strUrl = serviceUrl + "?KEY=" + serviceKey + "&ATPT_OFCDC_SC_CODE=" + district_code + "&SD_SCHUL_CODE=" + school_code +"&MMEAL_SC_CODE=" + "&MLSV_YMD=" + date_code;
                DownloadWebContent dwc1 = new DownloadWebContent();
                dwc1.execute(strUrl);
            }
        });

    }
}