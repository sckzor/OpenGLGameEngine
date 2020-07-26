package entities;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;
import com.bulletphysics.dynamics.character.KinematicCharacterController;
import com.bulletphysics.linearmath.Transform;

import animation.Animation;
import animation.Joint;
import audio.AudioMaster;
import models.AnimatedModel;
import models.TexturedModel;
import physics.PhysicsMaster;
import renderEngine.AnimationLoader;
import renderEngine.DisplayManager;
import scene.Scene;
import terrains.Terrain;
import toolbox.Maths;

public class Player extends AnimatedEntity{

	private static final float RUN_SPEED = 20;
	private static final float RUN_SPEED_IN_AIR = 5;
	private static final float TURN_SPEED = 160;
	private static final float JUMP_POWER = 30;
	private static boolean isOnGround = true;
	
	public Player(AnimatedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, boolean audio) {
		super(model, position, rotX, rotY, rotZ, scale, audio);
	}
	public void update(Scene scene)
	{	
		javax.vecmath.Vector3f lastVelosity = new javax.vecmath.Vector3f(0,0,0);
		physicsBody.getLinearVelocity(lastVelosity);
		PhysicsMaster.setVelocity(physicsBody, new Vector3f(0, lastVelosity.y, 0));
		
		javax.vecmath.Vector3f from = new javax.vecmath.Vector3f(super.getPosition().x, super.getPosition().y, super.getPosition().z);
		javax.vecmath.Vector3f to = new javax.vecmath.Vector3f(super.getPosition().x, super.getPosition().y-3, super.getPosition().z);
		ClosestRayResultCallback res = new ClosestRayResultCallback(from, to);

		PhysicsMaster.getDynamicsWorld().rayTest(from, to, res);

		if(res.hasHit()){
			isOnGround = true;
		}else
		{
			isOnGround = false;
		}
		
		checkInputs(lastVelosity.y);
		Vector3f playerTransform = PhysicsMaster.getPos(physicsBody);
		super.setPosition(new Vector3f(playerTransform.x, playerTransform.y, playerTransform.z));
		AudioMaster.setListenerData(super.getPosition().x, super.getPosition().y, super.getPosition().z);
		super.update();
	}
	
	private void checkInputs(float lastVelosity)
	{
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			doAnimation("model", 0.5f);
			if(isOnGround) {
				PhysicsMaster.setVelocity(physicsBody, new Vector3f(0, lastVelosity, RUN_SPEED));
			}else
			{
				PhysicsMaster.setVelocity(physicsBody, new Vector3f(0, lastVelosity, RUN_SPEED_IN_AIR));
			}
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			doAnimation("model", 0.5f);
			if(isOnGround) {
				PhysicsMaster.setVelocity(physicsBody, new Vector3f(0, lastVelosity, -RUN_SPEED));
			}else
			{
				PhysicsMaster.setVelocity(physicsBody, new Vector3f(0, lastVelosity, -RUN_SPEED_IN_AIR));
			}
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			doAnimation("model", 0.5f);
			if(isOnGround) {
				PhysicsMaster.setVelocity(physicsBody, new Vector3f(-RUN_SPEED, lastVelosity, 0));
			}else
			{
				PhysicsMaster.setVelocity(physicsBody, new Vector3f(-RUN_SPEED_IN_AIR, lastVelosity, 0));
			}
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			doAnimation("model", 0.5f);
			if(isOnGround) {
				PhysicsMaster.setVelocity(physicsBody, new Vector3f(RUN_SPEED, lastVelosity, 0));
			}else
			{
				PhysicsMaster.setVelocity(physicsBody, new Vector3f(RUN_SPEED_IN_AIR, lastVelosity, 0));
			}
		}else {
			doAnimation(null, 1);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			PhysicsMaster.applyForce(physicsBody, new Vector3f(0, RUN_SPEED, 0));
		}

			
	}
}
