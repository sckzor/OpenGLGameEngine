package entities;

import models.AnimatedModel;
import models.TexturedModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import animation.Animation;
import animation.Animator;
import animation.Joint;
import audio.AudioMaster;
import audio.Source;
import dataStructures.AnimatedModelData;

public class AnimatedEntity extends Entity{		
	private final Joint rootJoint;
	private final int jointCount;

	private List<Animation> animations = new ArrayList<Animation>();
	private final Animator animator;
	
	
	public AnimatedEntity(AnimatedModel model, Vector3f position, float rotX, float rotY, float rotZ,
			float scale, boolean emmitsSound, Animation...animations) {
		super(model.getModel(), position, rotX, rotY, rotZ, scale, false);
		
		this.rootJoint = model.getHeadJoint();
		this.animator = new Animator(this);
		this.jointCount = model.getJointCount();
		rootJoint.calcInverseBindTransform(new Matrix4f());
		for(Animation animation: animations)
		{
			this.animations.add(animation);
		}
	}

	public Animation getAnimations(String animName) {
		for(Animation animation: animations)
		{
			if(animation.getName() == animName)
			{
				return animation;
			}
		}
		System.err.println("[NON-FATAL] No animation called" + animName);
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
	
	public void doAnimation(String animation, float multiplier) {
		animator.doAnimation(animation, multiplier);
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
	
	public void addAnimation(Animation animation)
	{
		animations.add(animation);
	}
}
