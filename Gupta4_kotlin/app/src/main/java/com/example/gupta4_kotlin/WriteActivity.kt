package com.example.gupta4_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_write.*

class WriteActivity : AppCompatActivity() {
    var currentBgPosition = 0

    var mode = "post"
    var postId = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        intent.getStringExtra("mode")?.let {
            mode = intent.getStringExtra("mode")!!
            postId = intent.getStringExtra("postId")!!
        }

        when(mode) {
            "post" -> {
                supportActionBar?.title = "글쓰기"
            }
            "editPost" -> {
                supportActionBar?.title = "글 수정"
            }
            else -> {
                supportActionBar?.title = "댓글 쓰기"
            }
        }


        if(mode=="editPost") {

            val postRef = FirebaseDatabase.getInstance().getReference("/Posts/$postId")

            postRef.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val text = snapshot.child("message").getValue()
                    input.setText(text.toString())
                }
            })
        }

        registerButton.setOnClickListener {
            if(TextUtils.isEmpty(input.text)) {
                Toast.makeText(applicationContext, "메시지를 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(mode=="post") {
                val post = Post()
                val newRef = FirebaseDatabase.getInstance().getReference("Posts").push()
                post.writeTime = ServerValue.TIMESTAMP
                post.message = input.text.toString()
                post.writerId = getMyId()
                post.postId = newRef.key.toString()
                newRef.setValue(post)
                Toast.makeText(applicationContext, "공유 되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            } else if (mode=="editPost") {
                val postRef = FirebaseDatabase.getInstance().getReference("/Posts/$postId")
                postRef.child("message").setValue(input.text.toString())
                finish()

            } else {
                val comment = Comment()
                val newRef = FirebaseDatabase.getInstance().getReference("Comments/$postId").push()

                comment.writeTime = ServerValue.TIMESTAMP
                comment.message = input.text.toString()
                comment.writerId = getMyId()
                comment.commentId = newRef.key.toString()
                comment.postId = postId

                newRef.setValue(comment)

                val postRef = FirebaseDatabase.getInstance().getReference("/Posts/$postId")

                // post의 댓글 개수 불러와서 거기다가 1을 더해준다.
                postRef.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        var commentNum = snapshot.child("commentCount").value as Long
                        postRef.child("commentCount").setValue(commentNum + 1)
                        Log.d("tkandpf", commentNum.toString())
                    }
                })

                Toast.makeText(applicationContext, "공유 되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }

        }

    }

    fun getMyId(): String {
        return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    }

}