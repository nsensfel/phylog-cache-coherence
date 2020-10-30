package coproswi;

public class Location
{
   public static final Location NOWHERE = new Location(-1, -1);

   public final int line;
   public final int column;

   public Location (final int line, final int column)
   {
      this.line = line;
      this.column = column;
   }
}
