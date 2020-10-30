package coproswi;

public class Event
{
   public static enum CATEGORY { BUS_ACCESS, REQUEST, DATA, QUERY };

   private final Location declaration_location;
   private final String name;
   private final CATEGORY category;

   public Event
   (
      final Location declaration_location,
      final String name,
      final CATEGORY category
   )
   {
      this.declaration_location = declaration_location;
      this.name = name;
      this.category = category;
   }

   public CATEGORY get_category ()
   {
      return category;
   }

   public String get_name ()
   {
      return name;
   }

   @Override
   public boolean equals (final Object o)
   {
      if (o instanceof Event)
      {
         final Event b;

         b = (Event) o;

         return (b.name.equals(name));
      }

      return false;
   }

   @Override
   public int hashCode ()
   {
      return name.hashCode();
   }

   @Override
   public String toString ()
   {
      return name;
   }
}
