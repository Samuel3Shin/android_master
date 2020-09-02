package com.example.gupta3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView schoolName;
    TextView mealInfo;
    String getData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        schoolName = (TextView) findViewById(R.id.school_name);
        mealInfo = (TextView) findViewById(R.id.meal_info);

        String serviceUrl = "https://open.neis.go.kr/hub/mealServiceDietInfo";
        String serviceKey = "ce674eea5a53470680157d24c26d07a4";

        String district_code = "J10";
        String school_code = "7530184";
        String which_meal_code = "1";
        String date_code = "20200827";

        String strUrl = serviceUrl + "?KEY=" + serviceKey + "&ATPT_OFCDC_SC_CODE=" + district_code + "&SD_SCHUL_CODE=" + school_code +"&MMEAL_SC_CODE=" +which_meal_code + "&MLSV_YMD=" + date_code;
        DownloadWebContent dwc1 = new DownloadWebContent();
        dwc1.execute(strUrl);
    }


    public class DownloadWebContent extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return (String) downloadByUrl((String) urls[0]);
            } catch (IOException e) {
                Log.d("tkandpf", e.getMessage());
                return "다운로드 실패";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            String school = "";
            String date = "";
            String bld = "";
            String dish = "";
            boolean meal_school = false;
            boolean meal_date = false;
            boolean meal_bld = false;
            boolean meal_dish = false;

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xmlpp = factory.newPullParser();

                xmlpp.setInput(new StringReader(result));

                int eventType = xmlpp.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if(eventType == XmlPullParser.START_TAG) {
                        String tag_name = xmlpp.getName();

                        switch (tag_name) {
                            case "SCHUL_NM":
                                meal_school = true;
                                break;
                            case "MLSV_YMD":
                                meal_date = true;
                                break;
                            case "MMEAL_SC_NM":
                                meal_bld = true;
                                break;
                            case "DDISH_NM":
                                meal_dish = true;
                        }
                    } else if(eventType == XmlPullParser.TEXT) {
                        if(meal_school) {
                            school = xmlpp.getText();
                            schoolName.append(school);
                            meal_school = false;
                        }
                        if(meal_date) {
                            mealInfo.append("---------------------------------\n");
                            date = xmlpp.getText();
                            mealInfo.append("날짜: " + date + "\n");
                            meal_date = false;
                        }
                        if (meal_bld) {
                            bld = xmlpp.getText();
                            mealInfo.append(": " + bld + "\n");
                            meal_bld = false;
                        }

                        if (meal_dish) {
                            dish = xmlpp.getText();
                            mealInfo.append("급식: " + dish + "\n");
                            meal_dish = false;
                        }

                    } else if(eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xmlpp.next();
                }
            } catch (Exception e) {
                mealInfo.setText(e.getMessage());
            }

        }

        public String downloadByUrl(String myurl) throws IOException {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();

                BufferedInputStream buffer = new BufferedInputStream(conn.getInputStream());

                BufferedReader buffer_reader = new BufferedReader(new InputStreamReader(buffer, "UTF-8"));

                String line = null;
                getData = "";
                while((line = buffer_reader.readLine()) != null) {
                    getData += line;
                }
                Log.d("tkandpf", "첫번째" + getData.toString());
                return getData;
            } finally {
                conn.disconnect();
            }
        }
    }

}