package uz.ilhomjon.downloadmanager

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import uz.ilhomjon.downloadmanager.databinding.ActivityMainBinding
import java.io.File
import java.util.*


//link: https://github.com/MindorksOpenSource/PRDownloader

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PRDownloader.initialize(getApplicationContext());

// Setting timeout globally for the download network requests:
        val config = PRDownloaderConfig.newBuilder()
            .setReadTimeout(30000)
            .setConnectTimeout(30000)
            .build()
        PRDownloader.initialize(applicationContext, config)

        binding.btnStart.setOnClickListener {

            val downloadId = PRDownloader.download(
                "http://5.182.26.44:8080/storage/audios/GMdwSQUJpRrGFU8P8jOkY6zi2ucruC3a0VNKXGT8.mp3",
                getRootDirPath(this), "fileName"
            )
                .build()
                .setOnStartOrResumeListener {
                    Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show()
                    binding.tvInfo.text = "Start"
                }
                .setOnPauseListener { }
                .setOnProgressListener {
                    binding.tvInfo.text = getProgressDisplayLine(it.currentBytes, it.totalBytes)
                }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()
                        binding.tvInfo.text = "Success"
                    }

                    override fun onError(error: Error?) {
                        binding.tvInfo.text = error?.serverErrorMessage
                    }
                })

        }

        binding.btnPlay.setOnClickListener {
            val root = getRootDirPath(this@MainActivity)
            val filePath =  root + File.separator + "fileName"
            val file = File(filePath)
            if (file.exists()){
                Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show()
                MediaPlayer.create(this, Uri.parse(filePath)).start()
            }else{
                Toast.makeText(this, "Not Ok", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun getRootDirPath(context: Context): String? {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val file: File = ContextCompat.getExternalFilesDirs(
                context.getApplicationContext(),
                null
            )[0]
            file.getAbsolutePath()
        } else {
            context.getApplicationContext().getFilesDir().getAbsolutePath()
        }
    }

    fun getProgressDisplayLine(currentBytes: Long, totalBytes: Long): String? {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes)
    }

    private fun getBytesToMBString(bytes: Long): String {
        return java.lang.String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00))
    }

}