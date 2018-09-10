package com.example.oguro_shiyu.readnfc

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.Button

class SelectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)

        val buttonRead1 = findViewById(R.id.button1) as Button
        val buttonRead2 = findViewById(R.id.button1) as Button
        val buttonRead3 = findViewById(R.id.button1) as Button

        val getTag = intent.getStringExtra("GET_TAG")

        // start
        buttonRead1.setOnClickListener {
            //val intent = Intent(this, MainActivity::class.java)
            //intent.putExtra(EXTRA_MESSAGE, R.id.message)
            //intent.putExtra(WorkingActivity.GET_TAG,getTag)
            val intent = Intent(this, WorkingActivity::class.java)
            intent.putExtra(EXTRA_MESSAGE, R.id.message)
            startActivity(intent)
        }
    }
}
