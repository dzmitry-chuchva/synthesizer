package by.bsuir.iit.sem8.klea.lab4;

import java.io.Serializable;


public class Preset implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private boolean sealing;
	private int referenceFreq;
	private int volume;
	private int revTypeIndex;
	private int sampleRateIndex;
	private int bitsPerSampleIndex;
	private LinePreset[] linePresets;
	
	public Preset() {
		
	}

	public LinePreset[] getLinePresets() {
		return linePresets;
	}

	public void setLinePresets(final LinePreset[] linePresets) {
		this.linePresets = linePresets;
	}

	public int getReferenceFreq() {
		return referenceFreq;
	}

	public void setReferenceFreq(final int referenceFreq) {
		this.referenceFreq = referenceFreq;
	}

	public int getRevTypeIndex() {
		return revTypeIndex;
	}

	public void setRevTypeIndex(final int revTypeIndex) {
		this.revTypeIndex = revTypeIndex;
	}

	public boolean isSealing() {
		return sealing;
	}

	public void setSealing(final boolean sealing) {
		this.sealing = sealing;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(final int volume) {
		this.volume = volume;
	}

	public int getBitsPerSampleIndex() {
		return bitsPerSampleIndex;
	}

	public void setBitsPerSampleIndex(final int bitsPerSampleIndex) {
		this.bitsPerSampleIndex = bitsPerSampleIndex;
	}

	public int getSampleRateIndex() {
		return sampleRateIndex;
	}

	public void setSampleRateIndex(final int sampleRateIndex) {
		this.sampleRateIndex = sampleRateIndex;
	}
	
	

}
