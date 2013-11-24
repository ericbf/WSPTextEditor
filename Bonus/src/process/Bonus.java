/**
 * 
 */
package process;

/**
 * @author eric
 */
import gui.MainWindow;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.rtf.RTFEditorKit;

public class Bonus {
	
	public static JTextComponent getFile(File file) throws IOException,
			BadLocationException {
		if (file.getName().matches(".*\\.rtf")) {
			JEditorPane editor = new JEditorPane();
			editor.setEditorKit(new RTFEditorKit());
			editor.read(new FileReader(file), "reading " + file);
			editor.setName(file.getPath());
			return editor;
		} else if (file.getName().matches(".*\\.txt")) {
			JTextArea area = new JTextArea();
			area.read(new FileReader(file), "reading " + file);
			area.setWrapStyleWord(true);
			area.setLineWrap(true);
			area.setName(file.getPath());
			return area;
		} else {
			return null;
		}
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainWindow mainWindow = new MainWindow();
				mainWindow.setVisible(true);
			}
		});
	}
}
