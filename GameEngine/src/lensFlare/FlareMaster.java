package lensFlare;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import renderEngine.Loader;
import scene.Scene;

public class FlareMaster {
	private static final Vector2f CENTER_SCREEN = new Vector2f(0.5f, 0.5f);
	
	private FlareTexture[] flareTextures;
	private float spacing;
	
	private FlareRenderer renderer;
	
	public FlareMaster(float spacing, Loader loader, FlareTexture... textures)
	{
		this.spacing = spacing;
		this.flareTextures = textures;
		this.renderer = new FlareRenderer(loader);
	}

	public void render(Scene scene)
	{
		Vector2f sunCoords = convertToScreenSpace(new Vector3f(400, 120, 400),
				scene.getCamera().getViewMatrix(), scene.getCamera().getProjectionMatrix());
		if(sunCoords == null) {
			return;
		}
		Vector2f sunToCenter = Vector2f.sub(CENTER_SCREEN, sunCoords, null);
		float brightness = 1 - (sunToCenter.length() / 0.6f);
		System.out.println(sunToCenter);

		if(brightness > 0)
		{
			calcFlarePositions(sunToCenter, sunCoords);
			renderer.render(flareTextures, brightness);
		}
	}
	
	private void calcFlarePositions(Vector2f sunToCenter, Vector2f sunCoords)
	{
		for(int i = 0; i <flareTextures.length;i++)
		{
			Vector2f direction = new Vector2f(sunToCenter);
			direction.scale(i*spacing);
			Vector2f flarePos = Vector2f.add(sunCoords, direction, null);
			flareTextures[i].setPosition(flarePos);
		}
	}
	
	private Vector2f convertToScreenSpace(Vector3f worldPos, Matrix4f viewMat, Matrix4f projectionMat)
	{
		Vector4f coords = new Vector4f(worldPos.x, worldPos.y, worldPos.z, 1f);
		Matrix4f.transform(viewMat, coords, coords);
		Matrix4f.transform(projectionMat, coords, coords);
		if(coords.w <= 0)
		{
			return null;
		}
		float x = (coords.x/coords.w + 1) / 2f;
		float y = 1-((coords.y/coords.w + 1) / 2f);
		return new Vector2f(x, y);
	}
	
	public void cleanUp()
	{
		renderer.cleanUp();
	}
}
