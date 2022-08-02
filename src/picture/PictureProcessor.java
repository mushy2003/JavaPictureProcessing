package picture;

import java.util.ArrayList;
import java.util.List;

public class PictureProcessor {

  public static void main(String[] args) {
    String transformation = args[0];

    if (transformation.equals("invert")) {
      Picture picture = new Picture(args[1]);
      picture.invert().saveAs(args[2]);
    } else if (transformation.equals("grayscale")) {
      Picture picture = new Picture(args[1]);
      picture.grayscale().saveAs(args[2]);
    } else if (transformation.equals("rotate")) {
      Picture picture = new Picture(args[2]);
      picture.rotate(Integer.parseInt(args[1])).saveAs(args[3]);
    } else if (transformation.equals("flip")) {
      Picture picture = new Picture(args[2]);
      picture.flip(args[1].charAt(0)).saveAs(args[3]);
    } else if (transformation.equals("blend")) {
      Picture picture = new Picture(args[1]);
      List<Picture> pictures = new ArrayList<>();
      for (int i = 2; i < args.length - 1; i++) {
        pictures.add(new Picture(args[i]));
      }
      picture.blend(pictures).saveAs(args[args.length - 1]);
    } else if (transformation.equals("blur")) {
      Picture picture = new Picture(args[1]);
      picture.blur().saveAs(args[2]);
    }
  }
}
