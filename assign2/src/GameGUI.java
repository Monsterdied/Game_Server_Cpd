import java.util.List;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFrame;

public class GameGUI {

    private JFrame gui;

    public GameGUI() {
        gui = new JFrame("Rocket Crash"); 
        gui.setSize(800, 600);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setLayout(null); 
        gui.getContentPane().setBackground(java.awt.Color.BLACK); 
    }

    public static void main(String[] args){
        GameGUI game = new GameGUI();
        game.gui.setVisible(true);
    }

    
}
