/**
 *
 * The MIT License
 *
 * Copyright 2018, 2019 Paul Conti
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package builder.models;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;

import builder.common.CommonUtil;
import builder.common.EnumFactory;
import builder.events.MsgBoard;
import builder.events.MsgEvent;
import builder.prefs.GeneralEditor;

/**
 * The Class ImgButtonModel implements the model for the Image Button widget.
 * 
 * @author Paul Conti
 * 
 */
public class ImgButtonModel extends WidgetModel { 
  
  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;
  
  /** The Constant PROP_IMAGE. */
  static private final int PROP_IMAGE        =6;
  
  /** The Constant PROP_IMAGE_SEL. */
  static private final int PROP_IMAGE_SEL    =7;
  
  /** The Constant PROP_DEFINE. */
  static private final int PROP_DEFINE       =8;
  
  /** The Constant PROP_DEFINE_SEL. */
  static private final int PROP_DEFINE_SEL   =9;
  
  /** The Constant PROP_FORMAT. */
  static private final int PROP_FORMAT       =10;
  
  /** The Constant PROP_FORMAT. */
  static private final int PROP_TRANSPARENCY =11;

  /** The Constant PROP_CHANGE_PAGE. */
  static private final int PROP_CHANGE_PAGE  =12;
  
  /** The Constant PROP_PAGE. */
  static private final int PROP_PAGE         =13;
  

  /** The general model. */
  private GeneralModel generalModel;
  
  /** The image. */
  private BufferedImage image;
  
  /** The image selected. */
  private BufferedImage imageSelected;
  
  /**
   * Instantiates a new img button model.
   */
  public ImgButtonModel() {
    initProperties();
    generalModel = (GeneralModel) GeneralEditor.getInstance().getModel();
  }
  
  /**
   * Initializes the properties.
   */
  protected void initProperties()
  {
    widgetType = EnumFactory.IMAGEBUTTON;
    data = new Object[14][5];
    
    initProp(PROP_KEY, String.class, "COM-001", Boolean.TRUE,"Key",widgetType);
    initProp(PROP_ENUM, String.class, "COM-002", Boolean.FALSE,"ENUM",widgetType);
    initProp(PROP_X, Integer.class, "COM-003", Boolean.FALSE,"X",Integer.valueOf(0));
    initProp(PROP_Y, Integer.class, "COM-004", Boolean.FALSE,"Y",Integer.valueOf(0));
    initProp(PROP_WIDTH, Integer.class, "COM-005", Boolean.TRUE,"Width",Integer.valueOf(0));
    initProp(PROP_HEIGHT, Integer.class, "COM-006", Boolean.TRUE,"Height",Integer.valueOf(0));
    
    initProp(PROP_IMAGE, String.class, "IBTN-100", Boolean.TRUE,"Image","");
    initProp(PROP_IMAGE_SEL, String.class, "IBTN-101", Boolean.TRUE,"Image When Selected","");
    initProp(PROP_DEFINE, String.class, "IBTN-102", Boolean.FALSE,"Image #defines","");
    initProp(PROP_DEFINE_SEL, String.class, "IBTN-103", Boolean.FALSE,"Select Image #defines","");
    initProp(PROP_FORMAT, String.class, "IBTN-104", Boolean.TRUE,"Image Format","");
    initProp(PROP_TRANSPARENCY, Boolean.class, "IBTN-107", Boolean.FALSE,"Transparent?",Boolean.FALSE);
    initProp(PROP_CHANGE_PAGE, Boolean.class, "IBTN-105", Boolean.FALSE,"Change Page Funct?",Boolean.FALSE);
    initProp(PROP_PAGE, String.class, "IBNT-106", Boolean.TRUE,"Jump to Page Enum","");

  }

  /**
   * Sets the image name.
   *
   * @param name
   *          the new image name
   */
  public void setImageName(String name) {
    data[PROP_IMAGE][PROP_VAL_VALUE]=name;
  }

