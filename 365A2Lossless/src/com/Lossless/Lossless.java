package com.Lossless;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class Lossless {
    private JButton selectBMPButton;
    private JPanel panel1;
    private JTextField textField1;
    private JButton selectIN3Button;

    /**
     * Method for this application to select files and display the names
     * of the files on the textbox
     */
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
        selectIN3Button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(null);
            File f = chooser.getSelectedFile();
            String fileName = f.getAbsolutePath();
            textField1.setText(fileName);

            try {
                byte[] bytesToRunLossy = readImageByteByByte(f);
                runDecompress(bytesToRunLossy);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    /**
     * Main method for this application to decompress IN3 file for displaying
     * @param data
     */
    private void runDecompress(byte[] data) {
        BufferedImage image = createImageFromBytes(data);

        JFrame originalFrame = buildFrame();
        JPanel originalPane = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };
        originalFrame.add(originalPane);
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
    }

    /**
     * Method for compressing the image losslessly into a IN3 file
     * @param data
     * @throws IOException
     */
    private void runLossless(byte[] data) throws IOException {

        BufferedImage image = createImageFromBytes(data);

        ArrayList<String> result = new ArrayList<>();
        int countConsecutive = 0;
        for(int i = 0; i<data.length; i++){
            countConsecutive++;
            //if next element is different, add this element to result
            if(i + 1 >= data.length || data[i] != data[i+1]){
                result.add(String.valueOf(data[i]));
                if(countConsecutive !=1){
                    result.add("A");
                    result.add(String.valueOf(countConsecutive));
                }
                countConsecutive=0;
            }
        }

        //Saving Result to IN3 File
        ByteArrayOutputStream resultInByteArray = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", resultInByteArray);
        byte[] byteResult = resultInByteArray.toByteArray();

        try(FileOutputStream stream = new FileOutputStream("Compressed.in3")){
            stream.write(byteResult);
        }

        //Showing the compression ratio
        int totalBitsAfterCompression = byteResult.length * 8;
        int totalBitsBeforeCompression = data.length *8;
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(500, 200);
        frame.setTitle("Lossless Compression");
        JTextField before = new JTextField(20);
        JTextField after = new JTextField(20);
        JTextField ratio = new JTextField(20);
        before.setText("Size before Compression: "+ totalBitsBeforeCompression + " bits");
        after.setText("Size after Compression: " + totalBitsAfterCompression + " bits");
        float percent = ((float) totalBitsBeforeCompression/totalBitsAfterCompression);
        ratio.setText("Compression Ratio: " + percent);
        frame.add(before);
        frame.add(after);
        frame.add(ratio);
        frame.setVisible(true);


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

    /**
     * Helper Method to create new frame.
     * @return
     */
    private static JFrame buildFrame(){
        JFrame frame = new JFrame();
        frame.setSize(704,576);
        frame.setVisible(true);
        return frame;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Lossless Compression");
        frame.setContentPane(new Lossless().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

