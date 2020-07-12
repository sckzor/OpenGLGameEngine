package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import particles.Particle;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import postProcessing.Fbo;
import postProcessing.PostProcessing;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import audio.AudioMaster;
import audio.Source;
import collisionBox.CollisionBox;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import scene.Scene;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import environmentMapRenderer.CubeMap;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;

public class MainGameLoop {

	public static void main(String[] args) {
		Scene scene = new Scene();
		
		DisplayManager.createDisplay();
		
		AudioMaster.init();
		AudioMaster.setListenerData(0, 0, 0);
		
		Loader loader = new Loader();
		TextMaster.init(loader);
		
		RawModel bunny = OBJLoader.loadObjModel("bunny", loader);
		TexturedModel staticBunny = new TexturedModel(bunny, new ModelTexture(loader.loadTexture("white", -0.4f)));
		Player player = new Player(staticBunny, new Vector3f(400, 10, 400), 0, 180, 0, 0.6f);
		player.addCollisionBox(new CollisionBox(new Vector3f(390, 0, 390),new Vector3f(410, 20, 410)));
		
		scene.setCamera(new Camera(false, player));	
		
		MasterRenderer renderer = new MasterRenderer(loader, scene);	
		ParticleMaster.init(loader, renderer.getProjectionMatrix()); 
		
		FontType font = new FontType(loader.loadTexture("arial", 0), new File("res/arial.fnt"));
		GUIText PlayerXposition = new GUIText("X: ---", 1, font, new Vector2f(0.05f, 0.05f), 0.1f, true);
		PlayerXposition.setColour(1, 1, 1);
		
		GUIText PlayerYposition = new GUIText("Y: ---", 1, font, new Vector2f(0.05f, 0.07f), 0.1f, true);
		PlayerYposition.setColour(1, 1, 1);
		
		GUIText PlayerZposition = new GUIText("Z: ---", 1, font, new Vector2f(0.05f, 0.09f), 0.1f, true);
		PlayerZposition.setColour(1, 1, 1);
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2", -0.4f));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud", -0.4f));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers", -0.4f));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path", -0.4f));
		
		TerrainTexturePack texturePack= new TerrainTexturePack(backgroundTexture, rTexture,
				gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap", -0.4f));
		
		RawModel model = OBJLoader.loadObjModel("tree", loader);
		TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("tree", -0.4f)));
		
		RawModel lamp = OBJLoader.loadObjModel("lantern", loader);
		TexturedModel staticlamp = new TexturedModel(lamp,new ModelTexture(loader.loadTexture("lantern", -0.4f)));
		staticlamp.getTexture().setSpecularMap(loader.loadTexture("lanternS", 0.4f));
		
		RawModel grass = OBJLoader.loadObjModel("grassModel", loader);
		TexturedModel staticGrass = new TexturedModel(grass,new ModelTexture(loader.loadTexture("grassTexture", -0.4f)));
		staticGrass.getTexture().setHasTrasparency(true);
		staticGrass.getTexture().setUseFakeLighting(true);
		
		RawModel fern = OBJLoader.loadObjModel("fern", loader);
		ModelTexture fernTexture = new ModelTexture(loader.loadTexture("fern", -0.4f));
		fernTexture.setNumberOfRows(2);
		TexturedModel staticFern = new TexturedModel(fern, fernTexture);
		staticFern.getTexture().setHasTrasparency(true);

		CubeMap enviroMap = new CubeMap(MasterRenderer.ENVIRO_MAP_SNOW, loader);	

		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader), new ModelTexture(loader.loadTexture("barrel", -0.4f)));
		barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal", -0.4f));
		barrelModel.getTexture().setShineDamper(10);
		barrelModel.getTexture().setReflectivity(0.5f);
		barrelModel.getTexture().setSpecularMap(loader.loadTexture("barrelS", -0.4f));
		
		scene.addNormalMappedEntity(new Entity(barrelModel, new Vector3f(400, 10, 400), 0, 0, 0, 1f, false));
		
		
		RawModel cherry = OBJLoader.loadObjModel("cherry", loader);
		TexturedModel staticCherry = new TexturedModel(cherry,new ModelTexture(loader.loadTexture("cherry", -0.4f)));
		staticGrass.getTexture().setHasTrasparency(true);
		barrelModel.getTexture().setSpecularMap(loader.loadTexture("cherryS", -0.4f));
		
		Terrain terrain = new Terrain(0, 0, loader, texturePack, blendMap, "heightmap");

		scene.addTerrain(terrain);
		
		
		GuiTexture gui = new GuiTexture(loader.loadTexture("cat", -0.4f), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));		
		scene.addGui(gui);
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		Random random = new Random();
		for(int i=0;i<200;i++){
			float x = random.nextFloat()* 800;
			float z = random.nextFloat()* 800;
			float y = terrain.getHeightOfTerrain(x, z);
			Entity tree = new Entity(staticCherry, new Vector3f(x, y, z),0f,0f,0f,3f, false);
			tree.addCollisionBox(new CollisionBox(new Vector3f(x-15, y-100, z-15), new Vector3f(x+15, y+100, z+15)));
			scene.addEntity(tree);
			x = random.nextFloat()* 800;
			z = random.nextFloat()* 800;
			y = terrain.getHeightOfTerrain(x, z);
			scene.addEntity(new Entity(staticGrass, new Vector3f(x, y, z),0,0,0,1, false));
			x = random.nextFloat()* 800;
			z = random.nextFloat()* 800;
			y = terrain.getHeightOfTerrain(x, z);
			scene.addEntity(new Entity(staticFern, random.nextInt(4), new Vector3f(x, y, z),0,0,0,1, false));
		}
		
		scene.setSun(new Light(new Vector3f(1000000,1500000,-1000000),new Vector3f(1f,1f,1f)));
		Light light = new Light(new Vector3f(400,-8,400),new Vector3f(0f,2f,0f), new Vector3f(1f,0.01f,0.002f));
		scene.addLight(light);
		
		scene.addEntity(player);
		
		
		MousePicker picker = new MousePicker(scene, renderer.getProjectionMatrix(), terrain);
		
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
		
		scene.addWater(new WaterTile(400, 400, -8));		
		
		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("fire", -0.4f), 8);
		
		ParticleSystem system = new ParticleSystem(particleTexture, 40, 10, -10f, 10, 10f, new Vector3f(410, 8, 410), true);
		system.playAudio("fire", 3, 1, true);
		system.randomizeRotation();
		system.setDirection(new Vector3f(0, 1, 0), 0.1f);
		system.setLifeError(0.1f);
		system.setSpeedError(0.4f);
		system.setScaleError(0.5f);
		
		Fbo multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight(), true);
		Fbo outputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		Fbo outputFbo2 = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);

		PostProcessing.init(loader);
				
		Entity lampPost = new Entity(staticlamp, new Vector3f(400,-8, 400), 0, 0, 0, 1, true);
		scene.addEntity(lampPost);
		lampPost.playAudio("bounce", 1, 1, true);

		
		while(!Display.isCloseRequested()){
			player.move(scene);
			scene.getCamera().move();
			picker.update();

			
			system.generateParticles();
			
			ParticleMaster.update(scene);
						
			renderer.renderShadowMap(scene);
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			fbos.bindReflectionFrameBuffer();
			float distance = 2 * (scene.getCamera().getPosition().y - scene.getWaters().get(0).getHeight()+1f);
			scene.getCamera().getPosition().y -= distance;
			scene.getCamera().invertPitch();
			renderer.renderScene(scene, new Vector4f(0, 1, 0, -scene.getWaters().get(0).getHeight()));
			scene.getCamera().getPosition().y += distance;
			scene.getCamera().invertPitch();
			
			fbos.bindRefractionFrameBuffer();
			renderer.renderScene(scene, new Vector4f(0, -1, 0, scene.getWaters().get(0).getHeight()+1));
			
			fbos.unbindCurrentFrameBuffer();
			
			Vector3f terrainPoint = picker.getCurrentTerrainPoint();
			
			if(terrainPoint!=null)
			{
				lampPost.setPosition(terrainPoint);
				light.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y+9, terrainPoint.z));
			}

			multisampleFbo.bindFrameBuffer();
			renderer.renderScene(scene, new Vector4f(0, -1, 0, 100000000));
			waterRenderer.render(scene);
			ParticleMaster.renderParticles(scene, false);
			
			multisampleFbo.unbindFrameBuffer();
			multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, outputFbo);
			multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, outputFbo2);
			PostProcessing.doPostProcessing(outputFbo.getColourTexture(), outputFbo2.getColourTexture());
			
			guiRenderer.render(scene);
			PlayerXposition.setTextString("X: " + String.valueOf(player.getPosition().x));
			PlayerYposition.setTextString("Y: " + String.valueOf(player.getPosition().y));
			PlayerZposition.setTextString("Z: " + String.valueOf(player.getPosition().z));

			TextMaster.render();
			
			DisplayManager.updateDisplay();
		}

		outputFbo.cleanUp();
		outputFbo2.cleanUp();
		PostProcessing.cleanUp();
		multisampleFbo.cleanUp();
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		fbos.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		AudioMaster.cleanUp();

	}

}