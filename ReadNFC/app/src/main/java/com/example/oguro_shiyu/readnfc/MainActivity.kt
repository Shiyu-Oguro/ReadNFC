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
import android.content.Intent.getIntent
import android.nfc.Tag
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.oguro_shiyu.readnfc.R.id.message
import android.nfc.tech.Ndef
import android.os.Handler
import android.provider.Contacts
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 100 // リクエストコード（呼び出しActivity識別用）

    private var intentFiltersArray: Array<IntentFilter>? = null
    private var techListsArray: Array<Array<String>>? = null
    private var mAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private val nfcReader = NfcReader()

    // 1度だけ代入するものはvalを使う
    val handler = Handler()
    // 繰り返し代入するためvarを使う
    var timeValue = 0

    //private val getTag = null

    companion object {
        const val GET_TAG = "com.shiyu.GET_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //Log.d("print","START !!!1")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //別のアプリケーションにPendingIntentを渡すと、同じアクセス権を与えられる
        pendingIntent = PendingIntent.getActivity(
                this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)

        //NDEFペイロードを持つタグが検出されたときにアクティビティを開始する
        val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        //val ndef = IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)
        val textConnect = findViewById<TextView>(R.id.textConnect)

        //照合する新しいIntentデータ型を追加
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

        // 1秒ごと(?)に処理を実行
        val runnable = object : Runnable {
            override fun run() {
                Log.d("print","Timer now ..........")
                timeValue++
                // 処理
                //textConnect.text = "Connect Now.."
                val getTag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return
                val ndef2 = NfcF.get(getTag)
                if(ndef2 != null) {
                    ndef2.connect()
                    if (ndef2.isConnected) {
                        textConnect.text = "Connect is Succesfull.. !!!"
                    }
                }else{
                    textConnect.text= "No Connect ..... "
                }
                handler.postDelayed(this, 100)
            }
        }
        handler.post(runnable)

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
                textStart.text = getTag.toString()
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
        intent.putExtra("ARG","aaa"); // 引数渡しする場合はIntentクラスのputExtraメソッド経由で渡す
        //intent.putExtra(GET_TAG,getTag)
        startActivityForResult(intent,REQUEST_CODE)
    }

    //このActivityに戻って来た時の処理
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        // 注意：superメソッドは呼ぶようにする
        // Activity側のonActivityResultで呼ばないとFragmentのonActivityResultが呼ばれない
        super.onActivityResult(requestCode, resultCode, data)
        val textFinish = findViewById<TextView>(R.id.textViewNFCdata)
        //val getTag2 = data.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return
        //textFinish.text = "終わりです！！！"

        when (requestCode) {
            REQUEST_CODE ->
                // 呼び出し先のActivityから結果を受け取る
                if (resultCode == Activity.RESULT_OK) {
                    val result = data.getStringExtra("RESULT")
                    //Log.d("ログ", result)
                    //textFinish.text = result.toString()
                    textFinish.text = "終わりです！"
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

    //どうすればActiveに検出できるか
    //どうすれば一定時間間隔で実行できるか

    @SuppressLint("MissingPermission")
    override fun onPause() {
        super.onPause()
        mAdapter!!.disableForegroundDispatch(this)
    }
}
