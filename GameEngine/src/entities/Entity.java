package entities;

import models.TexturedModel;
import physics.PhysicsMaster;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.dynamics.RigidBody;

import audio.AudioMaster;
import audio.Source;

public class Entity {

	private TexturedModel model;
	protected RigidBody physicsBody;
	private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	private Source audioSource;
		
	private int textureIndex = 0;
	
	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
			float scale, boolean emmitsSound) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		if(emmitsSound) {
			audioSource = new Source(AudioMaster.DEFAULT_ROLL_OFF_FACTOR, AudioMaster.DEFAULT_REFERENCE_DISTANCE, AudioMaster.DEFAULT_MAX_DISTANCE);
			audioSource.setPosition(position.x, position.y + 3, position.z);
		}
		
	}

	public Entity(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ,
			float scale, boolean emmitsSound) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.textureIndex = index;
		if(emmitsSound) {
			audioSource = new Source(AudioMaster.DEFAULT_ROLL_OFF_FACTOR, AudioMaster.DEFAULT_REFERENCE_DISTANCE, AudioMaster.DEFAULT_MAX_DISTANCE);
			audioSource.setPosition(position.x, position.y + 3, position.z);
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


	public float getTextureXOffset()
	{
		int column = textureIndex%model.getTexture().getNumberOfRows();
		return (float)column/(float)model.getTexture().getNumberOfRows();
	}
	
	public float getTextureYOffset()
	{
		int row = textureIndex%model.getTexture().getNumberOfRows();
		return (float)row/(float)model.getTexture().getNumberOfRows();
	}
	
	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
		if(audioSource != null) {
			audioSource.setPosition(position.x, position.y, position.z);
		}

	}

	public void increaseRotation(float dx, float dy, float dz) {
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}

	public TexturedModel getModel() {
		return model;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
		if(audioSource != null) {
			audioSource.setPosition(position.x, position.y, position.z);
		}
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public void addPhysicsBody(RigidBody body)
	{
		this.physicsBody = body;
		PhysicsMaster.addRigidBody(body);
	}
	
	public void update()
	{
		if(physicsBody != null) {
			Vector3f transform = PhysicsMaster.getPos(physicsBody);
			setPosition(new Vector3f(transform.x, transform.y, transform.z));
		}
	}
}
