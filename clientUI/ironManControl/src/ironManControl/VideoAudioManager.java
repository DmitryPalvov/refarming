package ironManControl;

import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.Element.PAD_ADDED;
import org.freedesktop.gstreamer.ElementFactory;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.GstObject;
import org.freedesktop.gstreamer.Pad;
import org.freedesktop.gstreamer.PadLinkReturn;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.State;
import org.freedesktop.gstreamer.interfaces.VideoOverlay;

public class VideoAudioManager {

	public static boolean DEBUG = false;
	
//	public Thread monitor;
	//C:\gstreamer\1.0\x86_64\bin\gst-launch-1.0 rtspsrc location=rtsp://192.168.0.144:554/udp/av0_0 protocols=GST_RTSP_LOWER_TRANS_TCP do-retransmission=FALSE tcp-timeout=200000 latency=1000 ! rtph264depay ! avdec_h264 ! videoconvert ! autovideosink sync=false
	//C:\gstreamer\1.0\x86_64\bin\gst-launch-1.0.exe rtspsrc location="rtsp://192.168.0.10:554/user=admin\&password=\&channel=0\&stream.1\?real_stream--rtp-caching=100" debug=TRUE ! rtph264depay ! avdec_h264 ! videoconvert ! autovideosink sync=false
	
	//"application/x-rtp, media=video, clock-rate=90000, encoding-name=H264, payload=96, interleaved=2-3"
//	public static String gstreamerPath = "C:\\gstreamer\\1.0\\x86_64\\bin\\gst-launch-1.0.exe";
	
//	public static final String debugForward = VideoAudioManager.gstreamerPath +" videotestsrc ! videoconvert ! autovideosink sync=false";
//	public static final String debugTop = VideoAudioManager.gstreamerPath +" videotestsrc ! videoconvert ! autovideosink sync=false";
//	public static final String debugAudio = VideoAudioManager.gstreamerPath +" audiotestsrc ! audioconvert ! autoaudiosink sync=false";
	
	public JFrame topVideoFrame;
	public JFrame forwardVideoFrame;
	
	public Pipeline topVideoPipe = null;
	public Pipeline forwardVideoPipe = null;
	
	public void setup() {
		initGst();
		initFrames();
	}

	public void initGst() {
		String args[] = new String[1];
		args[0] = "GST_DEBUG=4";
		args = Gst.init("Video", args);		
	}
	
	public void initFrames() {
        forwardVideoFrame = new JFrame();
        forwardVideoFrame.setUndecorated(true);
        forwardVideoFrame.setTitle("Forward");
        forwardVideoFrame.setVisible(true);
        forwardVideoFrame.setBounds(0, 0, 1280, 720);
        
        topVideoFrame = new JFrame();
        topVideoFrame.setUndecorated(true);
        topVideoFrame.setTitle("Top");        
        topVideoFrame.setVisible(true);
        topVideoFrame.setBounds(1280, 0, 640, 480);
	}
	
	public void runInputAudio() {
		//@todo
	}

	public void stopInputAudio() {
		//@todo
	}

	public void runForwardVideo(JControlViewButton callerButton) {
		this.stopForwardVideo();
		JFrame frame = forwardVideoFrame;

		Element source = ElementFactory.make("rtspsrc", "source");
	    String ipcam = "rtsp://192.168.0.10:554/user=admin&password=&channel=1&stream.1?real_stream--rtp-caching=0";
	    if(true) {
	    	ipcam = "rtsp://192.168.0.168:554/0";		    
	    	source.set("protocols", "4");
	    	source.set("do-retransmission", "FALSE");
	    	source.set("tcp-timeout", "200000");
	    	source.set("latency", "0");	    	
	    }
	    source.set("location", ipcam);
	    if (DEBUG) {
        	source = ElementFactory.make("videotestsrc", "source");
        }
	    this.forwardVideoPipe = new Pipeline("ForwardVideo");
	    this.runVideo(frame, source, this.forwardVideoPipe, callerButton);
	}
	
	public void stopForwardVideo() {
		if (this.forwardVideoPipe != null && this.forwardVideoPipe.isPlaying()) {
			this.forwardVideoPipe.stop();
		}
	}

