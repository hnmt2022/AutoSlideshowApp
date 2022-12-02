package jp.techacademy.hiromi.nomoto.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity: AppCompatActivity() {

    //private var mTimerSec = 0.0
    private var mTimer: Timer? = null
    private var mTimerRunning = false

    private val PERMISSIONS_REQUEST_CODE = 100


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()

                }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo()

            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )

            }
        }
    }


    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(

            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, //
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )

        cursor!!.moveToFirst()
        val fieldIndex =
            cursor.getColumnIndex(MediaStore.Images.Media._ID)//cursor.getColumnIndex()で現在cursorが指しているデータの中から画像のIDがセットされている位置を取得
        val id = cursor.getLong(fieldIndex)//cursor.getLong()で画像のIDを取得,カーソルクラスのgetLongメソッド
        val imageUri = ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            id
        )
        imageView.setImageURI(imageUri)

        forward_button.setOnClickListener {
            //cursor!!.moveToFirst() は検索結果の最初のデータを指します
            //検索結果が1つも無ければfalseを返すのでif文の{}には入らずすぐにcloseメソッドを呼び出します
            if (cursor!!.moveToNext()) {

                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex =
                    cursor.getColumnIndex(MediaStore.Images.Media._ID)//cursor.getColumnIndex()で現在cursorが指しているデータの中から画像のIDがセットされている位置を取得
                val id = cursor.getLong(fieldIndex)//cursor.getLong()で画像のIDを取得,カーソルクラスのgetLongメソッド
                val imageUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )//ContentUris.withAppendedId()でそこから実際の画像のURIを取得してログに出力
                // imageViewのsetImageURIメソッドを使い、URIが指している画像ファイルをImageViewに表示させていますmedia/13235
                Log.d("ANDROID", "URI : " + imageUri.toString())
                imageView.setImageURI(imageUri)

            }
            //if (!cursor.moveToNext()) {
              else  {cursor.moveToFirst()

                val fieldIndex =
                    cursor.getColumnIndex(MediaStore.Images.Media._ID)//cursor.getColumnIndex()で現在cursorが指しているデータの中から画像のIDがセットされている位置を取得
                val id = cursor.getLong(fieldIndex)//cursor.getLong()で画像のIDを取得,カーソルクラスのgetLongメソッド
                val imageUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )//ContentUris.withAppendedId()でそこから実際の画像のURIを取得してログに出力
                // imageViewのsetImageURIメソッドを使い、URIが指している画像ファイルをImageViewに表示させていますmedia/13235
                Log.d("ANDROID", "URI : " + imageUri.toString())
                imageView.setImageURI(imageUri)
            }

        }
        back_button.setOnClickListener {
            if (cursor!!.moveToPrevious()) {

                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
            }
            // else if (!cursor.moveToPrevious()) {
            else {
                cursor.moveToLast()

                val fieldIndex =
                cursor.getColumnIndex(MediaStore.Images.Media._ID)//cursor.getColumnIndex()で現在cursorが指しているデータの中から画像のIDがセットされている位置を取得
                val id = cursor.getLong(fieldIndex)//cursor.getLong()で画像のIDを取得,カーソルクラスのgetLongメソッド
                val imageUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
                )//ContentUris.withAppendedId()でそこから実際の画像のURIを取得してログに出力
            // imageViewのsetImageURIメソッドを使い、URIが指している画像ファイルをImageViewに表示させていますmedia/13235
                Log.d("ANDROID", "URI :back " + imageUri.toString())
                imageView.setImageURI(imageUri)
            }
        }



        start_reset_button.setOnClickListener {

            // var mTimer = Timer()これを消したら何回押しても早くならなくなった
            var mHandler = Handler()

            //if (mTimerRunning) {//止まらない
            if (mTimer != null) {//他のボタンは消えるけど再生で固まる

                start_reset_button.text = "再生"
                this.forward_button.isEnabled = true
                back_button.isEnabled = true
                mTimer!!.cancel()


                mTimer = null//これを入れると止まらなくなる、が、入れていないと一度止めるとうごかなくなる→下にelseが無かったので加えたら正しく動いた
                //cursor.close()
            }
            //!!はLesson3-12で学習したnot-nullアサーション演算子です。mTimerはnullでないことが明らかなので、強制的に非null型として処理を行っています
            //タイマーの始動  タイマー始動の関数 schedule(TimerTask task, long delay, long period)
            //} else
            else if (mTimer == null) {
                start_reset_button.text = "停止"//ここら辺をrunの中に入れていたせいでクラッシュした

                forward_button.isEnabled = false
                back_button.isEnabled = false

                mTimer = Timer()
                mTimer!!.schedule(object : TimerTask() {
                    // TimerTask task にはタイマー処理を記述するためのクラスを作成してインスタンスを渡します。
                    //mTimer.schedule() を使うと、アプリが終了するまで、run() 内のコードを実行し続けます。
                    override fun run() { //TimerTask の中の run() 関数が指定時間毎に呼び出される仕組み


                        mHandler.post {

                            if (cursor!!.moveToNext()) {
                                //  do{
                                val fieldIndex =
                                    cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                val id = cursor.getLong(fieldIndex)
                                val imageUri = ContentUris.withAppendedId(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    id
                                )

                                imageView.setImageURI(imageUri)

                                // cursor.close()

                            }

                            if (!cursor.moveToNext()) {
                                cursor.moveToFirst()


                                val fieldIndex =
                                    cursor.getColumnIndex(MediaStore.Images.Media._ID)//cursor.getColumnIndex()で現在cursorが指しているデータの中から画像のIDがセットされている位置を取得
                                val id =
                                    cursor.getLong(fieldIndex)//cursor.getLong()で画像のIDを取得,カーソルクラスのgetLongメソッド
                                val imageUri = ContentUris.withAppendedId(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    id
                                )//ContentUris.withAppendedId()でそこから実際の画像のURIを取得してログに出力
                                // imageViewのsetImageURIメソッドを使い、URIが指している画像ファイルをImageViewに表示させていますmedia/13235
                                Log.d("ANDROID", "URI : " + imageUri.toString())
                                imageView.setImageURI(imageUri)

                            }
                        }
                        //mTimerSec += 2.0
                    }


                }, 2000, 2000)


            }

        }
    }
}


