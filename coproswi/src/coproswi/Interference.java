package coproswi;

public enum Interference
{

   MINOR("INTERFERENCE_MINOR"),
   EXPELLING("INTERFERENCE_EXPELLING"),
   DEMOTING("INTERFERENCE_DEMOTING");


   private final String name;

   private Interference (final String name)
   {
      this.name = name;
   }

   public String get_name ()
   {
      return name;
   }
}
