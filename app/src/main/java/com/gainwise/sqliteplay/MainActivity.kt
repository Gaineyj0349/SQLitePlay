package com.gainwise.sqliteplay

import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.gainwise.seed.ExtensionFunctions.hideSoftKeyboard
import com.gainwise.seed.ExtensionFunctions.seedStartActivityWithStringExtra
import com.gainwise.seed.ExtensionFunctions.seedStartNewActivityBasic
import com.gainwise.seed.Vitals.FirstRunHandler
import com.gainwise.seed.Vitals.FirstRunner
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import osmandroid.project_basics.Task



class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var db: SQLiteDatabase
    var error: String? = null
    lateinit var dialog: Dialog
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        var helper = DBHelper(this, "DBName", null, 1)
        db = helper.writableDatabase
        var myFRHelper = FirstRunHandler(this, MyFirstRunner())


        fab.setOnClickListener { view ->
            val query = query_et.text.toString().trim()
            if(query.isNullOrBlank() || query.isNullOrEmpty()){
                inflateQueryErrorDialog("Query must not be empty")
            }else {
                if (isValidSQLiteQuery(query)) {
                    if (query.startsWith("select", true)) {
                        seedStartActivityWithStringExtra<TableView>("Query", query)
                    } else {
                    }
                } else {
                    inflateQueryErrorDialog(error)
                }
            }

        }
        var actionBarDrawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            override fun onDrawerClosed(drawerView: View) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView)
                hideSoftKeyboard(this@MainActivity)
            }

            override fun onDrawerOpened(drawerView: View) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView)

            }
        }

        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

    }



    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        when (item.itemId) {

            R.id._office -> {
                if(isValidSQLiteQuery("SELECT * FROM _office")){
                    seedStartActivityWithStringExtra<TableView>("Query", "SELECT * FROM _office")
                }else{
                    inflateQueryErrorDialog(error)
                }
            }
            R.id._reset -> {
                (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                        .clearApplicationUserData()
            }
            R.id._clients -> {
                if(isValidSQLiteQuery("SELECT * FROM _clients")){
                    seedStartActivityWithStringExtra<TableView>("Query", "SELECT * FROM _clients")
                }else{
                    inflateQueryErrorDialog(error)
                }

            }
            R.id._vehicles-> {
                if(isValidSQLiteQuery("SELECT * FROM _vehicles")){
                    seedStartActivityWithStringExtra<TableView>("Query", "SELECT * FROM _vehicles")
                }else{
                    inflateQueryErrorDialog(error)
                }
            }
            R.id._overview -> {
              seedStartNewActivityBasic<TableStructure>()
            }

            R.id.nav_share -> {
                Task.ShareApp(this, "com.gainwise.sqliteplay", "SQLite", "Practice simple SQLite queries!")
            }
            R.id.nav_rate -> {
            Task.RateApp(this,"com.gainwise.sqliteplay" )
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    inner class MyFirstRunner : FirstRunner {
        override fun execute() {

            TableResetter().execute()

        }

    }

    inner class TableResetter : AsyncTask<Context, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            startLoadingDialog()
        }
        override fun doInBackground(vararg p0: Context?): String {
            db.execSQL(resources.getString(R.string.dropTable1))
            db.execSQL(resources.getString(R.string.resetTable1))
            db.execSQL(resources.getString(R.string.dropTable2))
            db.execSQL(resources.getString(R.string.resetTable2))
            db.execSQL(resources.getString(R.string.dropTable3))
            db.execSQL(resources.getString(R.string.resetTable3))
            val sb = StringBuilder()

            sb.append(resources.getString(R.string.insertDataTable1))
            sb.append(resources.getString(R.string.insertDataTable2))
            sb.append(resources.getString(R.string.insertDataTable3))
            val insertString = sb.toString()

            val parts = insertString.split(";")
            val count = parts.size - 2
            progressBar.setMax(count)
            Log.i("JOSH","size: $count")
            for (i in 0..count){

                progressBar.setProgress(i)
                db.execSQL(parts[i])
                Log.i("JOSH",parts[i])
            }
            return "Success"
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            Toast.makeText(Outer@ this@MainActivity, result , Toast.LENGTH_LONG).show()
           stopLoadingDialog()
        }

    }
    fun isValidSQLiteQuery(query: String): Boolean{
        var valid = false
        db.beginTransaction();
        try {
            if(query.startsWith("select",true)) {
                var c: Cursor = db.rawQuery(query, null)
            }else{
                db.execSQL(query)
            }
            valid = true
            db.setTransactionSuccessful();
            query_et.setText("")
        } catch (e: SQLiteException){
            error = e.message
        }finally {
            db.endTransaction()
        }
        return valid
    }

    private fun inflateQueryErrorDialog(message: String?) {
        val alertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle("Query Error")
        alertDialog.setMessage(message)
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
        alertDialog.show()
    }

    fun startLoadingDialog(){
        dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.progress, null)
        progressBar = view.findViewById<ProgressBar>(R.id.progressbar)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.show()

    }
    fun stopLoadingDialog(){
        dialog.dismiss()
    }
}
