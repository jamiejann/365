package com.HuffLZW;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class App {
    private JButton button;
    private JPanel panel1;

    public App() {
        button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(null);
            File f = chooser.getSelectedFile();
            String fileName = f.getAbsolutePath();

            try {
                //TOOD: implement runnnable
            } catch (IOException el){
                el.printStackTrace();
            }




        });
    }
    public static JFrame buildFrame(){
        JFrame frame = new JFrame();
        frame.setSize(1000, 600);
        frame.setVisible(true);
        return frame
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
