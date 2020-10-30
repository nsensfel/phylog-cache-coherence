parser grammar LangParser;

options
{
   tokenVocab = LangLexer;
}

@header
{
   package coproswi;

   import java.util.ArrayList;
   import java.util.List;
}

@members
{
   Protocol PROTOCOL;
   StateComponent CACHE_CONTROLLER;
   StateComponent COHERENCY_MANAGER;
   /* of the class */
}

lang_file
   returns [Protocol result]

   @init
   {
      PROTOCOL = new Protocol();
      CACHE_CONTROLLER = PROTOCOL.get_cache_controller();
      COHERENCY_MANAGER = PROTOCOL.get_coherency_manager();
   }:

   (data_and_query_declaration)+
   (
      (
         (L_PAREN DEFINE_CC_KW component_def[CACHE_CONTROLLER] R_PAREN)
         (L_PAREN DEFINE_CMGR_KW component_def[COHERENCY_MANAGER] R_PAREN)
      )
      |
      (
         (L_PAREN DEFINE_CMGR_KW component_def[COHERENCY_MANAGER] R_PAREN)
         (L_PAREN DEFINE_CC_KW component_def[CACHE_CONTROLLER] R_PAREN)
      )
   )
   {
      $result = PROTOCOL;
   }
;

data_and_query_declaration:
   L_PAREN ADD_DATA_TYPE_KW name=ID R_PAREN
   {
      final Location loc;

      loc = new Location(($name.getLine()), ($name.getCharPositionInLine()));

      if (PROTOCOL.add_event(loc, ($name.text), Event.CATEGORY.DATA))
      {
         System.err.println
         (
            "[W] Data type \""
            + ($name.text)
            + "\" is declared again at line "
            + loc.line
            + ", column "
            + loc.column
            + "."
         );
      }
   }

   | L_PAREN ADD_QUERY_TYPE_KW name=ID R_PAREN
   {
      final Location loc;

      loc = new Location(($name.getLine()), ($name.getCharPositionInLine()));

      if (PROTOCOL.add_event(loc, ($name.text), Event.CATEGORY.QUERY))
      {
         System.err.println
         (
            "[W] Query type \""
            + ($name.text)
            + "\" is declared again at line "
            + loc.line
            + ", column "
            + loc.column
            + "."
         );
      }
   }
   {
   }
;

component_def [StateComponent SC]:

   (state_declaration[SC])+
   (actions_of_state_definition[SC])+
   {
   }
;

state_declaration [StateComponent SC]:
   L_PAREN ADD_STATE_KW STABLE_KW name=ID R_PAREN
   {
      final Location loc;

      loc = new Location(($name.getLine()), ($name.getCharPositionInLine()));

      if (SC.add_state(loc, ($name.text), State.CATEGORY.STABLE))
      {
         System.err.println
         (
            "[W] State \""
            + ($name.text)
            + "\" is declared again at line "
            + loc.line
            + ", column "
            + loc.column
            + "."
         );
      }
   }

   | L_PAREN ADD_STATE_KW TRANSIENT_KW name=ID R_PAREN
   {
      final Location loc;

      loc = new Location(($name.getLine()), ($name.getCharPositionInLine()));

      if (SC.add_state(loc, ($name.text), State.CATEGORY.TRANSIENT))
      {
         System.err.println
         (
            "[W] State \""
            + ($name.text)
            + "\" is declared again at line "
            + loc.line
            + ", column "
            + loc.column
            + "."
         );
      }
   }

   | L_PAREN SET_DEFAULT_STATE_KW name=ID R_PAREN
   {
      final State s;

      s = SC.get_state(($name.text));

      if (s == null)
      {
         System.err.println
         (
            "[F] Using an undeclared state as default (\""
            + ($name.text)
            + "\") at line "
            + ($name.getLine())
            + ", column "
            + ($name.getCharPositionInLine())
            + "."
         );
      }

      if (SC.set_default_state(s))
      {
         System.err.println
         (
            "[W] Multiple default states selected. Only the latest, \""
            + ($name.text)
            + "\" at line "
            + ($name.getLine())
            + " (column "
            + ($name.getCharPositionInLine())
            + ") will be considered."
         );
      }
   }
