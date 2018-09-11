package com.example.oguro_shiyu.readnfc

import android.annotation.SuppressLint
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.nfc.NfcAdapter
import android.content.Intent
import android.nfc.tech.NfcF
import android.content.IntentFilter
import android.app.PendingIntent
import android.nfc.Tag
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.oguro_shiyu.readnfc.R.id.message
import android.nfc.tech.Ndef
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 100 // リクエストコード（呼び出しActivity識別用）

    private var intentFiltersArray: Array<IntentFilter>? = null
    private var techListsArray: Array<Array<String>>? = null
    private var mAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private val nfcReader = NfcReader()
    //private val getTag = null

    companion object {
        const val GET_TAG = "com.shiyu.GET_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        // IntentにTagの基本データが入ってくるので取得。
        val getTag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return
        val textStart = findViewById<TextView>(R.id.textViewNFCdata)

        val ndef = NfcF.get(getTag)
        if(ndef != null) {
            ndef.connect()
            if (ndef.isConnected) {
                textStart.text = "タッチしました"
            }
        }else{
            textStart.text= "No Connect ..... "
            return
        }
        /*
        // ここで取得したTagを使ってデータの読み書きを行う。
        val textView = findViewById<TextView>(R.id.textViewNFCdata)
        textView.text = "カードを認識しました"
        val ndef = NfcF.get(getTag)
        if(ndef != null) {
            ndef.connect()
        }else{
            return
        }*/

        /*fun print(text:String) {
            Log.d("print", getTag.toString())
            Toast.makeText(this, "タッチしました", Toast.LENGTH_LONG).show()
        }*/
        val intent = Intent(this, SelectActivity::class.java)
        //intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra("ARG","カードを抜いてください"); // 引数渡しする場合はIntentクラスのputExtraメソッド経由で渡す
        //intent.putExtra(GET_TAG,getTag)
        startActivityForResult(intent,REQUEST_CODE)
    }

    //このActivityに戻って来た時の処理
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        // 注意：superメソッドは呼ぶようにする
        // Activity側のonActivityResultで呼ばないとFragmentのonActivityResultが呼ばれない
        super.onActivityResult(requestCode, resultCode, data)
        val textFinish = findViewById<TextView>(R.id.textViewNFCdata)
        //val getTag2 = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return

        when (requestCode) {
            REQUEST_CODE ->
                // 呼び出し先のActivityから結果を受け取る
                if (resultCode == Activity.RESULT_OK) {
                    val result = data.getStringExtra("RESULT")
                    //Log.d("ログ", result)
                    textFinish.text = result.toString()
                    //val ndef = NfcF.get(getTag2)
                    /*if(ndef != null) {
                        ndef.connect()
                        if (ndef.isConnected) {
                            textFinish.text = "タッチしました"
                        }
                    }else{
                        textFinish.text= "No Connect ..... "
                        return
                    }*/
                }
            else -> {
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onPause() {
        super.onPause()
        mAdapter!!.disableForegroundDispatch(this)
    }
}
