package december.timeruler.com.timeruler_december

import android.Manifest
import android.content.pm.PackageManager
import android.nfc.Tag
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector

import java.io.IOException

class SurfaceCamera : AppCompatActivity() {
    lateinit var surfaceView: SurfaceView
    lateinit var cameraSource: CameraSource
    lateinit var barcodeDetector: BarcodeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_source)


        surfaceView = findViewById<SurfaceView>(R.id.cameraPreview)
        val detector = FaceDetector.Builder(applicationContext)
            .setProminentFaceOnly(true)
            .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
            .build()


        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.QR_CODE).build()

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        Log.e("ScreenSize", (width + height).toString())

        cameraSource = CameraSource.Builder(this, detector)
            .setRequestedPreviewSize(640, 480)
            .setFacing(CameraSource.CAMERA_FACING_FRONT)
            .setRequestedFps(15.0f)
            .build()





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

        detector.setProcessor(object : Detector.Processor<Face> {
            override fun release() {

            }

            override fun receiveDetections(detections: Detector.Detections<Face>) {

                val faces = detections.detectedItems

                if (faces.size() != 0) {

                    Log.e(TAG, "gwapo ka")

                } else {
                    Log.e(TAG, "way nawong ka ")

                }

            }
        })

    }

    companion object {
        private val TAG = "SurfaceCamera"
    }
}
