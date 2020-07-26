package physics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConeShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import models.RawModel;

public class PhysicsHelper {

	public static RigidBody createCapsule(float radius, float height, float mass, float x, float y, float z)
	{
		CollisionShape shape = new CapsuleShape(radius, height);
		DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(x, y-height, z), 1.0f)));

    	Vector3f Inertia = new Vector3f(0,0,0); 
    	shape.calculateLocalInertia(mass,Inertia); 

    	RigidBodyConstructionInfo RigidBodyCI = new RigidBodyConstructionInfo(mass,motionState,shape,Inertia); 
    	RigidBodyCI.restitution = 0.95f;
    	RigidBodyCI.angularDamping = 0.95f;
    	RigidBodyCI.friction = 0.95f;
    	return new RigidBody(RigidBodyCI); 
	}
	
	public static RigidBody createCone(float radius, float height, float mass, float x, float y, float z)
	{
		CollisionShape shape = new ConeShape(radius, height);
		DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(x, y, z), 1.0f)));

    	Vector3f Inertia = new Vector3f(0,0,0); 
    	shape.calculateLocalInertia(mass,Inertia); 

    	RigidBodyConstructionInfo RigidBodyCI = new RigidBodyConstructionInfo(mass,motionState,shape,Inertia); 
    	RigidBodyCI.friction = 0.5f;
    	return new RigidBody(RigidBodyCI); 
	}
	
	public static RigidBody addMesh(CollisionMesh model, float mass, float x, float y, float z, float scale){
	    float[] coords = model.getVertecies();
	    int[] indices = model.getIndices();

	    if (indices.length > 0) {
	        IndexedMesh indexedMesh = new IndexedMesh();
	        indexedMesh.numTriangles = indices.length / 3;
	        indexedMesh.triangleIndexBase = ByteBuffer.allocateDirect(indices.length*Integer.BYTES).order(ByteOrder.nativeOrder());
	        indexedMesh.triangleIndexBase.rewind();
	        indexedMesh.triangleIndexBase.asIntBuffer().put(indices);
	        indexedMesh.triangleIndexStride = 3 * Integer.BYTES;
	        indexedMesh.numVertices = coords.length / 3;
	        indexedMesh.vertexBase = ByteBuffer.allocateDirect(coords.length*Float.BYTES).order(ByteOrder.nativeOrder());
	        indexedMesh.vertexBase.rewind();
	        indexedMesh.vertexBase.asFloatBuffer().put(coords);
	        indexedMesh.vertexStride = 3 * Float.BYTES;

	        TriangleIndexVertexArray vertArray = new TriangleIndexVertexArray();
	        vertArray.addIndexedMesh(indexedMesh);

	        boolean useQuantizedAabbCompression = true;
	        BvhTriangleMeshShape meshShape = new BvhTriangleMeshShape(vertArray, useQuantizedAabbCompression);
	        meshShape.setLocalScaling(new Vector3f(scale, scale, scale));

	        CollisionShape collisionShape = meshShape;

			DefaultMotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(x, y, z), 1.0f)));

	    	Vector3f Inertia = new Vector3f(0,0,0); 
	    	meshShape.calculateLocalInertia(mass,Inertia); 

	    	RigidBodyConstructionInfo RigidBodyCI = new RigidBodyConstructionInfo(mass,motionState,collisionShape,Inertia); 
	    	RigidBodyCI.friction = 0.5f;
	    	return new RigidBody(RigidBodyCI);
	    	
	    } else {
	        System.err.println("[FATAL] Failed to extract collision geometry from model. ");
	        System.exit(-1);
	        return null;
	    }

	}
}
