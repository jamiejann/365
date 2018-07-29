package com.HuffLZW;

import javax.swing.*;
import java.io.*;

public class HuffLZW {
    private JButton button;
    private JPanel panel1;
    private JTextField textField;

    public HuffLZW(){
        button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(null);
            File f = chooser.getSelectedFile();
            String fileName = f.getAbsolutePath();
            textField.setText(fileName);

            try {
                readWavByteByByte(f);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }
    private void readWavByteByByte(File file) throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        int read;
        byte [] buff = new byte[4];
        byte[] imageInBytes;
        read = in.read(buff);
        do{
            out.write(buff, 0, read);
            out.flush();
            imageInBytes = out.toByteArray();
        } while((read = in.read(buff))>0);

        runHuffman(imageInBytes);
    }

    private void runHuffman(byte[] data){

    }


    private static JFrame buildFrame(){
        JFrame frame = new JFrame();
        frame.setSize(704, 576);
        frame.setVisible(true);
        return frame;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Huff & LZW Compression");
        frame.setContentPane(new HuffLZW().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }



}
