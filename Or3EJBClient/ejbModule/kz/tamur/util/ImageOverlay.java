package kz.tamur.util;
import java.applet.Applet;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;
/**
 * Created by Eclipse
 * User: Naik
 * Date: 28.02.2011
 * Time: 18:34:00
 * To change this template use File | Settings | File Templates.
 */
//Наложение на исходное изображение других изображений 
public class ImageOverlay {
	private int TRANSPARENT_COLOR = 0xFF00;
	private Image image;
	private int width;
	private int height;
	private int[] pixels;
	private int[] layerPixels;
	private PixelGrabber grabber;
	public ImageOverlay(Icon originalImage){
		width = originalImage.getIconWidth();
		height = originalImage.getIconHeight();
		image = ((ImageIcon)originalImage).getImage();
		pixels = new int [width * height];
		grabber = new PixelGrabber(image,0,0,width,height,pixels,0,width);
		try{
			grabber.grabPixels();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	public void addLayer(Icon newLayer){
		int w = Math.min(width,newLayer.getIconWidth());
		int h = Math.min(height, newLayer.getIconHeight());
		image = ((ImageIcon)newLayer).getImage();
		layerPixels = new int[w * h];
		grabber = new PixelGrabber(image,0,0,w,h,layerPixels,0,w);
		try{
			grabber.grabPixels();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		for(int i=0;i<w;i++){
        	for(int j=0;j<h;j++){
        		int ndx = i + j * h;
    			int pix = pixels[ndx];
    			int layerPix = layerPixels[ndx];    			
    			if(layerPix != TRANSPARENT_COLOR){
    				pixels[ndx] = layerPix;
    			}       		
        	}        	
        }
	}
	public Icon finalImage(){
		ColorModel colorModel = grabber.getColorModel();
		MemoryImageSource memSource = new MemoryImageSource(width, height, colorModel, pixels, 0, width);
        Applet applet = new Applet();
        Image img = applet.createImage(memSource);
        Icon icon = new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_DEFAULT));
		return icon;
	}
}