	public void stopTopVideo() {
		if (this.topVideoPipe != null && this.topVideoPipe.isPlaying()) {
			this.topVideoPipe.stop();
		}
	}

	public void runTopVideo(JControlViewButton callerButton) {
		this.stopTopVideo();
        JFrame frame = topVideoFrame;

        Element source = ElementFactory.make("rtspsrc", "source");
	    String ipcam = "rtsp://192.168.0.10:554/user=admin&password=&channel=1&stream.1?real_stream--rtp-caching=0";
	    /*if(true) {
	    	ipcam = "rtsp://192.168.0.168:554/0";		    
	    	source.set("protocols", "4");
	    	source.set("do-retransmission", "FALSE");
	    	source.set("tcp-timeout", "200000");
	    	source.set("latency", "0");	    	
	    }*/
	    source.set("location", ipcam);
	    if (DEBUG) {
        	source = ElementFactory.make("videotestsrc", "source");
        	source.set("is-live", "TRUE");
        }
	    this.topVideoPipe = new Pipeline("TopVideo");
	    this.runVideo(frame, source, this.topVideoPipe, callerButton);
	}

	
	private void runVideo(JFrame frame, Element source, Pipeline pipe, JControlViewButton callerButton) {

	    //Elements
	    Element watchdog = ElementFactory.make("watchdog", "watchdog");
	    watchdog.set("timeout", "500");
	    Element rtpdepay = ElementFactory.make("rtph264depay", "rtpdepay");
	    Element avdec_h264 = ElementFactory.make("avdec_h264", "avdec_h264");
	    Element videoconvert = ElementFactory.make("videoconvert", "videoconvert");

	    //Sink
	    Element d3dvideosink = ElementFactory.make("d3dvideosink", "d3dvideosink");
	    d3dvideosink.set("sync", "false");
	    d3dvideosink.set("create-render-window", "false");
	    d3dvideosink.set("enable-navigation-events", "false");

        VideoOverlay overlay = VideoOverlay.wrap(d3dvideosink);

        pipe.getBus().connect(new Bus.STATE_CHANGED() {
			
			@Override
			public void stateChanged(GstObject src, State old, State current, State pending) {
				if (src.getName().equals(pipe.getName())) {
					IronManControl.logger.log(Level.INFO, "GST_"+pipe.getName()+": STATE from "+ old.toString() +" "+current.toString());
					if(current.intValue() == State.PLAYING.intValue()) {
						callerButton.setOK();
					} else {
						callerButton.setWarning();
					}					
				}
			}
		});
        pipe.getBus().connect(new Bus.ERROR() {
			public void errorMessage(GstObject src, int code, String message) {
				IronManControl.logger.log(Level.WARNING, "GST_"+pipe.getName()+":"+src.getName()+":[" + code + "] "+message);
				callerButton.setWarning();
				
			//EEEWatchdog triggered	
				//WWWsource:[9] Could not read from resource.
			}
		});
        pipe.getBus().connect(new Bus.WARNING() {
			public void warningMessage(GstObject src, int code, String message) {
				IronManControl.logger.log(Level.WARNING, "GST_"+pipe.getName()+":"+src.getName()+":[" + code + "] "+message);
				callerButton.setWarning();				
			}
		});
        
        overlay.setWindowHandle(frame);
        source.connect(new PAD_ADDED() {
			public void padAdded(Element element, Pad pad) {
				if (pad.getName().startsWith("recv_rtp_src")) {
					// sync them
					rtpdepay.syncStateWithParent();
					// link them
					if (!pad.link(rtpdepay.getStaticPad("sink")).equals(
							PadLinkReturn.OK)) {
						System.err.println("bin-decoder error");
					}
				}
			}
		});
	    
        //Connect
	    pipe.add(source);
	    if(!DEBUG) {
	    	pipe.add(rtpdepay);
	    	pipe.add(avdec_h264);
	    }
	    pipe.add(videoconvert);
	    pipe.add(watchdog);
	    pipe.add(d3dvideosink);
	    if (!DEBUG) {
		    if (!Element.linkPads(source, "source", rtpdepay, "rtpdepay")) {
		    	System.out.println("Failed to link source to rtpdepay");
		    }
	    	if (!Element.linkMany(rtpdepay, avdec_h264, watchdog, videoconvert, d3dvideosink)) {
	    		System.out.println("Failed to link elements");
	    		return;
	    	}
	    } else {
	    	Element.linkMany(source, watchdog, videoconvert, d3dvideosink);
	    }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	pipe.play();
            }
        });
	    
	}
	
	/*public void runForwardVideoGst() {
	    Pipeline pipe = new Pipeline("TopVideo");
	    this.forwardVideoPipe = pipe;
	    
	    // Source
	    Element source = ElementFactory.make("rtspsrc", "source");
	    String ipcam = "rtsp://192.168.0.10:554/user=admin&password=&channel=1&stream.1?real_stream--rtp-caching=0";
	    if(true) {
	    	ipcam = "rtsp://192.168.0.168:554/0";		    
	    	source.set("protocols", "4");
	    	source.set("do-retransmission", "FALSE");
	    	source.set("tcp-timeout", "200000");
	    	source.set("latency", "0");	    	
	    }
	    source.set("location", ipcam);

	    //Elements
	    Element rtpdepay = ElementFactory.make("rtph264depay", "rtpdepay");
	    Element avdec_h264 = ElementFactory.make("avdec_h264", "avdec_h264");
	    Element videoconvert = ElementFactory.make("videoconvert", "videoconvert");

	    //Sink
	    Element d3dvideosink = ElementFactory.make("d3dvideosink", "d3dvideosink");
	    d3dvideosink.set("sync", "false");
	    d3dvideosink.set("create-render-window", "false");
	    d3dvideosink.set("enable-navigation-events", "false");

        VideoOverlay overlay = VideoOverlay.wrap(d3dvideosink);
        JFrame fForwardVideo = new JFrame();
		fForwardVideo.setUndecorated(true);
		fForwardVideo.setVisible(true);
		fForwardVideo.setBounds(793+138, 720, 720, 500);

        overlay.setWindowHandle(fForwardVideo);
	    
	    source.connect(new PAD_ADDED() {
			public void padAdded(Element element, Pad pad) {
				if (pad.getName().startsWith("recv_rtp_src")) {
					// sync them
					rtpdepay.syncStateWithParent();
					// link them
					if (!pad.link(rtpdepay.getStaticPad("sink")).equals(
							PadLinkReturn.OK)) {
						System.err.println("bin-decoder error");
					}
				}
			}
		});
	    
	    //Connect
	    pipe.add(source);
	    pipe.add(rtpdepay);
	    pipe.add(avdec_h264);
	    pipe.add(videoconvert);
	    pipe.add(d3dvideosink);
	    if (!Element.linkPads(source, "source", rtpdepay, "rtpdepay")) {
	    	System.out.println("Failed to link source to rtpdepay");
	    }

	    if (!Element.linkMany(rtpdepay, avdec_h264, videoconvert, d3dvideosink)) {
	    	System.out.println("Failed to link elements");
	    } else {
	    	pipe.play();
	    }
		
	}*/
	
