package december.timeruler.com.timeruler_december;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

public class JavaFunctions {
    String TAG="JavaFunctions";
    int globalDetectorCounter = 0;



    public Bitmap detectFace(Bitmap bitmap,Context context) {

        FaceDetector detector = new FaceDetector.Builder(context)
                .setProminentFaceOnly(true)
                .setTrackingEnabled(false)
                .build();
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);
        detector.release();

        Log.e(TAG, "detect \n" +
                "bitmap width:" + bitmap.getWidth() + " height:" + bitmap.getHeight() + "\n" +
                "face width:" + " height:");
        Log.e(TAG,"face size:" + faces.size());
      //  globalFaceSize=faces.size();
        if (faces.size() > 0) {
            Face face = faces.valueAt(0);
//            int x = Math.max((int) (face.getPosition().x - FaceGraphic.ID_X_OFFSET), 0);
//            int y = Math.max((int) (face.getPosition().y - FaceGraphic.ID_Y_OFFSET), 0);
//            int width = Math.min((int) (face.getWidth() - FaceGraphic.ID_X_OFFSET), bitmap.getWidth() - x);
//            int height = Math.min((int) (face.getHeight() + FaceGraphic.ID_Y_OFFSET), bitmap.getHeight() - y);

            int x = (int) face.getPosition().x;
            int y = (int) face.getPosition().y;
            int width = Math.min((int) (face.getWidth()), bitmap.getWidth() - x);
            int height = Math.min((int) (face.getHeight()), bitmap.getHeight() - y);
            if (x < 0) {
                width += x; // += x is negative
                x = 0;
            }
            if (y < 0) {
                height += y; // += y is negative
                y = 0;

            }
            Log.e(TAG, "Y = " + y + " height " + height + " = " + (y + height) + " bH " + bitmap.getHeight());
            Log.e(TAG, "x = " + x + " width " + width + " = " + (x + width) + " bW " + bitmap.getWidth());

            if (y + height > bitmap.getHeight()) {// to avoid java.lang.IllegalArgumentException: y + height must be <= bitmap.height()
                int difference = Math.abs(y + height - bitmap.getHeight()); // Get the difference of bitmap height and y+height
                int divide = difference / 2;                              // divide it to 2
                y += divide;                                            // add it to y and subtract it to height
                height -= divide;                                       // so we can get the center of the fucking face

            }

            if (x + width > bitmap.getWidth()) {// to avoid java.lang.IllegalArgumentException: x + width must be <= bitmap.getWidth()
                int difference = Math.abs(y + height - bitmap.getHeight()); // Get the difference of bitmap width and x + width
                int divide = difference / 2;                              // divide it to 2
                x += divide;                                            // add it to y and subtract it to height
                width -= divide;                                       // so we can get the center of the fucking face
            }


            return Bitmap.createBitmap(bitmap, x, y, width, height);
        }
        return null;
    }





    public void faceScanner(Context mContext){
        Log.e(TAG, "faceScanner" + globalDetectorCounter);

        FaceDetector detector = new FaceDetector.Builder(mContext)
                .setProminentFaceOnly(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

//        detector.setProcessor(
//                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
//                        .build());z
        detector.setProcessor(new Detector.Processor<Face>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Face> detections) {
                globalDetectorCounter = globalDetectorCounter + 1;
                //Log.e(TAG, "I");

                SparseArray<Face> faces = detections.getDetectedItems();
                if (faces.size() != 0) {

                    Log.e(TAG, "nay nawong ka " + globalDetectorCounter);
                }
                else {
                    globalDetectorCounter =0;
                    Log.e(TAG, "way nawong ka "+globalDetectorCounter);

                }





    }
});

    }

}