;

actions_of_state_definition [StateComponent SC]
   @init
   {
      State STATE = null;
   }
   :

   L_PAREN
      name=ID
      {
         STATE = SC.get_state(($name.text));

         if (STATE == null)
         {
            System.err.println
            (
               "[F] Defining actions for undeclared state \""
               + ($name.text)
               + "\" at line "
               + ($name.getLine())
               + ", column "
               + ($name.getCharPositionInLine())
               + "."
            );

            System.exit(-1);
         }
      }
      (event_handling_definition[STATE, SC])+
   R_PAREN
   {
   }
;

event_handling_definition [State STATE, StateComponent SC]:
   cmd_start=L_PAREN
      BUS_ACCESS_KW
      actions=actions_list[SC]
   R_PAREN
   {
      try
      {
         STATE.set_actions(PROTOCOL.get_event("bus_access"), ($actions.result));
      }
      catch (final Exception e)
      {
         System.err.println
         (
            "[F] A command starting at line "
            + ($cmd_start.getLine())
            + " (column "
            +  ($cmd_start.getCharPositionInLine())
            + ") raised the following exception:"
         );
         e.printStackTrace();

         System.exit(-1);
      }
   }

   |
   cmd_start=L_PAREN
      event=ID
      actions=actions_list[SC]
   R_PAREN
   {
      final Event e;

      e = PROTOCOL.get_event(($event.text));

      if (e == null)
      {
         System.err.println
         (
            "[F] Unknown event at "
            + ($event.getLine())
            + " (column "
            +  ($event.getCharPositionInLine())
            + ") raised the following exception:"
         );
      }

      try
      {
         STATE.set_actions(e, ($actions.result));
      }
      catch (final Exception ex)
      {
         System.err.println
         (
            "[F] A command starting at line "
            + ($cmd_start.getLine())
            + " (column "
            +  ($cmd_start.getCharPositionInLine())
            + ") raised the following exception:"
         );
         ex.printStackTrace();

         System.exit(-1);
      }
   }

;

actions_list [StateComponent SC]
   returns [List<Action> result]

   @init
   {
      $result = new ArrayList<Action>();
   }
   :
   (action[SC] {($result).add(($action.result));})+ {}
;

