package collisionBox;

import org.lwjgl.util.vector.Vector3f;

public class CollisionBox {
	private Vector3f MaxCoords;
	private Vector3f MinCoords;
	public CollisionBox(Vector3f MaxCoords, Vector3f MinCoords)
	{
		this.MaxCoords = MaxCoords;
		this.MinCoords = MinCoords;
	}
	public Vector3f getMaxCoords() {
		return MaxCoords;
	}
	public Vector3f getMinCoords() {
		return MinCoords;
	}
	
	public void move(Vector3f coords) {
		MaxCoords.x = coords.x+10;
		MaxCoords.y = coords.y+10;
		MaxCoords.z = coords.z+10;		
		
		MinCoords.x = coords.x-10;
		MinCoords.y = coords.y-10;
		MinCoords.z = coords.z-10;
	}
	
	public boolean hasCollided(CollisionBox box)
	{
		if(box!=this) {			
			return (this.MinCoords.x > box.MaxCoords.x && this.MaxCoords.x < box.MinCoords.x) &&
			       (this.MinCoords.y > box.MaxCoords.y && this.MaxCoords.y < box.MinCoords.y) &&
			       (this.MinCoords.z > box.MaxCoords.z && this.MaxCoords.z < box.MinCoords.z);
		}else {
			return false;
		}
	}
	
	
}
