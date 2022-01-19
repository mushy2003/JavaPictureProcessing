package picture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A class that encapsulates and provides a simplified interface for manipulating an image. The
 * internal representation of the image is based on the RGB direct colour model.
 */
public class Picture {

  /** The internal image representation of this picture. */
  private final BufferedImage image;

  /** Construct a new (blank) Picture object with the specified width and height. */
  public Picture(int width, int height) {
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
  }

  /** Construct a new Picture from the image data in the specified file. */
  public Picture(String filepath) {
    try {
      image = ImageIO.read(new File(filepath));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Test if the specified point lies within the boundaries of this picture.
   *
   * @param x the x co-ordinate of the point
   * @param y the y co-ordinate of the point
   * @return <tt>true</tt> if the point lies within the boundaries of the picture, <tt>false</tt>
   *     otherwise.
   */
  public boolean contains(int x, int y) {
    return x >= 0 && y >= 0 && x < getWidth() && y < getHeight();
  }

  /**
   * Returns true if this Picture is graphically identical to the other one.
   *
   * @param other The other picture to compare to.
   * @return true iff this Picture is graphically identical to other.
   */
  @Override
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }
    if (!(other instanceof Picture)) {
      return false;
    }

    Picture otherPic = (Picture) other;

    if (image == null || otherPic.image == null) {
      return image == otherPic.image;
    }
    if (image.getWidth() != otherPic.image.getWidth()
        || image.getHeight() != otherPic.image.getHeight()) {
      return false;
    }

    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        if (image.getRGB(i, j) != otherPic.image.getRGB(i, j)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Return the height of the <tt>Picture</tt>.
   *
   * @return the height of this <tt>Picture</tt>.
   */
  public int getHeight() {
    return image.getHeight();
  }

  /**
   * Return the colour components (red, green, then blue) of the pixel-value located at (x,y).
   *
   * @param x x-coordinate of the pixel value to return
   * @param y y-coordinate of the pixel value to return
   * @return the RGB components of the pixel-value located at (x,y).
   * @throws ArrayIndexOutOfBoundsException if the specified pixel-location is not contained within
   *     the boundaries of this picture.
   */
  public Color getPixel(int x, int y) {
    int rgb = image.getRGB(x, y);
    return new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff);
  }

  /**
   * Return the width of the <tt>Picture</tt>.
   *
   * @return the width of this <tt>Picture</tt>.
   */
  public int getWidth() {
    return image.getWidth();
  }

  @Override
  public int hashCode() {
    if (image == null) {
      return -1;
    }
    int hashCode = 0;
    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        hashCode = 31 * hashCode + image.getRGB(i, j);
      }
    }
    return hashCode;
  }

  public void saveAs(String filepath) {
    try {
      ImageIO.write(image, "png", new File(filepath));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Update the pixel-value at the specified location.
   *
   * @param x the x-coordinate of the pixel to be updated
   * @param y the y-coordinate of the pixel to be updated
   * @param rgb the RGB components of the updated pixel-value
   * @throws ArrayIndexOutOfBoundsException if the specified pixel-location is not contained within
   *     the boundaries of this picture.
   */
  public void setPixel(int x, int y, Color rgb) {

    image.setRGB(
        x,
        y,
        0xff000000
            | (((0xff & rgb.getRed()) << 16)
                | ((0xff & rgb.getGreen()) << 8)
                | (0xff & rgb.getBlue())));
  }

  /** Returns a String representation of the RGB components of the picture. */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    for (int y = 0; y < getHeight(); y++) {
      for (int x = 0; x < getWidth(); x++) {
        Color rgb = getPixel(x, y);
        sb.append("(");
        sb.append(rgb.getRed());
        sb.append(",");
        sb.append(rgb.getGreen());
        sb.append(",");
        sb.append(rgb.getBlue());
        sb.append(")");
      }
      sb.append("\n");
    }
    sb.append("\n");
    return sb.toString();
  }

  public void invert() {
    for (int y = 0; y < getHeight(); y++) {
      for (int x = 0; x < getWidth(); x++) {
        Color pixel = getPixel(x, y);
        setPixel(
            x, y, new Color(255 - pixel.getRed(), 255 - pixel.getGreen(), 255 - pixel.getBlue()));
      }
    }
  }

  public void grayscale() {
    for (int y = 0; y < getHeight(); y++) {
      for (int x = 0; x < getWidth(); x++) {
        Color pixel = getPixel(x, y);
        int average = (pixel.getRed() + pixel.getBlue() + pixel.getGreen()) / 3;
        Color newColor = new Color(average, average, average);
        setPixel(x, y, newColor);
      }
    }
  }

  public Picture rotate(int angle) {
    int rotateTimes = angle / 90;
    Picture oldPic = this;
    Picture newPic = null;
    for (int t = 0; t < rotateTimes; t++) {
      newPic = new Picture(oldPic.getHeight(), oldPic.getWidth());
      for (int y = 0; y < oldPic.getHeight(); y++) {
        for (int x = 0; x < oldPic.getWidth(); x++) {
          Color pixel = oldPic.getPixel(x, y);
          newPic.setPixel(-y + oldPic.getHeight() - 1, x, pixel);
        }
      }
      oldPic = newPic;
    }
    return newPic;
  }

  public Picture flip(String direction) {
    Picture newPic = new Picture(this.getWidth(), this.getHeight());
    for (int y = 0; y < getHeight(); y++) {
      for (int x = 0; x < getWidth(); x++) {
        Color pixel = getPixel(x, y);
        if (direction.equals("H")) {
          newPic.setPixel(-x + getWidth() - 1, y, pixel);
        } else {
          newPic.setPixel(x, -y + getHeight() - 1, pixel);
        }
      }
    }
    return newPic;
  }

  public static Picture blend(Picture[] pics) {
    int minWidth = pics[0].getWidth();
    int minHeight = pics[0].getHeight();
    for (Picture pic : pics) {
      if (pic.getWidth() < minWidth) {
        minWidth = pic.getWidth();
      }
      if (pic.getHeight() < minHeight) {
        minHeight = pic.getHeight();
      }
    }

    final Picture blendedPic = new Picture(minWidth, minHeight);

    for (int y = 0; y < minHeight; y++) {
      for (int x = 0; x < minWidth; x++) {
        int sumOfRed = 0;
        int sumOfGreen = 0;
        int sumOfBlue = 0;
        for (Picture pic : pics) {
          Color pixel = pic.getPixel(x, y);
          sumOfRed += pixel.getRed();
          sumOfGreen += pixel.getGreen();
          sumOfBlue += pixel.getBlue();
        }
        int numOfPics = pics.length;
        blendedPic.setPixel(
            x, y, new Color(sumOfRed / numOfPics, sumOfGreen / numOfPics, sumOfBlue / numOfPics));
      }
    }

    return blendedPic;
  }

  public Picture blur() {
    Picture newPic = new Picture(getWidth(), getHeight());
    for (int y = 0; y < getHeight(); y++) {
      for (int x = 0; x < getWidth(); x++) {
        Color pixel = getPixel(x, y);
        if (contains(x - 1, y + 1)
            && contains(x - 1, y - 1)
            && contains(x + 1, y + 1)
            && contains(x + 1, y - 1)) {
          int redTotal = 0;
          int greenTotal = 0;
          int blueTotal = 0;
          for (int a = y - 1; a < y + 2; a++) {
            for (int b = x - 1; b < x + 2; b++) {
              redTotal += getPixel(b, a).getRed();
              greenTotal += getPixel(b, a).getGreen();
              blueTotal += getPixel(b, a).getBlue();
            }
          }
          int redAverage = redTotal / 9;
          int greenAverage = greenTotal / 9;
          int blueAverage = blueTotal / 9;
          newPic.setPixel(x, y, new Color(redAverage, greenAverage, blueAverage));
        } else {
          newPic.setPixel(x, y, pixel);
        }
      }
    }
    return newPic;
  }
}
