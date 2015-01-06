/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.sun.media.jai.codec.ByteArraySeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.SeekableStream;
import ij.plugin.frame.PlugInFrame;;  
import ij.gui.GenericDialog;  
import ij.ImagePlus;  
import ij.io.Opener;
import ij.io.OpenDialog;
import ij.io.DirectoryChooser;
import ij.process.*;  
import ij.IJ;  
import ij.Macro;  
import java.awt.CheckboxGroup;
import java.awt.Color; 
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import javax.media.jai.PlanarImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author expertoweb
 */
public class Batch_IJ extends PlugInFrame {

    /**
     * Creates new form Batch_IJ_Frame
     */
    public Batch_IJ() {
        super("FrameDemo");
        try{
            dpnl = new DrawPanel();
        }catch(Exception e){
            e.printStackTrace();
        }

        //setTitle("Image");
        
        //setLocationRelativeTo(null);
        //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        initComponents();
        System.out.println(dpnl);
        this.jPanel2.add(dpnl);
        pack();
    }
    
    public void run(String arg) {  
  
        // A - Without automation: the dialog shows  
        //ImagePlus imp = createImage();  
        //if (null != imp) imp.show();  
  
        // B - With automation: the dialog never shows:  
  
        Thread thread = Thread.currentThread();  
        thread.setName("Run$_create_image");  
  
        // ... so we can create many images in a loop, for example.  
        //for (int i=1; i<=3; i++) {  
            // Create a 1024x1024 image, 160bit, with noise added  
            // and with the title storing the loop index:  
        //    Macro.setOptions(thread, "title='My new image " + i + "'"  
        //          + " width=1024 height=1024 type='16-bit' add_noise");  
            // Above, notice how we do not set the fill_value key,  
            //     so that its default value (zero in this case) is taken.  
        //    ImagePlus imp2 = createImage();  
        //    imp2.show();  
        //}  
        java.awt.GraphicsConfiguration gc = this.getGraphicsConfiguration();  
        java.awt.Rectangle bounds = gc.getBounds();
        java.awt.Dimension size = this.getPreferredSize();  
        this.setLocation((int) ((bounds.width / 2) - (size.getWidth() / 2)),  
                    (int) ((bounds.height / 2) - (size.getHeight() / 2)));   
        setVisible(true);
       
        // Cleanup: remove reference to the Thread and its associated options  
        Macro.setOptions(thread, null);  
    }  
    
