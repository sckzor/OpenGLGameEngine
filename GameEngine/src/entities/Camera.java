package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;
	
	private static final float CUBEMAP_NEAR_PLANE = 0.1f;
	private static final float CUBEMAP_FAR_PLANE = 200f;
	private static final float CUBEMAP_FOV = 90;// don't change!
	private static final float CUBEMAP_ASPECT_RATIO = 1;
	
	private Vector3f position = new Vector3f(0,0,0);
	private float pitch = 20;
	private float yaw = 0;
	private float roll;
	private boolean doCubeMap;
	
	private Player player;
	
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f viewMatrix = new Matrix4f();
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
			calculateZoom();
			calculatePitch();
			calculateAngleAroundPlayer();
			float horizontalDistance = calculateHorizontalDistance();
			float verticalDistance = calculateVerticalDistance();
			calculateCameraPosition(horizontalDistance, verticalDistance);
			this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
		}
	}
	
	public void move(Vector3f position){
		if(!doCubeMap) {
			calculateZoom();
			calculatePitch();
			calculateAngleAroundPlayer();
			float horizontalDistance = calculateHorizontalDistance();
			float verticalDistance = calculateVerticalDistance();
			calculateCameraPosition(horizontalDistance, verticalDistance);
			this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
		}
		else
		{
			this.position = position;
		}
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
	
	private void calculateCameraPosition(float horizDistance, float verticDistance)
	{
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + verticDistance;
	}
	
	private float calculateHorizontalDistance()
	{
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance()
	{
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculateZoom()
	{
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromPlayer -= zoomLevel;
	}
	
	private void calculatePitch()
	{
		if(Mouse.isButtonDown(2))
		{
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
		}
	}
	
	private void calculateAngleAroundPlayer()
	{
		if(Mouse.isButtonDown(1))
		{
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
		}
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

}
