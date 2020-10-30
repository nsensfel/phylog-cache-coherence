package coproswi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State
{
   public static enum CATEGORY { STABLE, TRANSIENT };

   private final String name;
   private final Location declaration_location;
   private final Map<Event, List<Action>> actions;
   private final CATEGORY category;

   public State
   (
      final Location declaration_location,
      final String name,
      final CATEGORY category
   )
   {
      this.declaration_location = declaration_location;
      this.name = name;
      this.category = category;

      actions = new HashMap<Event, List<Action>>();
   }

   public CATEGORY get_category ()
   {
      return category;
   }

   public void set_actions (final Event e, final List<Action> actions)
   {
      this.actions.put(e, actions);
   }

   public List<Action> get_actions (final Event e)
   {
      return actions.get(e);
   }

   public String get_name ()
   {
      return name;
   }

   public void add_handle_function_content_for
   (
      final Event e,
      final CodeBuilder cb
   )
   {
      for (final Action a: actions.get(e))
      {
         a.append_to(cb);
      }
   }
}
