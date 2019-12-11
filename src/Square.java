public class Square {
  private int x;
  private int y;
  private Colour colour;

  public Square(int x, int y, Colour colour){
      this.x = x;
      this.y = y;
      this.colour = colour;
      assert (0 <= x && x <= 7);
      assert (0 <= y && y <= 7);
  }

  public int getX(){
      return x;
  }

  public char getXasChar(){
      return ( (char) (x + 'a'));
  }

  public int getY(){
      return y;
  }

  public Colour occupiedBy(){
      return colour;
  }

  public void setOccupier(Colour colour){
      this.colour = colour;
  }

  public String display() {
      return colour.toString();
  }

  public String toString() {
      StringBuffer squareInfo = new StringBuffer("");
      squareInfo.append( (char) (getX() + 'a') + " ");
      squareInfo.append(getY() + 1);
      return squareInfo.toString();
  }

  public Square clone(){
    return new Square(x,y,colour);
  }
}
