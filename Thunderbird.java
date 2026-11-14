
/******************************************************************************
 * Copyright (C) 2021 Eric Pogue.
 * Licensed under the BSD-3-Clause.
 ******************************************************************************/

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

class ContactTile extends JPanel {

    private ThunderbirdContact contactInSeat;
    private boolean isAnAisle;
    private int seatNumber;

    ContactTile(int seatNumberIn, ThunderbirdContact contactInSeatIn, boolean aisle) {

        seatNumber = seatNumberIn;
        contactInSeat = contactInSeatIn;
        isAnAisle = aisle;

        setPreferredSize(new Dimension(125, 115));

        // Todo: Remove everything to do with random colors.
        // AM: Removed random RGB values.

        // Todo: Implement visually appealing colors for aisles and seats.
        // AM: Added consistent seat and aisle colors.

        setBackground(aisle ? new Color(225, 228, 232) : new Color(218, 235, 246));
        setBorder(BorderFactory.createLineBorder(new Color(120, 135, 148), 1));
        setToolTipText(aisle ? "Aisle" : "Seat " + seatNumber);
    }

    public ThunderbirdContact getContact() {
        return contactInSeat;
    }

    protected void paintComponent(java.awt.Graphics g) {

        super.paintComponent(g);

        int w = getWidth();
        int h = getHeight();

        if (isAnAisle) {
            g.setColor(new Color(225, 228, 232));
            g.fillRoundRect(8, 8, w - 16, h - 16, 12, 12);
            g.setColor(new Color(105, 112, 120));
            g.setFont(new Font("SansSerif", Font.BOLD, 13));
            g.drawString("AISLE", w / 2 - 18, h / 2);
            return;
        }

        boolean occupied = contactInSeat != null;

        g.setColor(occupied ? new Color(42, 112, 139) : new Color(218, 235, 246));
        g.fillRoundRect(8, 8, w - 16, h - 16, 12, 12);

        g.setColor(occupied ? Color.WHITE : new Color(48, 73, 91));
        g.setFont(new Font("SansSerif", Font.BOLD, 12));

        String seatText = "Seat " + seatNumber;
        int x = (w - g.getFontMetrics().stringWidth(seatText)) / 2;
        g.drawString(seatText, x, 27);

        if (occupied) {

            // ToDo: Display preferred name instead of first and last name.
            // AM: Display preferred name when supplied.

            g.setFont(new Font("SansSerif", Font.PLAIN, 12));

            String displayName =
                contactInSeat.getPreferredName() + " " +
                contactInSeat.getLastName();

            while (displayName.length() > 3 &&
                   g.getFontMetrics().stringWidth(displayName) > w - 20) {
                displayName = displayName.substring(0, displayName.length() - 1);
            }

            int nameX = (w - g.getFontMetrics().stringWidth(displayName)) / 2;
            g.drawString(displayName, nameX, h / 2 + 12);

        } else {

            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.drawString("Available", w / 2 - 28, h / 2 + 12);
        }
    }
}

class ThunderbirdFrame extends JFrame implements ActionListener {

    private ArrayList<ContactTile> tileList;
    private JPanel contactGridPanel;
    private boolean reversed;
    private JButton reverseView;

    public ThunderbirdFrame() {

        super("Thunderbird Classroom Seating Chart");

        reversed = false;
        tileList = new ArrayList<ContactTile>();

        setBounds(100, 80, 1250, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(12, 12));

        JLabel title = new JLabel(
            "THUNDERBIRD CLASSROOM", SwingConstants.CENTER
        );

        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(35, 62, 82));
        title.setBorder(BorderFactory.createEmptyBorder(16, 10, 8, 10));

        contentPane.add(title, BorderLayout.NORTH);

        contactGridPanel = new JPanel(new GridLayout(4, 8, 9, 9));
        contactGridPanel.setBackground(new Color(245, 247, 249));
        contactGridPanel.setBorder(
            BorderFactory.createEmptyBorder(12, 18, 12, 18)
        );

        contentPane.add(contactGridPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        reverseView = new JButton("Reverse View");
        reverseView.addActionListener(this);
        buttonPanel.add(reverseView);

        JLabel hint = new JLabel(
            "Select an occupied seat to view contact details"
        );

        buttonPanel.add(hint);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        ThunderbirdModel tbM = new ThunderbirdModel();

        if (tbM.LoadIndex()) {

            // Todo: Review ThunderbirdModel in detail and implement a multithreaded version of loading contacts.
            // AM: Use the existing threaded contact-loading method.
            tbM.LoadContactsThreaded();

            System.out.println("Printing Model:");
            System.out.println(tbM);
            tbM.ValidateContacts();

        } else {

            JOptionPane.showMessageDialog(
                this,
                "The Thunderbird contact index could not be loaded.",
                "Index loading error",
                JOptionPane.ERROR_MESSAGE
            );
        }

        // Todo: Place all the aisle seats in a array or an ArrayList instead of hard coding them.
        // AM: Store aisle positions in an array.

        int[] aislePositions = {4, 12, 31};

        for (int i = 1; i <= 32; i++) {

            ThunderbirdContact contact = tbM.findContactInSeat(i);

            boolean aisle = false;

            for (int position : aislePositions) {
                if (i == position) {
                    aisle = true;
                }
            }

            ContactTile tile = new ContactTile(
                i, aisle ? null : contact, aisle
            );

            if (!aisle && contact != null) {

                tile.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        HTTPRequestIndex.showContact(
                            ThunderbirdFrame.this, contact
                        );
                    }
                });
            }

            tileList.add(tile);
        }

        rebuildSeatGrid();
    }

    private void rebuildSeatGrid() {

        contactGridPanel.removeAll();

        ArrayList<ContactTile> displayList =
            new ArrayList<ContactTile>(tileList);

        if (reversed) {
            Collections.reverse(displayList);
        }

        for (ContactTile tile : displayList) {
            contactGridPanel.add(tile);
        }

        contactGridPanel.revalidate();
        contactGridPanel.repaint();
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == reverseView) {

            // Todo: Remove randomization functionality and implement a visually appealing view of seats and aisles.
            // AM: Use consistent seat and aisle colors.

            // Todo: Implement reverse view where it looks like you are looking at the room from the back instead of the front.
            // AM: Reverse the seat order when the button is clicked.

            reversed = !reversed;

            reverseView.setText(reversed ? "Front View" : "Reverse View");

            rebuildSeatGrid();
        }
    }
}

// Todo: Rename the following class to Thunderbird.
// AM: Renamed the main application class.

public class Thunderbird {

    public static void main(String[] args) {

        // Todo: Update the following line so that it reflects the name change to Thunderbird.
        // AM: Updated startup message.

        System.out.println("Thunderbird Starting...");

        if (args.length > 0 &&
            args[0].equalsIgnoreCase("-HttpRequest")) {

            if (args.length >= 2) {
                HTTPRequest.retrieveAndPrint(args[1]);
            } else {
                System.out.println(
                    "Usage: java Thunderbird -HttpRequest [URL]"
                );
            }

            return;
        }

        if (args.length == 1 &&
            args[0].equalsIgnoreCase("-help")) {

            System.out.println(
                "Thunderbird classroom seating chart\n" +
                "Usage: java Thunderbird\n" +
                "       java Thunderbird -HttpRequest [URL]"
            );

            return;
        }

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ThunderbirdFrame frame = new ThunderbirdFrame();
                frame.setVisible(true);
            }
        });
    }
}
