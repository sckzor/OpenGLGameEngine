package entities;

import org.lwjgl.util.vector.Vector3f;

import animation.Joint;
import colladaLoader.ColladaLoader;
import dataStructures.AnimatedModelData;
import dataStructures.JointData;
import dataStructures.MeshData;
import dataStructures.SkeletonData;
import models.RawModel;
import models.TexturedModel;
import renderEngine.Loader;
import textures.ModelTexture;

public class AnimatedModelLoader {

	public static AnimatedEntity loadEntity(String modelFile, String textureFile, Loader loader, Vector3f position,
			float rotX, float rotY, float rotZ, float scale, boolean emmitsSound) {
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFile, 3);
		RawModel model = loader.loadToVAO(entityData.getMeshData().getVertices(), entityData.getMeshData().getTextureCoords(), 
				entityData.getMeshData().getNormals(), entityData.getMeshData().getIndices(), entityData.getMeshData().getJointIds(),
				entityData.getMeshData().getVertexWeights());
		ModelTexture texture = new ModelTexture(loader.loadTexture(textureFile, -0.4f));
		SkeletonData skeletonData = entityData.getJointsData();
		Joint headJoint = createJoints(skeletonData.headJoint);
		return new AnimatedEntity(new TexturedModel(model, texture), position, rotX, rotY, rotZ,
				scale, emmitsSound, headJoint, skeletonData.jointCount);
	}

	private static Joint createJoints(JointData data) {
		Joint joint = new Joint(data.index, data.nameId, data.bindLocalTransform);
		for (JointData child : data.children) {
			joint.addChild(createJoints(child));
		}
		return joint;
	}
}