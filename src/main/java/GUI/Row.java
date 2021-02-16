package GUI;

import DBUtils.DBManager;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Base64;

public final class Row extends JPanel {

    private JPanel labelsPanel, textsPanel;
    private JLabel appLabel, userLabel, passLabel;
    private JTextField appText, userText, passText;
    private JCheckBox selection;

    public Row(String appName, String username, byte[] pass) {
        this.labelsPanel=new JPanel();
        this.textsPanel=new JPanel();
        this.appLabel=new JLabel("App Name:");
        this.userLabel=new JLabel("Username:");
        this.passLabel=new JLabel("Password:");
        this.appText=new JTextField(appName);
        this.userText=new JTextField(username);
        this.passText=new JTextField(Base64.getEncoder().encodeToString(pass));
        this.selection=new JCheckBox();
        customize();
    }

    private void customize(){
        setLayout(new GridLayout(1, 3));
        setBorder(new LineBorder(Color.WHITE));
        labelsPanel.setLayout(new GridLayout(3,1));
        textsPanel.setLayout(new GridLayout(3,1));

        setBackground(Color.DARK_GRAY);
        CustomsShop.paintDark(labelsPanel, textsPanel);
        CustomsShop.customizeLabels(appLabel, userLabel, passLabel);
        CustomsShop.customizeText(false, appText, userText, passText);
        selection.setBackground(Color.DARK_GRAY);
        selection.setForeground(Color.WHITE);

        labelsPanel.add(appLabel);
        labelsPanel.add(userLabel);
        labelsPanel.add(passLabel);

        textsPanel.add(appText);
        textsPanel.add(userText);
        textsPanel.add(passText);

        add(selection);
        add(labelsPanel);
        add(textsPanel);

        align();

    }

    private void align(){
        selection.setAlignmentY(JCheckBox.CENTER_ALIGNMENT);
        selection.setAlignmentX(JCheckBox.RIGHT_ALIGNMENT);
        selection.setHorizontalAlignment(JCheckBox.RIGHT);
        selection.setVerticalAlignment(JCheckBox.CENTER);

        appLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        userLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        passLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        appLabel.setHorizontalAlignment(JLabel.CENTER);
        userLabel.setHorizontalAlignment(JLabel.CENTER);
        passLabel.setHorizontalAlignment(JLabel.CENTER);

        appText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
        userText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
        passText.setAlignmentX(JTextField.LEFT_ALIGNMENT);
    }

    public JCheckBox getSelection() {
        return selection;
    }

    public String getAppName(){
        return appText.getText();
    }

    public String getUsername(){
        return userText.getText();
    }

}
