package guis;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import renderEngine.DisplayManager;

public class GuiTexture {

	private int texture;
	private Vector2f positions;
	private Vector2f scale;
	
	public GuiTexture(int texture, Vector2f positions, Vector2f scale) {
		super();
		this.texture = texture;
		this.positions = positions;
		this.scale = scale;
	}
	
	public void setTexture(int texture)
	{
		this.texture = texture;
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
	
	public boolean hasBeenClicked()
	{
		float screenX = Mouse.getX();
		float screenY = Mouse.getY();
		if(Mouse.isButtonDown(0)) {

			screenX /= DisplayManager.getWidth()/2f;
			screenX -= 1;
			screenY /= DisplayManager.getHeight()/2f;
			screenY -= 1;		
		}
		
		if(screenX <= positions.x + (scale.x) && screenY <= positions.y + (scale.y)
				&& screenX >= positions.x - (scale.x) && screenY >= positions.y - (scale.y) && Mouse.isButtonDown(0))
		{
			return true;
		}else
		{
			return false;
			
		}
	}
}
