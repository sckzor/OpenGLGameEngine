package audio;

import org.lwjgl.openal.AL10;

public class Source {

	private static int sourceId;
	
	private float sourceX;
	private float sourceY;
	private float sourceZ;
	
	public Source(float rolloffFactor, float referenceDistance, float maxDistance)
	{
		sourceId = AL10.alGenSources();
		AL10.alSourcef(sourceId, AL10.AL_ROLLOFF_FACTOR, rolloffFactor);
		AL10.alSourcef(sourceId, AL10.AL_REFERENCE_DISTANCE, referenceDistance);
		AL10.alSourcef(sourceId, AL10.AL_MAX_DISTANCE, maxDistance);
	}
	
	public void setVolume(float volume)
	{
		AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);

	}
	
	public void pause()
	{
		AL10.alSourcePause(sourceId);
	}
	
	public void stop()
	{
		AL10.alSourceStop(sourceId);
	}
	
	public void continuePlaying()
	{
		AL10.alSourcePlay(sourceId);
	}
	
	public void setPitch(float pitch)
	{
		AL10.alSourcef(sourceId, AL10.AL_PITCH, pitch);
	}
	
	public void setPosition(float x, float y, float z)
	{
		sourceX = x;
		sourceY = y;
		sourceZ = z;
		AL10.alSource3f(sourceId, AL10.AL_POSITION, sourceX, sourceY, sourceZ);
	}
	
	public void move(float x, float y, float z)
	{
		sourceX += x;
		sourceY += y;
		sourceZ += z;
		AL10.alSource3f(sourceId, AL10.AL_POSITION, sourceX, sourceY, sourceZ);
	}
	
	public void setVelocity(float x, float y, float z)
	{
		AL10.alSource3f(sourceId, AL10.AL_VELOCITY, x, y, z);
	}
	
	public void setLooping(boolean looping)
	{
		AL10.alSourcei(sourceId, AL10.AL_LOOPING, looping ? AL10.AL_TRUE : AL10.AL_FALSE);
	}
	
	public boolean isPlaying()
	{
		return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}
	
	public void play(int buffer)
	{
		stop();
		AL10.alSourcei(sourceId, AL10.AL_BUFFER, buffer);
		continuePlaying();
	}
	
	public void delete()
	{
		AL10.alDeleteSources(sourceId);
	}
}
