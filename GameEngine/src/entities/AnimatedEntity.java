package entities;

import models.TexturedModel;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import animation.Animation;
import animation.Animator;
import animation.Joint;
import audio.AudioMaster;
import audio.Source;
import collisionBox.CollisionBox;
import dataStructures.AnimatedModelData;

public class AnimatedEntity {

	private TexturedModel model;
	private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	private Source audioSource;
	
	public List<CollisionBox> collisionBoxes = new ArrayList<CollisionBox>();
	
	private int textureIndex = 0;
	
	private final Joint rootJoint;
	private final int jointCount;

	private final Animator animator;
	
	public AnimatedEntity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ,
			float scale, boolean emmitsSound, Joint rootJoint, int jointCount) {
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		
		this.rootJoint = rootJoint;
		this.animator = new Animator(this);
		this.jointCount = jointCount;
		rootJoint.calcInverseBindTransform(new Matrix4f());
		
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
	
	public void addCollisionBox(CollisionBox box)
	{
		this.collisionBoxes.add(box);
	}
	
	public CollisionBox hasCollided(List<AnimatedEntity> entities)
	{
		for(AnimatedEntity entity : entities)
		{
			if(entity.getPosition().x > this.position.x - 10 && entity.getPosition().y > this.position.y - 10 && entity.getPosition().z > this.position.z - 10 &&
					entity.getPosition().x < this.position.x + 10 && entity.getPosition().y < this.position.y + 10 && entity.getPosition().z < this.position.z + 10) {
				for(CollisionBox thisBox : collisionBoxes) {
					if(entity.collisionBoxes != null)
					{
						for(CollisionBox EntityBox : entity.collisionBoxes) {
							if(thisBox.hasCollided(EntityBox))						
							{
								return EntityBox;
							}     
						}
					}
				}
			}
		}
		return null;		
	}

	public Joint getRootJoint() {
		return rootJoint;
	}

	public int getJointCount() {
		return jointCount;
	}

	public Animator getAnimator() {
		return animator;
	}
	
	public void doAnimation(Animation animation) {
		animator.doAnimation(animation);
	}
	
	public void update() {
		animator.update();
	}
	
	public Matrix4f[] getJointTransforms() {
		Matrix4f[] jointMatrices = new Matrix4f[jointCount];
		addJointsToArray(rootJoint, jointMatrices);
		return jointMatrices;
	}
	
	private void addJointsToArray(Joint headJoint, Matrix4f[] jointMatrices) {
		jointMatrices[headJoint.index] = headJoint.getAnimatedTransform();
		for (Joint childJoint : headJoint.children) {
			addJointsToArray(childJoint, jointMatrices);
		}
	}

}
