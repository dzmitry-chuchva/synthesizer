package by.bsuir.iit.sem8.klea.lab4;

import java.util.Arrays;

import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Synthesizer implements Runnable {
	
	private static final double EPSILON = 0.01;

	private SourceDataLine line = null;
	
	private int waveform = 0;
	private int volume = 50;
	private int gain = 0;
	private int pan = 0;
	private int freq = 0;
	private boolean mute = false;
	private float refFrequency = Constants.DEFAULT_REFERENCE_FREQ;
	
	private Thread thread = null;
	
	private byte[] waveformStream;
	
	private static final Object semaphore = new Object();
	
	public Synthesizer(final SourceDataLine line) {
		setLine(line);
	}

	public void run() {
		try {

			line.start();
//			synchronized (semaphore) {
//				try {
//					semaphore.wait();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
			
			while (Thread.currentThread() == thread) {
				try {
					synchronized (waveformStream) {
						line.write(waveformStream, 0, waveformStream.length);
					}
					Thread.sleep(1);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
			
//			synchronized (semaphore) {
//				try {
//					semaphore.wait();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
			
			line.stop();
			line.flush();
		} catch (/*LineUnavailable*/final Exception e) {
			e.printStackTrace();
		}
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(final int freq) {
		this.freq = freq;
		recalculateWaveForm();
	}

	public int getGain() {
		return gain;
	}

	public void setGain(final int gain) {
		this.gain = gain;
		final FloatControl control = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
		control.setValue(gain);
	}

	public int getPan() {
		return pan;
	}

	public void setPan(final int pan) {
		this.pan = pan;
		final FloatControl control = (FloatControl) line.getControl(FloatControl.Type.PAN);
		control.setValue(pan);
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(final int volume) {
		this.volume = volume;
		recalculateWaveForm();
	}

	public int getWaveform() {
		return waveform;
	}

	public void setWaveform(final int waveform) {
		this.waveform = waveform;
		if (!recalculateWaveForm()) {
			stop();
		}
	}

	public SourceDataLine getLine() {
		return line;
	}

	public void setLine(final SourceDataLine line) {
		if (this.line != null) {
			stop();
			this.line.close();
		}
		this.line = line;
		try {
			this.line.open();
			setGain(gain);
			setMute(mute);
			setPan(pan);
		} catch (final LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		if (thread != null) {
//			Thread copy = thread;
			thread = null;
			
//			try {
//				copy.join();
//				copy = null;
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
	}
	
	public void start(final float frequency) {
		this.refFrequency = frequency;
		if (recalculateWaveForm()) {
			if (thread == null) {
				thread = new Thread(this);
				thread.start();
				thread.setPriority(Thread.MAX_PRIORITY);
			}
		} else {
			stop();
		}
	}
	
	private boolean recalculateWaveForm() {
		boolean ret = true;
		final float frequency = refFrequency * (float)Math.pow(2, freq / (float)12);
		
		final float sampleRate = line.getFormat().getSampleRate();
		final int bits = line.getFormat().getSampleSizeInBits();
		
		int samplesOnePeriod = (int)(sampleRate / frequency);
		if (bits == 16 && samplesOnePeriod % 2 != 0) {
			samplesOnePeriod++;
		}
		
		final int sizeCorrection = bits / 8;
		int maxAmpl;
		int maxAmplVolumed;
		if (bits == 8) {
			maxAmpl = Byte.MAX_VALUE - 1;
		} else {
			maxAmpl = Short.MAX_VALUE - 1;
		}
		
		final int maxAmplHalf = maxAmpl / 2;
		maxAmplVolumed = (int)(maxAmplHalf * volume / 100.);
		
		final int len = samplesOnePeriod * sizeCorrection;
		
		final byte data[] = new byte[len];
		switch (waveform) {
		case 0:
			// none
			Arrays.fill(data, (byte)0);
			break;
		case 1:
			// sine
			final float f = (float)(2 * Math.PI / len);
			for (int i = 0; i < data.length; i += sizeCorrection) {
				final int sineVal = (int)(maxAmplVolumed * Math.sin(i * f));
				data[i] = (byte)sineVal;
				if (bits == 16) {
					data[i + 1] = (byte)(sineVal >> 8);
				}
			}
			break;
		case 2:
			// square
			int value = maxAmplVolumed;
			for (int i = 0; i < data.length / 2; i += sizeCorrection) {
				data[i] = (byte)value;
				if (bits == 16) {
					data[i + 1] = (byte)(value >> 8);
				}
			}
			value = -value;
			for (int i = data.length / 2; i < data.length; i += sizeCorrection) {
				data[i] = (byte)value;
				if (bits == 16) {
					data[i + 1] = (byte)(value >> 8);
				}
			}
			break;
		case 3:
			// saw
			final float sawIncr = (float)maxAmplVolumed / samplesOnePeriod;
			float sawAmpl = 0;
			for (int i = 0; i < data.length; i += sizeCorrection) {
				final int b = (int)sawAmpl;
				data[i] = (byte)b;
				if (bits == 16) {
					data[i + 1] = (byte)(b >> 8);
				}
				sawAmpl += sawIncr;
			}
			break;
		case 4:
			// triangle
			float triIncr = 4 * (float)maxAmplVolumed / samplesOnePeriod;
			float triAmpl = 0;
			for (int i = 0; i < data.length; i += sizeCorrection) {
				final int b = (int)triAmpl;
				data[i] = (byte)b;
				if (bits == 16) {
					data[i + 1] = (byte)(b >> 8);
				}
				triAmpl += triIncr;
				if (Math.abs(triAmpl - maxAmplVolumed) < EPSILON || triAmpl > maxAmplVolumed || Math.abs(triAmpl + maxAmplVolumed) < EPSILON || triAmpl < -maxAmplVolumed) {
					triIncr = -triIncr;
				}
			}
			break;
		case 5:
			// rounded square
			final float fr = (float)(2 * Math.PI / len);
			for (int i = 0; i < data.length / 2; i += sizeCorrection) {
				final int sineVal = (int)(maxAmplVolumed * Math.sin(i * fr));
				data[i] = (byte)sineVal;
				if (bits == 16) {
					data[i + 1] = (byte)(sineVal >> 8);
				}
			}
			final int val = -maxAmplVolumed;
			for (int i = data.length / 2; i < data.length; i += sizeCorrection) {
				data[i] = (byte)val;
				if (bits == 16) {
					data[i + 1] = (byte)(val >> 8);
				}
			}
			break;
		default:
			Arrays.fill(data, (byte)0);
			ret = false;
		}
		
		final int buffOversize = 10;
		
		if (ret) {
			if (waveformStream == null) {
				waveformStream = new byte[1];
			}
			synchronized (waveformStream) {
				waveformStream = new byte[len * 10];
				
				for (int i = 0; i < data.length; i++) {
					for (int j = 0; j < buffOversize; j++) {
						waveformStream[data.length * j + i] = data[i]; 
					}
				}
				
				line.flush();
			}
		}
		
		return ret;
	}

	public boolean isMute() {
		return mute;
	}

	public void setMute(final boolean mute) {
		this.mute = mute;
		final BooleanControl control = (BooleanControl) line.getControl(BooleanControl.Type.MUTE);
		control.setValue(mute);
	}
	
	public static void notifySynth() {
		synchronized (semaphore) {
			semaphore.notifyAll();
		}
	}

}
