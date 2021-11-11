package com.example.stackexchange.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.stackexchange.AnswerActivity
import com.example.stackexchange.Data.DataModel
import com.example.stackexchange.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class Adapter(var list:ArrayList<DataModel>,isfromQuestion : Boolean)  :
    RecyclerView.Adapter<Adapter.ViewHolder>() {
    var ctx : Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row, parent, false)
        ctx = parent!!.context
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: Adapter.ViewHolder, position: Int) {
        if(list[position].avatar !="") {
            Picasso.get().load(list[position].avatar).into(holder.img_avatar)
        }
        holder.txt_name.text = list[position].name
        holder.txt_question.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml( list[position].body, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml( list[position].body)
        }
        holder.ln.setOnClickListener {
           if(list[position].isAnswered){
               (ctx as Activity).startActivity(
                   Intent(ctx,AnswerActivity::class.java).putExtra("qid",list[position].id)
               )
           }else{
               Toast.makeText(ctx,"No Answer",Toast.LENGTH_SHORT).show()
           }
        }

       // holder.txt_question.text = list[position].body
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    inner class ViewHolder(v: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        var img_avatar = v.findViewById<CircleImageView>(R.id.img_avatar)
        var txt_name = v.findViewById<TextView>(R.id.txt_name)
        var txt_question = v.findViewById<TextView>(R.id.txt_body)
        var ln = v.findViewById<LinearLayout>(R.id.ln)
    }
}