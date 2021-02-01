package com.chooloo.www.callmanager.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("Registered")
class RecordService : Service() {
    private var mContext: Context? = null
    private var mFileName: String? = null
    private var mIsRecording = false
    private var mRecorder: MediaRecorder? = null
    private var mFile: File? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mContext = applicationContext
        Timber.d("RecordService OnStartCommand")
        if (intent == null) return START_NOT_STICKY
        val commandType = intent.getIntExtra("commandType", 0)
        if (commandType == 0) return START_NOT_STICKY
        when (commandType) {
            RECORD_SERVICE_START -> start()
            RECORD_SERVICE_STOP -> stop()
        }
        return START_STICKY
    }

    private fun deleteFile() {
        if (mFile == null) return
        mFile!!.delete()
        mFile = null
    }

    private fun terminate() {
        Timber.i("Terminating call recorder")
        stop()
        deleteFile()
        mIsRecording = false
    }

    /**
     * Start recordingT
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun start() {
//        if (name != null) mFileName = name + '.' + FILE_TYPE;
//        else mFileName = generateFileName();
        mFileName = generateFileName()
        val downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        mFile = File(downloadPath, mFileName)
        Timber.d("Path of file %s", mFile!!.path)

        // define recorder
        mRecorder = MediaRecorder()
        mRecorder!!.setAudioSource(AUDIO_SOURCE)
        mRecorder!!.setOutputFormat(AUDIO_FORMAT)
        mRecorder!!.setAudioEncoder(AUDIO_ENCODER)
        mRecorder!!.setOutputFile(mFile)
        mRecorder!!.setOnErrorListener { mr: MediaRecorder?, what: Int, extra: Int ->
            Timber.e("Recorder OnErrorListener $what, $extra")
            terminate()
        }
        mRecorder!!.setOnInfoListener { mr: MediaRecorder?, what: Int, extra: Int ->
            Timber.e("Recorder OnInfoListener $what, $extra")
            terminate()
        }
        try {
            mRecorder!!.prepare()
            mRecorder!!.start()
            mIsRecording = true
            Timber.i("Call recording started")
            Toast.makeText(mContext, "Recording call...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Timber.e(e, "Couldn't start call recording")
            Toast.makeText(mContext, "Couldn't start call recording :(", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            terminate()
        }
    }

    /**
     * Stop and release recording
     */
    fun stop() {
        if (mRecorder == null) return
        try {
            Toast.makeText(mContext, "Call recording ended", Toast.LENGTH_SHORT).show()
            mRecorder!!.stop()
        } catch (e: IllegalStateException) {
            Toast.makeText(mContext, "Couldn't record call", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            deleteFile()
        }
        mRecorder!!.release()
        mRecorder = null
        if (mIsRecording) mIsRecording = false
    }

    companion object {
        const val RECORD_SERVICE_START = 1
        const val RECORD_SERVICE_STOP = 2

        // Recorder Constants
        private const val AUDIO_SOURCE = MediaRecorder.AudioSource.MIC
        private const val AUDIO_FORMAT = MediaRecorder.OutputFormat.THREE_GPP
        private const val AUDIO_ENCODER = MediaRecorder.AudioEncoder.AMR_NB
        private const val FILE_TYPE = "3gpp"
        @SuppressLint("SimpleDateFormat")
        private fun generateFileName(): String {
            return "koler_record_" + SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(Date()) + "." + FILE_TYPE
        }
    }
}