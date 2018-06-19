package com.image;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.Buffer;
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
    /*
    private byte[] makeBrighter(byte[] data){
        byte[] newImage = new byte[data.length];
        for(byte b : data) {
            //byte temp = b*1.5;
        }

    }
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


    private BufferedImage dithering(byte[] data, int width, int height){
        int[][] ditheringMatrix = new int[5][5];
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
        frame.setSize(800,800);
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

