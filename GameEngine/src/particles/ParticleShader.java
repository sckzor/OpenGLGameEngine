package particles;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import shaders.ShaderProgram;

public class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/particles/particleVShader.txt";
	private static final String FRAGMENT_FILE = "src/particles/particleFShader.txt";

	private int location_numberOfRows;
	private int location_projectionMatrix;
	private int location_skyColour;
	private int location_density;
	private int location_gradient;

	public ParticleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_skyColour = super.getUniformLocation("skyColour");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_density = super.getUniformLocation("density");
		location_gradient = super.getUniformLocation("gradient");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "modelViewMatrix");
		super.bindAttribute(5, "texOffsets");
		super.bindAttribute(6, "blendFactor");
	}
	
	public void loadDensityAndGradient(float density, float gradient)
	{
		super.loadFloat(location_gradient, gradient);
		super.loadFloat(location_density, density);
	}
	
	
	public void loadSkyColour(float r, float g, float b)
	{
		super.loadVector(location_skyColour, new Vector3f(r,g,b));
	}
	
	protected void loadNumberOfRows(float numRows)
	{
		super.loadFloat(location_numberOfRows, numRows);
	}

	protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}

}
