package coproswi;

import java.util.Map;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;

public class StateComponent
{
   public static enum CATEGORY { CACHE_CONTROLLER, COHERENCY_MANAGER };

   private final Map<String, State> states;
   private final CATEGORY category;
   private State default_state;

   public StateComponent (final CATEGORY category)
   {
      this.category = category;
      states = new HashMap<String, State>();
      default_state = null;
   }

   public CATEGORY get_category ()
   {
      return category;
   }

   public Collection<State> get_states ()
   {
      return states.values();
   }

   public boolean add_state
   (
      final Location loc,
      final String name,
      final State.CATEGORY category
   )
   {
      final boolean result;

      result = (states.get(name) != null);

      states.put(name, new State(loc, name, category));

      return result;
   }

   public State get_state (final String name)
   {
      return states.get(name);
   }

   public boolean set_default_state (final State s)
   {
      final boolean result;

      result = (default_state != null);

      default_state = s;

      return result;
   }

   public int get_states_count ()
   {
      return states.size();
   }

   public String get_states_declaration (final String state_type_name)
   {
      final CodeBuilder cb;
      int index;

      cb = new CodeBuilder();
      index = 0;

      for (final String state_name: states.keySet())
      {
         cb.new_line();
         cb.append("const ");
         cb.append(state_type_name);
         cb.append(" ");
         cb.append(state_name);
         cb.append(" = ");
         cb.append(index);
         cb.append(";");

         index += 1;
      }
      cb.new_line();

      return cb.toString();
   }

   public State get_default_state ()
   {
      return default_state;
   }

   public void add_handle_function_content_for
   (
      final Event e,
      final String state_var_name,
      final CodeBuilder cb
   )
   {
      boolean isnt_first_choice;

      isnt_first_choice = false;

      for (final State state: states.values())
      {
         final List<Action> actions;

         actions = state.get_actions(e);

         if (!actions.stream().anyMatch(act -> act.has_an_effect()))
         {
            continue;
         }

         cb.new_line();

         if (isnt_first_choice)
         {
            cb.append("else ");
         }
         else
         {
            isnt_first_choice = true;
         }

         cb.append("if (");
         cb.append(state_var_name);
         cb.append(" == ");
         cb.append(state.get_name());
         cb.append(")");
         cb.new_line();
         cb.append("{");
         cb.increment_depth();

         for (final Action act: actions)
         {
            act.append_to(cb);
         }

         cb.decrement_depth();
         cb.new_line();
         cb.append("}");
      }
   }

   public String get_unstalls_request_functions
   (
      final Collection<Event> request_events,
      final String state_var_name
   )
   {
      final CodeBuilder cb;

      cb = new CodeBuilder();

      for (final Event e: request_events)
      {
         cb.new_line();
         cb.append("bool unstalls_");
         cb.append(e.get_name());
         cb.append(" ()");
         cb.new_line();
         cb.append("{");
         cb.increment_depth();

         cb.new_line();
         cb.append("return");

         cb.increment_depth();
         cb.new_line();
         cb.append("(");

         cb.increment_depth();
         cb.new_line();
         cb.append("FALSE");

         add_unstalls_request(e, state_var_name, cb);

         cb.decrement_depth();
         cb.new_line();
         cb.append(");");

         cb.decrement_depth();
         cb.decrement_depth();
         cb.new_line();
         cb.append("}");
      }

      return cb.toString();
   }

   private void add_unstalls_request
   (
      final Event request_event,
      final String state_var_name,
      final CodeBuilder cb
   )
   {
      for (final State s: states.values())
      {
         if (!((s.get_actions(request_event).get(0)) instanceof Action.Stall))
         {
            cb.new_line();
            cb.append("|| relevant_line_is(");
            cb.append(s.get_name());
            cb.append(")");
         }
      }
   }
}
