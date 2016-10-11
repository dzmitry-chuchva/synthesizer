package by.bsuir.iit.sem8.klea.lab4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PresetManager {
	
	private static final String PRESETS_FILTERNAME = Messages.getString("PresetManager.0"); //$NON-NLS-1$
	private static final String PRESET_FILE_EXT = "pst"; //$NON-NLS-1$
	private static final String PRESET_FILE_EXT_WITH_DOT = '.' + PRESET_FILE_EXT;
	
	private MainFrame frame;
	
	public PresetManager(final MainFrame frame) {
		this.frame = frame;
	}
	
	public Preset loadPreset() throws Exception {
		Preset preset = null;
		final JFileChooser chooser = new JFileChooser();
		final FileNameExtensionFilter filter = new FileNameExtensionFilter(PRESETS_FILTERNAME, PRESET_FILE_EXT);
		chooser.setFileFilter(filter);
		final int returnVal = chooser.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				final ObjectInputStream stream = new ObjectInputStream(new FileInputStream(chooser.getSelectedFile()));
				preset = (Preset)stream.readObject();
				stream.close();
			} catch (final FileNotFoundException e) {
				throw new Exception(e.getMessage());
			} catch (final IOException e) {
				throw new Exception(e.getMessage());
			}
		}
		return preset;		
	}
	
	public void savePreset(final Preset preset) throws Exception {
		final JFileChooser chooser = new JFileChooser();
		final FileNameExtensionFilter filter = new FileNameExtensionFilter(PRESETS_FILTERNAME, PRESET_FILE_EXT);
		chooser.setFileFilter(filter);
		final int returnVal = chooser.showSaveDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
	    		final File selectedFile = chooser.getSelectedFile();
				String path = selectedFile.getAbsolutePath();
	    		if (!path.endsWith(PRESET_FILE_EXT_WITH_DOT)) {
	    			path = path + PRESET_FILE_EXT_WITH_DOT;
	    		}
				final ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(path));
				stream.writeObject(preset);
				stream.close();
			} catch (final FileNotFoundException e) {
				throw new Exception(e.getMessage());
			} catch (final IOException e) {
				throw new Exception(e.getMessage());
			}
		}		
	}
	
	public void applyPreset(final Preset preset) throws Exception {
		frame.setBitsPerSampleIndex(preset.getBitsPerSampleIndex());
		frame.setSampleRateIndex(preset.getSampleRateIndex());
		frame.setReferenceFreq(preset.getReferenceFreq());
		frame.setReverbTypeIndex(preset.getRevTypeIndex());
		frame.setSealing(preset.isSealing());
		frame.setMasterVolume(preset.getVolume());
		
		
		final OscLine[] lines = frame.getLines();
		final LinePreset[] linePresets = preset.getLinePresets();
		if (linePresets == null || linePresets.length < lines.length) {
			throw new Exception(Messages.getString("PresetManager.2")); //$NON-NLS-1$
		}
		
		for (int i = 0; i < lines.length; i++) {
			applyPresetForLine(lines[i], linePresets[i]);
		}
	}
	
	private void applyPresetForLine(final OscLine line, final LinePreset preset) throws Exception {
		line.setFreq(preset.getFreq());
		line.setGain(preset.getGain());
		line.setMuted(preset.isMute());
		line.setPan(preset.getPan());
		line.setWaveformIndex(preset.getWaveFormIndex());
	}
	
	public Preset getCurrentPreset() {
		final Preset preset = new Preset();
		preset.setBitsPerSampleIndex(frame.getBitsPerSampleIndex());
		preset.setSampleRateIndex(frame.getSampleRateIndex());
		preset.setReferenceFreq(frame.getReferenceFreq());
		preset.setRevTypeIndex(frame.getReverbTypeIndex());
		preset.setSealing(frame.isSealing());
		preset.setVolume(frame.getMasterVolume());
		
		
		final OscLine[] lines = frame.getLines();
		final LinePreset[] linePresets = new LinePreset[lines.length];
		for (int i = 0; i < lines.length; i++) {
			linePresets[i] = getCurrentLinePreset(lines[i]);
		}
		
		preset.setLinePresets(linePresets);
		return preset;
	}
	
	private LinePreset getCurrentLinePreset(final OscLine line) {
		final LinePreset preset = new LinePreset();
		preset.setFreq(line.getFreq());
		preset.setGain(line.getGain());
		preset.setMute(line.isMuted());
		preset.setPan(line.getPan());
		preset.setWaveFormIndex(line.getWaveformIndex());
		return preset;
	}
	
	public static Preset getDefaultPreset() {
		final Preset preset = new Preset();
		preset.setBitsPerSampleIndex(Constants.DEFAULT_BITS_PER_SAMPLE_INDEX);
		preset.setSampleRateIndex(Constants.DEFAULT_SAMPLE_RATE_INDEX);
		preset.setReferenceFreq(Constants.DEFAULT_REFERENCE_FREQ);
		preset.setRevTypeIndex(Constants.DEFAULT_REVTYPE_INDEX);
		preset.setSealing(Constants.DEFAULT_SEALING);
		preset.setVolume(Constants.DEFAULT_MASTER_VOLUME);
		
		final LinePreset[] linePresets = new LinePreset[3];
		for (int i = 0; i < linePresets.length; i++) {
			linePresets[i] = getDefaultLinePreset();
		}
		preset.setLinePresets(linePresets);
		return preset;
	}
	
	private static LinePreset getDefaultLinePreset() {
		final LinePreset preset = new LinePreset();
		preset.setFreq(Constants.DEFAULT_FREQ);
		preset.setGain(Constants.DEFAULT_GAIN);
		preset.setMute(Constants.DEFAULT_MUTE);
		preset.setPan(Constants.DEFAULT_PAN);
		preset.setWaveFormIndex(Constants.DEFAULT_WAVEFORM_INDEX);
		return preset;
	}
	
	public static Preset getRandomPreset() {
		final Preset preset = new Preset();
		preset.setBitsPerSampleIndex(randomValue(Constants.BITS_PER_SAMPLE.length - 1));
		preset.setSampleRateIndex(randomValue(Constants.SAMPLE_RATES.length - 1));
		preset.setReferenceFreq(randomValue(Constants.REFERENCE_FREQ_MIN, Constants.REFERENCE_FREQ_MAX));
		preset.setRevTypeIndex(randomValue(5));
		preset.setSealing(randomBoolValue());
		preset.setVolume(randomValue(Constants.MASTER_VOLUME_MIN, Constants.MASTER_VOLUME_MAX));
		
		final LinePreset[] lines = new LinePreset[3];
		for (int i = 0; i < lines.length; i++) {
			lines[i] = getRandomLinePreset();
		}
		preset.setLinePresets(lines);
		return preset;
	}

	private static LinePreset getRandomLinePreset() {
		final LinePreset preset = new LinePreset();
		preset.setFreq(randomValue(Constants.FREQ_SEMITONES_MIN, Constants.FREQ_SEMITONES_MAX));
		preset.setGain(randomValue(Constants.GAIN_MIN,Constants.GAIN_MAX));
		preset.setPan(randomValue(-1,1));
		preset.setMute(randomBoolValue());
		preset.setWaveFormIndex(randomValue(Constants.WAVEFORMS.length - 1));
		return preset;
	}
	
	private static final int randomValue(final int max) {
		return (int)(((long)max + 1) * Math.random());
	}
	
	private static final int randomValue(final int min, final int max) {
		return min + randomValue(max - min);
	}
	
	private static final boolean randomBoolValue() {
		final int randomValue = randomValue(1);
		if (randomValue == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	@SuppressWarnings("unused") 
	private static final int randomValue() {
		return randomValue(Integer.MIN_VALUE,Integer.MAX_VALUE);
	}

}
