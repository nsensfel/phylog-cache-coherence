package coproswi;

public class Parameters
{
   private static void print_usage ()
   {
      System.err.println
      (
         "Expected arguments: <protocol_file> <option>*."
      );
      System.err.println("Available options:");
      System.err.println
      (
         "\t-t|--trees <file>\tOutputs paths between stable states."
      );
      System.err.println
      (
         "\t-m|--model <file>\tMakes <file> use the protocol."
      );
   }

/******************************************************************************/
/******************************************************************************/
/******************************************************************************/

   private final String model_template_filename;
   private final String trees_destination_filename;
   private final String protocol_filename;

   public Parameters (final String[] args)
   {
      String trees_destination_filename = null;
      String model_template_filename = null;

      switch (args.length)
      {
         case 0:
            System.err.println("[F] Invalid usage.");
            print_usage();
            System.exit(-1);
            break;

         case 1:
            System.err.println("[W] No command given.");
      }

      this.protocol_filename = args[0];

      for (int i = 1; i < args.length; ++i)
      {
         switch (args[i])
         {
            case "-t":
            case "--trees":
               i += 1;

               if (i >= args.length)
               {
                  System.err.println
                  (
                     "[F] Missing <file> for option \""
                     + args[i-1]
                     + "\"."
                  );
                  print_usage();
                  System.exit(-1);
               }

               trees_destination_filename = args[i];
               break;

            case "-m":
            case "--model":
               i += 1;

               if (i >= args.length)
               {
                  System.err.println
                  (
                     "[F] Missing <file> for option \""
                     + args[i-1]
                     + "\"."
                  );
                  print_usage();
                  System.exit(-1);
               }

               model_template_filename = args[i];
               break;

            default:
               System.err.println("[F] Unknown option \"" + args[i] + "\".");
               print_usage();
               System.exit(-1);
         }
      }

      this.trees_destination_filename = trees_destination_filename;
      this.model_template_filename = model_template_filename;
   }

   public boolean should_modify_model ()
   {
      return (model_template_filename != null);
   }

   public String get_model_filename ()
   {
      return model_template_filename;
   }

   public boolean should_store_trees ()
   {
      return (trees_destination_filename != null);
   }

   public String get_trees_destination_filename ()
   {
      return trees_destination_filename;
   }

   public String get_protocol_filename ()
   {
      return protocol_filename;
   }
}
