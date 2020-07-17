package water;

import java.util.List;

import models.RawModel;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import scene.Scene;
import toolbox.Maths;
import entities.Camera;
import entities.Light;

public class WaterRenderer {
	
	public static final String DUDV_MAP = "waterDUDV";
	public static final String NORMAL_MAP = "matchingNormalMap";
	public static final float WAVE_SPEED = 0.03f;

	private RawModel quad;
	private WaterShader shader;
	private WaterFrameBuffers fbos;
	
	private float moveFactor = 0;
	
	private int dudvTexture;
	private int normalMap;
	
	public WaterRenderer(Loader loader, WaterShader shader, Matrix4f projectionMatrix, WaterFrameBuffers fbos) {
		this.shader = shader;
		this.fbos = fbos;
		dudvTexture = loader.loadTexture(DUDV_MAP, -0.4f);
		normalMap = loader.loadTexture(NORMAL_MAP, -0.4f);
		
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadNearAndFarPlane(MasterRenderer.NEAR_PLANE, MasterRenderer.FAR_PLANE);
		shader.stop();
		setUpVAO(loader);
	}

	public void render(Scene scene) {
		prepareRender(scene.getCamera(), scene.getLight());	
		for (WaterTile tile : scene.getWaters()) {
			Matrix4f modelMatrix = Maths.createTransformationMatrix(
					new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0,
					WaterTile.TILE_SIZE);
			shader.loadModelMatrix(modelMatrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());
		}
		unbind();
	}
	
	private void prepareRender(Camera camera, List<Light> lights){
		shader.start();
		shader.loadViewMatrix(camera);
		shader.loadSkyColour(0.5f, 0.5f, 0.5f);
		moveFactor += WAVE_SPEED * DisplayManager.getFrameTimeSeconds();
		moveFactor %= 1;
		shader.loadMoveFactor(moveFactor);
		shader.loadLights(lights);
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D,  fbos.getReflectionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D,  fbos.getRefractionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D,  dudvTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D,  normalMap);
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D,  fbos.getRefractionDepthTexture());
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	private void unbind(){
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	private void setUpVAO(Loader loader) {
		// Just x and z vectex positions here, y is set to 0 in v.shader
		float[] vertices = { -1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1 };
		quad = loader.loadToVAO(vertices, 2);
	}
	
	public void reflectOffWater(Scene scene)
	{
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		
		fbos.bindReflectionFrameBuffer();
		float distance = 2 * (scene.getCamera().getPosition().y - scene.getWaters().get(0).getHeight()+1f);
		scene.getCamera().getPosition().y -= distance;
		scene.getCamera().invertPitch();
		scene.getRenderer().renderScene(scene, new Vector4f(0, 1, 0, -scene.getWaters().get(0).getHeight()));
		scene.getCamera().getPosition().y += distance;
		scene.getCamera().invertPitch();
		
		fbos.bindRefractionFrameBuffer();
		scene.getRenderer().renderScene(scene, new Vector4f(0, -1, 0, scene.getWaters().get(0).getHeight()+1));
		
		fbos.unbindCurrentFrameBuffer();
	}

}
