package de.mpa.client.ui.loggingpanel;

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
import java.io.IOException;
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
import de.mpa.client.ui.sharedelements.PanelConfig;

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
@SuppressWarnings("serial")
public class LoggingPanel extends JPanel {
	
	private static final int MAXLINES = 1000;
	
	private final ClientFrame clientFrame;
	private JScrollPane logScpn;
	private JTextArea textArea;
	private final LoggingPanel.LogWriter writer;
	private final WriterAppender appender;

	/**
	 * Constructs a panel containing a logging widget.
	 */
	public LoggingPanel() {
        clientFrame = ClientFrame.getInstance();

        this.initComponents();
        this.initListener();


        // configure local log
        this.writer = new LoggingPanel.LogWriter(this);
        this.appender = new WriterAppender(new PatternLayout("%d{HH:mm:ss}: %-5p [%c{1}] %m%n"), this.writer);
        BasicConfigurator.configure(this.appender);

	}

	/**
	 * Creates and places this panel's contents.
	 */
	private void initComponents() {
		// configure panel layout
        setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, p, 5dlu, b:p:g, 5dlu"));

		// create container for text area
		JPanel brdPnl = new JPanel();
		brdPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));

        this.textArea = new JTextArea();
        this.textArea.setEditable(false);
        this.textArea.setFont(new Font("Courier", this.textArea.getFont().getStyle(), 12));

        this.logScpn = new JScrollPane(this.textArea);
        this.logScpn.setPreferredSize(new Dimension(640, 400));
        this.logScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // add line number widget to scroll pane
        TextLineNumber tln = new TextLineNumber(this.textArea);
        this.logScpn.setRowHeaderView(tln);

        this.textArea.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		if (e.getButton() == MouseEvent.BUTTON3) {
        			// Create popup
        			JPopupMenu popup = new JPopupMenu();
        	        JMenuItem copyItem = new JMenuItem("Copy to Clipboard");
        	        if (LoggingPanel.this.textArea.getSelectedText() != null) {
        	        	copyItem.addActionListener(new ActionListener() {
            				public void actionPerformed(ActionEvent e) {
            					StringSelection selection = new StringSelection(LoggingPanel.this.textArea.getSelectedText());
            				    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            				    clipboard.setContents(selection, selection);
            				}
            			});
        	        } else {
        	        	copyItem.setEnabled(false);
        	        }
        	        popup.add(copyItem);

        			Point p = SwingUtilities.convertPoint(LoggingPanel.this.textArea, e.getPoint(), LoggingPanel.this.logScpn);
        			popup.show(LoggingPanel.this.logScpn, p.x, p.y);
        		}
        	}
		});

		brdPnl.add(this.logScpn, CC.xy(2, 2));

		// wrap text area in titled panel
		JXTitledPanel logTtlPnl = new JXTitledPanel("Logging", brdPnl);
		logTtlPnl.setTitleFont(PanelConfig.getTitleFont());
		logTtlPnl.setTitlePainter(PanelConfig.getTitlePainter());
		logTtlPnl.setBorder(PanelConfig.getTitleBorder());

		// create navigation button panel
		JPanel navPnl = new JPanel(new FormLayout("r:p:g, 5dlu, r:p", "b:p:g"));

		navPnl.add(this.clientFrame.createNavigationButton(false, true), CC.xy(1, 1));
		navPnl.add(this.clientFrame.createNavigationButton(true, false), CC.xy(3, 1));

		// add everything to main panel
        add(logTtlPnl, CC.xy(2, 2));
        add(navPnl, CC.xy(2, 4));
	}

	/**
	 * Installs a property change listener in the client to intercept incoming messages.
	 */
	private void initListener() {
		PropertyChangeListener listener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent pce) {
				if (pce.getPropertyName().equalsIgnoreCase("new message")) {
                    LoggingPanel.this.append(pce.getNewValue().toString());
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
        this.textArea.append("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) +  "] " + str + "\n");

		// check line count
		int lines = this.textArea.getLineCount();
        if (lines > LoggingPanel.MAXLINES) {
            try {
                this.textArea.getDocument().remove(0, lines - LoggingPanel.MAXLINES);
            }
            catch (BadLocationException exception) {
                exception.printStackTrace();
            }
        }

        // scroll down
        Point point = new Point(0, this.textArea.getSize().height);
        JViewport port = this.logScpn.getViewport();
        port.setViewPosition(point);
	}

	/**
	 * Log writer class used for the logging appender.
	 * @author T.Muth
	 * @data 25-07-2012
	 */
	private class LogWriter extends Writer {

        private final LoggingPanel logpanel;

        public LogWriter(LoggingPanel logframe) {
            logpanel = logframe;
        }

        public void close() throws IOException {

        }

        public void flush() throws IOException {

        }

        public void write(String str) {SwingUtilities.invokeLater(new Runnable() {

				public void run() {
                    LoggingPanel.LogWriter.this.logpanel.append(str);
				}
			});
        }

        public void write(char[] parm1, int parm2, int parm3) throws IOException {
            this.write(String.valueOf(parm1, parm2, parm3));
        }
    }
	
}

