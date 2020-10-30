package coproswi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.IOException;

public class Protocol
{
   private final Map<Event.CATEGORY, List<Event>> events_by_category;
   private final Map<String, Event> events;
   private final StateComponent cache_controller;
   private final StateComponent coherency_manager;

   public Protocol ()
   {
      events = new HashMap<String, Event>();
      events_by_category = new HashMap<Event.CATEGORY, List<Event>>();

      for (final Event.CATEGORY event_cat: Event.CATEGORY.values())
      {
         events_by_category.put(event_cat, new ArrayList<Event>());
      }

      cache_controller =
         new StateComponent(StateComponent.CATEGORY.CACHE_CONTROLLER);

      coherency_manager =
         new StateComponent(StateComponent.CATEGORY.COHERENCY_MANAGER);

      add_event(Location.NOWHERE, "bus_access", Event.CATEGORY.BUS_ACCESS);

      add_event(Location.NOWHERE, "CMD_CCTRL_LOAD", Event.CATEGORY.REQUEST);
      add_event(Location.NOWHERE, "CMD_CCTRL_STORE", Event.CATEGORY.REQUEST);
      add_event(Location.NOWHERE, "CMD_CCTRL_EVICT", Event.CATEGORY.REQUEST);
   }

   public StateComponent get_coherency_manager ()
   {
      return coherency_manager;
   }

   public StateComponent get_cache_controller ()
   {
      return cache_controller;
   }

   public boolean add_event
   (
      final Location loc,
      final String name,
      final Event.CATEGORY category
   )
   {
      final Event new_event;
      final boolean result;

      new_event = new Event(loc, name, category);
      result = (events.get(name) != null);

      events.put(name, new_event);
      events_by_category.get(category).add(new_event);

      return result;
   }

   public void add_post_parsing_additions ()
   {
      final Collection<State> cache_controller_states;

      cache_controller_states = cache_controller.get_states();

      for (final Event event: events_by_category.get(Event.CATEGORY.REQUEST))
      {
         for (final State state: cache_controller_states)
         {
            final List<Action> actions;

            actions = state.get_actions(event);

            if (actions == null)
            {
               System.err.println
               (
                  "[F] CACHE_CONTROLLER->"
                  + state.get_name()
                  + " does not have any actions defined for the \""
                  + event.get_name()
                  + "\" event."
               );

               System.exit(-1);
            }

            if
            (
               actions.stream().allMatch
               (
                  act ->
                     (
                        !(act instanceof Action.Succeed)
                        && !(act instanceof Action.Ignore)
                        && !(act instanceof Action.None)
                        && !(act instanceof Action.Stall)
                     )
               )
            )
            {
               actions.add(0, new Action.PropagateUseCount(Location.NOWHERE));
               actions.add(1, new Action.MarkAsCacheMiss(Location.NOWHERE));
               actions.add(2, new Action.Acted(Location.NOWHERE));
               actions.add
               (
                  3,
                  new Action.ClearInterferenceBy(Location.NOWHERE, event)
               );
            }
            else if
            (
               actions.stream().anyMatch
               (
                  act -> (act instanceof Action.Succeed)
               )
            )
            {
               actions.add(0, new Action.PropagateUseCount(Location.NOWHERE));
               actions.add
               (
                  1,
                  new Action.ClearInterferenceBy(Location.NOWHERE, event)
               );
            }
         }
      }
   }

   public Event get_event (final String name)
   {
      return events.get(name);
   }

   public Collection<Event> get_events ()
   {
      return events.values();
   }

   private String get_events_declaration ()
   {
      final CodeBuilder cb;
      int index;

      cb = new CodeBuilder();
      index = 0;


      for (final String event_name: events.keySet())
      {
         cb.new_line();
         cb.append("const cmd_id_t ");
         cb.append(event_name);
         cb.append(" = ");
         cb.append(index);
         cb.append(";");

         index += 1;
      }

      cb.new_line();

      return cb.toString();
   }

