package com.example.stackexchange

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stackexchange.Adapter.Adapter
import com.example.stackexchange.Data.DataModel
import com.example.stackexchange.Utils.Urls
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class AnswerActivity : AppCompatActivity() {
    var list = ArrayList<DataModel>()
    var recycler_answer : RecyclerView? = null
    var layoutManager =  LinearLayoutManager(this, RecyclerView.VERTICAL,false)
    var page = 1
    private var loading = true
    var pastVisiblesItems = 0
    var visibleItemCount:kotlin.Int = 0
    var totalItemCount:kotlin.Int = 0
    var  qid = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        qid = intent.getStringExtra("qid")!!
        setContentView(R.layout.activity_answer)
        initViews()
        getAnswer(qid,page.toString())
    }

    fun initViews(){
        recycler_answer = findViewById<RecyclerView>(R.id.recycler_answer)
        recycler_answer!!.layoutManager = layoutManager
        recycler_answer!!.adapter = Adapter(list,false)
        recycler_answer!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount = layoutManager.childCount
                    totalItemCount = layoutManager.itemCount
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()
                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            loading = false
                            page += 1
                            getAnswer(page.toString(),qid)
                            loading = true
                        }
                    }
                }
            }
        })

    }

    fun getAnswer(qid:String,page:String){
        var pd =  ProgressDialog.show(this, "","Please Wait...", true);
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(Urls().getAnswers(qid,page).toString())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                pd.dismiss()
            }

            override fun onResponse(call: Call, response: Response) {
                try{
                    var obj = JSONObject(response.body!!.string())
                    var array = obj.getJSONArray("items")
                    for(i in 0 until array.length()){
                        var obj = array.getJSONObject(i)
                        list.add(DataModel(obj.getString("answer_id"),obj.getJSONObject("owner").getString("profile_image"),
                            obj.getJSONObject("owner").getString("display_name"),obj.getString("body"),true))
                    }

                    runOnUiThread {
                        pd.dismiss()
                        recycler_answer!!.adapter!!.notifyDataSetChanged()
                    }
                }catch (e:Exception){
                    pd.dismiss()
                    e.toString()
                }
            }
        })
    }
}