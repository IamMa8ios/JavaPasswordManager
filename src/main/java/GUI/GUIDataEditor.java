package GUI;

import DBUtils.DBManager;
import Security.MattSecurityHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class GUIDataEditor extends JFrame {

    private Container pane;
    private JPanel mainPanel, buttonsPanel, labelsPanel, textsPanel;
    private JLabel appLabel, userLabel, passLabel;
    private JTextField appText, userText, passText;
    private JButton saveButton, autoGenerateButton;
    private boolean editMode;
    private int[] serialNums;

    public GUIDataEditor(){
        super("Matt's Passwords");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editMode=false;

        initialize();
        customize();
        addActions();

        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public GUIDataEditor(String appName, String username){
        this();
        appText.setText(appName);
        userText.setText(username);
        serialNums=GUIMiscHelper.readPartNums("Insert the sequence of numbers to load your password");
        String plainPass=DBManager.getPass(appName, username, serialNums);
        if(plainPass!=null){
            passText.setText(plainPass);
        }else{
            JOptionPane.showMessageDialog(null, "Password for the data given not found", "ERROR", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
        editMode=true;
    }

    private void initialize(){
        pane=getContentPane();
        mainPanel=new JPanel();
        labelsPanel=new JPanel();
        textsPanel=new JPanel();
        buttonsPanel=new JPanel();
        appLabel=new JLabel("App Name:");
        userLabel=new JLabel("Username:");
        passLabel=new JLabel("Password:");
        appText=new JTextField();
        userText=new JTextField();
        passText=new JTextField();
        saveButton=new JButton("Save");
        autoGenerateButton =new JButton("Auto-Generate Password");
    }

    private void customize(){
        CustomsShop.paintDark(mainPanel, labelsPanel, textsPanel, buttonsPanel);
        CustomsShop.customizeLabels(appLabel, userLabel, passLabel);
        CustomsShop.customizeText(true, appText, userText, passText);
        CustomsShop.customizeButtons(saveButton, autoGenerateButton);

        pane.setLayout(new BorderLayout());
        pane.add(mainPanel, BorderLayout.CENTER);
        pane.add(buttonsPanel, BorderLayout.SOUTH);

        mainPanel.setLayout(new GridLayout(1,2));
        mainPanel.add(labelsPanel);
        mainPanel.add(textsPanel);

        labelsPanel.setLayout(new GridLayout(3,1));
        labelsPanel.add(appLabel);
        labelsPanel.add(userLabel);
        labelsPanel.add(passLabel);

        textsPanel.setLayout(new GridLayout(3,1));
        textsPanel.add(appText);
        textsPanel.add(userText);
        textsPanel.add(passText);

        buttonsPanel.add(saveButton);
        buttonsPanel.add(autoGenerateButton);
    }

    private void addActions(){
        saveButton.addActionListener(new SaveAction());
        autoGenerateButton.addActionListener(new GenerateAction());
    }

    private boolean allFilled(){
        return !appText.getText().equals("") && !userText.getText().equals("") && !passText.getText().equals("");
    }

    private class SaveAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            if(allFilled()){
                int[] partNums=GUIMiscHelper.readPartNums("Insert the sequence of numbers to encrypt your password");
                if(partNums!=null){
                    DBManager.saveData(appText.getText(), userText.getText(), passText.getText(), partNums,editMode);
                    dispose();
                    new HomePage();
                }
            }else{
                JOptionPane.showMessageDialog(null, "Please fill all fields first", "ERROR", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    private class GenerateAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            //int passSize=0;
            //passSize=GUIMiscHelper.getPasswordSize();
            if(1>0){
                passText.setText(MattSecurityHelper.generateStrongPassword(20));
            }else{
                JOptionPane.showMessageDialog(null, "Insert a valid number", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
