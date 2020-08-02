package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;

import physics.PhysicsMaster;

public class Camera {
	
	private float targetDistanceFromPlayer = 50;
	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;

	private static final float CUBEMAP_NEAR_PLANE = 0.1f;
	private static final float CUBEMAP_FAR_PLANE = 200f;
	private static final float CUBEMAP_FOV = 90;// don't change!
	private static final float CUBEMAP_ASPECT_RATIO = 1;
	private static float Sensitivity = 0.001f;
	
	private Vector3f position = new Vector3f(0,0,0);
	private float pitch = 20;
	private float yaw = 0;
	private float roll;
	private boolean doCubeMap;
	
	private Player player;
	
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f viewMatrix = new Matrix4f();
	
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}

	private Matrix4f projectionViewMatrix = new Matrix4f();
	
	public Camera(boolean doCubeMap, Player player){
		this.doCubeMap = doCubeMap;
		if (doCubeMap)
		{
			createProjectionMatrix();
		}
		this.player = player;
	}
	
	public Camera(boolean doCubeMap){
		this.doCubeMap = doCubeMap;
		if (doCubeMap)
		{
			createProjectionMatrix();
		}
	}

	
	public void move(){
		if(!doCubeMap) {
			calculatePitch();
			calculateAngleAroundPlayer();
			float horizontalDistance = calculateHorizontalDistance(targetDistanceFromPlayer);
			float verticalDistance = calculateVerticalDistance(targetDistanceFromPlayer);
			Vector3f pos = calculateCameraPosition(horizontalDistance, verticalDistance);
			javax.vecmath.Vector3f from = new javax.vecmath.Vector3f(player.getPosition().x/2, player.getPosition().y/2, player.getPosition().z/2);
			javax.vecmath.Vector3f to = new javax.vecmath.Vector3f(pos.x, pos.y, pos.z);
			ClosestRayResultCallback res = new ClosestRayResultCallback(from, to);
			
			PhysicsMaster.getDynamicsWorld().rayTest(from, to, res);

			if(!res.hasHit()){
			}else
			{
				position = new Vector3f(res.hitPointWorld.x,res.hitPointWorld.y,res.hitPointWorld.z);
				System.out.println(pos);
			}
			position = pos;

			this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
		}
	}
	
	public void move(Vector3f position){

		this.position = position;
	}
	
	public void switchToFace(int faceIndex) {
		if(doCubeMap) {
			switch (faceIndex) {
			case 0:
				pitch = 0;
				yaw = 90;
				break;
			case 1:
				pitch = 0;
				yaw = -90;
				break;
			case 2:
				pitch = -90;
				yaw = 180;
				break;
			case 3:
				pitch = 90;
				yaw = 180;
				break;
			case 4:
				pitch = 0;
				yaw = 180;
				break;
			case 5:
				pitch = 0;
				yaw = 0;
				break;
			}
			updateViewMatrix();
		}
	}
	
	
	
	
	public void invertPitch() {
		this.pitch = -pitch;
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	private Vector3f calculateCameraPosition(float horizDistance, float verticDistance)
	{
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		Vector3f pos = new Vector3f();
		pos.x = player.getPosition().x - offsetX;
		pos.z = player.getPosition().z - offsetZ;
		pos.y = player.getPosition().y + verticDistance;
		return pos;
	}
	
	private float calculateHorizontalDistance(float distance)
	{
		return (float) (distance * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance(float distance)
	{
		return (float) (distance * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculatePitch()
	{
		float pitchChange = Mouse.getDY()  * 30 * Sensitivity;
		pitch -= pitchChange;
	}
	
	private void calculateAngleAroundPlayer()
	{
		float angleChange = Mouse.getDX()* Sensitivity;
		angleAroundPlayer -= angleChange;
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public Matrix4f getProjectionViewMatrix() {
		return projectionViewMatrix;
	}

	private void createProjectionMatrix() {
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(CUBEMAP_FOV / 2f))));
		float x_scale = y_scale / CUBEMAP_ASPECT_RATIO;
		float frustum_length = CUBEMAP_FAR_PLANE - CUBEMAP_NEAR_PLANE;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((CUBEMAP_FAR_PLANE + CUBEMAP_NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * CUBEMAP_NEAR_PLANE * CUBEMAP_FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}

	private void updateViewMatrix() {
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(180), new Vector3f(0, 0, 1), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f negativeCameraPos = new Vector3f(-position.x, -position.y, -position.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);

		Matrix4f.mul(projectionMatrix, viewMatrix, projectionViewMatrix);
	}
	
	
	public float getAngleAroundPlayer() {
		return angleAroundPlayer;
	}
	
	public float getDistanceFromPlayer() {
		return distanceFromPlayer;
	}

}
