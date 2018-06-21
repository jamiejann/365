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

    /**
     * File Chooser, displays name of the file chosen in the textbox.
     * Inputs file name into method readFileBytebyByte.
     */
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

    /**
     * Main part of WavReader, Conversion of bytes & reading of raw audio bytes.
     * Converts byte [] into Integer Arraylist
     * Converts contents of Integer Arraylist into unslgned Integer arrayList.
     * Starting from 44th byte in the Wav file, (1st byte ~ 43rd byte is Wave header), combine
     *       every two bytes and convert to integer
     * Calls WavDisplay for Wave Display.
     *
     * @param audioInBytes
     */
    private void runProcess(byte[] audioInBytes){

        ArrayList<Byte> list= new ArrayList<>();
        for(byte b: audioInBytes){
            list.add(b);
        }
        ArrayList<Integer> listInteger = new ArrayList<>();
        for(byte b: audioInBytes){
            listInteger.add(Integer.valueOf(String.valueOf(b)));
        }
        System.out.println(listInteger);

        ArrayList<Integer> unsignedIntList = new ArrayList<>();
        System.out.println(" ");
        for (byte audioByte : audioInBytes) {
            int temp = byteToUnsignedInt(audioByte);
            unsignedIntList.add(temp);
        }

        //combine two bytes for each sample and add to Integer arrayList
        ArrayList<Integer> toDraw = new ArrayList<>();
        for(int index = 43; index<audioInBytes.length-1; index++){
            byte first = audioInBytes[index];
            byte second = audioInBytes[index+1];
            short combined = twoBytestoShort(first, second);
            Integer shortToInteger = (int) combined;
            toDraw.add(shortToInteger);
        }

        displayWave(toDraw);
    }

    /**
     * Reads Wav file Byte by Byte
     * @param file
     * @throws IOException
     */
    private void readFileByteByByte(File file) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        int read;
        byte [] buff = new byte[4];
        byte[] audioInBytes;
        read = in.read(buff);
        do{
            out.write(buff, 0, read);
            out.flush();
            audioInBytes = out.toByteArray();
        } while((read = in.read(buff))>0);

        runProcess(audioInBytes);
    }

    /**
     * Helper Method to combine two bytes into short
     * @param b1
     * @param b2
     * @return
     */
    public static short twoBytestoShort(byte b1, byte b2){
        return (short) ((b1 << 8) | (b2 & 0xFF));
    }

    /**
     * Helper method to convert byte into Unsigned Integer
     * @param b
     * @return
     */
    public static int byteToUnsignedInt(byte b){
        return 0x00 << 24 | b & 0xff;
    }

    /**
     * Helper method to convert Decimal into Hex
     * (Not in use for application, used for checking integrety of Wave file
     * @param decimal
     * @return
     */
    public static String decToHex(Integer decimal) {
        return Integer.toHexString(decimal);
    }


    /**
     * Main method for displaying Wave file
     * Builds a new frame with panel
     * Normalizes the data, if over 300 in height, divide by two. This avoids drawn data
     *      to be out of bounds for the JFrame.
     * Normalizes the width, to be able to fit the data into frame with 700 width, takes
     *      samples over a certain interval.
     * Draws the waveform with fillRect pixel by pixel.
     * @param list
     */
    private void displayWave(ArrayList<Integer> list) {

        //For Question 2 in Project.
        int maximumInFile = getMax(list);
        int numSamples = list.size();
        System.out.println("Maximum: " + getMax(list));
        System.out.println("Samples: " + list.size());

        JFrame displayFrame = buildFrame();
        JPanel displayPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                int maximum = getMax(list);
                System.out.println(maximum);

                while(getMax(list)> 300){
                    for(int i = 0; i<list.size(); i++){
                        Integer temp = list.get(i);
                        temp = temp/2;
                        list.set(i, temp);
                    }
                }
                int interval = list.size()/1000;
                ArrayList<Integer> normalized = new ArrayList<>();
                for(int i = 0; i<list.size(); i=i+interval){
                    normalized.add(list.get(i));
                }
                for(int i=0; i< normalized.size(); i++) {
                    g.fillRect(i, 300, 1, Math.abs(normalized.get(i)));
                    g.fillRect(i, 300, 1, -Math.abs(normalized.get(i)));
                }
            }
        };
        displayFrame.add(displayPanel);
        displaySummary(maximumInFile, numSamples);

    }

    /**
     * Method to display new JFrame that contains the summary of the file.
     * For #2 in Question 2.
     * @param maximum
     * @param samples
     */
    private void displaySummary(int maximum, int samples){
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(500, 200);
        frame.setTitle("Summary");
        JTextField maximumField = new JTextField(20);
        JTextField samplesField = new JTextField(20);
        maximumField.setText("Maximum Value: "+ maximum);
        samplesField.setText("Samples Amount: " + samples);
        frame.add(maximumField);
        frame.add(samplesField);

        frame.setVisible(true);
    }

    /**
     * Helper method to get maximum of an arrayList
     * @param list
     * @return
     */
    private int getMax(ArrayList<Integer> list){
        int max = 0;
        for(Integer i : list){
            if(i > max){
                max = i;
            }
        }
        return max;
    }
    /**
     * Helper Method to build a new JFrame.
     * @return
     */
    private static JFrame buildFrame(){
        JFrame frame = new JFrame();
        frame.setSize(1000, 600);
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
