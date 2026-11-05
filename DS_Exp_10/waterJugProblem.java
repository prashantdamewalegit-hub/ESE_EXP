import java.util.*;

public class waterJugProblem{
    static int capacity1=4;
    static int capacity2=3;
    static int target=2;
    static void printPath(Node node){
        if(node==null){
            return;
        }
        printPath(node.parent);
        System.out.println(node.state);
    }
    static List<State> getNextStates(State S){
        List<State> nextStates=new ArrayList<>();

        nextStates.add(new State(capacity1,S.jug2));
        nextStates.add(new State(S.jug1,capacity2));

        nextStates.add(new State(0,S.jug2));
        nextStates.add(new State(S.jug1,0));

        int pour=Math.min(capacity1-S.jug1,S.jug2);
        nextStates.add(new State(S.jug1+pour,S.jug2-pour));

        pour=Math.min(capacity2-S.jug2,S.jug1);
        nextStates.add(new State(S.jug1-pour,S.jug2+pour));


        return nextStates;
    }
    static void BFS(){
        System.out.println("BFS");
        Queue<Node> q=new LinkedList<>();
        Set<String> visited=new HashSet<>();
        Node start=new Node(new State(0,0),null);
        q.add(start);
        visited.add(start.state.key());
        System.out.println("Search Tree:");
        while(!q.isEmpty()){
            Node current=q.poll();
            State S=current.state;
            System.out.println("Visiting: " + S);
            if(S.jug1==target||S.jug2==target){
                System.out.println("Target reached: " + S);
                printPath(current);
                return;
            }
            List<State> nextStates=getNextStates(S);
            for(State nextState:nextStates){
                if(!visited.contains(nextState.key())){
                    visited.add(nextState.key());
                    q.add(new Node(nextState, current));
                    System.out.println(S+" -> "+nextState);
                }
               
            }
        }
        System.out.println("No Solution Found");
    }

    static boolean DFS(Node current, Set<String> visited){
        State s=current.state;
        System.out.println("Visiting "+s);
         if(s.jug1==target&&s.jug2==target){
                System.out.println("Target reached: " + s);
                printPath(current);
                return true;
            }

            List<State> nextStates=getNextStates(s);
            for(State nextState:nextStates){
                if(!visited.contains(nextState.key())){
                    visited.add(nextState.key());
                    System.out.println(s + " -> " + nextState);
                    Node node=new Node(nextState,current);

                    if(DFS(node,visited)) return true;
                }
            }
            return false;
    }


    
    public static void main(String[] args){
        BFS();
        System.out.println("DFS");
        Set<String> visited=new HashSet<>();
        Node start=new Node(new State(0,0),null);
        visited.add(start.state.key());
        if(!DFS(start,visited)){
            System.out.println("Solution not found");
        }
    }

}
class State{
    int jug1;
    int jug2;

    State(int jug1,int jug2){
        this.jug1=jug1;
        this.jug2=jug2;

    }
    String key(){
        return jug1+" , "+jug2;
    }
    public String toString(){
        return "("+jug1+","+jug2+")";

    }

}
class Node{
    State state;
    Node parent;
    Node(State state,Node parent){
        this.state=state;
        this.parent=parent;
    }
}
