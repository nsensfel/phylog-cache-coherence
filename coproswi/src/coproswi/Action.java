package coproswi;

import java.util.List;

public class Action
{
   protected final Location declaration_location;

   protected Action (final Location declaration_location)
   {
      this.declaration_location = declaration_location;
   }

   protected void append_to (final CodeBuilder cb)
   {
      cb.new_line();
      cb.append("// Action from l.");
      cb.append(declaration_location.line);
      cb.append(" c. ");
      cb.append(declaration_location.column);
   }

   public static class Succeed extends Action
   {
      public Succeed (final Location declaration_location)
      {
         super(declaration_location);
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("action_succeed();");
      }
   }

   public static class Acted extends Action
   {
      public Acted (final Location declaration_location)
      {
         super(declaration_location);
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("action_acted();");
      }
   }

   public static class Ignore extends Action
   {
      public Ignore (final Location declaration_location)
      {
         super(declaration_location);
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("// (ignore)");
      }

      @Override
      public boolean has_an_effect ()
      {
         return false;
      }
   }

   public static class None extends Action
   {
      public None (final Location declaration_location)
      {
         super(declaration_location);
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("// (none)");
      }

      @Override
      public boolean has_an_effect ()
      {
         return false;
      }
   }

   public static class Stall extends Action
   {
      public Stall (final Location declaration_location)
      {
         super(declaration_location);
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("action_stall();");
      }
   }

   public static class PropagateUseCount extends Action
   {
      public PropagateUseCount (final Location declaration_location)
      {
         super(declaration_location);
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("action_propagate_use_count();");
      }
   }

   public static class MarkAsCacheMiss extends Action
   {
      public MarkAsCacheMiss (final Location declaration_location)
      {
         super(declaration_location);
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("action_mark_as_cache_miss();");
      }
   }

   public static class ClearInterferenceBy extends Action
   {
      final Event ev;

      private ClearInterferenceBy (final Location declaration_location)
      {
         super(declaration_location);

         ev = null;
      }

      public ClearInterferenceBy
      (
         final Location declaration_location,
         final Event ev
      )
      {
         super(declaration_location);
         this.ev = ev;
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("action_clear_interference_by(");
         cb.append(ev.toString());
         cb.append(");");
      }
   }

   public static class StoreReplyTo extends Action
   {
      public StoreReplyTo (final Location declaration_location)
      {
         super(declaration_location);
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("action_store_reply_to();");
      }
   }

   public static class ResetReplyTo extends Action
   {
      public ResetReplyTo (final Location declaration_location)
      {
         super(declaration_location);
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("action_reset_reply_to();");
      }
   }

   public static class Resume extends Action
   {
      public Resume (final Location declaration_location)
      {
         super(declaration_location);
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("action_resume();");
      }
   }

   public static class WriteData extends Action
   {
      public WriteData (final Location declaration_location)
      {
         super(declaration_location);
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("action_write_data();");
      }
   }

   public static class ReadData extends Action
   {
      public ReadData (final Location declaration_location)
      {
         super(declaration_location);
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("action_read_data();");
      }
   }

   public static class Complete extends Action
   {
      private final Event event;

      private Complete (final Location declaration_location)
      {
         super(declaration_location);
         event = null;
      }

      public Complete
      (
         final Location declaration_location,
         final Event event
      )
      {
         super(declaration_location);
         this.event = event;
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("action_complete(");
         cb.append(event.get_name());
         cb.append(");");
      }
   }

   public static class SendQuery extends Action
   {
      private final Event event;

      private SendQuery (final Location declaration_location)
      {
         super(declaration_location);
         event = null;
      }

      public SendQuery
      (
         final Location declaration_location,
         final Event event
      )
      {
         super(declaration_location);
         this.event = event;
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("action_send_query(");
         cb.append(event.get_name());
         cb.append(");");
      }
   }

   public static class MarkInterference extends Action
   {
      private final Interference interference;

