package by.bsuir.iit.sem8.klea.lab4;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

public class Keyboard extends JComponent implements MouseListener, MouseMotionListener {

	private static final int MAXIMUM_HZ = 10000;
	private static final int MINIMUM_HZ = 20;
	private static final int A_FREQ_HZ = 440;
	private static final Color WHITEKEY_PRESSED_COLOR = Color.LIGHT_GRAY;
	private static final Color BLACKKEY_PRESSED_COLOR = Color.DARK_GRAY;
	private static final Color BLACKKEY_COLOR = Color.BLACK;
	private static final Color CONTOUR_COLOR = BLACKKEY_COLOR;
	private static final Color BASEKEY_COLOR = Color.ORANGE;
	private static final Color WHITEKEY_COLOR = Color.WHITE;
	private static final int TONES_PER_OCTAVE = 12;
	private static final double BLACKKEY_WIDTH_MULT = 1.5;
	private static final double BLACKKEY_HEIGHT_MULT = 0.6;
	private static final int KEYBOARD_HEIGHT = 80;
	private static final int WHITEKEYS_COUNT = 7;
	private static final int WHITEKEY_WIDTH = 15;
	private static final int MIN_OCTAVES = 1;
	private static final int MAX_OCTAVES = 10;
	private static final long serialVersionUID = 1L;
	
	// calculable constants
	private static final int BLACKKEY_HEIGHT = (int)(KEYBOARD_HEIGHT * BLACKKEY_HEIGHT_MULT);
	private static final int HALF_WHITEKEY_WIDTH = WHITEKEY_WIDTH / 2;
	private static final int BLACKKEY_WIDTH = (int)(HALF_WHITEKEY_WIDTH * BLACKKEY_WIDTH_MULT);
	private static final int HALF_BLACKKEY_WIDTH = BLACKKEY_WIDTH / 2;
	private static final int OCTAVE_WIDTH = WHITEKEYS_COUNT * WHITEKEY_WIDTH;
	
	private int octaves = 3;
	private int baseOctave = 2;
	private int baseNote = 10;
	
	private float currentReferenceFreq = A_FREQ_HZ;
	
	private int currentNote = -1;
	private int currentOctave = -1;
	
	private final EventListenerList listeners = new EventListenerList();
	
	private boolean sealing = false;
	
	public Keyboard() {
		super();
		
		initialize();
	}

