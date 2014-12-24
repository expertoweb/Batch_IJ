/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.expertoweb.Batch_IJ;
/**
 *
 * @author expertoweb
 */
 /*  An example plugin to illustrate the automation of GenericDialog 
 *  i.e. how to call a function that would normally show a dialog 
 *  with options, with those options already filled in automatically. 
 * 
 *  The process consists in: 
 *  1 - Renaming the current thread to "Run$_" + any name. 
 *  2 - Setting the Macro options String for that thread. 
 *  3 - Calling the function normally, but now the dialog never shows 
 *      and its options are automatically filled in from the Macro options. 
 * 
 *  Notice that: 
 * 
 *   1 - The text label of the option becomes a key in the table of options. 
 * 
 *   2 - Options with an underscore in their name will show it in the dialog as 
 *   a blank space, but still the option name itself (the key) has the 
 *   underscore. This enables options like 'the_width' and 'the_height' to be 
 *   different (as opposed to the value set for 'the' being given to both). 
 * 
 *   3 - Boolean options, a.k.a. checkboxes, are true when present and false 
 *   otherwise. 
 * 
 *   4 - Choices, a.k.a. pulldown menus, need the exact String value desired, 
 *   not an index. 
 * 
 *   5 - If a key is absent in the Macro options string, its default value is 
 *   taken. The default value is the one that appears in the dialog when 
 *   opened. 
 * 
 *   6 - Text values are set with single quotes: title='this and that' 
 */  
  
import ij.plugin.PlugIn;  
import ij.gui.GenericDialog;  
import ij.ImagePlus;  
import ij.process.*;  
import ij.IJ;  
import ij.Macro;  
import java.awt.Color;  
  
public class Batch_IJ implements PlugIn {  
  
    public void run(String arg) {  
  
        // A - Without automation: the dialog shows  
        ImagePlus imp = createImage();  
        if (null != imp) imp.show();  
  
        // B - With automation: the dialog never shows:  
  
        Thread thread = Thread.currentThread();  
        thread.setName("Run$_create_image");  
  
        // ... so we can create many images in a loop, for example.  
        for (int i=1; i<=3; i++) {  
            // Create a 1024x1024 image, 160bit, with noise added  
            // and with the title storing the loop index:  
            Macro.setOptions(thread, "title='My new image " + i + "'"  
                  + " width=1024 height=1024 type='16-bit' add_noise");  
            // Above, notice how we do not set the fill_value key,  
            //     so that its default value (zero in this case) is taken.  
            ImagePlus imp2 = createImage();  
            imp2.show();  
        }  
  
        // Cleanup: remove reference to the Thread and its associated options  
        Macro.setOptions(thread, null);  
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
}     
