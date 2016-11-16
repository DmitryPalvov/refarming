
package org.freedesktop.gstreamer.examples;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.Map;
import java.util.HashMap;

import javax.swing.JFrame;
import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.lowlevel.GstAPI;
import org.freedesktop.gstreamer.lowlevel.GstNative;

import com.sun.jna.Library;
import com.sun.jna.NativeLibrary;


/**
 *
 * @author Neil C Smith (http://neilcsmith.net)
 */
public class CameraTest implements Library {

    /**
     * @param args the command line arguments
     */
    
    private static Pipeline pipe;
    
    public static void main(String[] args) {

    	//Map a = new HashMap();
    	//com.sun.jna.NativeLibrary.loadLibrary("libgstreamer-1.0-0.dll", GstAPI.class);
    	//com.sun.jna.NativeLibrary.loadLibrary("libgstreamer-1.0-0.dll", a);
    	//NativeLibrary.addSearchPath("libgstreamer-1.0-0.dll", "C:\\gstreamer\\1.0\\x86_64\\bin");
        Gst.init("CameraTest", args);
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                SimpleVideoComponent vc = new SimpleVideoComponent();
                Bin bin = Bin.launch("autovideosrc ! videoconvert ! capsfilter caps=video/x-raw,width=640,height=480", true);
                pipe = new Pipeline();
                pipe.addMany(bin, vc.getElement());
                Pipeline.linkMany(bin, vc.getElement());           

                JFrame f = new JFrame("Camera Test");
                f.add(vc);
                vc.setPreferredSize(new Dimension(640, 480));
                f.pack();
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                pipe.play();
                f.setVisible(true);
            }
        });
    }

}