      private MarkInterference (final Location declaration_location)
      {
         super(declaration_location);
         interference = null;
      }

      public MarkInterference
      (
         final Location declaration_location,
         final Interference interference
      )
      {
         super(declaration_location);
         this.interference = interference;
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("action_mark_interference(");
         cb.append(interference.get_name());
         cb.append(");");
      }
   }


   public static class SendDataToSender extends Action
   {
      private final Event event;

      private SendDataToSender (final Location declaration_location)
      {
         super(declaration_location);
         event = null;
      }

      public SendDataToSender
      (
         final Location declaration_location,
         final Event event
      )
      {
         super(declaration_location);
         this.event = event;
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("action_send_data_to_sender(");
         cb.append(event.get_name());
         cb.append(");");
      }
   }

   public static class SendDataToReplyTo extends Action
   {
      private final Event event;

      private SendDataToReplyTo (final Location declaration_location)
      {
         super(declaration_location);
         event = null;
      }

      public SendDataToReplyTo
      (
         final Location declaration_location,
         final Event event
      )
      {
         super(declaration_location);
         this.event = event;
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("action_send_data_to_reply_to(");
         cb.append(event.get_name());
         cb.append(");");
      }
   }

   public static class SendDataToMemory extends Action
   {
      private final Event event;

      private SendDataToMemory (final Location declaration_location)
      {
         super(declaration_location);
         event = null;
      }

      public SendDataToMemory
      (
         final Location declaration_location,
         final Event event
      )
      {
         super(declaration_location);
         this.event = event;
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("action_send_data_to_memory(");
         cb.append(event.get_name());
         cb.append(");");
      }
   }

   public static class SetState extends Action
   {
      private final State state;

      private SetState (final Location declaration_location)
      {
         super(declaration_location);
         state = null;
      }

      public SetState
      (
         final Location declaration_location,
         final State state
      )
      {
         super(declaration_location);
         this.state = state;
      }

      public State get_state ()
      {
         return state;
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("action_set_state(");
         cb.append(state.get_name());
         cb.append(");");
      }
   }

   public static class Sequence extends Action
   {
      private final List<Action> actions;

      private Sequence (final Location declaration_location)
      {
         super(declaration_location);
         actions = null;
      }

      public Sequence
      (
         final Location declaration_location,
         final List<Action> actions
      )
      {
         super(declaration_location);
         this.actions = actions;
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);

         cb.new_line();
         cb.append("// (sequence)");

         for (final Action a: actions)
         {
            a.append_to(cb);
         }
      }

      @Override
      public boolean has_an_effect ()
      {
         return actions.stream().anyMatch(act -> act.has_an_effect());
      }
   }

   public static class IfIsOwner extends Action
   {
      private final Action if_true, if_false;

      private IfIsOwner (final Location declaration_location)
      {
         super(declaration_location);
         if_true = null;
         if_false = null;
      }

      public IfIsOwner
      (
         final Location declaration_location,
         final Action if_true,
         final Action if_false
      )
      {
         super(declaration_location);
         this.if_true = if_true;
         this.if_false = if_false;
      }

      @Override
      public void append_to (final CodeBuilder cb)
      {
         super.append_to(cb);
         cb.new_line();
         cb.append("if (meta_is_owner)");
         cb.new_line();
         cb.append("{");
         cb.increment_depth();

         if_true.append_to(cb);

         cb.decrement_depth();
         cb.new_line();
         cb.append("}");
         cb.new_line();
         cb.append("else");
         cb.new_line();
         cb.append("{");
         cb.increment_depth();

         if_false.append_to(cb);

         cb.decrement_depth();
         cb.new_line();
         cb.append("}");
      }

      @Override
      public boolean has_an_effect ()
      {
         return (if_true.has_an_effect() || if_false.has_an_effect());
      }
   }

   public boolean has_an_effect ()
   {
      return true;
   }
}
