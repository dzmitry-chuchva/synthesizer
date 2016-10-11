package by.bsuir.iit.sem8.klea.lab4;

import java.util.EventListener;

public interface KeyboardListener extends EventListener {
	
	public void keyPressed(KeyboardEvent event);
	public void keyReleased(KeyboardEvent event);

}
