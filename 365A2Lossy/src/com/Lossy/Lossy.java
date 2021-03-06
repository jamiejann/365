package com.Lossy;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;
import java.util.Arrays;

public class Lossy {
    private JTextField textField1;
    private JButton button1;
    private JPanel panel;
    private JButton selectIM3Button;

    /**
     *  Method to react to button presses, adding files to the compressor.
     */
    public Lossy(){
        button1.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(null);
            File f = chooser.getSelectedFile();
            String fileName = f.getAbsolutePath();
            textField1.setText(fileName);

            try {
                byte[] bytesToRunLossy = readImageByteByByte(f);
                runLossy(bytesToRunLossy);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        selectIM3Button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(null);
            File f = chooser.getSelectedFile();
            String fileName = f.getAbsolutePath();
            textField1.setText(fileName);

            try {
                byte[] bytesForDecompression = readImageByteByByte(f);
                runDecompress(bytesForDecompression);

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    /**
     * Method to read Image file byte by byte. This method is reused from my own Project 1.
     * @param file
     * @return
     * @throws IOException
     */
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
     * Method to run the Lossy portion of the project. It first displays the original image, then
     * compresses it via taking the average of 4 blocks, then loops through the whole image. Then
     * displays the compressed image.
     * @param data
     * @throws IOException
     */
    private void runLossy(byte [] data) throws IOException {

        BufferedImage image = createImageFromBytes(data);
        int height = image.getHeight();
        int width = image.getWidth();

        //Displaying the Original Image
        JFrame originalFrame = buildFrame();
        JPanel originalPane = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };
        originalFrame.add(originalPane);
        System.out.println(Arrays.toString(data));

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for(int i = 0; i< height; i=i+2){
            for(int j = 0; j< width; j=j+2){

                Color color1 = new Color(image.getRGB(i,j));
                int red1 = color1.getRed();
                int green1 = color1.getGreen();
                int blue1 = color1.getBlue();

                Color color2 = new Color(image.getRGB(i,j+1));
                int red2 = color2.getRed();
                int green2 = color2.getGreen();
                int blue2 = color2.getBlue();

                Color color3 = new Color(image.getRGB(i+1,j));
                int red3 = color3.getRed();
                int green3 = color3.getGreen();
                int blue3 = color3.getBlue();

                Color color4 = new Color(image.getRGB(i+1,j+1));
                int red4 = color4.getRed();
                int green4 = color4.getGreen();
                int blue4 = color4.getBlue();

                int averageRed = (int) Math.sqrt(Math.floor((red1*red1+red2*red2+red3*red3+red4*red4)/4));
                int averageGreen = (int) Math.sqrt(Math.floor((green1*green1+green2*green2+green3*green3+green4*green4)/4));
                int averageBlue = (int) Math.sqrt(Math.floor((blue1*blue1+blue2*blue2+blue3*blue3+blue4*blue4)/4));

                Color averageColor = new Color(averageRed, averageGreen, averageBlue);

                result.setRGB(i, j, averageColor.getRGB());
                result.setRGB(i, j+1, averageColor.getRGB());
                result.setRGB(i+1, j, averageColor.getRGB());
                result.setRGB(i+1, j+1, averageColor.getRGB());
            }
        }
        JFrame lossyFrame = buildFrame();
        JPanel lossyPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(result, 0, 0, null);
            }
        };
        lossyFrame.add(lossyPanel);

        //Saving the file to IM3 & BMP
        ByteArrayOutputStream resultInByteArray = new ByteArrayOutputStream();
        ImageIO.write(result, "jpg", resultInByteArray);
        byte[] byteResult = resultInByteArray.toByteArray();

        try(FileOutputStream stream = new FileOutputStream("Compressed.im3")){
            stream.write(byteResult);
        }
        //Showing the compression ratio
        int totalBitsAfterCompression = byteResult.length * 8;
        int totalBitsBeforeCompression = data.length *8;
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(500, 200);
        frame.setTitle("Lossy Compression");
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
     * Method to run the decompresser, takes in a byte array of the image and then disaplys
     * it in a new frame.
     * @param bytesForDecompression
     */
    private void runDecompress(byte[] bytesForDecompression) {
        BufferedImage image = createImageFromBytes(bytesForDecompression);

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
        JFrame frame = new JFrame("Image Application");
        frame.setContentPane(new Lossy().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


}
