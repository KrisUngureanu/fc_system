package kz.tamur.comps.multiSlider;
import java.awt.*;


 //
 // MThumbSliderAdditionalUI <--> BasicMThumbSliderUI
 //                          <--> MetalMThumbSliderUI
 //                          <--> MotifMThumbSliderUI
 //
public interface MThumbSliderAdditional {

  public Rectangle getTrackRect();
  
  public Dimension getThumbSize();
  
  public int xPositionForValue(int value);
  
  public int yPositionForValue(int value);
  
}

