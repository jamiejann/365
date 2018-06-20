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



       histogram(data, width, height );
        //Histogram
        //BufferedImage histogram = histogram(data, width, height);

    }

    private void readImageByteByByte(File file) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        int read;
        byte [] buff = new byte[4];
        byte[] res;
        read = in.read(buff);
        do{
            out.write(buff, 0, read);
            out.flush();
            res = out.toByteArray();
        } while((read = in.read(buff))>0);

        runProcesses(res);
    }

    private BufferedImage createImageFromBytes(byte[] data) {
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        try {
            return ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

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
                //Color newRgb = new Color(red+green+blue, red+green+blue, red+green+blue);
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


    private int getMax(int [] data){
        int max = 0;
        for (int aData : data) {
            if (aData > max) {
                max = aData;
            }
        }
        return max;
    }


    private int getMin(int [] data){
        int min = data[0];
        for(int aData : data) {
            if (aData < min) {
                min = aData;
            }
        }
        return min;
    }


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

        int[][] image2d = new int[height][width];
        for(int i = 0; i< height; i++){
            for(int j = 0; j< width; j++){
                Color mycolor = new Color(original.getRGB(i,j));
                int red = mycolor.getRed();
                int green = mycolor.getGreen();
                int blue = mycolor.getBlue();
                int rgb = (int) (0.21 * red + 0.72 * green + 0.07 * blue);
                //int rgb = (red + green + blue)/3;
                image2d[i][j] = rgb;
            }
        }
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


    private static JFrame buildFrame(){
        JFrame frame = new JFrame();
        //frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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

