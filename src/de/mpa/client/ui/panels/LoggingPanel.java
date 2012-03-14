package de.mpa.client.ui.panels;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.Writer;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

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
	private JScrollPane logScpn;
	private JTextArea textArea;
	private LogWriter writer;
	private WriterAppender appender;
	
	public LoggingPanel() {

		initComponents();
        
        // configure local log
        writer = new LogWriter(this);
        appender = new WriterAppender(new PatternLayout("%d{HH:mm:ss}: %-5p [%c{1}] %m%n"), writer);
        BasicConfigurator.configure(appender);

	}
	
	private void initComponents() {
		
		CellConstraints cc = new CellConstraints();
		
		// configure panel
		this.setLayout(new FormLayout("5dlu, p, 5dlu",		// col
									  "5dlu, p, 5dlu"));	// row
		// container for titled border
		JPanel brdPnl = new JPanel();
		brdPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",		// col
										"3dlu, f:p:g, 5dlu"));	// row
		brdPnl.setBorder(BorderFactory.createTitledBorder("Logging"));
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font("Courier", textArea.getFont().getStyle(), 12));
		
		logScpn = new JScrollPane();
        logScpn.getViewport().add(textArea, null);
        logScpn.setPreferredSize(new Dimension(640, 400));
        logScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		brdPnl.add(logScpn, cc.xy(2, 2));
		
		this.add(brdPnl, cc.xy(2, 2));
		
	}

	public Appender getAppender() {
		return appender;
	}
	
	public void append(String str) {
		// append string
		textArea.append(str);

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

