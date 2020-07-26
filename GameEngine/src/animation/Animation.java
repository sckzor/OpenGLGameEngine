package animation;

public class Animation {

	private final float length;//in seconds
	private final KeyFrame[] keyFrames;
	private String name;

	public Animation(float lengthInSeconds, KeyFrame[] frames, String name) {
		this.keyFrames = frames;
		this.length = lengthInSeconds;
		this.name = name;
	}

	public float getLength() {
		return length;
	}

	public KeyFrame[] getKeyFrames() {
		return keyFrames;
	}

	public String getName() {
		return name;
	}

}
