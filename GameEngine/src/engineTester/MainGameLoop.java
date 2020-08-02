package engineTester;

import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import models.AnimatedModel;
import models.RawModel;
import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;
import particles.Particle;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import physics.PhysicsHelper;
import physics.PhysicsMaster;
import postProcessing.Fbo;
import postProcessing.PostProcessing;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.ImageIOImageData;

import animation.Animation;
import audio.AudioMaster;
import audio.Source;
import colladaLoader.ColladaLoader;
import dataStructures.AnimatedModelData;
import dataStructures.AnimationData;
import renderEngine.AnimationLoader;
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
import entities.AnimatedEntity;
import entities.AnimatedModelLoader;
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
import guis.Menu;
import lensFlare.FlareMaster;
import lensFlare.FlareRenderer;
import lensFlare.FlareTexture;

public class MainGameLoop {	
	public static void main(String[] args) {
		Scene scene = new Scene();
		
		DisplayManager.createDisplay(false, DisplayManager.WIDTH, DisplayManager.HEIGHT);
		PhysicsMaster.initPhysics();
		
		AudioMaster.init();
		AudioMaster.setListenerData(0, 0, 0);
		
		Loader loader = new Loader();
		
		TextMaster.init(loader);	
		Mouse.setGrabbed(true);
		AnimatedModel animModel = AnimatedModelLoader.loadEntity("model", "diffuse", loader);
		Animation animation = AnimationLoader.loadAnimation("model");
				
		Player player = new Player(animModel, new Vector3f(400, 0, 400), 0, 0, 0, 1f, true);
		player.addAnimation(animation);
		player.addPhysicsBody(PhysicsHelper.createCapsule(2f, 1f, 1, 387, 100, 392));
		scene.addAnimatedEntity(player);
		
		scene.setCamera(new Camera(false, player));	
		scene.setRenderer(new MasterRenderer(loader, scene));
		
		ParticleMaster.init(loader, scene.getRenderer().getProjectionMatrix()); 
		
		FontType font = new FontType(loader.loadTexture("arial", 0), new File("res/arial.fnt"));
		
		GUIText Time = new GUIText("--:--", 1, font, new Vector2f(0.02f, 0.03f), 0.2f, true);
		Time.setColour(1, 1, 1);
		
		GUIText PlayerXposition = new GUIText("X: ---", 1, font, new Vector2f(0.02f, 0.05f), 0.2f, true);
		PlayerXposition.setColour(1, 1, 1);
		
		GUIText PlayerYposition = new GUIText("Y: ---", 1, font, new Vector2f(0.02f, 0.07f), 0.2f, true);
		PlayerYposition.setColour(1, 1, 1);
		
		GUIText PlayerZposition = new GUIText("Z: ---", 1, font, new Vector2f(0.02f, 0.09f), 0.2f, true);
		PlayerZposition.setColour(1, 1, 1);
		
		GUIText FPS = new GUIText("FPS: --", 1, font, new Vector2f(0.02f, 0.11f), 0.2f, true);
		FPS.setColour(1, 1, 1);
		
		RawModel model = OBJLoader.loadObjModel("tree", loader);
		TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("tree", -0.4f)));
		
		RawModel lamp = OBJLoader.loadObjModel("lamp", loader);
		TexturedModel staticlamp = new TexturedModel(lamp,new ModelTexture(loader.loadTexture("lamp", -0.4f)));
		staticlamp.getTexture().setSpecularMap(loader.loadTexture("lampS", 0.4f));
		
		RawModel mill = OBJLoader.loadObjModel("LowPolyMill", loader);
		TexturedModel staticMill = new TexturedModel(mill,new ModelTexture(loader.loadTexture("IslandAtlas", -0.4f)));
		
		RawModel fern = OBJLoader.loadObjModel("fern", loader);
		ModelTexture fernTexture = new ModelTexture(loader.loadTexture("fern", -0.4f));
		fernTexture.setNumberOfRows(2);
		TexturedModel staticFern = new TexturedModel(fern, fernTexture);
		staticFern.getTexture().setHasTrasparency(true);
		
		
		RawModel cherry = OBJLoader.loadObjModel("cherry", loader);
		TexturedModel staticCherry = new TexturedModel(cherry,new ModelTexture(loader.loadTexture("cherry", -0.4f)));
		staticMill.getTexture().setHasTrasparency(true);

		
		int toggleTexture = loader.loadTexture("toggleFullscreen", -0.4f);
		GuiTexture toggelFullscreen = new GuiTexture(toggleTexture, new Vector2f(0.1f, 0f), new Vector2f(0.2f, 0.1f));	
		int quitTexture = loader.loadTexture("quit", -0.4f);
		GuiTexture quit = new GuiTexture(quitTexture, new Vector2f(0.1f, -0.2f), new Vector2f(0.2f, 0.1f));	
		int menuTexture = loader.loadTexture("menu", -0.4f);
		GuiTexture menu = new GuiTexture(menuTexture, new Vector2f(0.1f, 0.2f), new Vector2f(0.2f, 0.1f));	

		Menu pauseMenu = new Menu(scene);
		
		pauseMenu.addGui(toggelFullscreen);
		pauseMenu.addGui(quit);
		pauseMenu.addGui(menu);
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");  
		
		Entity millIsland = new Entity(staticMill, new Vector3f(400, 1, 400),0,0,0,50, false);
		millIsland.addPhysicsBody(PhysicsHelper.createCustomMesh(OBJLoader.loadObjModelCollsion("LowPolyMill"), 0f, 400, 50, 400, 50));
		scene.addEntity(millIsland);
		
		scene.setSun(new Light(new Vector3f(-1000000,1500000,1000000),new Vector3f(0.5f,0.5f,0.5f)));
		/*		
		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("fire", -0.4f), 8);
		
		ParticleSystem system = new ParticleSystem(particleTexture, 40, 10, -10f, 10, 10f, new Vector3f(410, 8, 410), true);
		system.playAudio("fire", 3, 1, true);
		system.randomizeRotation();
		system.setDirection(new Vector3f(0, 1, 0), 0.1f);
		system.setLifeError(0.1f);
		system.setSpeedError(0.4f);
		system.setScaleError(0.5f);
		*/
		Fbo multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight(), true);
		Fbo outputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
		Fbo outputFbo2 = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);

		PostProcessing.init(loader);
				
		Entity lampPost = new Entity(staticlamp, new Vector3f(385,60, 405), 0, 0, 0, 1, true);
		scene.addEntity(lampPost);
		lampPost.playAudio("bounce", 1, 1, true);
		Light light = new Light(new Vector3f(385,80,405),new Vector3f(1f,1f,1f), new Vector3f(1f,0.01f,0.002f));
		scene.addLight(light);		
		

		while(!Display.isCloseRequested()){
			player.update(scene);
			scene.getCamera().move();
		    			
			//system.generateParticles();
			if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && !Keyboard.isRepeatEvent()) {
				if(!pauseMenu.getRenderState()) {
					pauseMenu.enableRendering();
					player.setControlEnabled(false);
					Mouse.setGrabbed(false);
				}else
				{
					pauseMenu.disableRendering();
					player.setControlEnabled(true);
					Mouse.setGrabbed(true);
				}
			}
			
			if(pauseMenu.getRenderState()) {
				if(toggelFullscreen.hasBeenClicked())
				{
					if(DisplayManager.isFullscreen())
					{
						DisplayManager.setScreenResolution(false, DisplayManager.WIDTH, DisplayManager.HEIGHT);
					} else if (!DisplayManager.isFullscreen())
					{
						DisplayManager.setScreenResolution(true, DisplayManager.WIDTH, DisplayManager.HEIGHT);
					}
				}
				if(quit.hasBeenClicked())
				{
					break;
				}
			}
						
			ParticleMaster.update(scene);
					
			PhysicsMaster.update();
			
			scene.getRenderer().renderShadowMap(scene, scene.getSun());

			player.update();
			
			multisampleFbo.bindFrameBuffer();
			scene.getRenderer().renderScene(scene, new Vector4f(0, -1, 0, 100000000));
			ParticleMaster.renderParticles(scene, false);
			
			multisampleFbo.unbindFrameBuffer();
			multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, outputFbo);
			multisampleFbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, outputFbo2);
			PostProcessing.doPostProcessing(outputFbo.getColourTexture(), outputFbo2.getColourTexture());
			
			guiRenderer.render(scene);
			PlayerXposition.setTextString("X: " + String.valueOf(player.getPosition().x));
			PlayerYposition.setTextString("Y: " + String.valueOf(player.getPosition().y));
			PlayerZposition.setTextString("Z: " + String.valueOf(player.getPosition().z));
			FPS.setTextString("FPS: " + DisplayManager.getFPS());
			
			Time.setTextString(dtf.format(LocalDateTime.now()));

			TextMaster.render();
			//System.out.println(DisplayManager.getFPS());
			
			DisplayManager.updateDisplay();

		}

		outputFbo.cleanUp();
		outputFbo2.cleanUp();
		PostProcessing.cleanUp();
		multisampleFbo.cleanUp();
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		guiRenderer.cleanUp();
		scene.getRenderer().cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
		AudioMaster.cleanUp();

	}

}