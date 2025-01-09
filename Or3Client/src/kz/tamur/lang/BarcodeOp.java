package kz.tamur.lang;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.oned.Code128Reader;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * The Class BarcodeOp.
 * 
 * @author Erik
 */
public class BarcodeOp {

	/** 
	 * Разбить текст на части по portion байт. Добавить в конце символы addSymbol
	 * @param text
	 * @param portion
	 * @param addSymbol
	 * @return
	 */
	public List<String> split(String text, int portion, String addSymbol) {
		List<String> res = new ArrayList<String>();
    	try {
    		byte[] all = text.getBytes("UTF-8");
    		int count = all.length/portion;
    		
    		int lastPos = 0;
    		int strPos = 0;
    		int delta = 0;
    		for (int i=0; i<count; i++) {
				byte[] b1 = new byte[portion + delta];
				System.arraycopy(all, lastPos, b1, 0, portion + delta);
				String str1 = new String(b1, "UTF-8");
				
				if (str1.charAt(str1.length() - 1) == text.charAt(strPos + str1.length() - 1)) {
					lastPos += portion + delta;
					strPos += str1.length();
					delta = 0;
					res.add(str1);
				} else {
					byte[] b2 = new byte[portion + delta - 1];
					System.arraycopy(all, lastPos, b2, 0, portion + delta - 1);
					String str2 = new String(b2, "UTF-8");

					lastPos += portion + delta - 1;
					strPos += str2.length();
					delta = 1;
					res.add(str2);
				}
    		}
    		if (lastPos < all.length) {
				byte[] b = new byte[portion];
				System.arraycopy(all, lastPos, b, 0, all.length - lastPos);
				for (int i = all.length - lastPos; i<portion; i++) {
					b[i] = addSymbol.getBytes("UTF-8")[0];
				}
				String str = new String(b, "UTF-8");
				res.add(str);
    		}
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    	return res;
	}

	/**
     * Получить QR-код (Матричный код) для текста.
     * 
     * @param text
     *            текст. 
     * @param width
     *            ширина изображения кода
     * @param height
     *            высота изображения кода
     * @return QR-код
     */
    public byte[] getQRCodeFor(String text, int width, int height) {
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 0);
            
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "png", os);
            os.close();
            return os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Получить Bar-код (Штрихкод) для текста.
     * 
     * @param text
     *            текст. 
     * @param width
     *            ширина изображения кода
     * @param height
     *            высота изображения кода
     * @return Bar-код
     */
    public byte[] getBarCodeFor(String text, int width, int height) {
        try {
            BitMatrix bitMatrix = new Code128Writer().encode(text, BarcodeFormat.CODE_128, width, height, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "gif", os);
            os.close();
            return os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Получить текст из QR-кода (Матричный код.).
     * 
     * @param code
     *            QR-код в виде массива байт
     * @return расшифрованный текст.
     */
    public String getTextFromQRCode(byte[] code) {
        ByteArrayInputStream is = new ByteArrayInputStream(code);
        try {
            BufferedImage image = ImageIO.read(is);
            LuminanceSource ls = new BufferedImageLuminanceSource(image);
            Result result = new QRCodeReader().decode(new BinaryBitmap(new HybridBinarizer(ls)));
            Vector byteSegments = (Vector) result.getResultMetadata().get(ResultMetadataType.BYTE_SEGMENTS);
            int i = 0;
            int tam = 0;
            for (Object o : byteSegments) {
                byte[] bs = (byte[]) o;
                tam += bs.length;
            }
            byte[] resultBytes = new byte[tam];
            i = 0;
            for (Object o : byteSegments) {
                byte[] bs = (byte[]) o;
                for (byte b : bs) {
                    resultBytes[i++] = b;
                }
            }
            return new String(resultBytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Получить текст из Bar-кода (Штришкод).
     * 
     * @param code
     *            Bar-код в виде массива байт
     * @return расшифрованный текст.
     */
    public String getTextFromBarCode(byte[] code) {
        ByteArrayInputStream is = new ByteArrayInputStream(code);
        try {
            BufferedImage image = ImageIO.read(is);
            LuminanceSource ls = new BufferedImageLuminanceSource(image);
            Result result = new Code128Reader().decode(new BinaryBitmap(new HybridBinarizer(ls)));
            return result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Главный метод.
     * 
     * @param args
     *            аргументы
     */
    public static void main(String args[]) {
        BarcodeOp b = new BarcodeOp();
        byte[] bs = b.getBarCodeFor("Only ASCii :( !!!!!", 200, 200);
        try {
            File f = new File("C:\\test200.gif");
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bs);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String t = b.getTextFromBarCode(bs);
        System.out.println(t);
    }
}
