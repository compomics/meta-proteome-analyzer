package de.mpa.client.ui.panels;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.jdesktop.swingx.JXTitledPanel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.TextLineNumber;

/**
 * <b>Log Panel</b>
 * 
 * <p>
 * This class displays LogEvents in a {@link JPanel}.
 * </p>
 * 
 * @author Thilo Muth
 *
 */
public class LoggingPanel extends JPanel {
	
	private static final int MAXLINES = 1000;
	
	private ClientFrame clientFrame;
	private JScrollPane logScpn;
	private JTextArea textArea;
	private LogWriter writer;
	private WriterAppender appender;
	
	/**
	 * Constructs a panel containing a logging widget.
	 */
	public LoggingPanel() {
		this.clientFrame = ClientFrame.getInstance();

		initComponents();
		initListener();

        
        // configure local log
        writer = new LogWriter(this);
        appender = new WriterAppender(new PatternLayout("%d{HH:mm:ss}: %-5p [%c{1}] %m"), writer);
        BasicConfigurator.configure(appender);

	}

	/**
	 * Creates and places this panel's contents.
	 */
	private void initComponents() {
		// configure panel layout
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, p, 5dlu, b:p:g, 5dlu"));
		
		// create container for text area
		JPanel brdPnl = new JPanel();
		brdPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font("Courier", textArea.getFont().getStyle(), 12));
		
		logScpn = new JScrollPane(textArea);
        logScpn.setPreferredSize(new Dimension(640, 400));
        logScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // add line number widget to scroll pane
        TextLineNumber tln = new TextLineNumber(textArea);
        logScpn.setRowHeaderView(tln);
        
        textArea.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		if (e.getButton() == MouseEvent.BUTTON3) {
        			// Create popup
        			JPopupMenu popup = new JPopupMenu();
        	        JMenuItem copyItem = new JMenuItem("Copy to Clipboard");
        	        if (textArea.getSelectedText() != null) {
        	        	copyItem.addActionListener(new ActionListener() {
            				public void actionPerformed(ActionEvent e) {
            					StringSelection selection = new StringSelection(textArea.getSelectedText());
            				    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            				    clipboard.setContents(selection, selection);
            				}
            			});
        	        } else {
        	        	copyItem.setEnabled(false);
        	        }
        	        popup.add(copyItem);
        			
        			Point p = SwingUtilities.convertPoint(textArea, e.getPoint(), logScpn);
        			popup.show(logScpn, p.x, p.y);
        		}
        	}
		});
        
		brdPnl.add(logScpn, CC.xy(2, 2));

		// wrap text area in titled panel
		JXTitledPanel logTtlPnl = new JXTitledPanel("Logging", brdPnl);
		logTtlPnl.setTitleFont(PanelConfig.getTitleFont());
		logTtlPnl.setTitlePainter(PanelConfig.getTitlePainter());
		logTtlPnl.setBorder(PanelConfig.getTitleBorder());

		// create navigation button panel
		JPanel navPnl = new JPanel(new FormLayout("r:p:g, 5dlu, r:p", "b:p:g"));

		navPnl.add(clientFrame.createNavigationButton(false, true), CC.xy(1, 1));
		navPnl.add(clientFrame.createNavigationButton(true, false), CC.xy(3, 1));

		// add everything to main panel
		this.add(logTtlPnl, CC.xy(2, 2));
		this.add(navPnl, CC.xy(2, 4));
	}
	
	/**
	 * Installs a property change listener in the client to intercept incoming messages.
	 */
	private void initListener() {
		PropertyChangeListener listener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent pce) {
				if (pce.getPropertyName().equalsIgnoreCase("new message")) {
					append(pce.getNewValue().toString());
				}
			}
		};
		Client.getInstance().addPropertyChangeListener(listener);
	}

	/**
	 * Appends the specified string to the text area with a timestamp.
	 * @param str The string to be added.
	 */
	public void append(String str) {
		// append string
		textArea.append("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) +  "] " + str + "\n");

		// check line count
		int lines = textArea.getLineCount();
        if (lines > MAXLINES) {
            try {
                textArea.getDocument().remove(0, lines - MAXLINES);
            }
            catch (BadLocationException exception) {
                exception.printStackTrace();
            }
        }

        // scroll down
        Point point = new Point(0, textArea.getSize().height);
        JViewport port = logScpn.getViewport();
        port.setViewPosition(point);
	}
	
	/**
	 * Log writer class used for the logging appender. 
	 * @author T.Muth
	 * @data 25-07-2012
	 */
	private class LogWriter extends Writer {
		
        private LoggingPanel logpanel;

        public LogWriter(LoggingPanel logframe) {
            this.logpanel = logframe;
        }

        public void close() throws java.io.IOException {
        	
        }

        public void flush() throws java.io.IOException {
        	
        }

        public void write(final String str) {SwingUtilities.invokeLater(new Runnable() {
			
				public void run() {
					logpanel.append(str);
				}
			});
        }

        public void write(char[] parm1, int parm2, int parm3) throws java.io.IOException {
            write(String.valueOf(parm1, parm2, parm3));
        }
    }
	
}

