package physics;

public class CollisionMesh {
	private int[] indices;
	private float[] vertecies;
	
	public CollisionMesh(int[] indices, float[] vertecies)
	{
		this.indices = indices;
		this.vertecies = vertecies;
	}
	public int[] getIndices() {
		return indices;
	}
	public float[] getVertecies() {
		return vertecies;
	}
}
