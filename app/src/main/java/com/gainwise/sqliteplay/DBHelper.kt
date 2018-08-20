package com.gainwise.sqliteplay

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(val c: Context, name: String, cf: SQLiteDatabase.CursorFactory?, v: Int) : SQLiteOpenHelper(c,name, cf, v) {
    override fun onCreate(p0: SQLiteDatabase?) {

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }
}