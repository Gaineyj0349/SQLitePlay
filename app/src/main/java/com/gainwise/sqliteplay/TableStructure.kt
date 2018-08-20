package com.gainwise.sqliteplay

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_table_structure.*

class TableStructure : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_structure)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sb = StringBuilder()
        sb.append("\n")
        sb.append(resources.getString(R.string.resetTable1))
        sb.append("\n\n\n")
        sb.append(resources.getString(R.string.resetTable2))
        sb.append("\n\n\n")
        sb.append(resources.getString(R.string.resetTable3))
        sb.append("\n\n\n")

        tables_structure.setText(sb.toString())
    }
}
