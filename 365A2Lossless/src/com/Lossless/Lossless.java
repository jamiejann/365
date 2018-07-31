package com.Lossless;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class Lossless {
    private JButton selectBMPButton;
    private JPanel panel1;
    private JTextField textField1;
    private JButton selectIM2Button;

    public Lossless(){
        selectBMPButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(null);
            File f = chooser.getSelectedFile();
            String fileName = f.getAbsolutePath();
            textField1.setText(fileName);

            try {
                byte[] bytesToRunLossy = readImageByteByByte(f);
                runLossless(bytesToRunLossy);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    private byte[] readImageByteByByte(File file) throws IOException{
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

        return fileInBytes;
        //runLossy(fileInBytes);
    }
    private void runLossless(byte[] data) {

        /*
        ArrayList<Byte> list= new ArrayList<>();
        for(byte b: data){
            list.add(b);
        }
        ArrayList<Integer> listInteger = new ArrayList<>();
        for(byte b: data){
            listInteger.add(Integer.valueOf(String.valueOf(b)));
        }
        System.out.println(listInteger);
        ArrayList<Integer> unsignedIntList = new ArrayList<>();
        System.out.println(" ");
        for (byte audioByte : data) {
            int temp = byteToUnsignedInt(audioByte);
            unsignedIntList.add(temp);
        }
        System.out.println(unsignedIntList);


        //Building a frequency matrix
        ArrayList<Integer> newFreq= new ArrayList<>();
        for(int i =0; i<data.length; i+=4){
            int temp = getBytesAsWord(data, 0);
            newFreq.add(temp);
        }
        System.out.println(newFreq);
        */
        ArrayList<String> list = new ArrayList<>();
        int countConsecutive = 0;
        for(int i = 0; i<data.length; i++){
            countConsecutive++;
            if(i + 1 >= data.length || data[i] != data[i+1]){
                list.add("*");
                list.add(String.valueOf(data[i]));
                list.add(String.valueOf(countConsecutive));
                list.add("*");
                countConsecutive=0;
            }


           // while(data[i] == data[i+1]){
           //     list.add("*");
           //     list.add(String.valueOf(data[i]));
           //     list.add("*");
           // }
        }

        System.out.println(list);
    }





    private int getBytesAsWord(byte[] data, int off){
        return data[off] << 8 & 0xFF00 | data[off+1]&0xFF;
    }
    private static int byteToUnsignedInt(byte b){
        return 0x00 << 24 | b & 0xff;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Lossless Compression");
        frame.setContentPane(new Lossless().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

