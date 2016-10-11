package by.bsuir.iit.sem8.klea.lab4;

public class Constants {

	public static final Integer[] SAMPLE_RATES = new Integer[] { new Integer(44100), new Integer(22050), new Integer(11025) };
	public static final Integer[] BITS_PER_SAMPLE = new Integer[] { new Integer(8), new Integer(16) };
	
	public static final int DEFAULT_SAMPLE_RATE_INDEX = 0;
	public static final int DEFAULT_BITS_PER_SAMPLE_INDEX = 0;
	public static final boolean DEFAULT_SEALING = false;
	public static final int DEFAULT_REVTYPE_INDEX = 0;
	public static final int DEFAULT_MASTER_VOLUME = 50;
	public static final int MASTER_VOLUME_MAX = 100;
	public static final int MASTER_VOLUME_MIN = 0;
	public static final int REFERENCE_FREQ_MAX = 2000;
	public static final int REFERENCE_FREQ_MIN = 100;
	public static final int DEFAULT_REFERENCE_FREQ = 440;
	
	public static final String[] WAVEFORMS = new String[] { Messages.getString("Constants.0"), Messages.getString("Constants.1"), Messages.getString("Constants.2"), Messages.getString("Constants.3"), Messages.getString("Constants.4"), Messages.getString("Constants.5") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	public static final int DEFAULT_WAVEFORM_INDEX = 0;
	public static final boolean DEFAULT_MUTE = false;
	public static final int DEFAULT_FREQ = 0;
	public static final int DEFAULT_PAN = 0;
	public static final int DEFAULT_GAIN = 0;
	public static final int FREQ_SEMITONES_MIN = -24;
	public static final int FREQ_SEMITONES_MAX = 24;
	public static final int GAIN_MAX = 10;
	public static final int GAIN_MIN = -10;

}