action [StateComponent SC]
   returns [Action result]:

   L_PAREN act=SUCCEED_KW R_PAREN
   {
      final Location loc;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));

      $result = new Action.Succeed(loc);
   }

   | L_PAREN act=HIT_KW cmd=ID R_PAREN
   {
      final Location loc;
      final Event e;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));
      e = PROTOCOL.get_event(($cmd.text));

      if ((e == null) || !(e.get_category() == Event.CATEGORY.REQUEST))
      {
         System.err.println
         (
            "[F] The event at line "
            + ($cmd.getLine())
            + " (column "
            + ($cmd.getCharPositionInLine())
            + ") has to be a core request."
         );

         System.exit(-1);
      }

      $result = new Action.Complete(loc, e);
   }

   | L_PAREN act=SEND_QUERY_KW cmd=ID R_PAREN
   {
      final Location loc;
      final Event e;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));
      e = PROTOCOL.get_event(($cmd.text));

      if ((e == null) || (e.get_category() != Event.CATEGORY.QUERY))
      {
         System.err.println
         (
            "[F] The event at line "
            + ($cmd.getLine())
            + " (column "
            + ($cmd.getCharPositionInLine())
            + ") has to be a query. Are you sure it is declared as such?"
         );

         System.exit(-1);
      }

      $result = new Action.SendQuery(loc, e);
   }

   | L_PAREN act=SET_STATE_KW cmd=ID R_PAREN
   {
      final State s;
      final Location loc;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));

      s = SC.get_state(($cmd.text));

      if (s == null)
      {
         System.err.println
         (
            "[F] Undeclared state at line "
            + ($cmd.getLine())
            + ", column "
            + ($cmd.getCharPositionInLine())
            + "."
         );

         System.exit(-1);
      }

      $result = new Action.SetState(loc, s);
   }

   | L_PAREN act=MARK_INTERFERENCE_KW MINOR_INTERFERENCE_KW R_PAREN
   {
      final Location loc;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));

      $result = new Action.MarkInterference(loc, Interference.MINOR);
   }

   | L_PAREN act=MARK_INTERFERENCE_KW EXPELLING_INTERFERENCE_KW R_PAREN
   {
      final Location loc;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));

      $result = new Action.MarkInterference(loc, Interference.EXPELLING);
   }

   | L_PAREN act=MARK_INTERFERENCE_KW DEMOTING_INTERFERENCE_KW R_PAREN
   {
      final Location loc;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));

      $result = new Action.MarkInterference(loc, Interference.DEMOTING);
   }

   | L_PAREN act=IGNORE_KW R_PAREN
   {
      final Location loc;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));

      $result = new Action.Ignore(loc);
   }

   | L_PAREN act=STALL_KW R_PAREN
   {
      final Location loc;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));

      $result = new Action.Stall(loc);
   }

   | L_PAREN act=RESUME_KW R_PAREN
   {
      final Location loc;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));

      $result = new Action.Resume(loc);
   }

   | L_PAREN act=NONE_KW R_PAREN
   {
      final Location loc;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));

      $result = new Action.None(loc);
   }

   | L_PAREN act=STORE_REPLY_TO_KW R_PAREN
   {
      final Location loc;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));

      $result = new Action.StoreReplyTo(loc);
   }

   | L_PAREN act=RESET_REPLY_TO_KW R_PAREN
   {
      final Location loc;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));

      $result = new Action.ResetReplyTo(loc);
   }

   | L_PAREN act=WRITE_DATA_KW R_PAREN
   {
      final Location loc;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));

      $result = new Action.WriteData(loc);
   }

   | L_PAREN act=READ_DATA_KW R_PAREN
   {
      final Location loc;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));

      $result = new Action.ReadData(loc);
   }

   | L_PAREN act=REPLY_TO_KW ERL_BANG_KW data_type=ID R_PAREN
   {
      final Location loc;
      final Event e;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));
      e = PROTOCOL.get_event($data_type.text);

      if ((e == null) || (e.get_category() != Event.CATEGORY.DATA))
      {
         System.err.println
         (
            "[F] Undeclared data type at line "
            + ($data_type.getLine())
            + ", column "
            + ($data_type.getCharPositionInLine())
            + "."
         );

         System.exit(-1);
      }

      $result = new Action.SendDataToReplyTo(loc, e);
   }

   | L_PAREN act=SENDER_KW ERL_BANG_KW data_type=ID R_PAREN
   {
      final Location loc;
      final Event e;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));

      e = PROTOCOL.get_event(($data_type.text));

      if ((e == null) || (e.get_category() != Event.CATEGORY.DATA))
      {
         System.err.println
         (
            "[F] Undeclared data type at line "
            + ($data_type.getLine())
            + ", column "
            + ($data_type.getCharPositionInLine())
            + "."
         );

         System.exit(-1);
      }

      $result = new Action.SendDataToSender(loc, e);
   }

   | L_PAREN act=MEMORY_KW ERL_BANG_KW data_type=ID R_PAREN
   {
      final Location loc;
      final Event e;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));

      e = PROTOCOL.get_event($data_type.text);

      if ((e == null) || (e.get_category() != Event.CATEGORY.DATA))
      {
         System.err.println
         (
            "[F] Undeclared data type at line "
            + ($data_type.getLine())
            + ", column "
            + ($data_type.getCharPositionInLine())
            + "."
         );

         System.exit(-1);
      }

      $result = new Action.SendDataToMemory(loc, e);
   }

   | act=L_PAREN seq=actions_list[SC] R_PAREN
   {
      final Location loc;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));

      $result = new Action.Sequence(loc, ($seq.result));
   }

   | L_PAREN act=IF_IS_OWNER_KW p0=action[SC] p1=action[SC] R_PAREN
   {
      final Location loc;

      loc = new Location(($act.getLine()), ($act.getCharPositionInLine()));

      $result = new Action.IfIsOwner(loc, ($p0.result), ($p1.result));
   }
;

