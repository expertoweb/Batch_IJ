/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author expertoweb
 */
import java.util.Random;
import java.io.File;
import java.io.IOException;
import ij.ImagePlus; 
import java.net.URLConnection;
import java.net.URL;
import java.io.OutputStreamWriter;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

public class Base64Stuff
{
    public static void main(String[] args) {
        //Random random = new Random();
        try{
            File file1 = new File("C:\\\\Program Files\\\\ImageJ\\\\images\\\\confocal-series-10001.tif");
            //File file2 = new File("C:\\Program Files\\ImageJ\\images\\confocal-series-10000.tif");
            ImagePlus image1 = new ImagePlus("C:\\\\Program Files\\\\ImageJ\\\\images\\\\confocal-series-10001.tif");
            //ImagePlus image2 = new ImagePlus("C:\\Program Files\\ImageJ\\images\\two.tif");
            
            
            byte[] myBytes1 = org.apache.commons.io.FileUtils.readFileToByteArray(file1);
            //byte[] myBytes2 = org.apache.commons.io.FileUtils.readFileToByteArray(file2);
            //random.nextBytes(randomBytes);

            //String internalVersion1 = com.sun.org.apache.xerces.internal.impl.dv.util.Base64.encode(myBytes1);
            //String internalVersion2 = com.sun.org.apache.xerces.internal.impl.dv.util.Base64.encode(myBytes2);
            byte[] apacheBytes1 =  org.apache.commons.codec.binary.Base64.encodeBase64(myBytes1);
            //byte[] apacheBytes2 =  org.apache.commons.codec.binary.Base64.encodeBase64(myBytes2);
            String string1 = new String(apacheBytes1);
            //String string2 = new String(apacheBytes2);
        
            System.out.println("File1 length:" + string1.length());
            //System.out.println("File2 length:" + string2.length());
            System.out.println(string1);
            //System.out.println(string2);
            
            
            System.out.println("Image1 size: (" + image1.getWidth() + "," + image1.getHeight() + ")");
            //System.out.println("Image2 size: (" + image2.getWidth() + "," + image2.getHeight() + ")");
            
            String urlParameters  = "data=" +  string1 + "&size=1000x1000";
          
            URL url = new URL("http://api.qrserver.com/v1/create-qr-code/");
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            writer.write(urlParameters);
            writer.flush();
            
            //byte buf[] = new byte[700000000];

            BufferedInputStream reader = new BufferedInputStream(conn.getInputStream());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("C:\\Users\\expertoweb\\Desktop\\qrcode2.png"));  

            int data;
            while ((data = reader.read()) != -1){
                bos.write(data);
            }   
            
            writer.close();
            reader.close(); 
            bos.close();
            
            
        }catch(IOException e){}
    }
}
