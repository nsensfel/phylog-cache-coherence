package coproswi;

public class CodeBuilder
{
   private static final String tab = "   ";
   private static final String newline = System.lineSeparator();

   private final StringBuilder string_builder;
   private int code_depth;

   public CodeBuilder ()
   {
      code_depth = 0;
      string_builder = new StringBuilder();
   }

   public CodeBuilder (final int initial_depth)
   {
      code_depth = initial_depth;
      string_builder = new StringBuilder();
   }

   public void append (final String s)
   {
      string_builder.append(s);
   }

   public void append (final int i)
   {
      string_builder.append(i);
   }

   public void new_line ()
   {
      string_builder.append(newline);

      for (int i = 0; i < code_depth; i++)
      {
         string_builder.append(tab);
      }
   }

   public void increment_depth ()
   {
      code_depth++;
   }

   public void decrement_depth ()
   {
      code_depth--;
   }

   @Override
   public String toString ()
   {
      return string_builder.toString();
   }

   public int indexOf (final String pattern, final int start_point)
   {
      return string_builder.indexOf(pattern, start_point);
   }

   public void replace
   (
      final int start_point,
      final int end_point,
      final String replacement
   )
   {
      string_builder.replace(start_point, end_point, replacement);
   }
}
