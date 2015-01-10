/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;

import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;
import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.util.SardineException;

import org.apache.commons.io.FileUtils;
 
/**
 * @author Crunchify.com
 */
 
public class OwnCloud {
 
    Sardine sardine;
    
    public OwnCloud (String user, String password)
    {
        try {
            sardine = SardineFactory.begin(user, password);
        } catch (SardineException e){
            System.out.println("The conection to the OwnCloud failed: ");
        }
    }
    
    public  void upload(String path, File myFile) {
        try {
            Sardine sardine = SardineFactory.begin("admin", "qrdb.me2015");

            //System.out.println("Directory list: ");
            //List<DavResource> resources = sardine.getResources("http://www.qrdb.me/owncloud/remote.php/webdav/" + path);
            //for (DavResource res : resources)
            //{
            //     System.out.println(res.getName()); // calls the .toString() method.
            //}
            try {
                byte[] data = FileUtils.readFileToByteArray(myFile);
                sardine.put("http://www.qrdb.me/owncloud/remote.php/webdav/" + path, data);
            } catch (IOException e){
                System.out.println("The conection to the OwnCloud failed: ");
            }
            boolean exists = sardine.exists("http://www.qrdb.me/owncloud/remote.php/webdav/" + path);
            if (exists)
            {
                System.out.println("The file: " + myFile + " was created in the OwnCloud server ");
            }
        } 
        catch (SardineException e){
            
        }
    }  
}