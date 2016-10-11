package by.bsuir.iit.sem8.klea.lab4;

import java.io.Serializable;

public class LinePreset implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int waveFormIndex;
	private int gain;
	private int pan;
	private int freq;
	private boolean mute;
	
	public LinePreset() {
		
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(final int freq) {
		this.freq = freq;
	}

	public int getGain() {
		return gain;
	}

	public void setGain(final int gain) {
		this.gain = gain;
	}

	public boolean isMute() {
		return mute;
	}

	public void setMute(final boolean mute) {
		this.mute = mute;
	}

	public int getPan() {
		return pan;
	}

	public void setPan(final int pan) {
		this.pan = pan;
	}

	public int getWaveFormIndex() {
		return waveFormIndex;
	}

	public void setWaveFormIndex(final int waveFormIndex) {
		this.waveFormIndex = waveFormIndex;
	}
	
	

}