  /**
   * Gets the image format.
   *
   * @return the image format
   */
  public String getImageFormat() {
    return (String) data[PROP_FORMAT][PROP_VAL_VALUE];
  }
  
  /**
   * Sets the image format.
   *
   * @param name
   *          the new image format
   */
  public void setImageFormat(String name) {
    data[PROP_FORMAT][PROP_VAL_VALUE]=name;
  }

  /**
   * Sets the image selected name.
   *
   * @param name
   *          the new image selected name
   */
  public void setImageSelectedName(String name) {
    data[PROP_IMAGE_SEL][PROP_VAL_VALUE]=name;
  }

  /**
   * Use default colors.
   *
   * @return <code>true</code>, if successful
   */
  public boolean useDefaultColors() {
    return true;
  }
  
  /**
   * is Transparent?
   *
   * @return <code>true</code>, if successful
   */
  public boolean isTransparent() {
    return (((Boolean) data[PROP_TRANSPARENCY][PROP_VAL_VALUE]).booleanValue());
  }
  
  /**
   * changeValueAt
   *
   * @see builder.models.WidgetModel#changeValueAt(java.lang.Object, int)
   */
  @Override
  public void changeValueAt(Object value, int row) {
    // The test for Integer supports copy and paste from clipboard.
    // Otherwise we get a can't cast class String to Integer fault
    if ( (getClassAt(row) == Integer.class) && (value instanceof String)) {
        data[row][PROP_VAL_VALUE] = Integer.valueOf(Integer.parseInt((String)value));
    } else {
      data[row][PROP_VAL_VALUE] = value;
    }
    fireTableCellUpdated(row, 1);
    if (row == PROP_CHANGE_PAGE) {
      if (isChangePageFunct()) {
        data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
      } else {
        data[PROP_PAGE][PROP_VAL_VALUE]="";
        data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
      }
      fireTableCellUpdated(PROP_PAGE, 1);
    }
    if (bSendEvents) {
      event = new MsgEvent();
      event.code = MsgEvent.WIDGET_REPAINT;
      event.message = getKey();
      MsgBoard.getInstance().publish(event);
    }
  }

  /**
   * Checks if is change page funct.
   *
   * @return true, if is change page funct
   */
  public boolean isChangePageFunct() {
    return ((Boolean) data[PROP_CHANGE_PAGE][PROP_VAL_VALUE]).booleanValue();
  }

  /**
   * Gets the change page enum.
   *
   * @return the change page enum
   */
  public String getChangePageEnum() {
    return ((String) data[PROP_PAGE][PROP_VAL_VALUE]);
  }

  /**
   * Gets the image.
   *
   * @return the image
   */
  public BufferedImage getImage() {
    return image;
  }

  /**
   * Sets the image.
   *
   * @param file
   *          the file
   * @param x
   *          the x
   * @param y
   *          the y
   */
  public void setImage(File file, int x, int y) {
    image = null;
    try {
        image = ImageIO.read(file);
    } catch(IOException e) {
        System.out.println("read error: " + e.getMessage());
    }
    setWidth(image.getWidth());
    setHeight(image.getHeight());
    if (image.getType() == BufferedImage.TYPE_3BYTE_BGR)
      setImageFormat("GSLC_IMGREF_FMT_BMP24");
    else if (image.getType() == BufferedImage.TYPE_USHORT_555_RGB) 
      setImageFormat("GSLC_IMGREF_FMT_BMP16");
    else
      setImageFormat("GSLC_IMGREF_FMT_RAW1");
    // now construct a #define to use during code generation
    String fileName = file.getName();
    int n = fileName.indexOf(".bmp");
    if (n > 0) {
      String tmp = fileName.substring(0,n);
      fileName = tmp.toUpperCase();
    }
    // remove all special characters
    fileName = fileName.replaceAll("\\W", ""); 
    fileName = "IMG_" + fileName;
    setDefine(fileName);
    fileName = file.getName();
    setImageName(fileName);
  }

