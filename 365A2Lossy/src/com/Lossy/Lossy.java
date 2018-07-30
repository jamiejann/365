package com.Lossy;

import javax.swing.*;
import java.io.*;

public class Lossy {
    private JTextField textField1;
    private JButton button1;

    public Lossy(){
        button1.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(null);
            File f = chooser.getSelectedFile();
            String fileName = f.getAbsolutePath();
            textField1.setText(fileName);

            try {
                readImageByteByByte(f);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    private void readImageByteByByte(File file) throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        int read;
        byte [] buff = new byte[4];
        byte[] fileInBytes;
        read = in.read(buff);
        do{
            out.write(buff, 0, read);
            out.flush();
            fileInBytes = out.toByteArray();
        } while((read = in.read(buff))>0);

        runLossy(fileInBytes);
    }

    private void runLossy(byte [] data){
        

    }




}
