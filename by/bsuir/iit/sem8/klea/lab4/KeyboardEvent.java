package by.bsuir.iit.sem8.klea.lab4;

import java.awt.AWTEvent;

public class KeyboardEvent extends AWTEvent {
	
	public static final int KEYBOARD_EVENT = AWTEvent.RESERVED_ID_MAX + 100;
	
	private float frequency;

	public KeyboardEvent(final Object source, final float frequency) {
		super(source,KEYBOARD_EVENT);
		
		this.frequency = frequency;
	}
	
	public float getFrequency() {
		return frequency;
	}

	private static final long serialVersionUID = 1L;

}
