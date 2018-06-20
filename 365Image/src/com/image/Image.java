package com.image;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Image {
    private JButton attachImageButton;
    private JPanel panel;
    private JTextField textField;

    /**
     * File Chooser, displays name of the file chosen in the textbox.
     * Inputs file name into method readFileBytebyByte.
     */
    public Image() {
        attachImageButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(null);
            File f = chooser.getSelectedFile();
            String fileName = f.getAbsolutePath();
            textField.setText(fileName);

            try {
                readImageByteByByte(f);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    /**
     * Main part of ImageReader, converts data in bytes into working image with createImageFromBytes
     * Commented description of what the block of code is doing
     * createImageFromBytes creates images based on the modified byte data converted into BufferedImage.
     * PaintComponent displays the BufferedImage into the JFrame.
     * @param data
     */
    private void runProcesses(byte[] data){
        BufferedImage image = createImageFromBytes(data);
        int height = image.getHeight();
        int width = image.getWidth();

        //Original Image
        JFrame originalFrame = buildFrame();
        JPanel originalPane = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };
        originalFrame.add(originalPane);

        //Histogram
        histogram(data, width, height );

        //Make Brighter
        BufferedImage brighterImage = makeBrighter(data, width, height);
        JFrame brighterFrame = buildFrame();
        JPanel brighterPane = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(brighterImage, 0, 0, null);
            }
        };
        brighterFrame.add(brighterPane);


        //MONOCHROME
        BufferedImage greyscaleImage = greyscale(data, width, height);
        JFrame frame = buildFrame();
        JPanel pane = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(greyscaleImage, 0, 0, null);
            }
        };
        frame.add(pane);

        //Dither
        BufferedImage ditheredImage = dithering(data, width, height);
        JFrame ditheringFrame = buildFrame();
        JPanel ditheringPane = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(ditheredImage, 0, 0, null);
            }
        };
        ditheringFrame.add(ditheringPane);
    }

    /**
     *
     * @param file
     * @throws IOException
     */

    private void readImageByteByByte(File file) throws IOException {
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

        runProcesses(imageInBytes);
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
     * Method to make the image brighter 1.5x by first extracting each RBG Value of each pixel,
     * multiplied by 1.5, and then setRBG of each pixel in a new image by the modified pixel.
     * @param data
     * @param width
     * @param height
     * @return
     */
    private BufferedImage makeBrighter(byte[] data, int width, int height){
        BufferedImage original = createImageFromBytes(data);
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i< height; i++) {
            for (int j = 0; j < width; j++) {
                Color mycolor = new Color(original.getRGB(i, j));
                int newRed = (int)(mycolor.getRed() * 1.5);
                int newGreen = (int)(mycolor.getGreen() *1.5);
                int newBlue = (int)(mycolor.getBlue()*1.5);
                if(newRed > 255){
                    newRed = 255;
                }
                if(newGreen > 255){
                    newGreen = 255;
                }
                if(newBlue > 255){
                    newBlue = 255;
                }
                Color newColor = new Color(newRed, newGreen, newBlue);
                newImage.setRGB(i,j, newColor.getRGB());
            }
        }
        return newImage;
    }

    /**
     * Method to extract the frequency of each R, G, B into 3 individual arrays
     * Normalizes the numbers so it can fit into JPanel, and then display each
     * by fillRect pixel by pixel.
     * @param data
     * @param width
     * @param height
     */
    private void histogram(byte[] data, int width, int height){
        BufferedImage original = createImageFromBytes(data);

        int [] redFreq = new int [256];
        int [] greenFreq = new int [256];
        int [] blueFreq = new int [256];
        for (int i = 0; i< height; i++) {
            for (int j = 0; j < width; j++) {
                Color mycolor = new Color(original.getRGB(i,j));
                int red = mycolor.getRed();
                int green = mycolor.getGreen();
                int blue = mycolor.getRed();
                redFreq[red]++;
                greenFreq[green]++;
                blueFreq[blue]++;
            }
        }
        while(getMax(redFreq) > 300){
            for(int i = 0; i<redFreq.length; i++) {
                int temp = redFreq[i];
                temp = temp/2;
                redFreq[i] = temp;
            }
        }
        while(getMax(greenFreq) > 300){
            for(int i = 0; i<greenFreq.length; i++) {
                int temp = greenFreq[i];
                temp = temp/2;
                greenFreq[i] = temp;
            }
        }
        while(getMax(blueFreq) > 300){
            for(int i = 0; i<blueFreq.length; i++) {
                int temp = blueFreq[i];
                temp = temp/2;
                blueFreq[i] = temp;
            }
        }

        JFrame histogramFrameRed = buildFrame();
        JPanel histogramPaneRed = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                g.setColor(Color.red);
                for(int i = 0; i<255; i++){
                    g.fillRect(i,300, 1, -redFreq[i]);
                }
                }
            };
        histogramFrameRed.add(histogramPaneRed);

        JFrame histogramFrameGreen = buildFrame();
        JPanel histogramPaneGreen = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                g.setColor(Color.green);
                for(int i = 0; i<255; i++){
                    g.fillRect(i,300, 1, -greenFreq[i]);
                }
            }
        };
        histogramFrameGreen.add(histogramPaneGreen);

        JFrame histogramFrameBlue = buildFrame();
        JPanel histogramPaneBlue = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                g.setColor(Color.blue);
                for(int i = 0; i<255; i++){
                    g.fillRect(i,300, 1, -blueFreq[i]);
                }
            }
        };
        histogramFrameBlue.add(histogramPaneBlue);
    }

    /**
     * Helper method to obtain maximum value in an int array.
     * @param data
     * @return
     */
    private int getMax(int [] data){
        int max = 0;
        for (int aData : data) {
            if (aData > max) {
                max = aData;
            }
        }
        return max;
    }

    /**
     * Helper method to obtain minimum value in an int array
     * @param data
     * @return
     */
    private int getMin(int [] data){
        int min = data[0];
        for(int aData : data) {
            if (aData < min) {
                min = aData;
            }
        }
        return min;
    }

    /**
     * Method to obtain greyscale image by multiplying each R, G, B, with 0.299, 0.587 and 0.144.
     * and drawing each individual pixel by setRBG into a new BufferedImage.
     * @param data
     * @param width
     * @param height
     * @return
     */
    private BufferedImage greyscale(byte[] data, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        BufferedImage original = createImageFromBytes(data);

         for (int i = 0; i< height; i++) {
             for (int j = 0; j < width; j++) {
                 Color mycolor = new Color(original.getRGB(i,j));
                 int red = (int)(mycolor.getRed() * 0.299);
                 int green = (int)(mycolor.getGreen() * 0.587);
                 int blue = (int)(mycolor.getRed() * 0.114);

                 Color newRgb = new Color(red+green+blue, red+green+blue, red+green+blue);
                 img.setRGB(i,j, newRgb.getRGB());
             }
         }

        return img;
    }


    /**
     * Method to apply dithering to an image.
     * Used 4x4 Dithering matrix, generated by random values
     * First converted byte array into 2d matrix of integers (Greyscaled)
     * Then used ordered dithering on 2d Greyscale matrix of pixels.
     *
     * @param data
     * @param width
     * @param height
     * @return
     */
    private BufferedImage dithering(byte[] data, int width, int height){
        int[][] ditheringMatrix = new int[4][4];
        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++){
                int randomNum = ThreadLocalRandom.current().nextInt(0, 255+1);
                ditheringMatrix[i][j] = randomNum;
            }
        }
        System.out.println(Arrays.deepToString(ditheringMatrix));

        BufferedImage original = createImageFromBytes(data);
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        //2D Array of greyscale pixels
        int[][] image2d = new int[height][width];
        for(int i = 0; i< height; i++){
            for(int j = 0; j< width; j++){
                Color mycolor = new Color(original.getRGB(i,j));
                int red = mycolor.getRed();
                int green = mycolor.getGreen();
                int blue = mycolor.getBlue();
                //Converting to Greyscale
                int rgb = (int) (0.21 * red + 0.72 * green + 0.07 * blue);
                image2d[i][j] = rgb;
            }
        }
        //Ordered Dithering
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                if(image2d[i][j] < ditheringMatrix[i%4][j%4]){
                    newImage.setRGB(i,j, Color.BLACK.getRGB());
                } else {
                    newImage.setRGB(i,j,Color.WHITE.getRGB());
                }
            }
        }
        return newImage;
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
        frame.setContentPane(new Image().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

