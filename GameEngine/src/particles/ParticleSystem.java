package particles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import audio.AudioMaster;
import audio.Source;
import renderEngine.DisplayManager;

public class ParticleSystem {

	private float pps, averageSpeed, gravityComplient, averageLifeLength, averageScale;

	private float speedError, lifeError, scaleError = 0;
	private boolean randomRotation = false;
	private Vector3f direction;
	private float directionDeviation = 0;
	
	private Source audioSource;
	private Vector3f systemCenter;

	private Random random = new Random();
	private ParticleTexture texture;

	public ParticleSystem(ParticleTexture texture, float pps, float speed, float gravityComplient, float lifeLength, float scale, Vector3f systemCenter, boolean emmitsSound) {
		this.texture = texture;
		this.pps = pps;
		this.averageSpeed = speed;
		this.gravityComplient = gravityComplient;
		this.averageLifeLength = lifeLength;
		this.averageScale = scale;
		this.systemCenter = systemCenter;
		if(emmitsSound) {
			audioSource = new Source(AudioMaster.DEFAULT_ROLL_OFF_FACTOR, AudioMaster.DEFAULT_REFERENCE_DISTANCE, AudioMaster.DEFAULT_MAX_DISTANCE);
			audioSource.setPosition(systemCenter.x, systemCenter.y, systemCenter.z);
		}
	}
	
	public void playAudio(String sourceName, float volume, float pitch, boolean looping)
	{
		if(audioSource != null) {
			int buffer = AudioMaster.loadSound("audio/" + sourceName + ".wav");
			audioSource.setLooping(looping);
			audioSource.setVolume(volume);
			audioSource.setPitch(pitch);
			audioSource.play(buffer);
		}
	}
	
	public void stopAudio()
	{
		if(audioSource != null) {
			audioSource.stop();
		}
	}
	
	public void moveSystem(Vector3f centerOffset)
	{
		systemCenter.x += centerOffset.x;
		systemCenter.y += centerOffset.y;
		systemCenter.z += centerOffset.z;
		if(audioSource != null) {
			audioSource.setPosition(systemCenter.x, systemCenter.y, systemCenter.z);
		}
	}

	public void setDirection(Vector3f direction, float deviation) {
		this.direction = new Vector3f(direction);
		this.directionDeviation = (float) (deviation * Math.PI);
	}

	public void randomizeRotation() {
		randomRotation = true;
	}

	public void setSpeedError(float error) {
		this.speedError = error * averageSpeed;
	}

	public void setLifeError(float error) {
		this.lifeError = error * averageLifeLength;
	}

	public void setScaleError(float error) {
		this.scaleError = error * averageScale;
	}

	public void generateParticles() {
		float delta = DisplayManager.getFrameTimeSeconds();
		float particlesToCreate = pps * delta;
		int count = (int) Math.floor(particlesToCreate);
		float partialParticle = particlesToCreate % 1;
		for (int i = 0; i < count; i++) {
			emitParticle(systemCenter);
		}
		if (Math.random() < partialParticle) {
			emitParticle(systemCenter);
		}
	}

	private void emitParticle(Vector3f center) {
		Vector3f velocity = null;
		if(direction!=null){
			velocity = generateRandomUnitVectorWithinCone(direction, directionDeviation);
		}else{
			velocity = generateRandomUnitVector();
		}
		velocity.normalise();
		velocity.scale(generateValue(averageSpeed, speedError));
		float scale = generateValue(averageScale, scaleError);
		float lifeLength = generateValue(averageLifeLength, lifeError);
		new Particle(texture, new Vector3f(center), velocity, gravityComplient, lifeLength, generateRotation(), scale);
	}

	private float generateValue(float average, float errorMargin) {
		float offset = (random.nextFloat() - 0.5f) * 2f * errorMargin;
		return average + offset;
	}

	private float generateRotation() {
		if (randomRotation) {
			return random.nextFloat() * 360f;
		} else {
			return 0;
		}
	}

	private static Vector3f generateRandomUnitVectorWithinCone(Vector3f coneDirection, float angle) {
		float cosAngle = (float) Math.cos(angle);
		Random random = new Random();
		float theta = (float) (random.nextFloat() * 2f * Math.PI);
		float z = cosAngle + (random.nextFloat() * (1 - cosAngle));
		float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
		float x = (float) (rootOneMinusZSquared * Math.cos(theta));
		float y = (float) (rootOneMinusZSquared * Math.sin(theta));

		Vector4f direction = new Vector4f(x, y, z, 1);
		if (coneDirection.x != 0 || coneDirection.y != 0 || (coneDirection.z != 1 && coneDirection.z != -1)) {
			Vector3f rotateAxis = Vector3f.cross(coneDirection, new Vector3f(0, 0, 1), null);
			rotateAxis.normalise();
			float rotateAngle = (float) Math.acos(Vector3f.dot(coneDirection, new Vector3f(0, 0, 1)));
			Matrix4f rotationMatrix = new Matrix4f();
			rotationMatrix.rotate(-rotateAngle, rotateAxis);
			Matrix4f.transform(rotationMatrix, direction, direction);
		} else if (coneDirection.z == -1) {
			direction.z *= -1;
		}
		return new Vector3f(direction);
	}
	
	private Vector3f generateRandomUnitVector() {
		float theta = (float) (random.nextFloat() * 2f * Math.PI);
		float z = (random.nextFloat() * 2) - 1;
		float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
		float x = (float) (rootOneMinusZSquared * Math.cos(theta));
		float y = (float) (rootOneMinusZSquared * Math.sin(theta));
		return new Vector3f(x, y, z);
	}

}