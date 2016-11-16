package ironManControl;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;
import com.sun.jna.win32.StdCallLibrary;

public class TryWithHWND {
   public interface User32 extends StdCallLibrary {
      User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);
      boolean EnumWindows(WinUser.WNDENUMPROC lpEnumFunc, Pointer arg);
      int GetWindowTextA(HWND hWnd, byte[] lpString, int nMaxCount);
   }

   public static void main(String[] args) {
      
	  //Pointer p = JnaUtil.getWinHwnd("C:\\Windows\\system32\\cmd.exe");
	  Pointer p = JnaUtil.getWinHwnd("GStreamer D3D video sink (internal window)");
	  JnaUtil.moveWindow(p, 10, 10, 1024, 768);
	  p.setChar(0, 'A');
      
	  //p.
	  /* final User32 user32 = User32.INSTANCE;
      user32.EnumWindows(new WNDENUMPROC() {
         int count = 0;
         @Override
         public boolean callback(HWND hWnd, Pointer arg1) {
            byte[] windowText = new byte[512];
            //user32.
            user32.GetWindowTextA(hWnd, windowText, 512);
            String wText = Native.toString(windowText);

            // get rid of this if block if you want all windows regardless of whether
            // or not they have text
            if (wText.isEmpty()) {
               return true;
            }
            System.out.println("Found window with text " + hWnd + ", total " + ++count
                  + " Text: " + wText);
            return true;
         }
      }, null);*/
   }
}
class JnaUtil {
	   private static final User32 user32 = User32.INSTANCE;
	   private static Pointer callBackHwnd;

	   public static Pointer getWinHwnd(final String startOfWindowName) {
	      callBackHwnd = null;

	      user32.EnumWindows(new User32.WNDENUMPROC() {
	         @Override
	         public boolean callback(Pointer hWnd, Pointer userData) {
	            byte[] windowText = new byte[512];
	            user32.GetWindowTextA(hWnd, windowText, 512);
	            String wText = Native.toString(windowText).trim();

	            if (!wText.isEmpty() && wText.startsWith(startOfWindowName)) {
	               callBackHwnd = hWnd;
	               return false;
	            }
	            return true;
	         }
	      }, null);
	      return callBackHwnd;
	   }

	   public static boolean moveWindow(Pointer hWnd, int x, int y, int nWidth,
	         int nHeight) {
	      boolean bRepaint = true;
	      return user32.MoveWindow(hWnd, x, y, nWidth, nHeight, bRepaint);
	   }

	}

	interface User32 extends StdCallLibrary {
	   User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);

	   interface WNDENUMPROC extends StdCallCallback {
	      boolean callback(Pointer hWnd, Pointer arg);
	   }

	   boolean EnumWindows(WNDENUMPROC lpEnumFunc, Pointer userData);

	   boolean MoveWindow(Pointer hWnd, int x, int y, int nWidth, int nHeight,
	         boolean bRepaint);

	   int GetWindowTextA(Pointer hWnd, byte[] lpString, int nMaxCount);

	}