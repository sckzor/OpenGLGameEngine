package textures;

public class ModelTexture {
	
	private int textureID;
	private int normalMap;
	private int specularMap;
	
	public int getNormalMap() {
		return normalMap;
	}

	public void setNormalMap(int normalMap) {
		this.normalMap = normalMap;
	}

	private float shineDamper = 1;
	private float reflectivity = 0;
	
	private boolean hasTrasparency = false;
	private boolean useFakeLighting = false;
	private boolean hasSpecularMap = false;
	private int numberOfRows = 1;
	
	public void setSpecularMap(int specMap)
	{
		this.specularMap = specMap;
		this.hasSpecularMap = true;
		
	}
	
	public boolean hasSpecluarMap()
	{
		return hasSpecularMap;
	}
	
	public int getSpecularMap()
	{
		return specularMap;
	}
	
	public boolean isHasTrasparency() {
		return hasTrasparency;
	}

	public void setHasTrasparency(boolean hasTrasparency) {
		this.hasTrasparency = hasTrasparency;
	}

	public ModelTexture(int texture){
		this.textureID = texture;
	}
	
	public int getID(){
		return textureID;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}

	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}
	
	

}
