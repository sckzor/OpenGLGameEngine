package lensFlare;

import org.lwjgl.util.vector.Matrix4f;

import shaders.ShaderProgram;

public class FlareShader extends ShaderProgram{
	
	private static final String VERTEX_FILE = "src/lensFlare/flareVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/lensFlare/flareFragmentShader.txt";
	
	private int location_transformationMatrix;

	public FlareShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadTransformation(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	
	

}