package com.Lossless;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
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
    private void runLossless(byte[] data) throws IOException {

        BufferedImage image = createImageFromBytes(data);

        ArrayList<String> result = new ArrayList<>();
        int countConsecutive = 0;
        for(int i = 0; i<data.length; i++){
            countConsecutive++;
            //if next element is different, add this element to result

            if(i + 1 >= data.length || data[i] != data[i+1]){
                //result.add(" ");
                result.add(String.valueOf(data[i]));
                //result.add(countConsecutive);
                if(countConsecutive !=1){
                    result.add("A");
                    result.add(String.valueOf(countConsecutive));
                }
                //result.add(String.valueOf(countConsecutive));
                //result.add(" ");
                countConsecutive=0;
            }
        }

        System.out.println(result);

        ByteArrayOutputStream resultInByteArray = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", resultInByteArray);
        byte[] byteResult = resultInByteArray.toByteArray();

        try(FileOutputStream stream = new FileOutputStream("Compressed.in3")){
            stream.write(byteResult);
        }

//        PrintWriter pw = new PrintWriter(new FileOutputStream("Compressed.in3"));
//        for(byte s : data){
//            pw.println(s);
//        }
//        pw.close();

        //Saving file
        //Saving the file to IM3 & BMP

        //byte[] res = new byte[result.size()];
        //for(int i = 0; i<result.size(); i++){
        //    res = result.get(i).getBytes();
        //}

/*
        ByteArrayOutputStream resultInByteArray = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(resultInByteArray);
        for(String s : result){
            output.writeUTF(s);
        }
        //byte [] byteResult = resultInByteArray.toByteArray();
        byte [] byteResult = resultInByteArray.toByteArray();

        //try(FileOutputStream stream = new FileOutputStream("C://Users/egg/Desktop/Compressed.im3")){
        try(FileOutputStream stream = new FileOutputStream("Compressed.im3")){
            //stream.write(byteResult);
            stream.write(byteResult);
        }
        */
        /*
        try {
            //ImageIO.write(result, "bmp", new File("C://Users/egg/Desktop/CompressedInJPG.bmp"));
            ImageIO.write(result, "bmp", new File("CompressedInBMP.bmp"));
        } catch(IOException e){

        }
        */
    }

    /**
     * Helper method to create image from modified byte array
     * @param data
     * @return
     */
    private BufferedImage createImageFromBytes(byte[] data) {
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        try {
            return ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

