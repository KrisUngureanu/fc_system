package kz.tamur.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.itextpdf.text.exceptions.InvalidPdfException;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.parser.PdfImageObject;

import kz.tamur.rt.Utils;

public class ImageUtil {
	public static BufferedImage combineImages(int spacing, List<BufferedImage> images) {
		return combineImages(spacing, images.toArray(new BufferedImage[images.size()]));
	}
	
	public static BufferedImage combineImages(int spacing, Image... images) {
		// No images given
		if (images == null || images.length == 0) {
			return null;
		}

		// Finding the maximum image size first
		Dimension maxSize = new Dimension(0, 0);
		for (Image image : images) {
			if (image != null) {
				maxSize.width = maxSize.width + image.getWidth(null) + spacing;
				maxSize.height = Math.max(maxSize.height, image.getHeight(null));
			}
		}
		maxSize.width -= spacing;

		// Return null image if sizes are invalid
		if (maxSize.width <= 0 || maxSize.height <= 0) {
			return null;
		}

		// Creating new merged image
		BufferedImage bi = new BufferedImage(maxSize.width, maxSize.height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.getGraphics();
		int x = 0;
		for (Image image : images) {
			if (image != null) {
				g.drawImage(image, x, 0, null);
				x += image.getWidth(null) + spacing;
			}
		}
		g.dispose();

		return bi;
	}
	
	public static BufferedImage centerImage(Image image, int width, int height) {
		int width0 = image.getWidth(null);
		int height0 = image.getHeight(null);
		
		//if (width0 > width || height0 > height) return null;
		
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.getGraphics();
		int x = 0;
		g.drawImage(image, (width - width0)/2, (height - height0)/2, null);

		g.dispose();
		
		return bi;
	}
	
	public static byte[] getImageData(BufferedImage img) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ImageIO.write(img, "PNG", os);
			os.close();
		} catch (IOException e) {
			System.err.println("Error writing image");
		}
		return os.toByteArray();
	}
    
	public static File getImageFile(BufferedImage img) {
		try {
	        File f = File.createTempFile("image", null);
	        f.deleteOnExit();
	        FileOutputStream os = new FileOutputStream(f);
			ImageIO.write(img, "PNG", os);
			os.close();
			return f;
		} catch (IOException e) {
			System.err.println("Error writing image");
		}
		return null;
	}

	public static void writeImage(BufferedImage img, OutputStream os) {
		try {
			ImageIO.write(img, "PNG", os);
		} catch (IOException e) {
			System.err.println("Error writing image");
		}
	}

    public static BufferedImage loadImage(final String path) {
    	BufferedImage result = null;
        try {
	        ImageInputStream ios = new FileImageInputStream(new File(path));
	        Iterator<ImageReader> iter = ImageIO.getImageReaders(ios);
	        if (iter.hasNext()) {
	            ImageReader reader = iter.next();
                ImageInputStream stream = new FileImageInputStream(new File(path));
                reader.setInput(stream);
                result = reader.read(reader.getMinIndex());
                reader.dispose();
	        } else {
	            System.err.println("No reader found for given image");
	        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static BufferedImage loadImage(final InputStream is) {
    	BufferedImage result = null;
        try {
        	result = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static BufferedImage loadImage(final byte[] img) {
    	ByteArrayInputStream bis = new ByteArrayInputStream(img);
    	try {
    		return loadImage(bis);
    	} finally {
    		Utils.closeQuietly(bis);
    	}
    }

    public static BufferedImage loadImageFromPDF(final String path) {
    	BufferedImage res = null;
    	try {
			PdfReader reader = new PdfReader(new FileInputStream(path));
	
			if (reader.getNumberOfPages() > 0) {
				PdfDictionary page = reader.getPageN(1);
				
				PdfDictionary resources = page.getAsDict(PdfName.RESOURCES);
		        PdfDictionary xobjects = resources.getAsDict(PdfName.XOBJECT);
		        
		        Iterator<PdfName> it = xobjects.getKeys().iterator(); 
		        if (it.hasNext()) {
			        PdfName imgRef = it.next();
			        PdfStream stream = xobjects.getAsStream(imgRef);
			        
			        PdfImageObject iObj = new PdfImageObject((PRStream)stream);
			        res = iObj.getBufferedImage();
				} else {
		            System.out.println("No image resources on first page in PDF document!");
		        }
			} else {
	            System.out.println("No pages in PDF document!");
			}
    	} catch (InvalidPdfException e) {
            // System.out.println("Trying to load non-PDF as PDF document!");
    	} catch (Throwable e) {
            System.out.println("Error loading image from PDF document!");
    	}
        return res;
    }

    public static File savePDFasImage(final File pdfFile, File dir) {
    	File res = null;
    	try {
			PDDocument document = PDDocument.load(pdfFile);
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			
        	res = Funcs.createTempFile("blob", null, dir);

			//for (int i=0; i<document.getNumberOfPages(); i++) {
				BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
	
			    // suffix in filename will be used as the file format
				ImageIO.write(bim, "png", res);
			//}
			document.close();
			return res;
    	} catch (Exception e) {
    		System.out.println("Error converting PDF document to Image!");
    		if (res != null)
    			res.delete();
    	}
    	return null;
    }
    
    public static BufferedImage createCompatibleImage(int width, int height, int transparency) {
        return getGraphicsConfiguration().createCompatibleImage(width, height, transparency);
    }
	
    public static GraphicsConfiguration getGraphicsConfiguration() {
        return getGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }
    
    private static GraphicsEnvironment getGraphicsEnvironment() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment();
    }
}
