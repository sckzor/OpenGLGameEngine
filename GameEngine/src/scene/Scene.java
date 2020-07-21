package scene;

import java.util.ArrayList;
import java.util.List;

import entities.AnimatedEntity;
import entities.Camera;
import entities.Entity;
import entities.Light;
import environmentMapRenderer.CubeMap;
import guis.GuiTexture;
import lensFlare.FlareTexture;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrains.Terrain;
import water.WaterTile;

public class Scene {
	private List<Entity> entities = new ArrayList<Entity>();
	private List<Entity> normalMappedEntities = new ArrayList<Entity>();
	private List<CubeMap> envMaps = new ArrayList<CubeMap>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private List<GuiTexture> guis = new ArrayList<GuiTexture>();
	private List<WaterTile> waters = new ArrayList<WaterTile>();
	private List<Light> lights = new ArrayList<Light>();
	private List<FlareTexture> flares = new ArrayList<FlareTexture>();
	private List<AnimatedEntity> animatedEntites = new ArrayList<AnimatedEntity>();
	private Light sun;
	private Camera camera;
	private Loader loader = new Loader();
	private MasterRenderer renderer;

	
	//---------------------------- Entities ----------------------------\\
	
	public void addEntity(Entity entity)
	{
		entities.add(entity);
	}
	
	public List<Entity> getEntities() {
		return entities;
	}
	
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	
	//--------------------- Normal Mapped Entities ---------------------\\
	
	public void addNormalMappedEntity(Entity entity)
	{
		normalMappedEntities.add(entity);
	}
	
	public List<Entity> getNormalMappedEntities() {
		return normalMappedEntities;
	}
	
	public void setNormalMappedEntities(List<Entity> normalMappedEntities) {
		this.normalMappedEntities = normalMappedEntities;
	}
	
	//------------------------- Environment Maps ------------------------\\
	
	public void addEnvMap(CubeMap envMap)
	{
		envMaps.add(envMap);
	}
	
	public List<CubeMap> getEnvMaps() {
		return envMaps;
	}
	
	public void setEnvMaps(List<CubeMap> envMaps) {
		this.envMaps = envMaps;
	}
	
	//----------------------------- Terrains ----------------------------\\
	
	public void addTerrain(Terrain terrain)
	{
		terrains.add(terrain);
	}
	
	public List<Terrain> getTerrains() {
		return terrains;
	}
	
	public void setTerrains(List<Terrain> terrains) {
		this.terrains = terrains;
	}
	
	//------------------------------- GUIS ------------------------------\\
	
	public void addGui(GuiTexture gui)
	{
		guis.add(gui);
	}
	
	public List<GuiTexture> getGuis() {
		return guis;
	}
	
	public void setGuis(List<GuiTexture> guis) {
		this.guis = guis;
	}
	
	//------------------------------ Waters -----------------------------\\
	
	public void addWater(WaterTile water)
	{
		waters.add(water);
	}
	
	public List<WaterTile> getWaters() {
		return waters;
	}
	
	public void setWaters(List<WaterTile> waters) {
		this.waters = waters;
	}
	
	//------------------------------ Lights -----------------------------\\
	
	public void addLight(Light light)
	{
		lights.add(light);
	}
	
	public List<Light> getLight() {
		return lights;
	}
	
	public void setLight(List<Light> lights) {
		this.lights = lights;
	}

	//------------------------------- Sun -------------------------------\\

	public Light getSun()
	{
		return sun;
	}
	
	public void setSun(Light sun)
	{
		this.sun = sun;
		addLight(sun);
	}

	//------------------------------ Camera -----------------------------\\
	
	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	//------------------------------ Loader -----------------------------\\

	public Loader getLoader() {
		return loader;
	}

	public void setLoader(Loader loader) {
		this.loader = loader;
	}
	
	//----------------------------- Renderer ----------------------------\\

	public MasterRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(MasterRenderer renderer) {
		this.renderer = renderer;
	}

	//---------------------------- Lens Flare ---------------------------\\
	
	public void addFlare(FlareTexture flare)
	{
		flares.add(flare);
	}
	
	public List<FlareTexture> getFlares() {
		return flares;
	}

	public void setFlares(List<FlareTexture> flares) {
		this.flares = flares;
	}
	
	//------------------------ Animated Entities ------------------------\\

	public void addAnimatedEntity(AnimatedEntity entity) {
		animatedEntites.add(entity);
	}
	
	public List<AnimatedEntity> getAnimatedEntites() {
		return animatedEntites;
	}

	public void setAnimatedEntites(List<AnimatedEntity> animatedEntites) {
		this.animatedEntites = animatedEntites;
	}
}
