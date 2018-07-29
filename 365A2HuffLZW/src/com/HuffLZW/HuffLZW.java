package com.HuffLZW;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
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
        byte[] imageInBytes;
        read = in.read(buff);
        do{
            out.write(buff, 0, read);
            out.flush();
            imageInBytes = out.toByteArray();
        } while((read = in.read(buff))>0);

        runHuffman(imageInBytes);
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

        //Build Freq table
        int freq[] = new int[unsignedIntList.size()];
        for (int i : unsignedIntList) {
            freq[unsignedIntList.get(i)] += 1;
        }
        /*
        //printing
        for(int i : freq){
            System.out.println(i);
        }
        */
        PriorityQueue<Node> pq = new PriorityQueue<>(unsignedIntList.size(), new huffComparator());
        for(int i : freq) {
            Node node = new Node();
            node.x = String.valueOf(i);
            node.data = freq[i];

            pq.add(node);
        }
        Node root = null;

        while(pq.size()>1){
            Node x = pq.peek();
            pq.poll();

            Node y = pq.peek();
            pq.poll();

            Node newNode = new Node();
            newNode.data = x.data + y.data;
            newNode.x = "-";
            newNode.left = x;
            newNode.right = y;
            root = newNode;
            pq.add(newNode);

        }

        printCode(root, "");


    }

    private static void printCode(Node root, String s){
        ArrayList<String> compressed = new ArrayList<>();
        if (root.left == null && root.right == null) {
            //System.out.print(root.x  + ":" + s);
            return;
        }
        printCode(root.left, s + "0");
        compressed.add(s + "0");
        printCode(root.right, s + "1");
        compressed.add(s + "1");
    }

    private static int byteToUnsignedInt(byte b){
        return 0x00 << 24 | b & 0xff;
    }

    private static JFrame buildFrame(){
        JFrame frame = new JFrame();
        frame.setSize(704, 576);
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
