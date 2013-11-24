/**
 * 
 */
package gui;

import gui.Tab.ClosableTabHeader;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import process.CloseCancelledException;

/**
 * @author eric
 */
public class TabbedPane extends JTabbedPane {
	private static final long	serialVersionUID	= 1L;
	
	public void addTab(String title, ClosableTabHeader header,
			JTextComponent component, int index) {
		
		JPanel componentHolder = new JPanel(new GridBagLayout());
		componentHolder.setOpaque(false);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 3, 3, 4);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		componentHolder.add(new JScrollPane(component), c);
		header.setName(component.getText());
		header.setComponent(component);
		
		this.insertTab(title, null, componentHolder,
				JComponent.TOOL_TIP_TEXT_KEY, index);
		this.setTabComponentAt(index, header);
	}
	
	public boolean isSaved(int index) {
		ClosableTabHeader h = (ClosableTabHeader) this.getTabComponentAt(index);
		return !(h.getComponent().getName() == null);
	}
	
	public boolean needsSave(int index) {
		ClosableTabHeader h = (ClosableTabHeader) this.getTabComponentAt(index);
		return !h.getName().equals(h.getComponent().getText());
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JTabbedPane#removeTabAt(int)
	 */
	@Override
	public void removeTabAt(int index) {
		// TODO Auto-generated method stub
		if (this.needsSave(index)) {
			int response = JOptionPane.showConfirmDialog(this,
					"Do you want to save first?", "Close Dialog",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (response == JOptionPane.YES_OPTION) {
				this.save(index, !this.isSaved(index));
			} else if (response == JOptionPane.CANCEL_OPTION) {
				throw new CloseCancelledException();
			}
		}
		super.removeTabAt(index);
	}
	
	public void save(int index, boolean saveAs) throws CloseCancelledException {
		ClosableTabHeader head;
		JTextComponent text;
		String loc = (text = (head = ((ClosableTabHeader) this
				.getTabComponentAt(index))).getComponent()).getName();
		String newName = null;
		if (saveAs) {
			FileDialog d = new FileDialog(
					(Frame) SwingUtilities.getWindowAncestor(this),
					"Pick a location", FileDialog.SAVE);
			d.setFile(this.getTitleAt(index));
			d.setVisible(true);
			if (d.getDirectory() == null || d.getFile() == null) {
				throw new CloseCancelledException();
			}
			if (!d.getFile().matches(".+\\..+")) {
				d.setFile(d.getFile() + ".txt");
			}
			loc = d.getDirectory() + (newName = d.getFile());
			this.setTitleAt(index, newName);
		}
		try {
			FileWriter fw = new FileWriter(loc);
			fw.write(text.getText());
			fw.close();
			text.setName(loc);
			head.setName(text.getText());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setTitleAt(int index, String title) {
		((ClosableTabHeader) this.getTabComponentAt(index)).setTitle(title);
		super.setTitleAt(index, title);
	}
}
