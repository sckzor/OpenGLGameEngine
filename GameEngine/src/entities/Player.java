package entities;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import collisionBox.CollisionBox;
import models.TexturedModel;
import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity{

	private static final float RUN_SPEED = 50;
	private static final float TURN_SPEED = 160;
	public static final float GRAVITY = -70;
	private static final float JUMP_POWER = 30;
		
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	private boolean collided = false;
	private float collisionDx = 0f;
	private float collisionDz = 0f;

	
	private boolean isInAir = false;
	
	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}
	
	public Player(TexturedModel model, CollisionBox box, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, box,position, rotX, rotY, rotZ, scale);
	}
	
	public void move(Terrain terrain, List<Entity> entities)
	{		
		checkInputs();
		
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();;
		
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		
		for(Entity entity:entities)
		{
			if(entity.collisionBox != null)
			{
				if(this.collisionBox.hasCollided(entity.collisionBox) && !collided )						
				{
					collided = true;
					collisionDx = dx;
					collisionDz = dz;
				}                            
			}
		}
		
		System.out.println(Math.signum(dx) + " - " + Math.signum(dz) + "  /  " + collisionDx + " - " + collisionDz);
		
		if(collided)
		{
			if((-Math.signum(collisionDx) == Math.signum(dx) || -Math.signum(collisionDz) == Math.signum(dz)) && collided )
			{
				super.increasePosition(dx, 0, dz);
				collided = false;

			}

		}else
		{

			super.increasePosition(dx, 0, dz);
		}
		
		
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if(super.getPosition().y<terrainHeight)
		{
			upwardsSpeed = 0;
			super.getPosition().y = terrainHeight;
			isInAir = false;
		}
		
		super.collisionBox.move(super.getPosition());
	}
	
	private void jump()
	{
		if(!isInAir)
		{
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
		
	}
	
	private void checkInputs()
	{
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			this.currentSpeed = RUN_SPEED;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			this.currentSpeed = -RUN_SPEED;
		}
		else {
			this.currentSpeed = 0;
		}
			
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			this.currentTurnSpeed = -TURN_SPEED;
		}
		else if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			this.currentTurnSpeed = TURN_SPEED;
		} else {
			this.currentTurnSpeed = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			jump();
		}
			
	}
}