package animation;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;

import entities.AnimatedEntity;
import renderEngine.DisplayManager;
import shaders.AnimatedModelShader;

public class Animator {

	private final AnimatedEntity entity;

	private Animation currentAnimation;
	private float speedMultiplier = 1;
	private float animationTime = 0;
	private boolean isPlaying= false;
	private boolean shouldPlay= false;
	
	public Animator(AnimatedEntity entity) {
		this.entity = entity;
	}

	public void doAnimation(String animation, float speedMultiplier) {
		this.speedMultiplier = speedMultiplier;
		if(animation != null) {
			shouldPlay = true;
		}else {
			shouldPlay = false;
		}
		if(shouldPlay && !isPlaying) {
			this.animationTime = 0;
			this.currentAnimation = entity.getAnimations(animation);
			isPlaying = true;
		}
		if(!shouldPlay && isPlaying)
		{
			this.currentAnimation = null;
			isPlaying = false;
		}
	}

	public void update() {
		if (currentAnimation == null) {
			return;
		}
		increaseAnimationTime();
		Map<String, Matrix4f> currentPose = calculateCurrentAnimationPose();
		applyPoseToJoints(currentPose, entity.getRootJoint(), new Matrix4f());

	}

	private void increaseAnimationTime() {
		animationTime += DisplayManager.getFrameTimeSeconds()*speedMultiplier;
		if (animationTime > currentAnimation.getLength()) {
			this.animationTime %= currentAnimation.getLength();
		}
	}

	private Map<String, Matrix4f> calculateCurrentAnimationPose() {
		KeyFrame[] frames = getPreviousAndNextFrames();
		float progression = calculateProgression(frames[0], frames[1]);
		return interpolatePoses(frames[0], frames[1], progression);
	}

	private void applyPoseToJoints(Map<String, Matrix4f> currentPose, Joint joint, Matrix4f parentTransform) {
		Matrix4f currentLocalTransform = currentPose.get(joint.name);
		Matrix4f currentTransform = Matrix4f.mul(parentTransform, currentLocalTransform, null);
		for (Joint childJoint : joint.children) {
			applyPoseToJoints(currentPose, childJoint, currentTransform);
		}
		Matrix4f.mul(currentTransform, joint.getInverseBindTransform(), currentTransform);
		joint.setAnimationTransform(currentTransform);
	}

	private KeyFrame[] getPreviousAndNextFrames() {
		KeyFrame[] allFrames = currentAnimation.getKeyFrames();
		KeyFrame previousFrame = allFrames[0];
		KeyFrame nextFrame = allFrames[0];
		for (int i = 1; i < allFrames.length; i++) {
			nextFrame = allFrames[i];
			if (nextFrame.getTimeStamp() > animationTime) {
				break;
			}
			previousFrame = allFrames[i];
		}
		return new KeyFrame[] { previousFrame, nextFrame };
	}

	private float calculateProgression(KeyFrame previousFrame, KeyFrame nextFrame) {
		float totalTime = nextFrame.getTimeStamp() - previousFrame.getTimeStamp();
		float currentTime = animationTime - previousFrame.getTimeStamp();
		return currentTime / totalTime;
	}

	private Map<String, Matrix4f> interpolatePoses(KeyFrame previousFrame, KeyFrame nextFrame, float progression) {
		Map<String, Matrix4f> currentPose = new HashMap<String, Matrix4f>();
		for (String jointName : previousFrame.getJointKeyFrames().keySet()) {
			JointTransform previousTransform = previousFrame.getJointKeyFrames().get(jointName);
			JointTransform nextTransform = nextFrame.getJointKeyFrames().get(jointName);
			JointTransform currentTransform = JointTransform.interpolate(previousTransform, nextTransform, progression);
			currentPose.put(jointName, currentTransform.getLocalTransform());
		}
		return currentPose;
	}

}
