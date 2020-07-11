package audio;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.util.WaveData;

public class AudioMaster {

	private static List<Integer> buffers = new ArrayList<Integer>();
	
	public static final float DEFAULT_ROLL_OFF_FACTOR = 1;
	public static final float DEFAULT_REFERENCE_DISTANCE = 1;
	public static final float DEFAULT_MAX_DISTANCE = 250;
	
	
	private static float listenerX = 0f;
	private static float listenerY = 0f;
	private static float listenerZ = 0f;
	
	public static void init()
	{
		try {
			AL.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);

	}
	
	public static void setListenerData(float x, float y, float z)
	{
		listenerX = x;
		listenerY = y;
		listenerZ = z;
		AL10.alListener3f(AL10.AL_POSITION, listenerX,listenerY, listenerZ);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
	}
	
	public static void moveListener(float x, float y, float z)
	{
		listenerX += x;
		listenerY += y;
		listenerZ += z;
		AL10.alListener3f(AL10.AL_POSITION, listenerX,listenerY, listenerZ);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
	}
	
	public static int loadSound(String file)
	{
		int buffer = AL10.alGenBuffers();
		buffers.add(buffer);
		WaveData waveFile = WaveData.create(file);
		AL10.alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();
		return buffer;
	}
	
	public static void cleanUp()
	{
		for(int buffer:buffers)
		{
			AL10.alDeleteBuffers(buffer);
		}
		AL.destroy();
	}
}
