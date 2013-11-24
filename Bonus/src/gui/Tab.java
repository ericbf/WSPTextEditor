package gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import process.CloseCancelledException;

/**
 * @author eric
 */
public class Tab {
	/**
	 * This class is basically a JPanel that can be used as a tab header when
	 * the programmer wants a closable tab. The header will have a button to
	 * close the tab and the tab's title.
	 * 
	 * @author eric
	 */
	static class ClosableTabHeader extends JPanel {
		private static final long	serialVersionUID	= 1L;
		private JLabel				title;
		private JTextComponent		component;
		
		/**
		 * @param tabbedPane
		 * @param title
		 * @throws NullPointerException
		 *             If the passed tabbedPane is null.
		 */
		private ClosableTabHeader(JTabbedPane tabbedPane, String title)
				throws NullPointerException {
			super(new GridBagLayout());
			if (tabbedPane == null) {
				throw new NullPointerException(
						"The closable tab header must have a parent tabbed pane!");
			}
			final JTabbedPane parent = tabbedPane;
			
			GridBagConstraints c = new GridBagConstraints();
			
			c.insets = new Insets(2, 0, 0, 6);
			
			final JButton close;
			JButton temp;
			try {
				temp = new JButton(getIcon(this, "icon.png", "The close icon"));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				temp = new JButton((new ImageIcon()));
			}
			close = temp;
			temp = null;
			
			this.add(close, c);
			
			c = new GridBagConstraints();
			c.weightx = 1;
			c.gridx = 1;
			
			this.title = new JLabel(title);
			this.add(this.title, c);
			
			this.setOpaque(false);
			close.setBorder(new EmptyBorder(2, 2, 2, 2));
			close.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int i = parent.indexOfTabComponent(ClosableTabHeader.this);
					if (i != -1) {
						try {
							parent.remove(i);
						} catch (CloseCancelledException cce) {}
					}
				}
			});
			close.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent me) {}
				
				@Override
				public void mouseEntered(MouseEvent me) {
					close.setBorder(new BevelBorder(BevelBorder.RAISED));
					close.repaint();
				}
				
				@Override
				public void mouseExited(MouseEvent me) {
					close.setBorder(new EmptyBorder(2, 2, 2, 2));
					close.repaint();
				}
				
				@Override
				public void mousePressed(MouseEvent me) {
					close.setBorder(new BevelBorder(BevelBorder.LOWERED));
					close.repaint();
				}
				
				@Override
				public void mouseReleased(MouseEvent me) {
					close.setBorder(new EmptyBorder(2, 2, 2, 2));
					close.repaint();
				}
			});
		}
		
		public JTextComponent getComponent() {
			return this.component;
		}
		
		public void setComponent(JTextComponent comp) {
			this.component = comp;
		}
		
		public void setTitle(String title) {
			this.title.setText(title);
		}
	}
	
	/**
	 * Gets the icon with the passed name in the resources folders.
	 * 
	 * @param parent
	 * @param filename
	 * @param description
	 * @return
	 * @throws FileNotFoundException
	 */
	private static ImageIcon getIcon(Object parent, String filename,
			String description) throws FileNotFoundException {
		URL imgURL = parent.getClass().getResource("/" + filename);
		if (imgURL != null) {
			ImageIcon icon = new ImageIcon(imgURL, description);
			return icon;
		} else {
			throw new FileNotFoundException("Couldn't find the file "
					+ filename + "!!");
		}
	}
	
	private TabbedPane			tabbedPane;
	private String				title;
	private JTextComponent		textComponent;
	private ClosableTabHeader	header;
	
	private int					index;
	
	private boolean				closable;
	private boolean				added;
	
	/**
	 * A new tab. Use the provided methods to set its title, content, index, or
	 * whether it should be closable. If those are not set, defaults will be
	 * used.
	 * 
	 * @param tabbedPane
	 *            The JTabbedPane that is to own this tab. This cannot be
	 *            changed after its setting in the constructor, and it cannot be
	 *            null.
	 */
	public Tab(TabbedPane tabbedPane) {
		if (tabbedPane == null) {
			throw new NullPointerException(
					"Can't create tab for null JTabbedPane!!!");
		}
		
		this.tabbedPane = tabbedPane;
		this.title = null;
		this.textComponent = null;
		this.index = -1;
		this.closable = true;
		this.added = false;
	}
	
	public void add() {
		if (this.added) {
			throw new IllegalStateException(
					"Attempted to add a tab more than once!!");
		} else {
			this.added = true;
		}
		
		if (this.title == null) {
			this.title = "Untitled";
		}
		if (this.index == -1) {
			this.index = this.tabbedPane.getTabCount();
		}
		if (this.textComponent == null) {
			this.textComponent = new JTextPane();
		}
		
		if (this.closable) {
			this.tabbedPane.addTab(this.title,
					this.header = new ClosableTabHeader(this.tabbedPane,
							this.title), this.textComponent, this.index);
		} else {
			JPanel componentHolder = new JPanel(new GridBagLayout());
			componentHolder.setOpaque(false);
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(0, 3, 3, 4);
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1;
			c.weighty = 1;
			componentHolder.add(this.textComponent, c);
			
			this.tabbedPane.insertTab(this.title, null, componentHolder, null,
					this.index);
		}
		this.tabbedPane.setSelectedIndex(this.index);
		this.header.getComponent().requestFocusInWindow();
	}
	
	public Component getComponent() {
		return this.textComponent;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public boolean isClosable() {
		return this.closable;
	}
	
	/**
	 * Sets whether this tab should be closable. The default is true.
	 * 
	 * @param closable
	 * @return
	 */
	public Tab setClosable(boolean closable) {
		this.closable = closable;
		return this;
	}
	
	/**
	 * Sets the content of this tab. Default is a new JTextPane.
	 * 
	 * @param component
	 * @return
	 */
	public Tab setComponent(JTextComponent component) {
		this.textComponent = component;
		return this;
	}
	
	/**
	 * Sets the index of the added tab. Default is at the end.
	 * 
	 * @param index
	 * @return
	 */
	public Tab setIndex(int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException(
					"The index can't be negative!!!");
		}
		this.index = index;
		return this;
	}
	
	/**
	 * Sets the title of this tab. Default is "Untitled".
	 * 
	 * @param title
	 * @return
	 */
	public Tab setTitle(String title) {
		this.title = title;
		if (this.added) {
			this.header.setTitle(title);
		}
		return this;
	}
}