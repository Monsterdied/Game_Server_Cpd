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
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import java.awt.Image;



public class GameGUI {

    private JFrame gui;

    private Player player;
    private String gamemode;


    public GameGUI(Player player, String gamemode) {

        this.player = player;
        this.gamemode = gamemode;

        gui = new JFrame("Rocket Crash"); 
        gui.setSize(800, 600);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setLayout(null); 
        gui.getContentPane().setBackground(java.awt.Color.BLACK); 

        ImageIcon rocket = new ImageIcon("../img/rocky.png");
        Image image = rocket.getImage();
        Image newImage = image.getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH);
        rocket = new ImageIcon(newImage); 
        JLabel rocketLabel = new JLabel(rocket);
        rocketLabel.setBounds(550, -10, 200, 200);
        gui.add(rocketLabel);

        JLabel title = new JLabel("Rocket Crash", SwingConstants.CENTER);
        title.setBounds(260,0,200,50);
        title.setForeground(java.awt.Color.WHITE);
        gui.add(title);

        JLabel gamemodeLabel = new JLabel("Gamemode: " + gamemode, SwingConstants.CENTER);
        gamemodeLabel.setBounds(260,20,200,50);
        gamemodeLabel.setForeground(java.awt.Color.WHITE);
        gui.add(gamemodeLabel);


        JLabel playerLabel = new JLabel("Player: " + player.getName(), SwingConstants.LEFT);
        playerLabel.setBounds(10,60,200,50);
        playerLabel.setForeground(java.awt.Color.WHITE);
        gui.add(playerLabel);

        JLabel playerMoney = new JLabel("Money: " + player.getMoney(), SwingConstants.LEFT);
        playerMoney.setBounds(10,90,200,50);
        playerMoney.setForeground(java.awt.Color.WHITE);
        gui.add(playerMoney);




    }

    public static void main(String[] args){
        String gamemode = "Casual";
        Player player = new Player(1, "Rodrigo", 1000, 0, 200, 1.4);
        GameGUI game = new GameGUI(player, gamemode);
        game.gui.setVisible(true);
    }

    
}
