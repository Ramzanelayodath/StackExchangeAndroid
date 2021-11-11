package com.example.stackexchange

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stackexchange.Adapter.Adapter
import com.example.stackexchange.Data.DataModel
import com.example.stackexchange.LocalDb.DatabaseHandler
import com.example.stackexchange.Utils.Urls
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import android.net.ConnectivityManager





class MainActivity : AppCompatActivity() {
    var list = ArrayList<DataModel>()
    var recycler_question : RecyclerView? = null
    var layoutManager =  LinearLayoutManager(this,RecyclerView.VERTICAL,false)
    var page = 1
    private var loading = true
    var pastVisiblesItems = 0
    var visibleItemCount:kotlin.Int = 0
    var totalItemCount:kotlin.Int = 0
    var db :DatabaseHandler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = DatabaseHandler(this)
        initViews()
        if (isNetworkConnected()){
            getQuestions(page.toString())
        }else{
            getQuestionsFromdb()
        }



    }

    fun initViews(){
        recycler_question = findViewById<RecyclerView>(R.id.recycler_questions)
        recycler_question!!.layoutManager = layoutManager
        recycler_question!!.adapter = Adapter(list,true)
        recycler_question!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount = layoutManager.childCount
                    totalItemCount = layoutManager.itemCount
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()
                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            loading = false
                            page += 1
                            getQuestions(page.toString())
                            loading = true
                        }
                    }
                }
            }
        })

    }

    fun getQuestions(page:String){
        var pd =  ProgressDialog.show(this, "","Please Wait...", true);
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(Urls().getQuestions(page).toString())
            .build()

        client.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
             pd.dismiss()
            }

            override fun onResponse(call: Call, response: Response) {
                try{
                    var obj = JSONObject(response.body!!.string())
                    var array = obj.getJSONArray("items")
                    for(i in 0 until array.length()){
                        var obj = array.getJSONObject(i)
                        list.add(DataModel(obj.getString("question_id"),obj.getJSONObject("owner").getString("profile_image"),
                            obj.getJSONObject("owner").getString("display_name"),obj.getString("title"),obj.getBoolean("is_answered")))
                    }
                    runOnUiThread {
                        pd.dismiss()
                        db!!.deleteQuestions()
                        db!!.addQuestion(list)
                        recycler_question!!.adapter!!.notifyDataSetChanged()
                    }
                }catch (e:Exception){
                    pd.dismiss()
                    e.toString()
                }
            }
        })


    }

    fun getQuestionsFromdb(){
        list.addAll(db!!.getQuestions as ArrayList<DataModel>)
        recycler_question!!.adapter!!.notifyDataSetChanged()
    }
    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }
}