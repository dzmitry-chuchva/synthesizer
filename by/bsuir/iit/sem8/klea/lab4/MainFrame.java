package by.bsuir.iit.sem8.klea.lab4;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.EnumControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.ReverbType;
import javax.sound.sampled.SourceDataLine;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainFrame extends JFrame implements ActionListener, KeyboardListener, ChangeListener {

	private static final String SAMPLE_SIZE_INDEX_0_OUT_OF_RANGE_1_2 = Messages.getString("MainFrame.0"); //$NON-NLS-1$
	private static final String SAMPLE_RATE_INDEX_0_OUT_OF_RANGE_1_2 = Messages.getString("MainFrame.1"); //$NON-NLS-1$
	private static final String REFERENCE_FREQUENCY_0_OUT_OF_RANGE_1_2 = Messages.getString("MainFrame.2"); //$NON-NLS-1$
	private static final String DEFAULTS_COMMAND = "defaults"; //$NON-NLS-1$
	private static final String RANDOMIZE_COMMAND = "random"; //$NON-NLS-1$
	private static final String SAVE_COMMAND = "save"; //$NON-NLS-1$
	private static final String LOAD_COMMAND = "load"; //$NON-NLS-1$
	private static final int SYNCH_TIME = 300;
	private static final String SAMPLE_RATE_COMMAND = "sampleRate"; //$NON-NLS-1$
	private static final String BITS_PER_SAMPLE_COMMAND = "bitsPerSample"; //$NON-NLS-1$
	private static final String REVERB_TYPE_COMMAND = "reverbType"; //$NON-NLS-1$
	private static final String FRAME_TITLE = Messages.getString("MainFrame.10"); //$NON-NLS-1$
	private static final long serialVersionUID = 1L;
	
	private Mixer mixer = null;
	
	private Keyboard keyboard;
	
	private JComboBox sampleRatesCombo;
	private JComboBox bitsPerSampleCombo;
	private JCheckBox sealingCheckbox;
	
	private OscLine oscLine1;
	private OscLine oscLine2;
	private OscLine oscLine3;
	private JSpinner referenceFreqSpinner;
	private JSlider masterVolumeSlider;
	private JComboBox reverbTypeCombo;
	
	private final PresetManager presetManager = new PresetManager(this);
	
	public MainFrame() {
		super(FRAME_TITLE);
		
		initializeAudio();
		
		getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.PAGE_AXIS));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(final WindowEvent e) {
				if (mixer != null) {
					mixer.close();
				}
			}
			
		});
		
		final JPanel inputPanel = new JPanel();
		final JPanel mixerPanel = new JPanel();
		final JPanel linesPanel = new JPanel();
		
		inputPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("MainFrame.11"))); //$NON-NLS-1$
		mixerPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("MainFrame.12"))); //$NON-NLS-1$
		linesPanel.setBorder(BorderFactory.createTitledBorder(Messages.getString("MainFrame.13"))); //$NON-NLS-1$
		
		inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
		
		final JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.PAGE_AXIS));
		sealingCheckbox = new JCheckBox(Messages.getString("MainFrame.14"),Constants.DEFAULT_SEALING); //$NON-NLS-1$
		sealingCheckbox.addChangeListener(this);
		
		keyboard = new Keyboard(Constants.DEFAULT_SEALING);
		keyboard.setCurrentReferenceFreq(Constants.DEFAULT_REFERENCE_FREQ);
		keyboard.addKeyboardListener(this);
		
		final JPanel refInputPanel = new JPanel();
		refInputPanel.setLayout(new BoxLayout(refInputPanel,BoxLayout.PAGE_AXIS));
		referenceFreqSpinner = new JSpinner(new SpinnerNumberModel(Constants.DEFAULT_REFERENCE_FREQ,Constants.REFERENCE_FREQ_MIN,Constants.REFERENCE_FREQ_MAX,1));
		referenceFreqSpinner.addChangeListener(this);
		referenceFreqSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
		final JLabel label = new JLabel(Messages.getString("MainFrame.15")); //$NON-NLS-1$
		
		refInputPanel.add(label);
		refInputPanel.add(referenceFreqSpinner);
		
		leftPanel.add(sealingCheckbox);
		leftPanel.add(Box.createRigidArea(new Dimension(0,20)));
		leftPanel.add(refInputPanel);
		
		inputPanel.add(leftPanel);
		inputPanel.add(keyboard);
		
		mixerPanel.setLayout(new FlowLayout());
		
		final JPanel sliderPanel = new JPanel(new FlowLayout());
		final JPanel revTypePanel = new JPanel(new FlowLayout());
		final JPanel samplePanel = new JPanel(new FlowLayout());
		final JPanel bitsPanel = new JPanel(new FlowLayout());
		
		masterVolumeSlider = new JSlider(Constants.MASTER_VOLUME_MIN,Constants.MASTER_VOLUME_MAX,Constants.DEFAULT_MASTER_VOLUME);
		masterVolumeSlider.setPaintLabels(true);
		masterVolumeSlider.setPaintTicks(true);
		masterVolumeSlider.setLabelTable(masterVolumeSlider.createStandardLabels(10));
		masterVolumeSlider.setMajorTickSpacing(10);
		masterVolumeSlider.setSnapToTicks(true);
		masterVolumeSlider.addChangeListener(this);
		reverbTypeCombo = new JComboBox(getReverbTypes());
		reverbTypeCombo.setSelectedIndex(Constants.DEFAULT_REVTYPE_INDEX);
		reverbTypeCombo.setActionCommand(REVERB_TYPE_COMMAND);
		reverbTypeCombo.addActionListener(this);
		sampleRatesCombo = new JComboBox(Constants.SAMPLE_RATES);
		sampleRatesCombo.setSelectedIndex(Constants.DEFAULT_SAMPLE_RATE_INDEX);
		sampleRatesCombo.setActionCommand(SAMPLE_RATE_COMMAND);
		sampleRatesCombo.addActionListener(this);
		bitsPerSampleCombo = new JComboBox(Constants.BITS_PER_SAMPLE);
		bitsPerSampleCombo.setSelectedIndex(Constants.DEFAULT_BITS_PER_SAMPLE_INDEX);
		bitsPerSampleCombo.setActionCommand(BITS_PER_SAMPLE_COMMAND);
		bitsPerSampleCombo.addActionListener(this);
		
		sliderPanel.add(new JLabel(Messages.getString("MainFrame.16"))); //$NON-NLS-1$
		revTypePanel.add(new JLabel(Messages.getString("MainFrame.17"))); //$NON-NLS-1$
		samplePanel.add(new JLabel(Messages.getString("MainFrame.18"))); //$NON-NLS-1$
		bitsPanel.add(new JLabel(Messages.getString("MainFrame.19"))); //$NON-NLS-1$
		
		sliderPanel.add(masterVolumeSlider);
		revTypePanel.add(reverbTypeCombo);
		samplePanel.add(sampleRatesCombo);
		bitsPanel.add(bitsPerSampleCombo);
		
		mixerPanel.add(sliderPanel);
		mixerPanel.add(revTypePanel);
		mixerPanel.add(samplePanel);
		mixerPanel.add(bitsPanel);
		
		linesPanel.setLayout(new GridLayout(3,0));
		linesPanel.add(oscLine1);
		linesPanel.add(oscLine2);
		linesPanel.add(oscLine3);
		
		getContentPane().add(inputPanel);
		getContentPane().add(mixerPanel);
		getContentPane().add(linesPanel);
		
		final JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);
		
		final JMenu menu = new JMenu(Messages.getString("MainFrame.20")); //$NON-NLS-1$
		menubar.add(menu);
		
		final JMenuItem menuItem = new JMenuItem(Messages.getString("MainFrame.21")); //$NON-NLS-1$
		menuItem.setActionCommand(LOAD_COMMAND);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		final JMenuItem menuItem2 = new JMenuItem(Messages.getString("MainFrame.22")); //$NON-NLS-1$
		menuItem2.setActionCommand(SAVE_COMMAND);
		menuItem2.addActionListener(this);
		menu.add(menuItem2);
		
		menu.addSeparator();
		
		final JMenuItem menuItem3 = new JMenuItem(Messages.getString("MainFrame.23")); //$NON-NLS-1$
		menuItem3.setActionCommand(RANDOMIZE_COMMAND);
		menuItem3.addActionListener(this);
		menu.add(menuItem3);

		final JMenuItem menuItem4 = new JMenuItem(Messages.getString("MainFrame.24")); //$NON-NLS-1$
		menuItem4.addActionListener(this);
		menuItem4.setActionCommand(DEFAULTS_COMMAND);
		menu.add(menuItem4);
	}

	public void actionPerformed(final ActionEvent arg0) {
		final Object source = arg0.getSource();
		try {
			final String actionCommand = arg0.getActionCommand();
			if (REVERB_TYPE_COMMAND.equals(actionCommand)) {
				final JComboBox combo = (JComboBox) source;
				final int selected = combo.getSelectedIndex();
				setReverbTypeIndex(selected);
			} else if (SAMPLE_RATE_COMMAND.equals(actionCommand)) {
				final JComboBox combo = (JComboBox) source;
				setSampleRateIndex(combo.getSelectedIndex());
			} else if (BITS_PER_SAMPLE_COMMAND.equals(actionCommand)) {
				final JComboBox combo = (JComboBox) source;
				setBitsPerSampleIndex(combo.getSelectedIndex());
			} else if (LOAD_COMMAND.equals(actionCommand)) {
				final Preset preset = presetManager.loadPreset();
				if (preset != null) {
					presetManager.applyPreset(preset);
				}
			} else if (SAVE_COMMAND.equals(actionCommand)) {
				final Preset preset = presetManager.getCurrentPreset();
				presetManager.savePreset(preset);
			} else if (RANDOMIZE_COMMAND.equals(actionCommand)) {
				final Preset randomPreset = PresetManager.getRandomPreset();
				presetManager.applyPreset(randomPreset);
			} else if (DEFAULTS_COMMAND.equals(actionCommand)) {
				final Preset defaultPreset = PresetManager.getDefaultPreset();
				presetManager.applyPreset(defaultPreset);
			}
		} catch (final Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			e.printStackTrace();
		}
	}

	public void keyPressed(final KeyboardEvent event) {
		final float frequency = event.getFrequency();
		
		final boolean selected = sealingCheckbox.isSelected();
		if (selected) {
			sampleRatesCombo.setEnabled(false);
			bitsPerSampleCombo.setEnabled(false);
			referenceFreqSpinner.setEnabled(false);
			sealingCheckbox.setEnabled(false);
		}
		
		startSynthAt(frequency);
	}

	private void startSynthAt(final float frequency) {
		oscLine1.startSound(frequency);
		oscLine2.startSound(frequency);
		oscLine3.startSound(frequency);
		try {
			Thread.sleep(SYNCH_TIME);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		Synthesizer.notifySynth();
	}

	public void keyReleased(final KeyboardEvent event) {
		final boolean selected = sealingCheckbox.isSelected();
		if (selected) {
			sampleRatesCombo.setEnabled(true);
			bitsPerSampleCombo.setEnabled(true);
			referenceFreqSpinner.setEnabled(true);
			sealingCheckbox.setEnabled(true);
		}
		
		stopSynth();
	}

	private void stopSynth() {
		oscLine1.stopSound();
		oscLine2.stopSound();
		oscLine3.stopSound();
		try {
			Thread.sleep(SYNCH_TIME);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		Synthesizer.notifySynth();
	}
	
	private Object[] getReverbTypes() {
		final EnumControl revType = (EnumControl) mixer.getControl(EnumControl.Type.REVERB);
		final Object[] values = revType.getValues();
		final String[] names = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			final ReverbType rev = (ReverbType) values[i];
			names[i] = rev.getName();
		}
		return names;		
	}
	
	private void initializeAudio() {
		final Mixer.Info[] info = AudioSystem.getMixerInfo();
		for (int i = 0; i < info.length; i++) {
			final Mixer mixer = AudioSystem.getMixer(info[i]);
			if (mixer.isControlSupported(EnumControl.Type.REVERB)) {
				this.mixer = mixer;
				break;
			}
		}
		
		final Integer defaultSampleRate = Constants.SAMPLE_RATES[Constants.DEFAULT_SAMPLE_RATE_INDEX];
		final Integer defaultBitsPerSample = Constants.BITS_PER_SAMPLE[Constants.DEFAULT_BITS_PER_SAMPLE_INDEX];
		final DataLine.Info dinfo = new DataLine.Info(SourceDataLine.class, new AudioFormat(defaultSampleRate,defaultBitsPerSample,1,true,false));		
		
		try {
			mixer.open();
			
			final SourceDataLine line1 = (SourceDataLine) mixer.getLine(dinfo);
			final SourceDataLine line2 = (SourceDataLine) mixer.getLine(dinfo);
			final SourceDataLine line3 = (SourceDataLine) mixer.getLine(dinfo);
			
			oscLine1 = new OscLine(Messages.getString("MainFrame.25"),line1); //$NON-NLS-1$
			oscLine1.setVolume(Constants.DEFAULT_MASTER_VOLUME);
			oscLine2 = new OscLine(Messages.getString("MainFrame.26"),line2); //$NON-NLS-1$
			oscLine2.setVolume(Constants.DEFAULT_MASTER_VOLUME);			
			oscLine3 = new OscLine(Messages.getString("MainFrame.27"),line3); //$NON-NLS-1$
			oscLine3.setVolume(Constants.DEFAULT_MASTER_VOLUME);			
		} catch (final LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void stateChanged(final ChangeEvent event) {
		final Object source = event.getSource();
		try {
			if (source instanceof JCheckBox) {
				// sealing key on/off
				final JCheckBox checkbox = (JCheckBox) source;
				
				final boolean selected = checkbox.isSelected();
				setSealing(selected);
			} else if (source instanceof JSpinner) {
				// reference freq change
				final JSpinner spinner = (JSpinner) source;
				final int value = (Integer)spinner.getValue();
				setReferenceFreq(value);
			} else if (source instanceof JSlider) {
				// volume change
				final JSlider slider = (JSlider) source;
				final int volume = slider.getValue();
				setMasterVolume(volume);
			}
		} catch (final Exception e) {
			//never
			e.printStackTrace();
		}
	}

	public void setReferenceFreq(final int value) throws Exception {
		if (value < Constants.REFERENCE_FREQ_MIN || value > Constants.REFERENCE_FREQ_MAX) {
			throw new Exception(MessageFormat.format(REFERENCE_FREQUENCY_0_OUT_OF_RANGE_1_2,new Object[] { value, Constants.REFERENCE_FREQ_MIN, Constants.REFERENCE_FREQ_MAX }));
		}
		referenceFreqSpinner.setValue(value);
		keyboard.setCurrentReferenceFreq(value);
	}

	public void setSealing(final boolean selected) {
		sealingCheckbox.setSelected(selected);
		keyboard.setSealing(selected);
	}
	
	public void setReverbTypeIndex(final int type) throws Exception {
		final EnumControl control = (EnumControl) mixer.getControl(EnumControl.Type.REVERB);
		final Object[] values = control.getValues();
		
		if (type < 0 || type >= values.length) {
			throw new Exception(Messages.getString("MainFrame.28") + type); //$NON-NLS-1$
		}
		control.setValue(values[type]);
		reverbTypeCombo.setSelectedIndex(type);
	}
	
	private void resetAudioFormat() {
		final int sampleRate = getSampleRate();
		final int bitsPerSample = getBitsPerSample();

		final DataLine.Info dinfo = new DataLine.Info(SourceDataLine.class, new AudioFormat(sampleRate,bitsPerSample,1,true,false));
		
		try {
			final SourceDataLine line1 = (SourceDataLine) mixer.getLine(dinfo);
			final SourceDataLine line2 = (SourceDataLine) mixer.getLine(dinfo);
			final SourceDataLine line3 = (SourceDataLine) mixer.getLine(dinfo);
			
			oscLine1.setLine(line1);
			oscLine1.setLine(line2);
			oscLine1.setLine(line3);
		} catch (final LineUnavailableException e) {
			e.printStackTrace();
		}		
	}
	
	public void setMasterVolume(final int volume) throws Exception {
		final int maximum = masterVolumeSlider.getMaximum();
		final int minimum = masterVolumeSlider.getMinimum();
		if (volume < minimum || volume > maximum) {
			throw new Exception(MessageFormat.format(Messages.getString("MainFrame.29"), new Object[] { volume, minimum, maximum })); //$NON-NLS-1$
		}
		masterVolumeSlider.setValue(volume);
		oscLine1.setVolume(volume);
		oscLine2.setVolume(volume);
		oscLine3.setVolume(volume);
	}
	
	public OscLine[] getLines() {
		return new OscLine[] { oscLine1, oscLine2, oscLine3 };
	}
	
	public int getSampleRate() {
		return (Integer)sampleRatesCombo.getSelectedItem();
	}
	
	public int getBitsPerSample() {
		return (Integer)bitsPerSampleCombo.getSelectedItem();
	}
	
	public int getMasterVolume() {
		return masterVolumeSlider.getValue();
	}
	
	public int getReferenceFreq() {
		return (Integer)referenceFreqSpinner.getValue();
	}
	
	public String getReverbType() {
		return (String)reverbTypeCombo.getSelectedItem();
	}
	
	public boolean isSealing() {
		return sealingCheckbox.isSelected();
	}
	
	public void setSampleRateIndex(final int sampleRateIndex) throws Exception {
		final int itemCount = sampleRatesCombo.getItemCount();
		if (sampleRateIndex < 0 || sampleRateIndex > itemCount) {
			throw new Exception(MessageFormat.format(SAMPLE_RATE_INDEX_0_OUT_OF_RANGE_1_2, new Object[] { sampleRateIndex, 0, itemCount }));
		}
		sampleRatesCombo.setSelectedIndex(sampleRateIndex);
		resetAudioFormat();
	}
	
	public void setBitsPerSampleIndex(final int bitsPerSampleIndex) throws Exception {
		final int itemCount = bitsPerSampleCombo.getItemCount();
		if (bitsPerSampleIndex < 0 || bitsPerSampleIndex > itemCount) {
			throw new Exception(MessageFormat.format(SAMPLE_SIZE_INDEX_0_OUT_OF_RANGE_1_2, new Object[] { bitsPerSampleIndex, 0, itemCount }));
		}
		bitsPerSampleCombo.setSelectedIndex(bitsPerSampleIndex);
		resetAudioFormat();
	}
	
	public int getSampleRateIndex() {
		return sampleRatesCombo.getSelectedIndex();
	}
	
	public int getBitsPerSampleIndex() {
		return bitsPerSampleCombo.getSelectedIndex();
	}
	
	public int getReverbTypeIndex() {
		return reverbTypeCombo.getSelectedIndex();
	}
	
}
