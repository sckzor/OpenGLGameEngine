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
	private static final float JUMP_POWER = 5;
	private static boolean isOnGround = true;
	private static float currentSpeedX = 0;
	private static float currentSpeedZ = 0;
	private static float currentJump = 0;
	private boolean controlEnabled = true;

	public Player(AnimatedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, boolean audio) {
		super(model, position, rotX, rotY, rotZ, scale, audio);
	}
	public void update(Scene scene)
	{	
		javax.vecmath.Vector3f lastVelosity = new javax.vecmath.Vector3f(0,0,0);
		physicsBody.getLinearVelocity(lastVelosity);
		PhysicsMaster.setVelocity(physicsBody, new Vector3f(0, lastVelosity.y, 0));
		
		javax.vecmath.Vector3f from = new javax.vecmath.Vector3f(super.getPosition().x, super.getPosition().y, super.getPosition().z);
		javax.vecmath.Vector3f to = new javax.vecmath.Vector3f(super.getPosition().x, super.getPosition().y-1, super.getPosition().z);
		ClosestRayResultCallback res = new ClosestRayResultCallback(from, to);
		
		PhysicsMaster.getDynamicsWorld().rayTest(from, to, res);

		if(res.hasHit()){
			isOnGround = true;
		}else
		{
			isOnGround = false;
		}
		
		checkInputs();
		
		float dx = (float) (currentSpeedX * Math.sin(Math.toRadians(super.getRotY())) + (currentSpeedZ * Math.sin(Math.toRadians(super.getRotY()+90))));
		float dz = (float) (currentSpeedX * Math.cos(Math.toRadians(super.getRotY())) + (currentSpeedZ * Math.cos(Math.toRadians(super.getRotY()+90))));
		if(isOnGround) {
			PhysicsMaster.setVelocity(physicsBody, new Vector3f(dx, lastVelosity.y+currentJump, dz));
		}else {
			PhysicsMaster.setVelocity(physicsBody, new Vector3f(dx/4, lastVelosity.y, dz/4));
		}
		Vector3f playerTransform = PhysicsMaster.getPos(physicsBody);
		super.setPosition(new Vector3f(playerTransform.x, playerTransform.y-2, playerTransform.z));
		AudioMaster.setListenerData(super.getPosition().x, super.getPosition().y, super.getPosition().z);
		super.setRotY(scene.getCamera().getAngleAroundPlayer()*scene.getCamera().getDistanceFromPlayer());
	}
	
	private void checkInputs()
	{
		if(controlEnabled) {
			if(Keyboard.isKeyDown(Keyboard.KEY_W)){
				doAnimation("model", 0.5f);
				currentSpeedX = RUN_SPEED;
			}
			else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
				doAnimation("model", 0.5f);
				currentSpeedX = -RUN_SPEED;
			}else
			{
				doAnimation(null, 1);
				currentSpeedX = 0;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_D)){
				doAnimation("model", 0.5f);
				currentSpeedZ = -RUN_SPEED;
			}
			else if(Keyboard.isKeyDown(Keyboard.KEY_A)){
				doAnimation("model", 0.5f);
				currentSpeedZ = RUN_SPEED;
			}else {
				//doAnimation(null, 1);
				currentSpeedZ = 0;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
				currentJump = JUMP_POWER;
			}else
			{
				currentJump = 0;
			}
		}	
	}
	
	public boolean isControlEnabled() {
		return controlEnabled;
	}
	public void setControlEnabled(boolean controlEnabled) {
		this.controlEnabled = controlEnabled;
	}
}
