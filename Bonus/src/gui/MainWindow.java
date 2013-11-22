/**
 * 
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CancellationException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;

import process.Bonus;

/**
 * @author eric
 */
public class MainWindow extends JFrame {
	private static final long					serialVersionUID	= 1L;
	public static ArrayList<MainWindow>			openWindows			= new ArrayList<>();
	public static HashMap<Component, String>	saved				= new HashMap<>();
	
	private JTabbedPane							tabs;
	private ArrayList<JMenuItem>				toDeactivate;
	
	String										filename;
	
	public MainWindow() {
		this(true);
		
		this.setLocationRelativeTo(null);
	}
	
	private MainWindow(boolean internal) {
		super("Eric's TextEdit");
		MainWindow.openWindows.add(this);
		this.toDeactivate = new ArrayList<>();
		
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent e) {}
			
			@Override
			public void windowClosed(WindowEvent e) {}
			
			@Override
			public void windowClosing(WindowEvent e) {
				MainWindow.openWindows.remove(MainWindow.this);
				if (MainWindow.openWindows.isEmpty()) {
					System.exit(0);
				}
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {}
			
			@Override
			public void windowDeiconified(WindowEvent e) {}
			
			@Override
			public void windowIconified(WindowEvent e) {}
			
			@Override
			public void windowOpened(WindowEvent e) {}
		});
		
		JMenuBar menus = new JMenuBar();
		menus.setBorder(null);
		JMenu menu;
		JMenuItem item;
		
		// Start file menu!!!! -------------------------------------------------
		menu = new JMenu("file");
		
		item = new JMenuItem("New File in New Window");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				java.awt.event.InputEvent.SHIFT_MASK
						| Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainWindow newWindow = new MainWindow(MainWindow.this);
				new Tab(newWindow.getTabbedPane()).add();
				newWindow.setVisible(true);
			}
		});
		menu.add(item);
		item = new JMenuItem("New File in New Tab");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Tab(MainWindow.this.tabs).add();
			}
		});
		menu.add(item);
		
		item = new JMenuItem("Open in New Window...");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				java.awt.event.InputEvent.SHIFT_MASK
						| Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainWindow theNew = new MainWindow(MainWindow.this);
				new Tab(theNew.getTabbedPane())
						.setComponent(MainWindow.this.loadFileIntoComponent())
						.setTitle(MainWindow.this.filename).add();
				theNew.setVisible(true);
			}
		});
		menu.add(item);
		
		item = new JMenuItem("Open in New Tab...");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new Tab(MainWindow.this.tabs)
							.setComponent(
									MainWindow.this.loadFileIntoComponent())
							.setTitle(MainWindow.this.filename).add();
				} catch (CancellationException ce) {}
			}
		});
		menu.add(item);
		
		menu.addSeparator();
		
		item = new JMenuItem("Close Window");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
				java.awt.event.InputEvent.SHIFT_MASK
						| Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainWindow.this.dispatchEvent(new WindowEvent(MainWindow.this,
						WindowEvent.WINDOW_CLOSING));
			}
		});
		menu.add(item);
		
		item = new JMenuItem("Close All Windows");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainWindow[] windows = new MainWindow[MainWindow.openWindows
						.size()];
				MainWindow.openWindows.toArray(windows);
				for (MainWindow w : windows) {
					w.dispatchEvent(new WindowEvent(w,
							WindowEvent.WINDOW_CLOSING));
				}
			}
		});
		menu.add(item);
		
		this.toDeactivate.add(item = new JMenuItem("Close Tab"));
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTabbedPane tabs = MainWindow.this.getTabbedPane();
				int selected = tabs.getSelectedIndex();
				if (selected != -1) {
					tabs.remove(selected);
				}
			}
		});
		menu.add(item);
		
		this.toDeactivate.add(item = new JMenuItem("Save"));
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: Implement save
			}
		});
		menu.add(item);
		
		this.toDeactivate.add(item = new JMenuItem("Save As..."));
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				java.awt.event.InputEvent.SHIFT_MASK
						| Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: Implement save as
			}
		});
		menu.add(item);
		
		menus.add(menu);
		// End file menu!! -----------------------------------------------------
		
		menu = new JMenu("edit");
		// TODO: edit menu
		menus.add(menu);
		
		menu = new JMenu("help");
		// TODO: help menu
		menus.add(menu);
		
		this.add(menus, BorderLayout.NORTH);
		
		this.tabs = new JTabbedPane();
		this.tabs.setBorder(new EmptyBorder(5, 5, 5, 5));
		// Set the window name to the name of the current file
		this.tabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int selected = MainWindow.this.tabs.getSelectedIndex();
				if (selected == -1) {
					MainWindow.this.setTitle("Eric's TextEditor");
					for (JMenuItem i : MainWindow.this.toDeactivate) {
						i.setEnabled(false);
					}
				} else {
					MainWindow.this.setTitle(MainWindow.this.tabs
							.getTitleAt(selected));
					for (JMenuItem i : MainWindow.this.toDeactivate) {
						i.setEnabled(true);
					}
				}
			}
		});
		this.add(this.tabs, BorderLayout.CENTER);
		
		for (JMenuItem i : MainWindow.this.toDeactivate) {
			i.setEnabled(false);
		}
		this.setMinimumSize(new Dimension(175, 150));
		this.setSize(500, 500);
	}
	
	private MainWindow(MainWindow parent) {
		this(true);
		
		this.setLocation(parent.getX() + 20, parent.getY() + 20);
	}
	
	public JTabbedPane getTabbedPane() {
		return this.tabs;
	}
	
	public Component loadFileIntoComponent() { // TODO: work on rtf support
		FileDialog d = new FileDialog(MainWindow.this);
		d.setFilenameFilter(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return
				// name.matches(".*\\.rtf") ||
				name.matches(".*\\.txt");
			}
		});
		d.setVisible(true);
		try {
			File file = d.getFiles()[0];
			this.filename = file.getName();
			return Bonus.getFile(file);
		} catch (IOException | BadLocationException e) {
			JOptionPane.showMessageDialog(MainWindow.this, "File load error!");
			e.printStackTrace();
			return null;
		} catch (NullPointerException e) {
			JOptionPane.showMessageDialog(MainWindow.this, "File not found!");
			e.printStackTrace();
			return null;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new CancellationException();
		}
	}
}
