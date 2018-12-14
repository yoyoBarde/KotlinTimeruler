package december.timeruler.com.timeruler_december

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.Image
import android.nfc.Tag
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor
import kotlinx.android.synthetic.main.activity_camera_source.*
import kotlinx.android.synthetic.main.fragment_camera2_basic.*
import org.jetbrains.anko.*

import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SurfaceCamera : AppCompatActivity() {
    lateinit var surfaceView: SurfaceView
    lateinit var cameraSource: CameraSource
    lateinit var  mBtn_in: Button
    lateinit var  mBtn_out: Button
    var globalCounter = 0
    var smileSettoWhite = true
    val TAG = "SurfaceCamera"
    lateinit var my_iv_preview: ImageView
    lateinit var my_ic_face: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_source)
        my_ic_face = findViewById(R.id.iv_facedetected_ic)
        my_iv_preview = findViewById<ImageView>(R.id.iv_photoPreview)
        mBtn_in = findViewById<Button>(R.id.btn_in)
        mBtn_out= findViewById<Button>(R.id.btn_out)

        setupDigitalClock()

askPermissions()
        onCreateDoables()


    }



    private fun onCreateDoables() {
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val displayMetrics = DisplayMetrics()
        surfaceView = findViewById<SurfaceView>(R.id.cameraPreview)
        val detector = FaceDetector.Builder(applicationContext)
            .setProminentFaceOnly(true)
            .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
            .setTrackingEnabled(true)
            .build()

        windowManager.defaultDisplay.getMetrics(displayMetrics)
        cameraSource = CameraSource.Builder(this, detector)
            .setRequestedPreviewSize(640, 480)
            .setFacing(CameraSource.CAMERA_FACING_FRONT)
            .setRequestedFps(15.0f)
            .build()


        Log.e("ScreenSize", (width + height).toString())
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })
        //mGraphicOverlay = GraphicOverlay(this,attr())

//        //mGraphicOverlay = GraphicOverlay(applicationContext,null)
//        mGraphicOverlay = GraphicOverlay(applicationContext)
//        detector.setProcessor(LargestFaceFocusingProcessor(detector, GraphicFaceTracker(mGraphicOverlay)))

        detector.setProcessor(object : Detector.Processor<Face> {
            override fun release() {

            }

            override fun receiveDetections(detections: Detector.Detections<Face>) {
                doAsync {
                    val faces = detections.detectedItems

                    if (faces.size() != 0) {


                        globalCounter += 1
                        if (globalCounter == 1) {
                            uiThread {
                                my_ic_face.setImageResource(R.mipmap.ic_face)
                            }
                        }

                        if (globalCounter == 27) {
                            goVibrate()


                            uiThread {
                                my_ic_face.setImageResource(R.mipmap.ic_face_green)
                            }


                        }
                        if (globalCounter == 30) {
                            Log.e(TAG, "gwapo ka")
                            cameraSource.takePicture(mShutterCallback, mPictureCallback)
                            globalCounter = -20


                        }

                    } else {
                        uiThread {
                            my_ic_face.setImageResource(R.mipmap.ic_face_red)
                        }
                        globalCounter = 0


                    }
                }
            }
        })


    }

    private val mShutterCallback = CameraSource.ShutterCallback {


    }

    private val mPictureCallback = CameraSource.PictureCallback { bytes ->
        // val orientation = Exif.getOrientation(bytes)
        doAsync {
            var userBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                enableButton()
            Log.e(TAG, userBitmap.toString())

            uiThread {
                iv_photoPreview.visibility = View.VISIBLE
                iv_photoPreview.setImageBitmap(userBitmap)
            }

//        when (orientation) {
//            90 -> bitmap = rotateImage(bitmap, 90f)
//            180 -> bitmap = rotateImage(bitmap, 180f)
//            270 -> bitmap = rotateImage(bitmap, 270f)
//            0 -> {
//            }
//            // if orientation is zero we don't need to rotate this
//
    //            else -> {
//            }
//        }
//        //write your code here to save bitmap
//        val image = detect(bitmap)
//        GlobalImageBitmap = image
//        if (image != null) {
//            save(image)
//        } else {
//
//            save(bitmap)
//            //                AppUtils.toastShort("No face detected");
//            //                playNoFaceSound();
//            //                finish();
        }
    }
    fun setupDigitalClock(){

        doAsync {
            var myDate = getCurrentDate()

            for (i in 0 until 9008941372) {


                Thread.sleep(1000)
                //  Log.e(TAG, getCurrentTime()+getCurrentDate())
                var myTime = getCurrentTime()
                var myString = " $myDate \n $myTime "


                uiThread {


                    digitalClock2.text = myString

                }


            }

        }
    }
    fun goVibrate() {

        var v: Vibrator = applicationContext!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
// Vibrate for 500 milliseconds

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }


    }

    fun getCurrentTime(): String {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("h:mm:ss a")
        return df.format(c.time)
    }

    fun getCurrentDate(): String {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("MMM dd,yyyy")
        return df.format(c.time)
    }
    fun askPermissions(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (applicationContext.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
///method to get Images

            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(
                        applicationContext,
                        "Your Permission is needed to get access the camera",
                        Toast.LENGTH_LONG
                    ).show()
                }
                requestPermissions(
                    arrayOf(
                        // Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        // Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ), 0
                )
            }
        }
    }
    fun disableButtons(){
        doAsync {
            var myFloat: Float = 0.5.toFloat()
            uiThread {
                mBtn_in.isClickable = false
                mBtn_out.isClickable = false
                mBtn_in.alpha = myFloat
                mBtn_out.alpha = myFloat
            }
        }
    }
    fun enableButton(){
        doAsync {
            Log.e(TAG,"enableButtons")
            var myFloat:Float = .99.toFloat()
            uiThread {
                mBtn_in.isClickable = true
                mBtn_out.isClickable = true
                mBtn_in.alpha = myFloat
                mBtn_out.alpha = myFloat }
        }

    }

}