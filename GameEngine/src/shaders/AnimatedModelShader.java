package shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import toolbox.Maths;

import entities.Camera;
import entities.Light;

public class AnimatedModelShader extends ShaderProgram{
	
	private static final int MAX_LIGHTS = 4;
	private static final int MAX_JOINTS = 50;
	
	private static final String VERTEX_FILE = "src/shaders/animatedVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/shaders/animatedFragmentShader.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition[];
	private int location_lightColour[];
	private int location_attenuation[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_skyColour;
	private int location_numberOfRows;
	private int location_offset;
	private int location_clippingPlane;
	private int location_density;
	private int location_gradient;
	private int location_toShadowMapSpace;
	private int location_shadowMap;
	private int location_transitionDistance;
	private int location_shadowDistance;
	private int location_mapSize;
	private int location_specularMap;
	private int location_usesSpecularMap;
	private int location_modelTexture;
	private int location_jointTransforms[];
	
	
	public AnimatedModelShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "jointIndices");
		super.bindAttribute(4, "weights");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_specularMap = super.getUniformLocation("specularMap");
		location_usesSpecularMap = super.getUniformLocation("usesSpecularMap");
		location_modelTexture = super.getUniformLocation("modelTexture");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_skyColour = super.getUniformLocation("skyColour");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_offset = super.getUniformLocation("offset");
		location_clippingPlane = super.getUniformLocation("plane");
		location_density = super.getUniformLocation("density");
		location_gradient = super.getUniformLocation("gradient");
		location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
		location_shadowMap = super.getUniformLocation("shadowMap");
		location_transitionDistance = super.getUniformLocation("transitionDistance");
		location_shadowDistance = super.getUniformLocation("shadowDistance");
		location_mapSize = super.getUniformLocation("mapSize");
		
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColour = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		
		for(int i = 0; i < MAX_LIGHTS; i++)
		{
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
		
		location_jointTransforms = new int[MAX_JOINTS];
		
		for(int i = 0; i < MAX_JOINTS; i++)
		{
			location_jointTransforms[i] = super.getUniformLocation("jointTransforms[" + i + "]");
		}
	}
	
	
	public void loadMapSizeTransitionDistance(float size, float transition, float distance)
	{
		super.loadFloat(location_mapSize, size);
		super.loadFloat(location_shadowDistance ,distance);
		super.loadFloat(location_transitionDistance, transition);
	}
	
	public void connectTextureUnits()
	{
		super.loadInt(location_shadowMap, 5);
		super.loadInt(location_modelTexture, 0);
		super.loadInt(location_specularMap, 1);
	}
	
	public void loadUseSpecularMap(boolean useMap)
	{
		super.loadBoolean(location_usesSpecularMap, useMap);
	}
	
	public void loadToShadowSpaceMatrix(Matrix4f matrix)
	{
		super.loadMatrix(location_toShadowMapSpace, matrix);
	}
	
	public void loadDensityAndGradient(float density, float gradient)
	{
		super.loadFloat(location_gradient, gradient);
		super.loadFloat(location_density, density);
	}
	
	public void loadClippingPlane(Vector4f plane)
	{
		super.loadVector4D(location_clippingPlane, new Vector4f(plane.x, plane.y, plane.z, plane.w));
	}
	
	public void loadSkyColour(float r, float g, float b)
	{
		super.loadVector(location_skyColour, new Vector3f(r,g,b));
	}
	
	public void loadShineVariables(float damper,float reflectivity){
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadLights(List<Light> lights){
		for(int i = 0; i < MAX_LIGHTS; i++)
		{
			if (i<lights.size())
			{
				super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector(location_lightColour[i], lights.get(i).getColour());
				super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
			}
			else 
			{
				super.loadVector(location_lightPosition[i], new Vector3f(0, 0, 0));
				super.loadVector(location_lightColour[i], new Vector3f(0, 0, 0));
				super.loadVector(location_attenuation[i], new Vector3f(1, 0, 0));
			}
		}
	}
	
	public void loadJointTransforms(Matrix4f[] transforms)
	{
		for(int i = 0; i < transforms.length; i++)
		{
			super.loadMatrix(location_jointTransforms[i], transforms[i]);
		}
	}
	
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadUseFakeLighing(boolean useFake)
	{
		super.loadBoolean(location_useFakeLighting, useFake);
	}

	public void loadNumberOfRows(float rows)
	{
		super.loadFloat(location_numberOfRows, rows);
	}
	
	public void loadOffset(float x, float y)
	{
		super.loadVector2D(location_offset, x, y);
	}
}