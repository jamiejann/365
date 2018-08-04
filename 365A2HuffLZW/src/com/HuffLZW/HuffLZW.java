package com.HuffLZW;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Node class for Huffman coding. constructors to create Nodes
 */
class Node{
    public Node(Node left, Node right, int data){
        this.left = left;
        this.right = right;
        this.data = data;
    }
    public Node(int data, String x){
        this.data = data;
        this.x = x;
    }
    int data;
    String x;
    Node right = null;
    Node left = null;
}

/**
 * Method for button reaction, selecting file
 */
public class HuffLZW {
    private JButton button;
    private JPanel panel1;
    private JTextField textField;
    public static ArrayList<String> compressed = new ArrayList<>();

    public HuffLZW(){
        button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(null);
            File f = chooser.getSelectedFile();
            String fileName = f.getAbsolutePath();
            textField.setText(fileName);

            try {
                readWavByteByByte(f);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    /**
     * Method to read file byte by byte. This was reused from Project 1 by me.
     * @param file
     * @throws IOException
     */
    private void readWavByteByByte(File file) throws IOException{
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

        runHuffman(fileInBytes);
        runLZW(fileInBytes);
    }

    /**
     * Method to run the LZW portion of this project. Then displays the results in a new Frame
     * @param data
     */
    private void runLZW(byte[] data){
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

        ArrayList<String> dictionary = new ArrayList<>();
        dictionary.add((unsignedIntList.get(0)).toString());
        dictionary.add((unsignedIntList.get(1)).toString());
        dictionary.add((unsignedIntList.get(2)).toString());

        ArrayList<Integer> output = new ArrayList<>();
        String currentSymbol = "";

        for(int i = 0; i< unsignedIntList.size(); i++){
            String concatenated = currentSymbol + unsignedIntList.get(i);
            if(dictionary.contains(concatenated)){
                currentSymbol = concatenated;
            } else {
                output.add(dictionary.indexOf(currentSymbol));
                dictionary.add(concatenated);
                currentSymbol = "" + unsignedIntList.get(i);
            }
        }
        int totalBitsAfterCompression = output.size() * 8;

        //display results
        int beforeCompression = data.length * 8 ;
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(500, 200);
        frame.setTitle("LZW Compression");
        JTextField before = new JTextField(20);
        JTextField after = new JTextField(20);
        JTextField ratio = new JTextField(20);
        before.setText("Size before Compression: "+ beforeCompression + " bits");
        after.setText("Size after Compression: " + totalBitsAfterCompression + " bits");
        float percent = ((float) beforeCompression/totalBitsAfterCompression);
        ratio.setText("Compression Ratio: " + percent);
        frame.add(before);
        frame.add(after);
        frame.add(ratio);
        frame.setVisible(true);
    }

    /**
     * Main method to run the huffman coding portion of this project.
     * Displays results and compression ratios in a new frame
     * @param data
     */
    private void runHuffman(byte[] data){
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
        PriorityQueue<Node> myQueue = new PriorityQueue<>(unsignedIntList.size(), new huffComparator());
        Node root;
        for(int i = 0; i< newFreq.size(); i++){
                Node node = new Node(newFreq.get(i), String.valueOf(i));
                myQueue.add(node);
        }
        do{
            Node compareNode1 = myQueue.peek();
            myQueue.poll();
            assert compareNode1 != null;
            Node compareNode2 = myQueue.peek();
            assert compareNode2 != null;
            myQueue.poll();
            Node newNode = new Node(compareNode1, compareNode2, compareNode1.data + compareNode2.data);
            root = newNode;
            myQueue.add(newNode);
        } while(myQueue.size()>1);

        buildHuffCode(root, "");

        System.out.print(compressed);
        int totalBitsAfterCompression = 0;
        for(String i : compressed) {
            totalBitsAfterCompression += i.length();
        }

        //display results
        int beforeCompression = data.length * 8;
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(500, 200);
        frame.setTitle("Huffman Compression");
        JTextField before = new JTextField(20);
        JTextField after = new JTextField(20);
        JTextField ratio = new JTextField(20);
        before.setText("Size before Compression: "+ beforeCompression + " bits");
        after.setText("Size after Compression: " + totalBitsAfterCompression + " bits");
        float percent = ((float) beforeCompression/totalBitsAfterCompression);
        ratio.setText("Compression Ratio: " + percent);
        frame.add(before);
        frame.add(after);
        frame.add(ratio);
        frame.setVisible(true);
    }

    /**
     * Method to obtain bytes as word.
     * @param data
     * @param off
     * @return
     */
    private int getBytesAsWord(byte[] data, int off){
        return data[off] << 8 & 0xFF00 | data[off+1]&0xFF;
    }

    /**
     * Method to store compressed huffman codes in a new Arraylist.
     * @param root
     * @param s
     */
    private static void buildHuffCode(Node root, String s){
        if (root.left == null && root.right == null) {
            return;
        }
        assert root.left != null;
        assert root.right != null;
        buildHuffCode(root.left, s + "0");
        compressed.add(s + "0");
        buildHuffCode(root.right, s + "1");
        compressed.add(s + "1");
    }

    /**
     * Method to convert byte to unsinged int.
     * @param b
     * @return
     */
    private static int byteToUnsignedInt(byte b){
        return 0x00 << 24 | b & 0xff;
    }

    /**
     * Comparator helper for Huffman Comparator
     */
    class huffComparator implements Comparator<Node>{
        public int compare(Node x, Node y){
            int difference = x.data - y.data;
            return difference;
        }
    }
    /**
     * Helper method for creating new frame.
     * @return
     */
    private static JFrame buildFrame(){
        JFrame frame = new JFrame();
        frame.setSize(700, 700);
        frame.setVisible(true);
        return frame;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Huff & LZW Compression");
        frame.setContentPane(new HuffLZW().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
