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

  /**
   * The internal image representation of this picture.
   */
  private final BufferedImage image;

  /**
   * Construct a new (blank) Picture object with the specified width and height.
   */
  public Picture(int width, int height) {
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
  }

  /**
   * Construct a new Picture from the image data in the specified file.
   */
  public Picture(String filepath) {
    try {
      image = ImageIO.read(new File(filepath));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected Picture invert() {
    Picture newPicture = new Picture(getWidth(), getHeight());
    final int maxIntensity = 255;
    forEachPixel("invert", getWidth(), getHeight(), newPicture, maxIntensity, null, null);
    return newPicture;
  }

  protected Picture grayscale() {
    Picture newPicture = new Picture(getWidth(), getHeight());
    forEachPixel("grayscale", getWidth(), getHeight(), newPicture, 0, null, null);
    return newPicture;
  }

  protected Picture rotate(int angle) {
    int rotateTimes = angle / 90;
    Picture newPicture = new Picture(getHeight(), getWidth());

    // Rotation by 90 degrees
    forEachPixel("rotate", getWidth(), getHeight(), newPicture, 0, null, null);

    if (rotateTimes == 1) {
      return newPicture;
    }

    return newPicture.rotate(angle - 90);
  }

  protected Picture flip(char direction) {
    Picture newPicture = new Picture(getWidth(), getHeight());
    forEachPixel("flip", getWidth(), getHeight(), newPicture, 0, direction, null);
    return newPicture;
  }

  protected Picture blend(List<Picture> pictures) {
    int minHeight = getHeight();
    int minWidth = getWidth();
    for (Picture picture : pictures) {
      if (minWidth > picture.getWidth()) {
        minWidth = picture.getWidth();
      }
      if (minHeight > picture.getHeight()) {
        minHeight = picture.getHeight();
      }
    }

    Picture newPicture = new Picture(minWidth, minHeight);
    forEachPixel("blend", minWidth, minHeight, newPicture, 0, null, pictures);

    return newPicture;
  }

  protected Picture blur() {
    Picture newPicture = new Picture(getWidth(), getHeight());
    forEachPixel("blur", getWidth(), getHeight(), newPicture, 0, null, null);
    return newPicture;
  }

  private void forEachPixel(String transformation, int width, int height, Picture newPicture, int maxIntensity, Character direction, List<Picture> pictures) {
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        switch (transformation) {
          case "invert": {
            Color pixel = getPixel(x, y);
            newPicture.setPixel(x, y, new Color(maxIntensity - pixel.getRed(), maxIntensity - pixel.getGreen(), maxIntensity - pixel.getBlue()));
            break;
          }
          case "grayscale": {
            Color pixel = getPixel(x, y);
            int average = (pixel.getRed() + pixel.getGreen() + pixel.getBlue()) / 3;
            newPicture.setPixel(x, y, new Color(average, average, average));
            break;
          }
          case "rotate": {
            Color pixel = getPixel(x, y);
            newPicture.setPixel(-y + getHeight() - 1, x, pixel);
            break;
          }
          case "flip": {
            Color pixel = getPixel(x, y);
            if (direction == 'H') {
              newPicture.setPixel(-x + getWidth() - 1, y, pixel);
            } else {
              newPicture.setPixel(x, -y + getHeight() - 1, pixel);
            }
            break;
          }
          case "blend":
            int red_total = getPixel(x, y).getRed();
            int blue_total = getPixel(x, y).getBlue();
            int green_total = getPixel(x, y).getGreen();

            for (Picture picture : pictures) {
              red_total += picture.getPixel(x, y).getRed();
              blue_total += picture.getPixel(x, y).getBlue();
              green_total += picture.getPixel(x, y).getGreen();
            }

            int averageRed = red_total / (pictures.size() + 1);
            int averageBlue = blue_total / (pictures.size() + 1);
            int averageGreen = green_total / (pictures.size() + 1);

            newPicture.setPixel(x, y, new Color(averageRed, averageGreen, averageBlue));
            break;
          case "blur":
            if (hasNeighbourhood(x, y)) {
              Color averagePixel = averageOfNeighbourhood(x, y);
              newPicture.setPixel(x, y, averagePixel);
            } else {
              newPicture.setPixel(x, y, getPixel(x, y));
            }
            break;
        }

      }
    }
  }

  private boolean hasNeighbourhood(int x, int y) {
    return contains(x - 1, y) && contains(x + 1, y) && contains(x, y + 1) && contains(x, y - 1);
  }

  private Color averageOfNeighbourhood(int x, int y) {
    int totalRed = 0, totalGreen = 0, totalBlue = 0;
    for (int i = x - 1; i <= x + 1; i++) {
      for (int j = y - 1; j <= y + 1; j++) {
        totalRed += getPixel(i, j).getRed();
        totalBlue += getPixel(i, j).getBlue();
        totalGreen += getPixel(i, j).getGreen();
      }
    }

    final int NEIGHBOURHOODPIXELS = 9;

    return new Color(totalRed / NEIGHBOURHOODPIXELS,
            totalGreen / NEIGHBOURHOODPIXELS,
            totalBlue / NEIGHBOURHOODPIXELS);
  }



  /**
   * Test if the specified point lies within the boundaries of this picture.
   *
   * @param x the x co-ordinate of the point
   * @param y the y co-ordinate of the point
   * @return <tt>true</tt> if the point lies within the boundaries of the picture, <tt>false</tt>
   * otherwise.
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
   *                                        the boundaries of this picture.
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
   * @param x   the x-coordinate of the pixel to be updated
   * @param y   the y-coordinate of the pixel to be updated
   * @param rgb the RGB components of the updated pixel-value
   * @throws ArrayIndexOutOfBoundsException if the specified pixel-location is not contained within
   *                                        the boundaries of this picture.
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

  /**
   * Returns a String representation of the RGB components of the picture.
   */
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
}