  /**
   * Gets the image selected.
   *
   * @return the image selected
   */
  public BufferedImage getImageSelected() {
    return imageSelected;
  }

  /**
   * Sets the image selected.
   *
   * @param file
   *          the new image selected
   */
  public void setImageSelected(File file) {
    imageSelected = null;
    try {
      imageSelected = ImageIO.read(file);
    } catch(IOException e) {
        System.out.println("read error: " + e.getMessage());
    }
    String fileName = file.getName();
    // now construct a #define to use during code generation
    int n = fileName.indexOf(".bmp");
    if (n > 0) {
      String tmp = fileName.substring(0,n);
      fileName = tmp.toUpperCase();
    }
    // remove all special characters
    fileName = fileName.replaceAll("\\W", ""); 
    fileName = "IMG_BTN_" + fileName + "_SEL";
    setSelDefine(fileName);
    fileName = file.getName();
    setImageSelectedName(fileName);
  }

  /**
   * Gets the image name.
   *
   * @return the image name
   */
  public String getImageName() {
    return (String) data[PROP_IMAGE][PROP_VAL_VALUE];
  }
  
  /**
   * Gets the select image name.
   *
   * @return the select image name
   */
  public String getSelectImageName() {
    return (String) data[PROP_IMAGE_SEL][PROP_VAL_VALUE];
  }
  
  /**
   * Gets the define.
   *
   * @return the define
   */
  public String getDefine() {
    String dir = generalModel.getImageDir();
    String fileName = (String) data[PROP_DEFINE][PROP_VAL_VALUE];
    // do we need to add a relative path for code generation?
    if (dir.length() > 0)
      fileName = dir + fileName;
    return fileName;
  }
  
  /**
   * Sets the define.
   *
   * @param s
   *          the new define
   */
  public void setDefine(String s) {
    shortcutValue(s, PROP_DEFINE);
  }

  /**
   * Gets the sel define.
   *
   * @return the sel define
   */
  public String getSelDefine() {
    String dir = generalModel.getImageDir();
    String fileName = (String) data[PROP_DEFINE_SEL][PROP_VAL_VALUE];
    // do we need to add a relative path for code generation?
    if (dir.length() > 0)
      fileName = dir + fileName;
    return fileName;
  }
  
  /**
   * Sets the sel define.
   *
   * @param s
   *          the new sel define
   */
  public void setSelDefine(String s) {
    shortcutValue(s, PROP_DEFINE_SEL);
  }

  /**
   * writeModel
   * @param out
   *          the out stream
   *
   * @see builder.models.WidgetModel#writeModel(java.io.ObjectOutputStream)
   */
  @Override
  public void writeModel(ObjectOutputStream out) 
      throws IOException {
    super.writeModel(out);
    out.writeObject((String)CommonUtil.getInstance().encodeToString(image));
    out.writeObject((String)CommonUtil.getInstance().encodeToString(imageSelected));
  }
  
  /**
   * readModel() will deserialize our model's data from a string object for backup
   * and recovery.
   *
   * @param in
   *          the in
   * @param widgetType
   *          the widget type
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws ClassNotFoundException
   *           the class not found exception
   * @see builder.models.WidgetModel#readModel(java.io.ObjectInputStream, java.lang.String)
   */
  @Override
  public void readModel(ObjectInputStream in, String widgetType) 
      throws IOException, ClassNotFoundException {
    super.readModel(in,  widgetType);
    String imageString = (String)in.readObject();
    image = CommonUtil.getInstance().decodeToImage(imageString);
    String imageSelectedString = (String)in.readObject();
    imageSelected = CommonUtil.getInstance().decodeToImage(imageSelectedString);
    if (isChangePageFunct()) {
      data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.FALSE;
    } else {
      data[PROP_PAGE][PROP_VAL_READONLY]=Boolean.TRUE;
    }
  }     

}
