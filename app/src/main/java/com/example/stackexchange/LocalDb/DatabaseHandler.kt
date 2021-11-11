package com.example.stackexchange.LocalDb

import android.database.sqlite.SQLiteDatabase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

import android.database.sqlite.SQLiteOpenHelper
import com.example.stackexchange.Data.DataModel
import java.lang.String


class DatabaseHandler(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    // Creating Tables
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_QUESTIONS_TABLE = ("CREATE TABLE " + TABLE_QUESTIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_QUESTION + " TEXT" + ")")
        db.execSQL(CREATE_QUESTIONS_TABLE)
    }

    // Upgrading database
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS)

        // Create tables again
        onCreate(db)
    }

    fun addQuestion(questions: List<DataModel>) {
        val db = this.writableDatabase
         for (i in questions.indices){
             val values = ContentValues()
             values.put(KEY_NAME, questions[i].name )
             values.put(KEY_QUESTION,questions[i].body)
             db.insert(TABLE_QUESTIONS, null, values)
         }
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
    }


    val getQuestions: List<Any>
        get() {
            val questionList: MutableList<DataModel> = ArrayList<DataModel>()
            // Select All Query
            val selectQuery = "SELECT  * FROM $TABLE_QUESTIONS"
            val db = this.writableDatabase
            val cursor: Cursor = db.rawQuery(selectQuery, null)

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    val questions = DataModel("","","","",false)
                    questions.id = cursor.getString(0)
                    questions.name = cursor.getString(1)
                    questions.body = cursor.getString(2)
                    // Adding contact to list
                    questionList.add(questions)
                } while (cursor.moveToNext())
            }

            // return contact list
            return questionList
        }

    fun deleteQuestions(){
        val db = this.writableDatabase
        db.execSQL("delete from $TABLE_QUESTIONS");
        db.close()
    }




    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "db_questions"
        private const val TABLE_QUESTIONS = "table_questions"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_QUESTION = "question"
    }
}