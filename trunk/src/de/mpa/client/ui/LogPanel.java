package de.mpa.client.ui;


import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.io.Writer;

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
public class LogPanel extends JPanel {
	private static final int MAXLINES = 1000;
	private JScrollPane panel = new JScrollPane();
	private JTextArea textArea = new JTextArea();
	private LogWriter writer;
	private WriterAppender appender;
	
	public LogPanel() {
		super();
		// configure panel
		this.setLayout(new BorderLayout());
		textArea.setEditable(false);
		textArea.setFont(new Font("Courier", textArea.getFont().getStyle(), 12));
        panel.getViewport().add(textArea, null);
        this.add(panel, BorderLayout.CENTER);
        
        // configure local log
        writer = new LogWriter(this);
        appender = new WriterAppender(new PatternLayout("%d{HH:mm:ss}: %-5p [%c{1}] %m%n"), writer);
        BasicConfigurator.configure(appender);

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
        JViewport port = panel.getViewport();
        port.setViewPosition(point);
	}
	
	private class LogWriter extends Writer {
		
        private LogPanel logpanel;

        public LogWriter(LogPanel logframe) {
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

