package com.gainwise.sqliteplay

import android.app.Dialog
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.levitnudi.legacytableview.LegacyTableView
import kotlinx.android.synthetic.main.activity_table_view.*


class TableView : AppCompatActivity() {

    lateinit var db: SQLiteDatabase
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        var helper = DBHelper(this, "DBName", null, 1)
         db = helper.writableDatabase
        QueryFetcher().execute()

    }
    inner class QueryFetcher : AsyncTask<Void, Void, Void>(){
        override fun doInBackground(vararg p0: Void?): Void? {
            getFromDatabase()
            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
            startLoadingDialog()
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            legacy_table_view.setTheme(LegacyTableView.LEVICI)
            legacy_table_view.setTitle(LegacyTableView.readLegacyTitle());
            legacy_table_view.setContent(LegacyTableView.readLegacyContent());
            legacy_table_view.setTablePadding(7);
            legacy_table_view.setZoomEnabled(true);
            legacy_table_view.setShowZoomControls(true);
            legacy_table_view.build();
            stopLoadingDialog()
        }

    }
    fun getFromDatabase(){

        val string = intent.getStringExtra("Query")
        lateinit var cursor: Cursor
        try {
             cursor = db.rawQuery(string, null)
        }catch (e: SQLiteException){
            QueryFetcher().cancel(true)
        }

        Log.i("SQLitePlayI", "count ${cursor.count}")
        Log.i("SQLitePlayI", "columns ${cursor.columnCount}")

        if (cursor.columnCount > 0) {
          val columnNumbers = cursor.columnCount-1
         for(i in 0..columnNumbers){
             LegacyTableView.insertLegacyTitle(cursor.getColumnName(i));
        }
            val rowCount = cursor.count


            cursor.moveToFirst()
           do{
                               for(j in 0..columnNumbers){
                    LegacyTableView.insertLegacyContent(cursor.getString(j))
                }

            }while (cursor.moveToNext())

    }
        cursor.close()

}

    fun startLoadingDialog(){
        dialog = Dialog(this)
        dialog.setContentView(layoutInflater.inflate(R.layout.progressfetch, null))
        dialog.setCancelable(false)
        dialog.show()

    }
    fun stopLoadingDialog(){
        dialog.dismiss()
    }

}
