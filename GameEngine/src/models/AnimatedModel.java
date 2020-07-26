package models;

import animation.Joint;

public class AnimatedModel {
	TexturedModel model;
	Joint headJoint;
	int jointCount;
	
	public AnimatedModel(TexturedModel model, Joint headJoint, int jointCount)
	{
		this.headJoint = headJoint;
		this.jointCount = jointCount;
		this.model = model;
	}

	public TexturedModel getModel() {
		return model;
	}

	public Joint getHeadJoint() {
		return headJoint;
	}

	public int getJointCount() {
		return jointCount;
	}
	
}
