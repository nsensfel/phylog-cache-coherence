package coproswi;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;

public class Main
{
   private Main () {}

   public static void main (final String[] args)
   throws IOException
   {
      final Parameters params;
      final Protocol protocol;
      final CommonTokenStream tokens;
      final LangLexer lexer;
      final LangParser parser;

      params = new Parameters(args);

      lexer =
         new LangLexer
         (
            CharStreams.fromFileName(params.get_protocol_filename())
         );

      tokens = new CommonTokenStream(lexer);
      parser = new LangParser(tokens);

      protocol = parser.lang_file().result;
      protocol.add_post_parsing_additions();

      if (params.should_modify_model())
      {
         protocol.apply_to_file(params.get_model_filename());
      }

      if (params.should_store_trees())
      {
         final StringBuilder sb;

         sb = new StringBuilder();

         TreePaths.print
         (
            protocol.get_events(),
            protocol.get_cache_controller().get_states(),
            sb
         );

         System.out.println(sb.toString());
      }
   }
}
