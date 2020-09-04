package com.example.anonymoussns

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity() {
    val commentList = mutableListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val postId = intent.getStringExtra("postId")

        val layoutManager = LinearLayoutManager(this@DetailActivity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = MyAdapter()

        FirebaseDatabase.getInstance().getReference("/Posts/$postId")
            .addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot?.let {
                        val post = it.getValue(Post::class.java)
                        post?.let {
                            Picasso.get().load(it.bgUri)
                            contentsText.text = post.message
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
                        recyclerView.adapter?.notifyItemInserted(prevIndex + 1)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot?.let { snapshot ->
                    val comment = snapshot.getValue(Comment::class.java)
                    comment?.let{ comment ->
                        val prevIndex = commentList.map{it.commentId}.indexOf(previousChildName)
                        commentList[prevIndex + 1] = comment
                        recyclerView.adapter?.notifyItemChanged(prevIndex + 1)
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
                            recyclerView.adapter?.notifyItemInserted(commentList.size - 1)
                        } else {
                            val prevIndex = commentList.map{it.commentId}.indexOf(previousChildName)
                            commentList.add(prevIndex + 1, comment)
                            recyclerView.adapter?.notifyItemInserted(prevIndex + 1)
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
                        recyclerView.adapter?.notifyItemRemoved(existIndex)
                    }
                }
            }

        })

        floatingActionButton.setOnClickListener {
            val intent = Intent(this@DetailActivity, WriteActivity::class.java)
            intent.putExtra("mode", "comment")
            intent.putExtra("postId", postId)
            startActivity(intent)
        }
    }



    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.background)
        val commentText = itemView.findViewById<TextView>(R.id.commentText)
    }

    inner class MyAdapter: RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(this@DetailActivity)
                .inflate(R.layout.card_comment, parent, false))

        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val comment = commentList[position]
            comment?.let {
                Picasso.get()
                    .load(Uri.parse(comment.bgUri))
                    .fit()
                    .centerCrop()
                    .into(holder.imageView)
                holder.commentText.text = comment.message
            }
        }

        override fun getItemCount(): Int {
            return commentList.size
        }

    }

    fun showPopup(v: View?) {
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.getMenuInflater()
        inflater.inflate(R.menu.mymenu, popup.getMenu())
        popup.show()
    }


}