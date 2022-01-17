package picture;

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
    }

  }
}
