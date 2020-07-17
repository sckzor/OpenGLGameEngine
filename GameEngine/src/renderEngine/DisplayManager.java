package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static final int FPS_CAP = 30;
	
	private static long lastFrameTime;
	private static float delta;
	private static long fps;
	private static long lastFps;
	private static int finalFps;

	private static boolean Fullscreen;
	
	
	public static void createDisplay(Boolean fullscreen, int width, int height){		
		ContextAttribs attribs = new ContextAttribs(3,3)
		.withForwardCompatible(true)
		.withProfileCore(true);
				
		try {
			if(fullscreen)
			{
				Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
			}else {
				Display.setDisplayMode(new DisplayMode(width,height));
			}
			Fullscreen = fullscreen;
			Display.create(new PixelFormat().withDepthBits(24), attribs);
			Display.setTitle("Epic Game!");			
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		
		GL11.glViewport(0,0, WIDTH, HEIGHT);
		lastFrameTime = getCurrentTime();
		lastFps = getCurrentTime();
	}
	
	public static void updateDisplay(){
		
		Display.sync(FPS_CAP);
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		updateFPS();	
		lastFrameTime = getCurrentTime();
	}
	
	public static float getFrameTimeSeconds() {
		return delta;
	}

	public static void closeDisplay(){
		
		Display.destroy();
		
	}
	
	public static long getCurrentTime()
	{
		return Sys.getTime()*1000/Sys.getTimerResolution();
	}
	
	public static void updateFPS() {
	    if (getCurrentTime() - lastFps > 1000) {
	        finalFps = (int) fps;
	        fps = 0; //reset the FPS counter
	        lastFps += 1000; //add one second
	    }
	    fps++;
	}
	
	public static int getFPS()
	{
		return finalFps;
	}
	
	public static int getWidth() {
		return Display.getWidth();
	}

	public static int getHeight() {
		return Display.getHeight();
	}
	
	public static void setScreenResolution(Boolean fullscreen, int width, int height){
		try {
			if(fullscreen)
			{
				Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
			}else {
				Display.setDisplayMode(new DisplayMode(width,height));
			}
			Fullscreen = fullscreen;

		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	public static boolean isFullscreen() {
		return Fullscreen;
	}

}