	private void initialize() {
		setBackground(WHITEKEY_COLOR);
		setOpaque(true);
		
		final Dimension dimension = new Dimension(OCTAVE_WIDTH * octaves,KEYBOARD_HEIGHT);
		setPreferredSize(dimension);
		setMinimumSize(dimension);
		setMaximumSize(dimension);
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
		
	public Keyboard(final int octaves) {
		super();
		
		if (octaves >= MIN_OCTAVES && octaves <= MAX_OCTAVES) {
			this.octaves = octaves;
		}
		
		initialize();
	}
	
	public Keyboard(final boolean sealing, final int octaves) {
		super();
		
		this.sealing = sealing;
		if (octaves >= MIN_OCTAVES && octaves <= MAX_OCTAVES) {
			this.octaves = octaves;
		}
		
		initialize();
	}
	
	public Keyboard(final boolean sealing) {
		super();
		
		this.sealing = sealing;
		
		initialize();
	}

	@Override
	protected void paintComponent(final Graphics g) {
		final Insets insets = getInsets();
		final int x = insets.left;
		final int y = insets.top;
	    final int currentWidth = getWidth() - insets.left - insets.right - 1;
	    final int currentHeight = getHeight() - insets.top - insets.bottom - 1;
	
	    if (isOpaque()) {
		    g.setColor(getBackground());
		    g.fillRect(0, 0, getWidth(), getHeight());
	    }
	    
	    final Graphics2D g2 = (Graphics2D) g.create();
	    
	    int octaveOriginX = 0;
    	for (int octave = 0; octave < octaves; octave++) {
    		
    		g2.translate(octaveOriginX, 0);
    		
    		// draw keys
    		int keyOffset = 0;
    		int note = 1;
    		for (int i = 0; i < WHITEKEYS_COUNT; i++) {
    			
    			int startY = y;

    			Color whiteKeyColor = WHITEKEY_COLOR;
    			final int octave1 = octave + 1;
				if (octave1 == baseOctave && note == baseNote) {
    				whiteKeyColor = BASEKEY_COLOR;
    			}
    			if (octave1 == currentOctave && note == currentNote) {
    				whiteKeyColor = WHITEKEY_PRESSED_COLOR;
    			}
    			g2.setPaint(whiteKeyColor);
    			g2.fillRect(keyOffset + 1, y + 1, WHITEKEY_WIDTH - 1, currentHeight - 1);
    			
    			if (i != 0 && i != 3) {
    				g2.setPaint(CONTOUR_COLOR);
    				g2.drawRect(keyOffset - HALF_BLACKKEY_WIDTH, y, BLACKKEY_WIDTH, BLACKKEY_HEIGHT);
    				
    				Color blackKeyColor = BLACKKEY_COLOR;
    				final int note1 = note - 1;
					if (octave1 == baseOctave && note1 == baseNote) {
    					blackKeyColor = BASEKEY_COLOR;
    				}
    				if (octave1 == currentOctave && note1 == currentNote) {
    					blackKeyColor = BLACKKEY_PRESSED_COLOR;
    				}
    				
    				g2.setPaint(blackKeyColor);
    				g2.fillRect(keyOffset - HALF_BLACKKEY_WIDTH + 1, y + 1, BLACKKEY_WIDTH - 1, BLACKKEY_HEIGHT - 1);
    				
    				startY += BLACKKEY_HEIGHT;
    			}
    			
    			if (i == 2) {
    				note--;
    			}
    			
    			g2.setPaint(CONTOUR_COLOR);
				g2.drawLine(keyOffset, startY, keyOffset, currentHeight);
				keyOffset += WHITEKEY_WIDTH;
    			note += 2;
	    	}
    		
    		g2.translate(-octaveOriginX, 0);
    		
    		octaveOriginX += OCTAVE_WIDTH;
	    }
    	
    	// draw border
	    g2.setColor(CONTOUR_COLOR);
	    g2.drawRect(x, y, currentWidth, currentHeight);
	    
	    g2.dispose();
	}
	
	public void addKeyboardListener(final KeyboardListener listener) {
		listeners.add(KeyboardListener.class, listener);		
	}
	
	public void removeKeyboardListener(final KeyboardListener listener) {
		listeners.remove(KeyboardListener.class, listener);
	}
	
	private void fireKeyPressedEvent(final float frequency) {
		final KeyboardEvent event = new KeyboardEvent(this,frequency);
		final Object[] list = listeners.getListenerList();
		for (int i = list.length - 2; i >= 0; i -= 2) {
			if (list[i] == KeyboardListener.class) {
				((KeyboardListener)list[i + 1]).keyPressed(event);
			}
		}		
	}
	
	private void fireKeyReleasedEvent(final float frequency) {
		final KeyboardEvent event = new KeyboardEvent(this,frequency);
		final Object[] list = listeners.getListenerList();
		for (int i = list.length - 2; i >= 0; i -= 2) {
			if (list[i] == KeyboardListener.class) {
				((KeyboardListener)list[i + 1]).keyReleased(event);
			}
		}		
	}
	
	private void setCurrentNote(final int note) {
		if (note > 0) {
			this.currentNote = (note - 1) % TONES_PER_OCTAVE + 1;
			this.currentOctave = (note - 1) / TONES_PER_OCTAVE + 1;
		} else {
			this.currentNote = -1;
			this.currentOctave = -1;
		}
	}
	
	private int getCurrentNote() {
		return currentNote + (currentOctave - 1) * TONES_PER_OCTAVE;
	}
	
	private void setBaseNote(final int note) {
		if (note > 0) {
			this.baseNote = (note - 1) % TONES_PER_OCTAVE + 1;
			this.baseOctave = (note - 1) / TONES_PER_OCTAVE + 1;
		} else {
			this.baseNote = 10;
			this.baseOctave = 2;
		}
		repaint();
	}
	
	public void mouseClicked(final MouseEvent event) {
		if (event.getButton() == 3) {
			final int note = getNoteAt(event.getPoint());
			setBaseNote(note);
		}
	}

	public void mouseEntered(final MouseEvent event) {
	}

	public void mouseExited(final MouseEvent event) {
	}

	public void mousePressed(final MouseEvent event) {
		if (event.getButton() == 1) {
			final int note = getNoteAt(event.getPoint());
			final float frequency = getRelativeFrequencyOf(note);
			final int oldNote = getCurrentNote();
			
			if (sealing && oldNote == note) {
				setCurrentNote(0);
				fireKeyReleasedEvent(frequency);
			} else {
				setCurrentNote(note);
				fireKeyPressedEvent(frequency);
			}
			repaint();
		}
	}

	public void mouseReleased(final MouseEvent event) {
		if (event.getButton() == 1) {
			if (!sealing) {
				final int note = getNoteAt(event.getPoint());
				setCurrentNote(0);
				final float frequency = getRelativeFrequencyOf(note);
				fireKeyReleasedEvent(frequency);
				repaint();
			}
		}
	}
	
	private int getNoteAt(final Point p) {
		final Insets insets = getInsets();
		
		int x = p.x - insets.left;
		int y = p.y - insets.top;
		final int width = getWidth() - insets.left - insets.right;
	    final int height = getHeight() - insets.top - insets.bottom; 
		
		if (x <= 0) {
			x = insets.left + 1;
		}
		
		if (x >= width) {
			x = width - 1;
		}
		
		if (y <= 0) {
			y = insets.top + 1;
		}
		
		if (y >= height) {
			y = height - 1;
		}
	    
	    int note = 1;
	    
		final int octave = x / OCTAVE_WIDTH;
		note += octave * TONES_PER_OCTAVE;
		
		final int localx = x % OCTAVE_WIDTH;
		
		if (y > BLACKKEY_HEIGHT) {
			final int white = localx / WHITEKEY_WIDTH;
			note += white * 2;
			if (white > 2) {
				note--;
			}
		} else {
			int offset = 0;
			for (int i = 1; i <= 12; i++) {
				if (i < 6) {
					if (i % 2 == 0) {
						offset += BLACKKEY_WIDTH;
					} else {
						if (i == 1 || i == 5) {
							offset += WHITEKEY_WIDTH - HALF_BLACKKEY_WIDTH;
						} else {
							offset += WHITEKEY_WIDTH - BLACKKEY_WIDTH;
						}
					}
				} else {
					if (i % 2 == 0) {
						if (i == 6 || i == 12) {
							offset += WHITEKEY_WIDTH - HALF_BLACKKEY_WIDTH;
						} else {
							offset += WHITEKEY_WIDTH - BLACKKEY_WIDTH;
						}
					} else {
						offset += BLACKKEY_WIDTH;
					}
				}
				if (localx < offset) {
					note += i - 1;
					break;
				}
			}
		}
		
		return note;
	}
	
	private float getRelativeFrequencyOf(final int note) {
		final float p = (note - baseNote - (baseOctave - 1) * TONES_PER_OCTAVE) / (float)TONES_PER_OCTAVE;
		float freq = currentReferenceFreq * (float)Math.pow(2,p);
		if (freq < MINIMUM_HZ) {
			freq = MINIMUM_HZ;
		} else if (freq > MAXIMUM_HZ) {
			freq = MAXIMUM_HZ;
		}
		return freq;
	}

	public boolean isSealing() {
		return sealing;
	}

	public void setSealing(final boolean sealing) {
		this.sealing = sealing;
	}

	public float getCurrentReferenceFreq() {
		return currentReferenceFreq;
	}

	public void setCurrentReferenceFreq(final float currentReferenceFreq) {
		this.currentReferenceFreq = currentReferenceFreq;
	}

	public void mouseDragged(final MouseEvent e) {
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0 && !sealing) {
			final int note = getNoteAt(e.getPoint());
			final int oldNote = getCurrentNote();
			
			if (note != oldNote) {
				final float frequency = getRelativeFrequencyOf(note);
				
				setCurrentNote(note);
				fireKeyPressedEvent(frequency);
				repaint();
			}
		}
	}

	public void mouseMoved(final MouseEvent e) {
	}
	
}
