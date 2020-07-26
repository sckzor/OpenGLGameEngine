package physics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import renderEngine.DisplayManager;
import toolbox.Maths;

public class PhysicsMaster {

	private static DiscreteDynamicsWorld dynamicsWorld;
    private static List<RigidBody> rigidBodies = new ArrayList<RigidBody>();
    private static int GRAVITY = -10;
    public static void initPhysics() {
    	BroadphaseInterface broadphase = new DbvtBroadphase();
    	DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
    	CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);

    	SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

    	dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);

    	// set the gravity of our world
    	dynamicsWorld.setGravity(new Vector3f(0, GRAVITY, 0));
    	CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 1);
    	DefaultMotionState groundMotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, -1, 0), 1.0f))); 

    	RigidBodyConstructionInfo groundRigidBodyCI = new RigidBodyConstructionInfo(0, groundMotionState, groundShape, new Vector3f(0,0,0)); 
    	RigidBody groundRigidBody = new RigidBody(groundRigidBodyCI); 

    	dynamicsWorld.addRigidBody(groundRigidBody);
    }
    
    public static void update()
    {
	    dynamicsWorld.stepSimulation(1/(DisplayManager.getFPS() + 0.1f), 10); 

	    for(RigidBody body : rigidBodies) {
		    Transform trans = new Transform();
		    body.getMotionState().getWorldTransform(trans); 
	    }
    }
    
    public static org.lwjgl.util.vector.Vector3f getPos(RigidBody rigidBody)
    {
    	Transform trans = new Transform();
    	rigidBody.getMotionState().getWorldTransform(trans);
	    return new org.lwjgl.util.vector.Vector3f(trans.origin.x, trans.origin.y, trans.origin.z);
    }
    
    public static org.lwjgl.util.vector.Vector3f getRot(RigidBody rigidBody)
    {
    	Transform trans = new Transform();
    	rigidBody.getMotionState().getWorldTransform(trans);
	    Quat4f rot = new Quat4f();
	    return Maths.QuaternionToEuler(trans.getRotation(rot));
    }
    
    public static void addRigidBody(RigidBody rigidBody)
    {
    	rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
    	dynamicsWorld.addRigidBody(rigidBody); 
    	rigidBodies.add(rigidBody);
    	
    }
    
    public static void applyForce(RigidBody rigidBody, org.lwjgl.util.vector.Vector3f direction)
    {
    	rigidBody.applyCentralForce(new Vector3f(direction.x, direction.y, direction.z));
    }
    
    public static void setVelocity(RigidBody rigidBody, org.lwjgl.util.vector.Vector3f direction)
    {
    	rigidBody.setLinearVelocity(new Vector3f(direction.x, direction.y, direction.z));
    }
}
