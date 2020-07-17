package guis;

import java.util.ArrayList;
import java.util.List;

import scene.Scene;

public class Menu {

	private Scene scene;
	private List<GuiTexture> guis = new ArrayList<GuiTexture>();
	private boolean renderState;
	
	public Menu(Scene scene)
	{
		this.scene = scene;
	}
	
	public void addGui(GuiTexture gui)
	{
		guis.add(gui);
	}
	
	public void enableRendering()
	{
		for(GuiTexture gui:guis)
		{
			scene.addGui(gui);
		}
		renderState = true;
	}
	
	public void disableRendering()
	{
		scene.getGuis().removeAll(guis);
		renderState = false;
	}

	public boolean getRenderState() {
		return renderState;
	}
}
