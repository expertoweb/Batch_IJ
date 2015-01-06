/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

/**
 *
 * @author expertoweb
 */
class DrawPanel extends JPanel {

    Image img = null;

    public DrawPanel() {
        setBackground(Color.BLACK);  
        //loadImage();
        //Dimension dm = new Dimension(img.getWidth(null), img.getHeight(null));
        //setPreferredSize(dm);
    }
    
    public void loadImage(Image image) {
        img = image;
        repaint();
    }

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        if (img != null){
            g2d.drawImage(img, 0, 0, null);
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }
}