   public void apply_to_file (final String filename)
   throws IOException
   {
      final PatternEngine pe;

      pe = new PatternEngine();

      pe.add_pattern
      (
         "CC_STATES_COUNT",
         String.valueOf(cache_controller.get_states_count())
      );

      pe.add_pattern
      (
         "CC_STATES_DECLARATION",
         cache_controller.get_states_declaration("msi_state_t")
      );

      pe.add_pattern
      (
         "CC_DEFAULT_STATE",
          cache_controller.get_default_state().get_name()
      );

      pe.add_pattern
      (
         "CC_HANDLE_STALLED_REQUEST",
         generate_handle_functions
         (
            "",
            "stalled_",
            Event.CATEGORY.REQUEST,
            cache_controller
         )
      );

      pe.add_pattern
      (
         "CC_TEST_UNSTALLS_REQUEST",
         cache_controller.get_unstalls_request_functions
         (
            events_by_category.get(Event.CATEGORY.REQUEST),
            "meta_state"
         )
      );

      pe.add_pattern
      (
         "CC_TEST_UNSTALLS_REQUEST_SWITCH",
         get_test_unstalls_request_switch("cmd")
      );

      pe.add_pattern
      (
         "CC_UNSTALL_REQUEST_SWITCH",
         generate_switch
         (
            "cmd",
            "stalled_",
            "",
            Event.CATEGORY.REQUEST
         )
      );

      pe.add_pattern("CMD_CCTRL_EVICT", "CMD_CCTRL_EVICT");
      pe.add_pattern("CMD_CCTRL_STORE", "CMD_CCTRL_STORE");
      pe.add_pattern("CMD_CCTRL_READ", "CMD_CCTRL_READ");

      pe.add_pattern
      (
         "CC_HANDLE_BUS_ACCESS",
         generate_handle_functions
         (
            "",
            "",
            Event.CATEGORY.BUS_ACCESS,
            cache_controller
         )
      );

      pe.add_pattern
      (
         "CC_HANDLE_QUERY",
         generate_handle_functions
         (
            "",
            "",
            Event.CATEGORY.QUERY,
            cache_controller
         )
      );

      pe.add_pattern
      (
         "CC_HANDLE_BUS_ACCESS_SWITCH",
         "handle_bus_access();"
      );

      pe.add_pattern
      (
         "CC_HANDLE_QUERY_SWITCH",
         generate_switch
         (
            "req_in",
            "",
            "",
            Event.CATEGORY.QUERY
         )
      );

      pe.add_pattern
      (
         "CC_HANDLE_REQUEST",
         generate_handle_functions
         (
            "",
            "",
            Event.CATEGORY.REQUEST,
            cache_controller
         )
      );

      pe.add_pattern
      (
         "CC_HANDLE_REQUEST_SWITCH",
         generate_switch
         (
            "req_cmd",
            "",
            "",
            Event.CATEGORY.REQUEST
         )
      );

      pe.add_pattern
      (
         "CC_HANDLE_DATA",
         generate_handle_functions
         (
            "",
            "",
            Event.CATEGORY.DATA,
            cache_controller
         )
      );

      pe.add_pattern
      (
         "CC_HANDLE_DATA_SWITCH",
         generate_switch
         (
            "data_type",
            "",
            "",
            Event.CATEGORY.DATA
         )
      );

      pe.add_pattern
      (
         "EVENT_TYPE_COUNT",
         String.valueOf(events.size())
      );

      pe.add_pattern
      (
         "EVENTS_DECLARATION",
         get_events_declaration()
      );

      pe.add_pattern
      (
         "CMGR_STATES_COUNT",
         String.valueOf(coherency_manager.get_states_count())
      );

      pe.add_pattern
      (
         "CMGR_STATES_DECLARATION",
         coherency_manager.get_states_declaration("mem_state_t")
      );

      pe.add_pattern
      (
         "CMGR_DEFAULT_STATE",
         coherency_manager.get_default_state().get_name()
      );

      pe.add_pattern
      (
         "CMGR_HANDLE_QUERY_FUNCTIONS",
         generate_handle_functions
         (
            "",
            "",
            Event.CATEGORY.QUERY,
            coherency_manager
         )
      );

      pe.add_pattern
      (
         "CMGR_QUERY_SWITCH",
         generate_switch
         (
            "req_in",
            "",
            "",
            Event.CATEGORY.QUERY
         )
      );

      pe.add_pattern
      (
         "CMGR_HANDLE_DATA_FUNCTIONS",
         generate_handle_functions
         (
            "",
            "",
            Event.CATEGORY.DATA,
            coherency_manager
         )
      );

      pe.add_pattern
      (
         "CMGR_DATA_SWITCH",
         generate_switch
         (
            "req_in",
            "",
            "",
            Event.CATEGORY.DATA
         )
      );

      pe.apply_to_file(filename);
   }

   private String generate_switch
   (
      final String var_name,
      final String event_prefix,
      final String fun_args,
      final Event.CATEGORY category
   )
   {
      final CodeBuilder cb;
      boolean isnt_first_choice;

      cb = new CodeBuilder(1);
      isnt_first_choice = false;

      for (final Event e: events_by_category.get(category))
      {
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
         cb.append(var_name);
         cb.append(" == ");
         cb.append(e.get_name());
         cb.append(")");
         cb.new_line();
         cb.append("{");
         cb.increment_depth();
         cb.new_line();

         cb.append("handle_");
         cb.append(event_prefix);
         cb.append(e.get_name());
         cb.append("(");
         cb.append(fun_args);
         cb.append(");");

         cb.decrement_depth();
         cb.new_line();
         cb.append("}");
      }

      cb.new_line();

      return cb.toString();
   }

   private String generate_handle_functions
   (
      final String fun_args,
      final String event_prefix,
      final Event.CATEGORY category,
      final StateComponent sc
   )
   {
      final CodeBuilder cb;

      cb = new CodeBuilder();


      for (final Event e: events_by_category.get(category))
      {
         cb.new_line();

         cb.append("void handle_");
         cb.append(event_prefix);
         cb.append(e.get_name());
         cb.append(" (");
         cb.append(fun_args);
         cb.append(")");

         cb.new_line();
         cb.append("{");
         cb.increment_depth();

         sc.add_handle_function_content_for(e, "meta_state",  cb);

         cb.decrement_depth();
         cb.new_line();
         cb.append("}");
      }

      cb.new_line();

      return cb.toString();
   }

   private String get_test_unstalls_request_switch (final String cmd_var_name)
   {
      final CodeBuilder cb;

      cb = new CodeBuilder(3);

      cb.append("FALSE");

      for (final Event e: events_by_category.get(Event.CATEGORY.REQUEST))
      {
         cb.new_line();
         cb.append("|| ((");
         cb.append(cmd_var_name);
         cb.append(" == ");
         cb.append(e.get_name());
         cb.append(") &amp;&amp; (unstalls_");
         cb.append(e.get_name());
         cb.append("()))");
      }

      return cb.toString();
   }
}