    public void updateDir(String arg){
        
        this.path = arg;

        // Directory path here
        String path = arg; 

        File folder = new File(path);
        File[] listOfFiles = folder.listFiles(); 
        if (listOfFiles == null || listOfFiles.length <= 0){
            return;
        }
        String files[] = new String[listOfFiles.length];
        
        for (int i = 0; i < listOfFiles.length; i++) 
        {
            
            
            if (listOfFiles[i] != null && 
                listOfFiles[i].isFile() &&
                listOfFiles[i].getName().endsWith(".tif"))
            {
                
                try{
                    files[i] = listOfFiles[i].getName();
                    //System.out.println("Open: " + listOfFiles[i].getCanonicalPath());
    
                    //ImagePlus imp = (new Opener()).openImage(listOfFiles[i].getCanonicalPath());
                    
                    //System.out.println("Add Noise: " + listOfFiles[i].getCanonicalPath());
                    //IJ.run(imp, "Gaussian Blur...", "sigma=1");
                    //imp.show();
                    //Thread.sleep(4000);
                    //System.out.println("Save: " + listOfFiles[i].getCanonicalPath() + "f");
                    //IJ.save(imp, listOfFiles[i].getCanonicalPath());
                    //imp.close();
                }catch(Exception e){
                            IJ.run("Quit");
                            System.exit(0);
                }
                jPanel1.setLayout(new java.awt.BorderLayout());
            }
        }
        
        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = files;
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);
    }
  
    public ImagePlus createImage() {  
        final GenericDialog gd = new GenericDialog("Create image");  
        gd.addStringField("title:", "new");  
        gd.addNumericField("width:", 512, 0);  
        gd.addNumericField("height:", 512, 0);  
        final String[] types = new String[]{"8-bit", "16-bit", "32-bit", "RGB"};  
        gd.addChoice("type:", types, types[0]);  
        gd.addSlider("fill_value:", 0, 255, 0); // min, max, default  
        gd.addCheckbox("add_noise", false);  
        gd.showDialog();  
        if (gd.wasCanceled()) return null;  
  
        final String title = gd.getNextString();  
        final int width = (int)gd.getNextNumber();  
        final int height = (int)gd.getNextNumber();  
        final int itype = gd.getNextChoiceIndex();  
        final double fill_value = gd.getNextNumber();  
        final boolean add_noise = gd.getNextBoolean();  
  
        ImageProcessor ip = null;  
  
        switch (itype) {  
            case 0: ip = new ByteProcessor(width, height);  break;  
            case 1: ip = new ShortProcessor(width, height); break;  
            case 2: ip = new FloatProcessor(width, height); break;  
            case 3: ip = new ColorProcessor(width, height); break;  
        }  
  
        // Color images are created filled with white by default  
        if (3 == itype && 255 != fill_value) {  
            // color image  
            ip.setColor(new Color((int)fill_value, (int)fill_value, (int)fill_value));  
            ip.fill();  
        }  
        // non-color images  
        else if (0 != fill_value) {  
            ip.setValue(fill_value);  
            ip.fill();  
        }  
  
        final ImagePlus imp = new ImagePlus(title, ip);  
  
        if (add_noise) IJ.run(imp, "Add Noise", "");  
  
        return imp;  
    }  
    
    static Image load(byte[] data) throws Exception{
        Image image = null;
        SeekableStream stream = new ByteArraySeekableStream(data);
        String[] names = ImageCodec.getDecoderNames(stream);
        ImageDecoder dec = 
              ImageCodec.createImageDecoder(names[0], stream, null);
        RenderedImage im = dec.decodeAsRenderedImage();
        image = PlanarImage.wrapRenderedImage(im).getAsBufferedImage();
        return image;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        menuBar1 = new java.awt.MenuBar();
        menu1 = new java.awt.Menu();
        menuItem1 = new java.awt.MenuItem();
        menuItem2 = new java.awt.MenuItem();
        menuItem5 = new java.awt.MenuItem();
        menu2 = new java.awt.Menu();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("jMenu3");
        jMenuBar1.add(jMenu3);

        setMinimumSize(new java.awt.Dimension(800, 500));
        setPreferredSize(new java.awt.Dimension(800, 500));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setMinimumSize(new java.awt.Dimension(258, 23));

        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setRightComponent(jPanel2);

        jPanel1.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.CENTER);

        menu1.setLabel("File");

        menuItem1.setLabel("Open...");
        menuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem1ActionPerformed(evt);
            }
        });
        menu1.add(menuItem1);

        menuItem2.setLabel("Open with Bioformats...");
        menuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem2ActionPerformed(evt);
            }
        });
        menu1.add(menuItem2);

        menuItem5.setActionCommand("Open directory...");
        menuItem5.setLabel("Open directory...");
        menuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem5ActionPerformed(evt);
            }
        });
        menu1.add(menuItem5);

        menuBar1.add(menu1);

        menu2.setLabel("Edit");
        menuBar1.add(menu2);

        setMenuBar(menuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Exit the Application
     */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm

    private void menuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem1ActionPerformed
        // TODO add your handling code here:
        OpenDialog opener = new OpenDialog(".");  
        ImagePlus imp = (new Opener()).openImage(opener.getPath());
        imp.show();
    }//GEN-LAST:event_menuItem1ActionPerformed

    private void menuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem2ActionPerformed
        // TODO add your handling code here:
        IJ.run("Bio-Formats Importer");
    }//GEN-LAST:event_menuItem2ActionPerformed

    private void menuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem5ActionPerformed
        // TODO add your handling code here:
        DirectoryChooser opener = new DirectoryChooser(".");   
        this.updateDir(opener.getDirectory());
    }//GEN-LAST:event_menuItem5ActionPerformed

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        // TODO add your handling code here:
        String file;

        try{
            file = (String)((JList)evt.getSource()).getSelectedValue();

            FileInputStream in = new FileInputStream(path + "\\" + file);
            FileChannel channel = in.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate((int)channel.size());
            channel.read(buffer);
            Image image = load(buffer.array());
            // make sure that the image is not too big
            //  scale with a width of 500 
            Image imageScaled = 
              image.getScaledInstance(500, -1,  Image.SCALE_SMOOTH);
            //
            //IJ.log("image: " + path + "\n" + image);
            //
            dpnl.loadImage(imageScaled);
            dpnl.repaint();
            //Dimension dm = new Dimension(imageScaled.getWidth(null), imageScaled.getHeight(null));
            //setPreferredSize(dm);
        }catch(Exception e){}
    }//GEN-LAST:event_jList1ValueChanged


    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                (new Batch_IJ()).run(".");
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private java.awt.Menu menu1;
    private java.awt.Menu menu2;
    private java.awt.MenuBar menuBar1;
    private java.awt.MenuItem menuItem1;
    private java.awt.MenuItem menuItem2;
    private java.awt.MenuItem menuItem5;
    // End of variables declaration//GEN-END:variables

    String path = "";
    CheckboxGroup cbg = new CheckboxGroup();
    DrawPanel dpnl;
}
