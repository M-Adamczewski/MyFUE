package com.example.myfue;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.Toast;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;

import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;

import org.opencv.core.Core;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.CvType.CV_8UC3;


public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    CameraBridgeViewBase cameraBridgeViewBase;
    Mat mat1, mat2, mat3, mat4;
    BaseLoaderCallback baseLoaderCallback;
   // Mat Matu = new Mat(2,3, CV_8UC3, new Scalar(2,2,255));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.myCameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
        ///////////////////////////////////////////
        cameraBridgeViewBase.enableView();
        cameraBridgeViewBase.setMaxFrameSize(720,1280);
        ///////////////////////////////////////////


        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void OnManagerConnected(int status) {
                super.onManagerConnected(status);
                switch(status){
                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
    }
    ///////////////////////////////////TUTAJ//////////////////////////////////////
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mat1 = inputFrame.rgba();
        Core.transpose(mat1, mat2);
        //Imgproc.resize(mat1, mat1, mat1.size(), 0,0,0);
        Core.flip(mat2, mat1, 1);



        java.util.List<MatOfPoint> lista = new java.util.ArrayList<MatOfPoint>();
        Mat hierarchia= new Mat();

        Mat image32S = new Mat();
        //mat1.convertTo(image32S, CvType.CV_32SC1);
        Imgproc.cvtColor(mat1, mat3,  Imgproc.COLOR_BGR2GRAY );
      //  Imgproc.blur(mat3, mat4, new Size(3, 3));
        Imgproc.Canny( mat3, image32S, 50, 150, 3, false); //szuka krawedzi


        Imgproc.findContours(image32S, lista, hierarchia, Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);

        double maxwart=0;
        int maxwartIndex=0;

        /////////////////////////////////SORTOWANIE LISTY//////////////////////////////////////////////////////////////////////////
        for(int g=0; g<lista.size();g++){
            double contourArea=Imgproc.contourArea(lista.get(g));
            if (maxwart<contourArea){
                maxwart=contourArea;
                maxwartIndex=g;
            }
        }
        Imgproc.drawContours(mat1, lista, maxwartIndex, new Scalar(255,0,0),5);


        //mat2.release();
        hierarchia.release();
        image32S.release();


        return mat1;
        //
          //  Imgproc.drawContours(mat2, lista, 0, new Scalar(0,255,0,255), 3);
 //       for (int i = 0; i < 4; i++) {
 //           Imgproc.drawContours(mat1, lista, i, new Scalar(255, 255, 255), 3);
 //       }


        // Edge detection
       // Imgproc.Canny(mat1, dst, 50, 200, 3, false);
        //Mat dst = new Mat(), cdst = new Mat(), cdstP;
        // Copy edges to the images that will display the results in BGR
        //Imgproc.cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);
        //cdstP = cdst.clone();

        /*
           /////////PIERWSZA METODA
        // Standard Hough Line Transform
        Mat lines = new Mat(); // will hold the results of the detection
        Imgproc.HoughLines(dst, lines, 1, Math.PI/180, 150); // runs the actual detection
        // Draw the lines
        for (int x = 0; x < lines.rows(); x++) {
            double rho = lines.get(x, 0)[0],
                    theta = lines.get(x, 0)[1];
            double a = Math.cos(theta), b = Math.sin(theta);
            double x0 = a*rho, y0 = b*rho;
            Point pt1 = new Point(Math.round(x0 + 1000*(-b)), Math.round(y0 + 1000*(a)));
            Point pt2 = new Point(Math.round(x0 - 1000*(-b)), Math.round(y0 - 1000*(a)));
            Imgproc.line(cdst, pt1, pt2, new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
            mat1=cdst;
        }


//DRUGA METODA
        // Probabilistic Line Transform
        Mat linesP = new Mat(); // will hold the results of the detection
        Imgproc.HoughLinesP(dst, linesP, 1, Math.PI/180, 50, 50, 10); // runs the actual detection
        // Draw the lines
        for (int x = 0; x < linesP.rows(); x++) {
            double[] l = linesP.get(x, 0);
            Imgproc.line(cdstP, new Point(l[0], l[1]), new Point(l[2], l[3]), new Scalar(0, 255, 0), 3, Imgproc.LINE_AA, 0);
            mat1=cdstP;
        }
*/


    }

    @Override
    public void onCameraViewStopped() {

        mat1.release();
        // mat2.release();
        // mat3.release();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

        mat1= new Mat(width, height, CvType.CV_8UC4);
        mat2= new Mat(width, height, CvType.CV_8UC4);
        mat3= new Mat(width, height, CvType.CV_8UC4);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"there is a problem in opencv",Toast.LENGTH_SHORT).show();
        }
        else{
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }
}