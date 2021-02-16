package GUI;

import javax.swing.*;
import java.awt.*;

public final class CustomsShop {

    public static void paintDark(JPanel... panels){
        for(JPanel p:panels){
            p.setBackground(Color.DARK_GRAY);
        }
    }

    public static void customizeButtons(JButton... buttons){
        for(JButton b:buttons){
            b.setBackground(Color.white);
            b.setFont(new Font("ArialBlack", Font.BOLD, 16));
        }
    }

    public static void customizeLabels(JLabel... labels){
        for(JLabel l:labels){
            l.setForeground(Color.white);
            l.setAlignmentX(Component.RIGHT_ALIGNMENT);
            l.setFont(new Font("ArialBlack", Font.BOLD, 16));
        }
    }

    public static void customizeText(boolean isEditable, JTextField... textFields){
        for(JTextField t:textFields){
            t.setFont(new Font("ArialBlack", Font.BOLD, 16));
            t.setAlignmentX(Component.RIGHT_ALIGNMENT);
            t.setEditable(isEditable);
        }
    }

}
