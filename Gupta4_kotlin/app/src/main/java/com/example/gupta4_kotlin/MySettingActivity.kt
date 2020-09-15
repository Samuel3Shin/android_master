package com.example.gupta4_kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.activity_community.buttonUpper
import kotlinx.android.synthetic.main.activity_my_setting.*

class MySettingActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_setting)

        buttonUpper.setOnClickListener {
            val popup = PopupMenu(this@MySettingActivity, it)
            popup.setOnMenuItemClickListener(this@MySettingActivity)
            popup.inflate(R.menu.main)
            popup.show()
        }

        mySchoolInfoEditButton.setOnClickListener {
            Toast.makeText(this@MySettingActivity, "학교 수정!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SchoolSearchActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_mealInfo ->  {
                Toast.makeText(this@MySettingActivity, "급식메뉴!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_board ->  {
                Toast.makeText(this@MySettingActivity, "게시판!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, CommunityActivity::class.java)
                startActivity(intent)

                return true
            }

            R.id.menu_myPage ->  {
                Toast.makeText(this@MySettingActivity, "마이 페이지!", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item!!)
    }
}