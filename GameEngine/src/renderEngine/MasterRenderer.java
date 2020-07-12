package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.TexturedModel;
import normalMappingRenderer.NormalMappingRenderer;
import scene.Scene;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import shaders.StaticShader;
import shaders.TerrainShader;
import shadows.ShadowMapMasterRenderer;
import terrains.Terrain;
import entities.Camera;
import entities.Entity;
import entities.Light;
import environmentMapRenderer.CubeMap;
import environmentMapRenderer.SkyBoxRenderer;

public class MasterRenderer {
	
	public static final float FOV = 70;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000;
	
	public static final float RED = 0.5f;
	public static final float GREEN = 0.5f;
	public static final float BLUE = 0.5f;
	
	public static final float DENSITY = 0.003f;
	public static final float GRADIENT = 5.0f;
	
	public static final String[] TEXTURE_FILES = {"right", "left", "top", "bottom", "back", "front"};
	public static final String[] ENVIRO_MAP_SNOW = {"cposx", "cnegx", "cposy", "cnegy", "cposz", "cnegz"};
	public static final String[] ENVIRO_MAP_LAKE = {"posx", "negx", "posy", "negy", "posz", "negz"};
	public static final String[] ENVIRO_MAP_INSIDE = {"lposx", "lnegx", "lposy", "lnegy", "lposz", "lnegz"};
	
	private Matrix4f projectionMatrix;
	
	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;
	
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	
	private NormalMappingRenderer normalMapRenderer;
	private ShadowMapMasterRenderer shadowMapRenderer;
	
	
	private Map<TexturedModel,List<Entity>> entities = new HashMap<TexturedModel,List<Entity>>();
	private Map<TexturedModel,List<Entity>> normalMappedEntities = new HashMap<TexturedModel,List<Entity>>();
		
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private SkyBoxRenderer skyboxRenderer;
	
	public MasterRenderer(Loader loader, Scene scene){
		enableCulling();
		createProjectionMatrix();
		renderer = new EntityRenderer(shader,projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader,projectionMatrix);
		CubeMap enviroMap = new CubeMap(TEXTURE_FILES, loader);
		skyboxRenderer = new SkyBoxRenderer(enviroMap, projectionMatrix);
		normalMapRenderer = new NormalMappingRenderer(projectionMatrix);
		this.shadowMapRenderer = new ShadowMapMasterRenderer(scene.getCamera());
	}
	
	
	public Matrix4f getProjectionMatrix()
	{
		return projectionMatrix;
	}
	
	public void renderScene(Scene scene, Vector4f clippingPlane)
	{
		for (Terrain terrain:scene.getTerrains())
		{
			processTerrain(terrain);
		}
		for (Entity entity:scene.getEntities())
		{
			processEntity(entity);
		}
		for (Entity entity:scene.getNormalMappedEntities())
		{
			processNormalMappedEntity(entity);
		}
		
		
		render(scene.getLight(), scene.getCamera(), clippingPlane);
	}
	
	public void render(List<Light> lights,Camera camera, Vector4f clippingPlane){
		prepare();
		shader.start();
		shader.loadClippingPlane(clippingPlane);
		shader.loadSkyColour(RED, GREEN, BLUE);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		renderer.render(entities, shadowMapRenderer.getToShadowMapSpaceMatrix());
		shader.stop();
		normalMapRenderer.render(normalMappedEntities, clippingPlane, lights, camera, shadowMapRenderer.getToShadowMapSpaceMatrix());
		terrainShader.start();
		terrainShader.loadClippingPlane(clippingPlane);
		terrainShader.loadSkyColour(RED, GREEN, BLUE);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());
		terrainShader.stop();
		skyboxRenderer.render(camera, RED, GREEN, BLUE);
		terrains.clear();
		entities.clear();
		normalMappedEntities.clear();
		
	}
	
	public void processTerrain(Terrain terrain){
		terrains.add(terrain);
	}
	
	public void processEntity(Entity entity){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if(batch!=null){
			batch.add(entity);
		}else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);		
		}
	}
	
	public void processNormalMappedEntity(Entity entity){
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = normalMappedEntities.get(entityModel);
		if(batch!=null){
			batch.add(entity);
		}else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			normalMappedEntities.put(entityModel, newBatch);		
		}
	}
	
	public void renderShadowMap(Scene scene)
	{
		for(Entity entity:scene.getEntities())
		{
			processEntity(entity);
		}
		shadowMapRenderer.render(entities, scene.getSun());
		entities.clear();
	}
	
	public int getShadowMapTexture()
	{
		return shadowMapRenderer.getShadowMap();
	}
	
	public void cleanUp(){
		shader.cleanUp();
		terrainShader.cleanUp();
		normalMapRenderer.cleanUp();
		shadowMapRenderer.cleanUp();
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1);
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
	}
	
	public static void enableCulling()
	{
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling()
	{
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	 private void createProjectionMatrix(){
	    	projectionMatrix = new Matrix4f();
			float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
			float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
			float x_scale = y_scale / aspectRatio;
			float frustum_length = FAR_PLANE - NEAR_PLANE;

			projectionMatrix.m00 = x_scale;
			projectionMatrix.m11 = y_scale;
			projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
			projectionMatrix.m23 = -1;
			projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
			projectionMatrix.m33 = 0;
	}
}