package coproswi;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

public class TreePaths
{
   private TreePaths () {}

   public static void print
   (
      final Collection<Event> events,
      final Collection<State> states,
      final StringBuilder sb
   )
   {
      final Collection<State> stable_states;

      stable_states =
         states.stream().filter
         (
            e -> (e.get_category() == State.CATEGORY.STABLE)
         ).collect(Collectors.toList());

      print_paths(stable_states, events, sb);
   }

   private static void search_step
   (
      final PathNode origin,
      final Collection<Event> events,
      final Collection<PathNode> current_paths,
      final Collection<PathNode> results
   )
   {
      for (final Event event: events)
      {
         final List<Action> actions;
         State destination;

         destination = null;
         actions = origin.destination.get_actions(event);

         for (final Action action: actions)
         {
            if (action instanceof Action.SetState)
            {
               destination = ((Action.SetState) action).get_state();

               break;
            }
         }

         if (destination != null)
         {
            final PathNode new_node = new PathNode(origin, event, destination);

            if (destination.get_category() == State.CATEGORY.STABLE)
            {
               results.add(new_node);
            }
            else
            {
               current_paths.add(new_node);
            }
         }
      }
   }

   private static void print_paths
   (
      final Collection<State> stable_states,
      final Collection<Event> events,
      final StringBuilder sb
   )
   {
      final Collection<PathNode> results;

      Collection<PathNode> prev_paths, current_paths;

      results = new ArrayList<PathNode>();
      current_paths = new ArrayList<PathNode>();

      for (final State state: stable_states)
      {
         current_paths.add(new PathNode(null, null, state));
      }

      while (!current_paths.isEmpty())
      {
         prev_paths = current_paths;
         current_paths = new ArrayList<PathNode>();

         for (final PathNode node: prev_paths)
         {
            search_step(node, events, current_paths, results);
         }
      }

      for (final PathNode node: results)
      {
         node.print(sb);
         sb.append(System.lineSeparator());
      }
   }

   private static class PathNode
   {
      private final PathNode origin;
      private final Event event;
      private final State destination;

      public PathNode
      (
         final PathNode origin,
         final Event event,
         final State destination
      )
      {
         this.origin = origin;
         this.event = event;
         this.destination = destination;
      }

      public void print (final StringBuilder sb)
      {
         if (origin != null)
         {
            origin.print(sb);
            sb.append(".");
         }

         if (event != null)
         {
            sb.append(event.toString());
            sb.append("?.");
         }

         sb.append(destination.get_name());
      }
   }
}
