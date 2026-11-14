
/******************************************************************************
 * HTTPRequestIndex contact detail window.
 ******************************************************************************/

import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;

public class HTTPRequestIndex {

    public static void showContact(Component parent, ThunderbirdContact contact) {

        if (contact == null) return;

        JDialog dialog = new JDialog(
            javax.swing.SwingUtilities.getWindowAncestor(parent),
            "Thunderbird Contact Information",
            java.awt.Dialog.ModalityType.APPLICATION_MODAL
        );

        dialog.setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
            contact.getPreferredName() + " " + contact.getLastName()
        );

        heading.setFont(new Font("SansSerif", Font.BOLD, 20));
        heading.setForeground(new Color(35, 82, 108));

        heading.setBorder(
            BorderFactory.createEmptyBorder(14, 16, 4, 16)
        );

        dialog.add(heading, BorderLayout.NORTH);

        JTextArea details = new JTextArea(contact.toString());

        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);

        details.setFont(new Font("Monospaced", Font.PLAIN, 13));
        details.setBackground(new Color(248, 250, 252));

        details.setBorder(
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        );

        JScrollPane scrollPane = new JScrollPane(details);

        scrollPane.setPreferredSize(new Dimension(520, 360));

        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel();

        bottom.setBorder(
            BorderFactory.createEmptyBorder(0, 0, 8, 0)
        );

        JLabel seat = new JLabel("Seat " + contact.getSeat());

        seat.setFont(new Font("SansSerif", Font.BOLD, 13));

        bottom.add(seat);

        dialog.add(bottom, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
