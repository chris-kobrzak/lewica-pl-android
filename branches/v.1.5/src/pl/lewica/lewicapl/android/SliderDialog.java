package pl.lewica.lewicapl.android;

public class SliderDialog {

	private int mSliderMax; 
	private int mSliderValue; 
	private int mTitleResource;
	private int mOkButtonResource;


	public int getSliderMax() {
		return mSliderMax;
	}
	
	
	public void setSliderMax(int sliderMax) {
		this.mSliderMax = sliderMax;
	}


	public int getSliderValue() {
		return mSliderValue;
	}


	public void setSliderValue(int sliderValue) {
		this.mSliderValue = sliderValue;
	}


	public int getTitleResource() {
		return mTitleResource;
	}


	public void setTitleResource(int titleResource) {
		this.mTitleResource = titleResource;
	}


	public int getOkButtonResource() {
		return mOkButtonResource;
	}


	public void setOkButtonResource(int okButtonResource) {
		this.mOkButtonResource = okButtonResource;
	}
}
