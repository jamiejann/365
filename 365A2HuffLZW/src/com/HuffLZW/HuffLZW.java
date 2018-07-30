package com.HuffLZW;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;


class Node{
    int data;
    String x;
    Node right = null;
    Node left = null;
}

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

        //runHuffman(fileInBytes);
        runLZW(fileInBytes);
    }

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

        PriorityQueue<Node> pq = new PriorityQueue<>(unsignedIntList.size(), new huffComparator());
        Node root = null;

        for(int i = 0; i< newFreq.size(); i++){
                Node node = new Node();
                node.x = String.valueOf(i);
                node.data = newFreq.get(i);
                pq.add(node);
        }

        do{
            Node compareNode1 = pq.peek();
            pq.poll();
            Node compareNode2 = pq.peek();
            pq.poll();

            Node newNode = new Node();
            assert compareNode1 != null;
            assert compareNode2 != null;

            //sum of two comparing nodes
            newNode.data = compareNode1.data + compareNode2.data;
            newNode.left = compareNode1;
            newNode.right = compareNode2;
            root = newNode;
            pq.add(newNode);
        } while(pq.size()>1);

        printPriorityQueue(root, "");

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

        //String currentSymbol = dictionary.get(2);
        String currentSymbol = "";

        for(int i = 0; i< unsignedIntList.size(); i++){
        //for(int i : unsignedIntList){
            //String concatenated = unsignedIntList.get(current).toString() + unsignedIntList.get(current+1).toString();
            String concatenated = currentSymbol + unsignedIntList.get(i);
            if(dictionary.contains(concatenated)){
                //currentSymbol = currentSymbol + unsignedIntList.get(current).toString();
                currentSymbol = concatenated;
            } else {
                //output.add(dictionary.indexOf(currentSymbol));
                //output.add(dictionary.indexOf(currentSymbol));

                for(int j = 0; i< dictionary.size(); i++){
                    if(dictionary.get(j).equals(currentSymbol)){
                        output.add(j);
                    }
                }
                dictionary.add(concatenated);
                currentSymbol = "" + unsignedIntList.get(i);
            }

            System.out.println(output);
        }


        System.out.println(output);



    }

    private int getBytesAsWord(byte[] data, int off){
        return data[off] << 8 & 0xFF00 | data[off+1]&0xFF;
    }

    private static void printPriorityQueue(Node root, String s){
        if (root.left == null && root.right == null) {
            return;
        }
        assert root.left != null;
        printPriorityQueue(root.left, s + "0");
        compressed.add(s + "0");
        printPriorityQueue(root.right, s + "1");
        compressed.add(s + "1");
    }



    private static int byteToUnsignedInt(byte b){
        return 0x00 << 24 | b & 0xff;
    }

    private static JFrame buildFrame(){
        JFrame frame = new JFrame();
        frame.setSize(700, 700);
        frame.setVisible(true);
        return frame;
    }

    class huffComparator implements Comparator<Node>{
        public int compare(Node x, Node y){
            return x.data - y.data;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Huff & LZW Compression");
        frame.setContentPane(new HuffLZW().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }



}
