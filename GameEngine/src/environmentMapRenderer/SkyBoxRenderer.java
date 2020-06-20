package environmentMapRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;

public class SkyBoxRenderer {

	private SkyBoxShader shader;
	private CubeMap cubeMap;
		
	public SkyBoxRenderer(CubeMap cubeMap, Matrix4f projectionMatrix)
	{
		shader = new SkyBoxShader();
		shader.start();
		shader.connectTextureUnit();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		this.cubeMap = cubeMap;
	}
	
	public void render(Camera camera, float r, float g, float b)
	{
		shader.start();
		shader.loadViewMatrix(camera);
		shader.loadFogColour(r, g, b);
		GL30.glBindVertexArray(cubeMap.getCube().getVaoID());
		GL20.glEnableVertexAttribArray(0);
		bindTextures();
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cubeMap.getCube().getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	private void bindTextures(){
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, cubeMap.getTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, cubeMap.getTexture());
		shader.loadBlendFactor(0);
	}
}
