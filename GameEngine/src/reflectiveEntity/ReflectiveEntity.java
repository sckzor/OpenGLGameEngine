package reflectiveEntity;

import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import environmentMapRenderer.CubeMap;
import models.TexturedModel;

public class ReflectiveEntity extends Entity {

	private float refractivity;
	private float reflectivity;
	private CubeMap enviroMap;
	
	public ReflectiveEntity(TexturedModel model, CubeMap envMap, int index, Vector3f position, float rotX, float rotY, float rotZ,
			float scale, float reflect, float refract) {
		super(model, index, position, rotX, rotY, rotZ, scale);
		this.reflectivity = reflect;
		this.refractivity = refract;
		this.enviroMap = envMap;
	}
	
	public ReflectiveEntity(TexturedModel model, CubeMap envMap, Vector3f position, float rotX, float rotY, float rotZ,
			float scale, float reflect, float refract) {
		super(model, position, rotX, rotY, rotZ, scale);
		this.reflectivity = reflect;
		this.refractivity = refract;
		this.enviroMap = envMap;
	}

	public float getRefractivity() {
		return refractivity;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public CubeMap getEnviroMap() {
		return enviroMap;
	}

}
