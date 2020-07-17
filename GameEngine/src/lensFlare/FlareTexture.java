package lensFlare;

import org.lwjgl.util.vector.Vector2f;

import renderEngine.DisplayManager;

public class FlareTexture {

	private int texture;
	private Vector2f positions;
	private Vector2f scale;
	
	public FlareTexture(int texture, Vector2f positions, Vector2f scale) {
		super();
		this.texture = texture;
		this.positions = positions;
		this.scale = scale;
	}
	
	public void setPosition(Vector2f position)
	{
		this.positions = position;
	}
	
	public int getTexture() {
		return texture;
	}
	
	public Vector2f getPositions() {
		return positions;
	}
	
	public Vector2f getScale() {
		return scale;
	}
}
