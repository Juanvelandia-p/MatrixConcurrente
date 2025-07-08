package escuelaing.edu.arsw.Concurrencia.threads;

import java.util.concurrent.CyclicBarrier;

import escuelaing.edu.arsw.Concurrencia.MatrixSimulation;
import escuelaing.edu.arsw.Concurrencia.board.Position;
import escuelaing.edu.arsw.Concurrencia.interfaces.Entity;

public class Agent extends Entity {
    private CyclicBarrier barrier;
    private Position nextMove; // NUEVO

    public Agent(Position pos, CyclicBarrier barrier) {
        super(pos);
        this.barrier = barrier;
    }

    @Override
    public void run() {
        while (!MatrixSimulation.neoReachedPhone) {
            // Fase 1: decidir movimiento
            synchronized (MatrixSimulation.class) {
                Position neoPos = findNeo();
                nextMove = null;
                if (neoPos != null) {
                    Position next = bfs(pos, neoPos, false);
                    if (next != null) {
                        nextMove = next;
                    }
                }
            }

            // Esperar a que todos decidan
            try { barrier.await(); } catch (Exception e) { break; }

            // Fase 2: ejecutar movimiento
            synchronized (MatrixSimulation.class) {
                if (nextMove != null) {
                    MatrixSimulation.moveAgent(pos, nextMove);
                    pos = nextMove;
                }
            }

            // Esperar a que todos muevan y el printer imprima
            try { barrier.await(); } catch (Exception e) { break; }
        }
    }

    private Position findNeo() {
        for (int i = 0; i < MatrixSimulation.HEIGHT; i++) {
            for (int j = 0; j < MatrixSimulation.WIDTH; j++) {
                if (MatrixSimulation.map[i][j] == 'N') {
                    return new Position(i, j);
                }
            }
        }
        return null; // Si no encuentra a Neo (muy raro si aún no lo han atrapado)
    }
}
