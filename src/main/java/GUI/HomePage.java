package GUI;

import DBUtils.DBManager;
import Security.SimpleLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public final class HomePage extends JFrame {

    private Container pane;
    private JPanel buttonsPanel, dataPanel;
    private ArrayList<Row> rows;
    private JButton copyButton, newButton, editButton, deleteButton;
    private int selectedRow=-1;

    public HomePage(){
        super("Matt's Passwords");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initialize();
        customize();
        putInOrder();
        addActions();

        if(rows.size()>0){
            pack();
        }else{
            setSize(500, 300);
        }

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initialize(){

        pane=getContentPane();

        buttonsPanel=new JPanel();
        dataPanel=new JPanel();

        rows=DBManager.loadData();

        copyButton=new JButton("Copy");
        newButton=new JButton("New");
        editButton=new JButton("Edit");
        deleteButton=new JButton("Delete");

    }

    private void putInOrder(){
        pane.add(BorderLayout.CENTER, new JScrollPane(dataPanel));
        pane.add(buttonsPanel, BorderLayout.SOUTH);

        buttonsPanel.add(copyButton);
        buttonsPanel.add(newButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);

        for(int i=0;i<rows.size();i++){
            rows.get(i).getSelection().addActionListener(new SelectAction(i));
            dataPanel.add(rows.get(i));
        }

    }

    private void customize(){
        pane.setLayout(new BorderLayout());
        pane.setBackground(Color.DARK_GRAY);
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS));

        CustomsShop.paintDark(buttonsPanel, dataPanel);

        CustomsShop.customizeButtons(copyButton, newButton, editButton, deleteButton);

    }

    private void addActions(){
        copyButton.addActionListener(new CopyAction());
        newButton.addActionListener(new NewAction());
        editButton.addActionListener(new EditAction());
        deleteButton.addActionListener(new DeleteAction());
    }

    private final class CopyAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if(selectedRow!=-1){
                int[] serialNums=GUIMiscHelper.readPartNums("Insert number sequence to decrypt your password");
                if(serialNums!=null){
                    if(DBManager.getPass(rows.get(selectedRow).getAppName(), rows.get(selectedRow).getUsername(), serialNums)!=null){
                        SimpleLogger.logAction(this.getClass().getName(), "Password copy succeeded");
                    }else{
                        SimpleLogger.logAction(this.getClass().getName(), "Password copy failed");
                    }
                }
            }else{
                JOptionPane.showMessageDialog(null, "Select a password first", "WARNING", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private final class NewAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            SimpleLogger.logAction(this.getClass().getName(), "Attempting to add a new password");
            dispose();
            new GUIDataEditor();
        }
    }

    private final class SelectAction implements ActionListener{

        private final int rowNum;

        private SelectAction(int rowNum) {
            this.rowNum = rowNum;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            for(Row r:rows){
                r.getSelection().setSelected(false);
            }
            ((JCheckBox) e.getSource()).setSelected(true);
            selectedRow=rowNum;
        }
    }

    private final class EditAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if(selectedRow!=-1){
                SimpleLogger.logAction(this.getClass().getName(), "Attempting to edit a password");
                dispose();
                new GUIDataEditor(rows.get(selectedRow).getAppName(), rows.get(selectedRow).getAppName());
            }else{
                JOptionPane.showMessageDialog(null, "Select a password first", "WARNING", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private final class DeleteAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if(selectedRow!=-1){
                SimpleLogger.logAction(this.getClass().getName(), "Attempting to delete a password");
                if(DBManager.deleteData(rows.get(selectedRow).getAppName(), rows.get(selectedRow).getAppName())){
                    SimpleLogger.logAction(this.getClass().getName(), "Deleting password succeeded");
                }else{
                    SimpleLogger.logAction(this.getClass().getName(), "Deleting password failed");
                }
                dispose();
                new HomePage();
            }else{
                JOptionPane.showMessageDialog(null, "Select a password first", "WARNING", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

}
