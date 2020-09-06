package com.example.anonymoussns

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_write.*
import kotlinx.android.synthetic.main.gupsik_comment.view.*
import kotlinx.android.synthetic.main.gupsik_detail.*


class DetailActivity : AppCompatActivity() {
    val commentList = mutableListOf<Comment>()
    var postId: String? = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gupsik_detail)

        postId = intent.getStringExtra("postId")

        val layoutManager = LinearLayoutManager(this@DetailActivity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = MyAdapter()

        FirebaseDatabase.getInstance().getReference("/Posts/$postId")
            .addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot?.let {
                        val post = it.getValue(Post::class.java)
                        post?.let {
                            Picasso.get().load(it.bgUri)
                            contents.text = post.message
                        }
                    }
                }
            })

        FirebaseDatabase.getInstance().getReference("/Comments/$postId").addChildEventListener(object
            :ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
                error?.toException()?.printStackTrace()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot != null) {
                    val comment = snapshot.getValue(Comment::class.java)
                    comment?.let {
                        val existIndex = commentList.map{it.commentId}.indexOf(it.commentId)
                        commentList.removeAt(existIndex)

                        val prevIndex = commentList.map{it.commentId}.indexOf(previousChildName)
                        commentList.add(prevIndex+1, it)
                        recycler_view.adapter?.notifyItemInserted(prevIndex + 1)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot?.let { snapshot ->
                    val comment = snapshot.getValue(Comment::class.java)
                    comment?.let{ comment ->
                        val prevIndex = commentList.map{it.commentId}.indexOf(previousChildName)
                        commentList[prevIndex + 1] = comment
                        recycler_view.adapter?.notifyItemChanged(prevIndex + 1)
                    }
                }
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot?.let { snapshot ->
                    val comment = snapshot.getValue(Comment::class.java)
                    comment?.let{
                        // 이 부분은 책 내용과 다르게 내가 마음대로 해봄.
                        if(previousChildName == null) {
                            commentList.add(comment)
                            recycler_view.adapter?.notifyItemInserted(commentList.size - 1)
                        } else {
                            val prevIndex = commentList.map{it.commentId}.indexOf(previousChildName)
                            commentList.add(prevIndex + 1, comment)
                            recycler_view.adapter?.notifyItemInserted(prevIndex + 1)
                        }

                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                snapshot?.let {
                    val comment = snapshot.getValue(Comment::class.java)

                    comment?.let {comment ->
                        val existIndex = commentList.map{it.commentId}.indexOf(comment.commentId)
                        commentList.removeAt(existIndex)
                        recycler_view.adapter?.notifyItemRemoved(existIndex)
                    }
                }
            }

        })

        back_button.setOnClickListener {
            finish()
        }

        // hitsCountText 갱신해준다.
        val postRef = FirebaseDatabase.getInstance().getReference("/Posts/$postId")

        postRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val hitsCount = snapshot.child("hitsCount").getValue()
                hitsCountText.setText(hitsCount.toString())

                val writeTime = snapshot.child("writeTime").getValue()
                val date = Utils.getDiffTimeText(writeTime as Long)
                dateTextView.setText(date)
            }
        })

        register_button.setOnClickListener {
            val comment = Comment()
            val newRef = FirebaseDatabase.getInstance().getReference("Comments/$postId").push()

            comment.writeTime = ServerValue.TIMESTAMP
            comment.message = comments.text.toString()
            comment.writerId = getMyId()
            comment.commentId = newRef.key.toString()
            comment.postId = postId!!

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

        }

    }

    fun getMyId(): String {
        return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val commentText = itemView.findViewById<TextView>(R.id.comment_text)
        val commentWriteTime = itemView.dateTextView
        val commentNickname = itemView.nickname
    }

    inner class MyAdapter: RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(this@DetailActivity)
                .inflate(R.layout.gupsik_comment, parent, false))

        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val comment = commentList[position]
            comment?.let {
                holder.commentText.text = comment.message
                holder.commentWriteTime.text = Utils.getDiffTimeText(comment.writeTime as Long)
//                holder.commentNickname.text = comment.nickname
            }
        }

        override fun getItemCount(): Int {
            return commentList.size
        }

    }

    // 점점점 메뉴 옵션 넣은부분. 수정, 삭제 가능.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            R.id.edit -> {
                val intent = Intent(this, WriteActivity::class.java)
                intent.putExtra("mode", "editPost")
                intent.putExtra("postId", postId)
                startActivity(intent)

                return true
            }

            R.id.delete -> {
                val postRef = FirebaseDatabase.getInstance().getReference("/Posts/$postId")
                postRef.removeValue()

                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

}