/*	private long monitorCheckdelay = 0;
	
	private void processMonitor() {
		if (System.currentTimeMillis() - this.monitorCheckdelay < 2000) return;
		String name = "";
		Process process = null;
		boolean isBreak = false;
		synchronized (this.processMap) {
			for (Map.Entry<String, Process> entry : this.processMap.entrySet()) {
				name = entry.getKey();
				process = entry.getValue();
				if (!process.isAlive()) {
					isBreak = true;
					break;									
				} else {
					if ("forwardVideo".equals(name)) {
						if (IronManControl.guiFrame != null && IronManControl.guiFrame.controlThread != null) {
							IronManControl.guiFrame.controlThread.setLastVideoFrameTime(System.currentTimeMillis());
						}
					}
				}
			}	
			if (isBreak) {
				IronManControl.logger.log(Level.INFO, name + " Finish");			
				process.destroy();
				this.processMap.remove(name);
				runCommand(name, commandMap.get(name));
			}
			
		}		
	}*/
    
	public void finish() {
		System.out.println("Graceful stop for VideoAudioManager");
		if (topVideoPipe != null) {
			topVideoPipe.stop();
		}
		if (forwardVideoPipe != null) {
			forwardVideoPipe.stop();
		}

	}
	
	public void finalize(){
		System.out.println("finalize");
	}

}
