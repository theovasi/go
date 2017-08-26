import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by theovac on 23/5/2017.
 */
public class Controller {
    int [][] gameState;
    public Controller() {
    }

    public static void main(String args[]) {
        Controller controller = new Controller();
        GoUI ui = new GoUI(9);
        GoRules rules = new GoRules();
        GoAI ai = new GoAI(controller, 2, 2);
        controller.gameState = ui.getGameState();
        /*controller.gameState[0][0] = 1;
        controller.gameState[0][1] = 2;
        controller.gameState[0][2] = 0;
        controller.gameState[1][0] = 2;
        controller.gameState[1][1] = 0;
        controller.gameState[1][2] = 0;
        controller.gameState[2][0] = 1;
        controller.gameState[2][1] = 1;
        controller.gameState[2][2] = 2;
        controller.gameState[4][4] = 1;
        controller.gameState[3][4] = 2;
        controller.gameState[5][4] = 1;
        controller.gameState[5][5] = 2;
        controller.gameState[6][4] = 2;
        controller.gameState[5][3] = 2;
        controller.gameState[4][3] = 2;*/

        ui.setGameState(controller.gameState);
        rules.stoneCapture(controller.gameState);
        ui.setGameState(controller.gameState);

        MonteCarlo mcts = new MonteCarlo(1);
        /*if (!moves.isEmpty()) {
            for (MonteCarlo.Move move : moves) {
                if (move != null) {
                    System.out.println("Move: " + move.pos.getRow() + ", " + move.pos.getCol() + " - " + move.priority);
                } else System.out.println("pass");
                controller.gameState[move.pos.getRow()][move.pos.getCol()] = 2;
                rules.stoneCapture(controller.gameState);
                ui.setGameState(controller.gameState);
            }
        }*/
        int[][] nextState = new int[controller.gameState.length][controller.gameState.length];
        Node bestNode;
        Node childNode;
        while(true) {
            ui.getTurn(1);
            rules.stoneCapture(controller.gameState);
            ui.setGameState(controller.gameState);
            Node node = new Node(controller.gameState, 2);
            List<Node> children = new ArrayList<>();
            List<MonteCarlo.Move> moves = mcts.generate_move(node);
            for (MonteCarlo.Move move : moves) {
                System.out.println(move.pos.getRow() + ", " + move.pos.getCol());
                if (move != null) {
                    for (int i = 0; i < controller.gameState.length; i++) {
                        for (int j = 0; j < controller.gameState.length; j++) {
                            nextState[i][j] = controller.gameState[i][j];
                        }
                    }
                    nextState[move.pos.getRow()][move.pos.getCol()] = 2;
                    childNode = new Node(nextState, 1);
                    childNode.move = move;
                    childNode.setParent(node);
                    children.add(childNode);
                }
            }
            node.setChildren(children);
            bestNode = children.get(0);
            for (Node child : node.children) {
                for (int i = 0; i < 3; i++) {
                    System.out.println("Iteration " + i);
                    if (mcts.simulatePlayout(child)) {
                        child.addWin();
                    } else child.addLoss();
                }
                if (child.winrate > bestNode.winrate) bestNode = child;
            }
            controller.gameState[bestNode.move.pos.getRow()][bestNode.move.pos.getCol()] = 2;
            System.out.println("Computer played");
            rules.stoneCapture(controller.gameState);
            ui.setGameState(controller.gameState);
        }
    }
}