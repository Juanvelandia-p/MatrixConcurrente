package escuelaing.edu.arsw.Concurrencia.threads;

import java.util.concurrent.CyclicBarrier;

import escuelaing.edu.arsw.Concurrencia.MatrixSimulation;
import escuelaing.edu.arsw.Concurrencia.board.Position;
import escuelaing.edu.arsw.Concurrencia.interfaces.Entity;

public class Neo extends Entity {
    private CyclicBarrier barrier;
    private Position nextMove; // NUEVO

    public Neo(Position pos, CyclicBarrier barrier) {
        super(pos);
        this.barrier = barrier;
    }

    @Override
    public void run() {
        while (!MatrixSimulation.neoReachedPhone) {
            // Fase 1: decidir movimiento
            synchronized (MatrixSimulation.class) {
                Position phone = MatrixSimulation.phonePosition;
                nextMove = null;
                if (phone != null) {
                    Position next = bfs(pos, phone, true);
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
                    MatrixSimulation.moveNeo(pos, nextMove);
                    pos = nextMove;
                }
            }

            // Esperar a que todos muevan y el printer imprima
            try { barrier.await(); } catch (Exception e) { break; }
        }
    }

    private synchronized void move(Position next, char symbol) {
        if (MatrixSimulation.isFree(next.x, next.y,true)) {
            MatrixSimulation.map[pos.x][pos.y] = '-';
            pos = next;
            MatrixSimulation.map[pos.x][pos.y] = symbol;
        }
    }
}

