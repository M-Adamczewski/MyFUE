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
    //Mat matryca_wejsciowa, matryca_docelowa, mat2, mat3;
    BaseLoaderCallback baseLoaderCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.myCameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
        cameraBridgeViewBase.enableView();
        cameraBridgeViewBase.setMaxFrameSize(720,1280);

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

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //deklaracja zmiennych
        Mat hierarchia = new Mat();
        Mat matryca_krawedzi = new Mat();
        Mat matryca_docelowa = new Mat();
        Mat matryca_wejsciowa = new Mat();
        java.util.List<MatOfPoint> lista_konturow = new java.util.ArrayList<MatOfPoint>();
        double maxwart = 0;
        int maxwartIndex = 0;

        //pobranie obrazu
        matryca_wejsciowa = inputFrame.gray();
        matryca_docelowa = inputFrame.rgba();
        //transpozycja macierzy obrazu
        Core.transpose(matryca_wejsciowa, matryca_wejsciowa);
        Core.transpose(matryca_docelowa, matryca_docelowa);
        //zmiana wymiarów matrycy
        Imgproc.resize(matryca_wejsciowa, matryca_wejsciowa, new Size(720, 720));
        Imgproc.resize(matryca_docelowa, matryca_docelowa, new Size(720, 720));
        //lustrzane odbicie w osi Y
        Core.flip(matryca_wejsciowa, matryca_wejsciowa, 1);
        Core.flip(matryca_docelowa, matryca_docelowa, 1);
        //rozmycie krawędzi
 //       Imgproc.blur(mat3, mat4, new Size(3, 3));
        //wykrycie krawędzi
        Imgproc.Canny(matryca_wejsciowa, matryca_krawedzi, 50, 150, 3, false);
        //wykrycie konturów
        Imgproc.findContours(matryca_krawedzi, lista_konturow, hierarchia, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        //wyłonienie największego konturu z listy
        for (int g = 0; g < lista_konturow.size(); g++) {
            double contourArea = Imgproc.contourArea(lista_konturow.get(g));
            if (maxwart < contourArea) {
                maxwart = contourArea;
                maxwartIndex = g;
            }
        }

        //zaznaczenie wykrytych konturów na obrazie
        Imgproc.drawContours(matryca_docelowa, lista_konturow, maxwartIndex, new Scalar(255, 0, 0), 5);

        //zwolnienie pamięci
        hierarchia.release();
        matryca_krawedzi.release();
        matryca_wejsciowa.release();

        
        return matryca_docelowa;
    }
        //
          //  Imgproc.drawContours(mat2, lista_konturow, 0, new Scalar(0,255,0,255), 3);
 //       for (int i = 0; i < 4; i++) {
 //           Imgproc.drawContours(matryca_wejsciowa, lista_konturow, i, new Scalar(255, 255, 255), 3);
 //       }


        // Edge detection
       // Imgproc.Canny(matryca_wejsciowa, dst, 50, 200, 3, false);
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
            matryca_wejsciowa=cdst;
        }


//DRUGA METODA
        // Probabilistic Line Transform
        Mat linesP = new Mat(); // will hold the results of the detection
        Imgproc.HoughLinesP(dst, linesP, 1, Math.PI/180, 50, 50, 10); // runs the actual detection
        // Draw the lines
        for (int x = 0; x < linesP.rows(); x++) {
            double[] l = linesP.get(x, 0);
            Imgproc.line(cdstP, new Point(l[0], l[1]), new Point(l[2], l[3]), new Scalar(0, 255, 0), 3, Imgproc.LINE_AA, 0);
            matryca_wejsciowa=cdstP;
        }
*/


    //}

    @Override
    public void onCameraViewStopped() {

        //matryca_wejsciowa.release();
        // mat2.release();
        // mat3.release();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

        //matryca_wejsciowa= new Mat(width, height, CvType.CV_8UC4);
        //mat2= new Mat(width, height, CvType.CV_8UC4);
        //mat3= new Mat(width, height, CvType.CV_8UC4);

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