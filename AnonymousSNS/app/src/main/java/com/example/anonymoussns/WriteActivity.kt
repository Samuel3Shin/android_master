package com.example.anonymoussns

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.recyclerView
import kotlinx.android.synthetic.main.activity_write.*
import kotlinx.android.synthetic.main.card_background.view.*
import kotlinx.android.synthetic.main.card_post.*
import kotlinx.android.synthetic.main.card_post.contentsText

class WriteActivity : AppCompatActivity() {
    var currentBgPosition = 0

    var mode = "post"
    var postId = ""

    val bgList = mutableListOf(
        "android.resource://com.example.anonymoussns/drawable/default_bg"
        , "android.resource://com.example.anonymoussns/drawable/bg2"
        , "android.resource://com.example.anonymoussns/drawable/bg3"
        , "android.resource://com.example.anonymoussns/drawable/bg4"
        , "android.resource://com.example.anonymoussns/drawable/bg5"
        , "android.resource://com.example.anonymoussns/drawable/bg6"
        , "android.resource://com.example.anonymoussns/drawable/bg7"
        , "android.resource://com.example.anonymoussns/drawable/bg8"
        , "android.resource://com.example.anonymoussns/drawable/bg9"
    )

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        intent.getStringExtra("mode")?.let {
            mode = intent.getStringExtra("mode")!!
            postId = intent.getStringExtra("postId")!!
        }

        supportActionBar?.title = if(mode == "post") "글쓰기" else "댓글쓰기"

        val layoutManager = LinearLayoutManager(this@WriteActivity)

        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = MyAdapter()

        sendButton.setOnClickListener {
            if(TextUtils.isEmpty(input.text)) {
                Toast.makeText(applicationContext, "메시지를 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(mode=="post") {
                val post = Post()
                val newRef = FirebaseDatabase.getInstance().getReference("Posts").push()
                post.writeTime = ServerValue.TIMESTAMP
                post.bgUri = bgList[currentBgPosition]
                post.message = input.text.toString()
                post.writerId = getMyId()
                post.postId = newRef.key.toString()
                newRef.setValue(post)
                Toast.makeText(applicationContext, "공유 되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                val comment = Comment()
                val newRef = FirebaseDatabase.getInstance().getReference("Comments/$postId").push()

                comment.writeTime = ServerValue.TIMESTAMP
                comment.bgUri = bgList[currentBgPosition]
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

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.imageView
    }

    inner class MyAdapter : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(this@WriteActivity).inflate(R.layout.card_background, parent, false))
        }

        override fun getItemCount(): Int {
            return bgList.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            Picasso.get()
                .load(Uri.parse(bgList[position]))
                .fit()
                .centerCrop()
                .into(holder.imageView)

            holder.itemView.setOnClickListener {
                currentBgPosition = position

                Picasso.get()
                    .load(Uri.parse(bgList[position]))
                    .fit()
                    .centerCrop()
                    .into(writeBackground)
            }
        }

    }
}