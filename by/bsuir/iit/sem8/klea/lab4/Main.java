package by.bsuir.iit.sem8.klea.lab4;

public class Main {

	public static void main(final String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				createAndShowGUI();
			}
			
		});
	}
	
	public static void createAndShowGUI() {
		final MainFrame frame = new MainFrame();
		frame.pack();
		frame.setVisible(true);
	}

}
