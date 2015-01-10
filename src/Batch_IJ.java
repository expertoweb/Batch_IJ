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
import ij.gui.MessageDialog;  
import ij.ImagePlus;  
import ij.io.Opener;
import ij.io.OpenDialog;
import ij.io.DirectoryChooser;
import ij.Macro; 
import ij.process.*;  
import ij.IJ;  
import ij.WindowManager;
import java.awt.CheckboxGroup;
import java.awt.Color; 
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel;
import javax.media.jai.PlanarImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.SwingUtilities;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.util.UUID;
import javax.media.jai.PlanarImage;
import javax.imageio.ImageIO; 

/**
 *
 * @author expertoweb
 */
public class Batch_IJ extends PlugInFrame {

    /**
     * Creates new form Batch_IJ_Frame
     */
    public Batch_IJ() {
        super("Batch_IJ");

        //setTitle("Image");
        
        //setLocationRelativeTo(null);
        //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        initComponents();
        
        jPanel3.add(dpnl, java.awt.BorderLayout.CENTER);
        jTable1.setRowHeight(80);
        jTable1.setTableHeader(null);
        jTable1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            
            private int selected = -1;
            
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (jTable1.getSelectedRow() > -1) {
                    selected = jTable1.getSelectedRow();
                    String path = ((ImageIcon)jTable1.getValueAt(jTable1.getSelectedRow(), 0)).getDescription();
                    try{
                        FileInputStream in = new FileInputStream(path);
                        FileChannel channel = in.getChannel();
                        ByteBuffer buffer = ByteBuffer.allocate((int)channel.size());
                        channel.read(buffer);
                        Image image = load(buffer.array());
                            // make sure that the image is not too big
                            //  scale with a width of 500 
                        Image imageScaled = image.getScaledInstance(400, -1,  Image.SCALE_SMOOTH);

                        dpnl.loadImage(imageScaled);
                        dpnl.repaint();   
                    }catch(Exception e){}
                    
                }
            }
        });
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
        //updateDir(arg);
        try{
            ImagePlus img = WindowManager.getCurrentImage();  // current image
            ImageProcessor ip = img.getProcessor();  // current slice
            Image image = ip.createImage();
            //TODO: Display image in list and panel
            Image imageScaled = 
                    image.getScaledInstance(100, -1,  Image.SCALE_SMOOTH);
            updateList(imageScaled);
            imageScaled = 
                    image.getScaledInstance(400, -1,  Image.SCALE_SMOOTH);
            dpnl.loadImage(imageScaled);
            dpnl.repaint();
        }catch(NullPointerException e){
            MessageDialog gd = new MessageDialog(this, "No files open", "No files open");
        }
        
       
        // Cleanup: remove reference to the Thread and its associated options  
        Macro.setOptions(thread, null);  
    }  
    
    public void updateList(Image img){
        ((DefaultTableModel)jTable1.getModel()).setRowCount(0);
        ImageIcon icon = new ImageIcon(img, "Test");
        ((DefaultTableModel)jTable1.getModel()).addRow(new Object[] { icon });
        ((DefaultTableModel)jTable1.getModel()).addRow(new Object[] { icon });
        ((DefaultTableModel)jTable1.getModel()).fireTableDataChanged();
        IJ.log("" + jTable1.getRowCount());
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
        
        ((DefaultTableModel)jTable1.getModel()).setRowCount(0);
        
        for (int i = 0; i < listOfFiles.length; i++) 
        {
            if (listOfFiles[i] != null && 
                listOfFiles[i].isFile() &&
                listOfFiles[i].getName().endsWith(".tif"))
            {
                
                try{
                    files[i] = listOfFiles[i].getName();
                    FileInputStream in = new FileInputStream(path + "\\" + files[i]);
                    FileChannel channel = in.getChannel();
                    ByteBuffer buffer = ByteBuffer.allocate((int)channel.size());
                    channel.read(buffer);
                    Image image = load(buffer.array());
                    // make sure that the image is not too big
                    //  scale with a width of 500 
                    Image imageScaled = 
                    image.getScaledInstance(100, -1,  Image.SCALE_SMOOTH);
                    
                    ImageIcon icon = new ImageIcon(imageScaled, path + "\\" + files[i]);
                    
                    ((DefaultTableModel)jTable1.getModel()).addRow(new Object[] { icon });
                }catch(Exception e){
                }
            }
        }

        jScrollPane1.setViewportView(jTable1);
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
        jTable1 = new javax.swing.JTable();
        jList1 = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jButton3 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("jMenu3");
        jMenuBar1.add(jMenu3);

        setMinimumSize(new java.awt.Dimension(660, 660));
        setPreferredSize(new java.awt.Dimension(660, 660));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jPanel1.setPreferredSize(new java.awt.Dimension(600, 600));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setPreferredSize(new java.awt.Dimension(500, 500));

        jScrollPane1.setMinimumSize(new java.awt.Dimension(100, 23));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(100, 402));

        jTable1.setModel(new MyTableModel(0,1));
        jScrollPane1.setViewportView(jTable1);

        jScrollPane1.setViewportView(jList1);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(515, 788));
        jPanel2.setLayout(new java.awt.BorderLayout());
        jPanel2.add(jPanel4, java.awt.BorderLayout.NORTH);

        jPanel3.setLayout(new java.awt.BorderLayout());
        jPanel2.add(jPanel3, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jPanel2);

        jPanel1.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jToolBar1.setRollover(true);

        jButton3.setText("Abrir directorio...");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton3);

        jButton1.setText("Abrir en ImageJ");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        jButton2.setText("Subir a OwnCloud...");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        jButton4.setText("Crear QRcode...");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton4);

        jPanel1.add(jToolBar1, java.awt.BorderLayout.NORTH);

        add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Exit the Application
     */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        //System.exit(0);
    }//GEN-LAST:event_exitForm

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        ImagePlus imp = new ImagePlus(((ImageIcon)jTable1.getValueAt(jTable1.getSelectedRow(), 0)).getDescription());
        imp.show();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        DirectoryChooser opener = new DirectoryChooser(".");   
        this.updateDir(opener.getDirectory());
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        String imgpath = ((ImageIcon)jTable1.getValueAt(jTable1.getSelectedRow(), 0)).getDescription();
        System.out.println(imgpath);
        String codepath = QRCode.createQRCode(imgpath);
        ImagePlus imp = new ImagePlus(codepath);
        imp.show();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        UUID uuid = UUID.nameUUIDFromBytes(((ImageIcon)jTable1.getValueAt(jTable1.getSelectedRow(), 0)).getDescription().getBytes());
        OwnCloud oc = new OwnCloud("admin", "qrdb.me2015");
        oc.upload("data/" + uuid + ".tif", new File (((ImageIcon)jTable1.getValueAt(jTable1.getSelectedRow(), 0)).getDescription()));
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
                (new Batch_IJ()).run(".\\images");
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JList jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    String path = "";
    CheckboxGroup cbg = new CheckboxGroup();
    DrawPanel dpnl = new DrawPanel();
}
