package com.wavReader;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class App extends JFrame{
    private JButton mainButton;
    private JPanel panelMain;
    private JTextField textFieldMain;

    public App() {
        mainButton.addActionListener(e -> {
            //JOptionPane.showMessageDialog(null, "hello world");
            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(null);
            File f = chooser.getSelectedFile();
            String fileName = f.getAbsolutePath();
            textFieldMain.setText(fileName);

            try {
                readFileByteByByte(f);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    private void readFileByteByByte(File file) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        int read;
        byte [] buff = new byte[4];
        byte[] audioBytes;
        read = in.read(buff);
        do{
            out.write(buff, 0, read);
            out.flush();
            audioBytes = out.toByteArray();
        } while((read = in.read(buff))>0);


        ArrayList<Byte> list= new ArrayList<>();
        for(byte b: audioBytes){
           list.add(b);
        }
        System.out.println(list);
        System.out.println(list.get(1));


        ArrayList<Integer> listInteger = new ArrayList<>();
        for(byte b: audioBytes){
            listInteger.add(Integer.valueOf(String.valueOf(b)));
        }
        System.out.println(listInteger);

        //added
        ArrayList<Integer> unsignedIntList = new ArrayList<>();
        System.out.println(" ");
        for (byte audioByte : audioBytes) {
            int temp = byteToUnsignedInt(audioByte);
            unsignedIntList.add(temp);
            System.out.print(temp + " ");
        }

        System.out.println(" ");
        for (int i1 = 0; i1 < unsignedIntList.size(); i1++) {
            int i = unsignedIntList.get(i1);
            String temp = decToHex(i);
            System.out.println(i1 + 1 + " " + temp);
        }

        //combine two bytes for each sample and add to Integer arrayList
        ArrayList<Integer> toDraw = new ArrayList<>();

        for(int index = 43; index<audioBytes.length-1; index++){
            byte first = audioBytes[index];
            byte second = audioBytes[index+1];
            short combined = twoBytestoShort(first, second);
            Integer shortToInteger = (int) combined;
            toDraw.add(shortToInteger);
            System.out.print(combined + " ");
        }

        //can delete later, for testing
        System.out.println(" ");
        for(Integer i : toDraw){
            System.out.print(i + " ");
        }

        displayWave(toDraw);

    }
    public static short twoBytestoShort(byte b1, byte b2){
        return (short) ((b1 << 8) | (b2 & 0xFF));
    }

    public static int byteToUnsignedInt(byte b){
        return 0x00 << 24 | b & 0xff;
    }

    public static String decToHex(Integer decimal) {
        return Integer.toHexString(decimal);
    }


    private void displayWave(ArrayList<Integer> list) {
        JFrame wavFormFrame = new JFrame("draw");
        wavFormFrame.setSize(600, 500);
        wavFormFrame.setVisible(true);
        wavFormFrame.getContentPane().add(new MyCanvas());
        //wavFormFrame.paint(null);
    }

    private static JFrame buildFrame(){
        JFrame frame = new JFrame();
        frame.setSize(600, 600);
        frame.setVisible(true);
        return frame;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
