package by.bsuir.iit.sem8.klea.lab4;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Hashtable;

import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.SourceDataLine;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class OscLine extends JPanel implements ActionListener, ChangeListener {
	
	private static final String GAIN_0_OUT_OF_RANGE_1_2 = Messages.getString("OscLine.0"); //$NON-NLS-1$
	private static final String PAN_0_OUT_OF_RANGE_1_2 = Messages.getString("OscLine.1"); //$NON-NLS-1$
	private static final String FREQUENCY_DERIVANCE_0_OUT_OF_RANGE_1_2 = Messages.getString("OscLine.2"); //$NON-NLS-1$
	private static final long serialVersionUID = 1L;

	private static final String PAN_0 = Messages.getString("OscLine.3"); //$NON-NLS-1$
	private static final String GAIN_DB_0 = Messages.getString("OscLine.4"); //$NON-NLS-1$
	private static final String FREQUENCY_SEMITONES_0 = Messages.getString("OscLine.5"); //$NON-NLS-1$
	private static final String GAIN_SLIDER = "gain"; //$NON-NLS-1$
	private static final String PAN_SLIDER = "pan"; //$NON-NLS-1$
	private static final String FREQ_SLIDER = "freq"; //$NON-NLS-1$

	private Synthesizer synth;
	private JComboBox waveFormsCombo;

	private JLabel freqLabel;
	private JLabel panLabel;
	private JLabel gainLabel;
	private JSlider gainSlider;
	private JSlider panSlider;
	private JSlider freqSlider;
	private JCheckBox muteCheckbox;
	
	public OscLine(final String title, final SourceDataLine line) {
		super();
		
		synth = new Synthesizer(line);
		synth.setFreq(Constants.DEFAULT_FREQ);
		synth.setPan(Constants.DEFAULT_PAN);
		synth.setGain(Constants.DEFAULT_GAIN);
		synth.setVolume(Constants.DEFAULT_MASTER_VOLUME);
		synth.setMute(Constants.DEFAULT_MUTE);
		synth.setWaveform(Constants.DEFAULT_WAVEFORM_INDEX);
		
		setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		final JLabel label = new JLabel(title);
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		label.setFont(new Font(Font.DIALOG,Font.BOLD|Font.ITALIC,14));
		label.setForeground(Color.RED);
		add(label);
		
		final JPanel wavePanel = new JPanel();
		final BoxLayout waveLayout = new BoxLayout(wavePanel,BoxLayout.PAGE_AXIS);
		wavePanel.setLayout(waveLayout);
		final JLabel waveLabel = new JLabel(Messages.getString("OscLine.9")); //$NON-NLS-1$
		wavePanel.add(waveLabel);
		waveFormsCombo = new JComboBox(Constants.WAVEFORMS);
		waveFormsCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
		waveFormsCombo.addActionListener(this);
		wavePanel.add(waveFormsCombo);
		add(wavePanel);
		
		final JPanel gainPanel = new JPanel();
		final BoxLayout boxLayout = new BoxLayout(gainPanel,BoxLayout.PAGE_AXIS);
		gainPanel.setLayout(boxLayout);
		
		gainSlider = getGainSlider(line);
		gainSlider.addChangeListener(this);
		gainSlider.setName(GAIN_SLIDER);
		
		gainLabel = new JLabel(MessageFormat.format(GAIN_DB_0, new Object[] { new Integer(gainSlider.getValue()) }));
		gainLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		gainPanel.add(gainLabel);
		gainPanel.add(gainSlider);
		add(gainPanel);
		
		final JPanel panPanel = new JPanel();
		final BoxLayout panLayout = new BoxLayout(panPanel,BoxLayout.PAGE_AXIS);
		panPanel.setLayout(panLayout);
		
		panSlider = getPanSlider(line);
		panSlider.addChangeListener(this);
		panSlider.setName(PAN_SLIDER);
		panLabel = new JLabel(MessageFormat.format(PAN_0, new Object[] { new Integer(panSlider.getValue()) }));
		panLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		panPanel.add(panLabel);
		panPanel.add(panSlider);
		add(panPanel);
		
		final JPanel freqPanel = new JPanel();
		freqPanel.setLayout(new BoxLayout(freqPanel,BoxLayout.PAGE_AXIS));
		freqSlider = new JSlider(Constants.FREQ_SEMITONES_MIN,Constants.FREQ_SEMITONES_MAX,Constants.DEFAULT_FREQ);
		freqSlider.setPaintTicks(true);
		freqSlider.setMajorTickSpacing(6);
		freqSlider.setMinorTickSpacing(2);
		freqSlider.setPaintLabels(true);
		freqSlider.setLabelTable(freqSlider.createStandardLabels(12));
		freqSlider.addChangeListener(this);
		freqSlider.setName(FREQ_SLIDER);
		freqLabel = new JLabel(MessageFormat.format(FREQUENCY_SEMITONES_0, new Object[] { new Integer(freqSlider.getValue()) }));
		freqLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		freqPanel.add(freqLabel);
		freqPanel.add(freqSlider);
		add(freqPanel);
		
		muteCheckbox = new JCheckBox(Messages.getString("OscLine.10"),Constants.DEFAULT_MUTE); //$NON-NLS-1$
		muteCheckbox.addChangeListener(this);
		add(muteCheckbox);
	}
	
	private static JSlider getPanSlider(final Line line) {
		final FloatControl pan = (FloatControl) line.getControl(FloatControl.Type.PAN);
		
		final int minimum = (int)pan.getMinimum();
		final int maximum = (int)pan.getMaximum();
		
		final int value = Constants.DEFAULT_PAN;		
		final JSlider slider = new JSlider(minimum,maximum,value);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setMajorTickSpacing(1);
		slider.setSnapToTicks(true);
		
		final Hashtable<Integer, JComponent> hashtable = new Hashtable<Integer, JComponent>();
		hashtable.put(new Integer(minimum), new JLabel(Messages.getString("OscLine.11"))); //$NON-NLS-1$
		hashtable.put(new Integer(maximum), new JLabel(Messages.getString("OscLine.12"))); //$NON-NLS-1$
		hashtable.put(new Integer(value), new JLabel(Messages.getString("OscLine.13"))); //$NON-NLS-1$
		slider.setLabelTable(hashtable);
		
		return slider;
	}
	
	private static JSlider getGainSlider(final Line line) {
		final FloatControl gain = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
		
		int minimum = (int)gain.getMinimum();
		int maximum = (int)gain.getMaximum();
		
		final int value = Constants.DEFAULT_GAIN;
		
		if (minimum < Constants.GAIN_MIN) {
			minimum = Constants.GAIN_MIN;
		}
		
		if (maximum > Constants.GAIN_MAX) {
			maximum = Constants.GAIN_MAX;
		}
		
		final JSlider slider = new JSlider(minimum,maximum,value);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setMajorTickSpacing(5);
		slider.setLabelTable(slider.createStandardLabels(5));
		
		return slider;
	}
	
	public SourceDataLine getLine() {
		return synth.getLine();
	}

	public void setLine(final SourceDataLine line) {
		synth.setLine(line);
	}

	public void actionPerformed(final ActionEvent event) {
		if (event.getSource() instanceof JComboBox) {
			final JComboBox combo = (JComboBox) event.getSource();
			final int selectedIndex = combo.getSelectedIndex();
			
			try {
				setWaveformIndex(selectedIndex);
			} catch (final Exception e) {
				//never
				e.printStackTrace();
			}
		}
	}

	public void setWaveformIndex(final int selectedIndex) throws Exception {
		final int itemCount = waveFormsCombo.getItemCount();
		if (selectedIndex < 0 || selectedIndex > itemCount) {
			throw new Exception(Messages.getString("OscLine.14") + selectedIndex); //$NON-NLS-1$
		}
		waveFormsCombo.setSelectedIndex(selectedIndex);
		synth.setWaveform(selectedIndex);
	}

	public void stateChanged(final ChangeEvent event) {
		final Object source = event.getSource();
		if (source instanceof JSlider) {
			final JSlider slider = (JSlider) source;
			final int value = slider.getValue();
			try {
				if (GAIN_SLIDER.equals(slider.getName())) {
					setGain(value);
				} else if (PAN_SLIDER.equals(slider.getName())) {
					setPan(value);
				} else if (FREQ_SLIDER.equals(slider.getName())) {
					setFreq(value);
				}
			} catch (final Exception e) {
				//never
				e.printStackTrace();
			}
		} else if (source instanceof JCheckBox) {
			final JCheckBox checkbox = (JCheckBox) source;
			final boolean selected = checkbox.isSelected();
			setMuted(selected);
		}
	}

	public void setMuted(final boolean selected) {
		muteCheckbox.setSelected(selected);
		synth.setMute(selected);
	}

	public void setFreq(final int value) throws Exception {
		final int minimum = freqSlider.getMinimum();
		final int maximum = freqSlider.getMaximum();
		if (value < minimum || value > maximum) {
			throw new Exception(MessageFormat.format(FREQUENCY_DERIVANCE_0_OUT_OF_RANGE_1_2, new Object[] { value, minimum, maximum }));
		}
		synth.setFreq(value);
		freqLabel.setText(MessageFormat.format(FREQUENCY_SEMITONES_0, new Object[] { new Integer(value) }));
		freqSlider.setValue(value);
	}

	public void setPan(final int value) throws Exception {
		final int minimum = panSlider.getMinimum();
		final int maximum = panSlider.getMaximum();
		if (value < minimum || value > maximum) {
			throw new Exception(MessageFormat.format(PAN_0_OUT_OF_RANGE_1_2, new Object[] { value, minimum, maximum }));
		}
		synth.setPan(value);
		panLabel.setText(MessageFormat.format(PAN_0, new Object[] { new Integer(value) }));
		panSlider.setValue(value);
	}

	public void setGain(final int value) throws Exception {
		final int minimum = gainSlider.getMinimum();
		final int maximum = gainSlider.getMaximum();
		if (value < minimum || value > maximum) {
			throw new Exception(MessageFormat.format(GAIN_0_OUT_OF_RANGE_1_2, new Object[] { value, minimum, maximum }));
		}
		synth.setGain(value);
		gainLabel.setText(MessageFormat.format(GAIN_DB_0, new Object[] { new Integer(value) }));
		gainSlider.setValue(value);
	}
	
	public void startSound(final float frequency) {
		synth.start(frequency);
	}
	
	public void stopSound() {
		synth.stop();
	}
	
	public void setVolume(final int volume) {
		synth.setVolume(volume);
	}
	
	public int getWaveformIndex() {
		return waveFormsCombo.getSelectedIndex();
	}
	
	public int getGain() {
		return gainSlider.getValue();
	}
	
	public int getPan() {
		return panSlider.getValue();
	}
	
	public int getFreq() {
		return freqSlider.getValue();
	}
	
	public boolean isMuted() {
		return muteCheckbox.isSelected();
	}

}
