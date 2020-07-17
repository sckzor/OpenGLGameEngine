package lensFlare;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import models.RawModel;
import renderEngine.Loader;
import scene.Scene;
import toolbox.Maths;

public class FlareRenderer {

	private final RawModel quad;
	private FlareShader shader;
	
	public FlareRenderer(Loader loader)
	{
		float[] postitions = {-1, 1, -1, -1, 1, 1, 1, -1};
		quad = loader.loadToVAO(postitions, 2);
		shader = new FlareShader();
	}
	
	public void render(FlareTexture[] flareTextures, float brightness)
	{
		shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		for(FlareTexture flare:flareTextures)
		{
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, flare.getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(flare.getPositions(), flare.getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	public void cleanUp()
	{
		shader.cleanUp();
	}
	
}
