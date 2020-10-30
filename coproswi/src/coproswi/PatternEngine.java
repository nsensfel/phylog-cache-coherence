package coproswi;

import java.util.Map;
import java.util.HashMap;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class PatternEngine
{
   private final Map<String, String> replacements;

   public PatternEngine ()
   {
      replacements = new HashMap<String, String>();
   }

   public void add_pattern (final String pattern, final String replacement)
   {
      replacements.put(pattern, replacement);
   }

   private void load_file (final String filename, final CodeBuilder cb)
   throws IOException
   {
      final BufferedReader br;
      String buffer;

      br = new BufferedReader(new FileReader(filename));

      while ((buffer = br.readLine()) != null)
      {
         cb.append(buffer);
         cb.new_line();
      }

      br.close();
   }

   private void write_file (final CodeBuilder cb, final String filename)
   throws IOException
   {
      final FileWriter fw;

      fw = new FileWriter(filename);

      fw.write(cb.toString());

      fw.close();
   }

   private void apply_patterns (final CodeBuilder cb)
   {
      for (final Map.Entry<String, String> entry: replacements.entrySet())
      {
         final String key, open_pattern, close_pattern, replacement;
         int start_point, end_point, close_pattern_length, replacement_length;

         key = entry.getKey();
         open_pattern = "/*[" + key + "]*/";
         close_pattern = "/*[/" + key + "]*/";
         close_pattern_length = close_pattern.length();
         replacement = open_pattern + entry.getValue() + close_pattern;
         replacement_length = replacement.length();

         start_point = 0;

         for (;;)
         {
            start_point = cb.indexOf(open_pattern, start_point);

            if (start_point == -1)
            {
               break;
            }

            end_point = cb.indexOf(close_pattern, start_point);

            cb.replace
            (
               start_point,
               (end_point + close_pattern_length),
               replacement
            );

            start_point += replacement_length;
         }
      }
   }

   public void apply_to_file (final String filename)
   throws IOException
   {
      final CodeBuilder cb;

      cb = new CodeBuilder();

      load_file(filename, cb);
      apply_patterns(cb);
      write_file(cb, filename);
   }
}
