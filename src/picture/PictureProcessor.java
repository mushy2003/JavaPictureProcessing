package picture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PictureProcessor {

  public static void main(String[] args) {
    if (args[0] == "invert") {
      Picture pic = new Picture(args[1]);
      pic.invert();
      pic.saveAs(args[2]);
    } else if (args[0] == "grayscale") {
      Picture pic = new Picture(args[1]);
      pic.grayscale();
      pic.saveAs(args[2]);
    } else if (args[0] == "rotate") {
      Picture pic = new Picture(args[2]);
      Picture rotatedPic = pic.rotate(Integer.parseInt(args[1]));
      rotatedPic.saveAs(args[3]);
    } else if (args[0] == "flip") {
      Picture pic = new Picture(args[2]);
      Picture flipped = pic.flip(args[1]);
      flipped.saveAs(args[3]);
    } else if (args[0] == "blend") {
      //String[] ar = Arrays.copyOfRange(args, 1, args.length - 1);
      //Picture[] pics = Arrays.stream(ar).map(Picture::new).toArray(Picture[]::new);
      ArrayList<Picture> pics2 = new ArrayList<Picture>();
      for (int i = 1; i < (args.length - 1); i++) {
        pics2.add(new Picture(args[i]));
      }


      Picture blended = Picture.blend(pics2.toArray(new Picture[0]));
      blended.saveAs(args[args.length - 1]);


    } else if (args[0] == "blur") {
      Picture pic = new Picture(args[1]);
      Picture newPic = pic.blur();
      newPic.saveAs(args[2]);
    }

  }
}
