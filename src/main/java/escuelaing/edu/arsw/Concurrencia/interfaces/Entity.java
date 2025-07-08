package escuelaing.edu.arsw.Concurrencia.interfaces;

import escuelaing.edu.arsw.Concurrencia.MatrixSimulation;
import escuelaing.edu.arsw.Concurrencia.board.Position;

public abstract class Entity implements Runnable {
    protected Position pos;

    public Entity(Position pos) {
        this.pos = pos;
    }

    protected java.util.List<Position> getNeighbors(Position p, Position goal, boolean isNeo) {
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        java.util.List<Position> neighbors = new java.util.ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int nx = p.x + dx[i];
            int ny = p.y + dy[i];
            // Permitir moverse a la posición de Neo (goal)
            if (MatrixSimulation.isFree(nx, ny, isNeo) || (goal != null && nx == goal.x && ny == goal.y)) {
                neighbors.add(new Position(nx, ny));
            }
        }
        return neighbors;
    }

    protected Position bfs(Position start, Position goal, boolean isNeo) {
        java.util.Queue<Position> queue = new java.util.LinkedList<>();
        java.util.Map<Position, Position> cameFrom = new java.util.HashMap<>();
        queue.add(start);
        cameFrom.put(start, null);

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            if (current.x == goal.x && current.y == goal.y) break;

            for (Position neighbor : getNeighbors(current, goal, isNeo)) {
                if (!cameFrom.containsKey(neighbor)) {
                    queue.add(neighbor);
                    cameFrom.put(neighbor, current);
                }
            }
        }

        if (!cameFrom.containsKey(goal)) return null;

        Position current = goal;
        while (cameFrom.containsKey(current) && cameFrom.get(current) != start) {
            current = cameFrom.get(current);
        }
        return current.equals(start) ? null : current;
    }
}
