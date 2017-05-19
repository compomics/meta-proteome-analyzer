package de.mpa.client.ui.menubar;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;

import com.compomics.util.examples.BareBonesBrowserLaunch;

import de.mpa.client.Constants;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

/**
 * <b>HtmlFrame</b>
 * <p/>
 * <p>
 * This class is used to display a frame with external HTML resources model.
 * </p>
 *
 * @author T.Muth
 */
@SuppressWarnings("serial")
public class HtmlFrame extends JFrame {
    private final JEditorPane editorPane;
    private final JButton closeButton;
    private final JScrollPane scrollPane;
    private URL url;

    /**
     * Constructor constructs the frame with an editor pane.
     *
     * @param parent Parental frame
     * @param url The resources URL.
     * @param
     */
    public HtmlFrame(JFrame parent, URL url, String title) {
        // Sets icon image
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/de/mpa/resources/icons/mpa01.png")));
        this.setTitle(Constants.APPTITLE + " --- " + title);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.editorPane = new JEditorPane();
        this.editorPane.setEditable(false);
        this.editorPane.setContentType("text/html");
        this.closeButton = new JButton();
        this.scrollPane = new JScrollPane();

        this.closeButton.setText("Close");
        this.closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                HtmlFrame.this.close();
            }
        });

        this.editorPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.editorPane.setContentType("text/html");
        this.editorPane.setEditable(false);
        this.editorPane.setMinimumSize(new Dimension(10, 10));
        this.editorPane.setPreferredSize(new Dimension(10, 10));
        this.editorPane.addHyperlinkListener(new HyperlinkListener() {

            public void hyperlinkUpdate(HyperlinkEvent evt) {
                HtmlFrame.this.editorPaneHyperlinkUpdate(evt);
            }
        });
        this.scrollPane.setViewportView(this.editorPane);

        GroupLayout layout = new GroupLayout(
                this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                GroupLayout.LEADING).add(
                GroupLayout.TRAILING,
                layout.createSequentialGroup().addContainerGap().add(
                        layout.createParallelGroup(GroupLayout.TRAILING).add(
                                this.scrollPane, GroupLayout.DEFAULT_SIZE,
                                235, Short.MAX_VALUE).add(this.closeButton)).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(
                GroupLayout.LEADING).add(
                GroupLayout.TRAILING,
                layout.createSequentialGroup().addContainerGap().add(this.scrollPane,
                        GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE).addPreferredGap(
                        LayoutStyle.UNRELATED).add(this.closeButton).addContainerGap()));
        this.pack();

        try {
            this.url = url;
            if (this.url != null) {
                try {
                    this.editorPane.setPage(this.url);
                } catch (IOException e) {
                    this.editorPane.setText("The selected HTML file is not available.");
                }
            } else {
                this.editorPane.setText("The selected HTML file is not available.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.editorPane.setCaretPosition(0);
        this.setSize(550, 500);
        this.setLocationRelativeTo(parent);
        this.setVisible(true);
    }

    /**
     * Closes the help frame.
     */
    private void close() {
        dispose();
    }

    /**
     * Updates the hyperlinks
     *
     * @param evt
     */
    private void editorPaneHyperlinkUpdate(HyperlinkEvent evt) {
        if (evt.getEventType().toString().equalsIgnoreCase(
                EventType.ENTERED.toString())) {
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else if (evt.getEventType().toString().equalsIgnoreCase(
                EventType.EXITED.toString())) {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } else if (evt.getEventType().toString().equalsIgnoreCase(
                EventType.ACTIVATED.toString())) {
            if (evt.getDescription().startsWith("#")) {
                this.editorPane.scrollToReference(evt.getDescription());
            } else {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                BareBonesBrowserLaunch.openURL(evt.getDescription());
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

}


