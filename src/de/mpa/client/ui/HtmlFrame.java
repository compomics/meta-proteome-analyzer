package de.mpa.client.ui;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;

import com.compomics.util.examples.BareBonesBrowserLaunch;

import de.mpa.client.Constants;

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
    private JEditorPane editorPane;
    private JButton closeButton;
    private JScrollPane scrollPane;
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
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/de/mpa/resources/icons/mpa01.png")));
        setTitle(Constants.APPTITLE + " --- " + title);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        closeButton = new JButton();
        scrollPane = new JScrollPane();

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close();
            }
        });

        editorPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        editorPane.setMinimumSize(new java.awt.Dimension(10, 10));
        editorPane.setPreferredSize(new java.awt.Dimension(10, 10));
        editorPane.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {

            public void hyperlinkUpdate(HyperlinkEvent evt) {
                editorPaneHyperlinkUpdate(evt);
            }
        });
        scrollPane.setViewportView(editorPane);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
                getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                layout.createSequentialGroup().addContainerGap().add(
                        layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING).add(
                                scrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                235, Short.MAX_VALUE).add(closeButton)).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                layout.createSequentialGroup().addContainerGap().add(scrollPane,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE).addPreferredGap(
                        org.jdesktop.layout.LayoutStyle.UNRELATED).add(closeButton).addContainerGap()));
        pack();

        try {
            this.url = url;
            if (this.url != null) {
                try {
                    editorPane.setPage(this.url);
                } catch (IOException e) {
                    editorPane.setText("The selected HTML file is not available.");
                }
            } else {
                editorPane.setText("The selected HTML file is not available.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        editorPane.setCaretPosition(0);
        setSize(550, 500);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    /**
     * Closes the help frame.
     */
    private void close() {
        this.dispose();
    }

    /**
     * Updates the hyperlinks
     *
     * @param evt
     */
    private void editorPaneHyperlinkUpdate(HyperlinkEvent evt) {
        if (evt.getEventType().toString().equalsIgnoreCase(
                HyperlinkEvent.EventType.ENTERED.toString())) {
            setCursor(new java.awt.Cursor(Cursor.HAND_CURSOR));
        } else if (evt.getEventType().toString().equalsIgnoreCase(
                javax.swing.event.HyperlinkEvent.EventType.EXITED.toString())) {
            setCursor(new java.awt.Cursor(Cursor.DEFAULT_CURSOR));
        } else if (evt.getEventType().toString().equalsIgnoreCase(
                HyperlinkEvent.EventType.ACTIVATED.toString())) {
            if (evt.getDescription().startsWith("#")) {
                editorPane.scrollToReference(evt.getDescription());
            } else {
                this.setCursor(new Cursor(java.awt.Cursor.WAIT_CURSOR));
                BareBonesBrowserLaunch.openURL(evt.getDescription());
                this.setCursor(new Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
        }
    }

}


