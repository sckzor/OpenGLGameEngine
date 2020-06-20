package fontRendering;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import shaders.ShaderProgram;

public class FontShader extends ShaderProgram{

	private static final String VERTEX_FILE = "src/fontRendering/fontVertex.txt";
	private static final String FRAGMENT_FILE = "src/fontRendering/fontFragment.txt";
	
	private int locatiton_colour;
	private int locatiton_translation;
	
	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		locatiton_colour = super.getUniformLocation("colour");
		locatiton_translation = super.getUniformLocation("translation");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "positions");
		super.bindAttribute(1, "textureCoords");
	}
	
	protected void loadColour(Vector3f colour)
	{
		super.loadVector(locatiton_colour, colour);
	}
	
	protected void loadTranslation(Vector2f translation)
	{
		super.loadVector2D(locatiton_translation, translation.x, translation.y);
	}

}
