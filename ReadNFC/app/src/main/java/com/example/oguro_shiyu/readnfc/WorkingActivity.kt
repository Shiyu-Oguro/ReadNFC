package com.example.oguro_shiyu.readnfc

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.nfc.tech.NfcF
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.oguro_shiyu.readnfc.R.id.textWorking
import java.io.IOException
import android.R.attr.tag
import android.nfc.NdefMessage
import android.os.Parcelable


class WorkingActivity : AppCompatActivity() {

    private var intentFiltersArray: Array<IntentFilter>? = null
    private var techListsArray: Array<Array<String>>? = null
    private var mAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private val nfcReader = NfcReader()
    private val touchFlag = false

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_working)
        val textWorkingMain = findViewById(R.id.textWorking) as TextView
        //textWorking.text

        pendingIntent = PendingIntent.getActivity(
                this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)

        val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)

        try {
            ndef.addDataType("text/plain")
        } catch (e: IntentFilter.MalformedMimeTypeException) {
            throw RuntimeException("fail", e)
        }

        intentFiltersArray = arrayOf(ndef)

        // FelicaはNFC-TypeFなのでNfcFのみ指定でOK
        techListsArray = arrayOf(arrayOf(NfcF::class.java.name))

        // NfcAdapterを取得
        mAdapter = NfcAdapter.getDefaultAdapter(applicationContext)

    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        // NFCの読み込みを有効化
        mAdapter!!.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray)
    }

    override fun onNewIntent(intent: Intent) {

        //setIntent(intent)
        val textWorking = findViewById(R.id.textWorking) as TextView
        // IntentにTagの基本データが入ってくるので取得。
        val getTag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return

        //Toast.makeText(this, "タッチしています", Toast.LENGTH_LONG).show()
        //textWorking.text= "ID: " + getTag.toString()

        //getIntent()
        val ndef = NfcF.get(getTag)
        //textWorking.text = ndef.toString()
        if(ndef != null) {
            ndef.connect()
            while (ndef.isConnected) {
                textWorking.text = "タッチしています"
            }
            textWorking.text = "離れました"
        }else{
            textWorking.text= "No Connect ..... "
            return
        }

         /*try {
            ndef.connect()
             if(ndef.isConnected){
                 textWorking.text="Connect"
             }
            /*while (ndef.isConnected) {
                Toast.makeText(this, "タッチしています", Toast.LENGTH_LONG).show()
                textWorking.setText("タッチしています")
            }

            Toast.makeText(this, "タグが離れました", Toast.LENGTH_LONG).show()
            textWorking.setText("タグが離れました")
            */
             /*val intent = Intent(this, FinishActivity::class.java)
            intent.putExtra(EXTRA_MESSAGE, R.id.message)
            startActivity(intent)*/
        } catch (e: IOException) {
            Toast.makeText(this, "エラー", Toast.LENGTH_LONG).show()
             textWorking.setText("エラー")
             if (ndef.isConnected) {
                 ndef.close()
             }
         }*/

    }

    @SuppressLint("MissingPermission")
    override fun onPause() {
        super.onPause()
        mAdapter!!.disableForegroundDispatch(this)
    }
}