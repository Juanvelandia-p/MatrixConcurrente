package escuelaing.edu.arsw.Concurrencia;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;

import escuelaing.edu.arsw.Concurrencia.board.Position;
import escuelaing.edu.arsw.Concurrencia.threads.Agent;
import escuelaing.edu.arsw.Concurrencia.threads.Neo;

public class MatrixSimulation {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;
    public static final int AGENT_COUNT = 6;
    public static char[][] map = new char[HEIGHT][WIDTH];
    public static Random random = new Random();

    public static Position phonePosition;
    public static volatile boolean neoReachedPhone = false;
    public static CyclicBarrier barrier;

    public static void main(String[] args) {
        Set<Position> usedPositions = new HashSet<>();
        Position neoStart = generateUniquePosition(usedPositions);

        Position[] agentStarts = new Position[AGENT_COUNT];
        for (int i = 0; i < AGENT_COUNT; i++) {
            agentStarts[i] = generateUniquePosition(usedPositions);
        }
        phonePosition = generateUniquePosition(usedPositions);

        generateMap(neoStart, agentStarts);
        barrier = new CyclicBarrier(AGENT_COUNT + 2); // agentes + Neo + printer

        Neo neo = new Neo(neoStart, barrier);

        Agent[] agents = new Agent[AGENT_COUNT];
        Thread[] agentThreads = new Thread[AGENT_COUNT];
        for (int i = 0; i < AGENT_COUNT; i++) {
            agents[i] = new Agent(agentStarts[i], barrier);
            agentThreads[i] = new Thread(agents[i]);
        }

        Thread neoThread = new Thread(neo);

        Thread printer = new Thread(() -> {
            while (!neoReachedPhone) {
                try {
                    barrier.await(); // Espera a que todos decidan
                    barrier.await(); // Espera a que todos muevan

                    // Verificar si Neo llegó al teléfono
                    if (map[phonePosition.x][phonePosition.y] == 'N') {
                        neoReachedPhone = true;
                        printMap();
                        System.out.println("Neo llegó al teléfono!");
                        break;
                    }
                    // Verificar si un agente atrapó a Neo
                    boolean neoAtrapado = true;
                    outer:
                    for (int i = 0; i < HEIGHT; i++) {
                        for (int j = 0; j < WIDTH; j++) {
                            if (map[i][j] == 'N') {
                                neoAtrapado = false;
                                break outer;
                            }
                        }
                    }
                    if (neoAtrapado) {
                        neoReachedPhone = true;
                        printMap();
                        System.out.println("¡Neo fue atrapado por un Agente!");
                        break;
                    }
                    printMap();
                    Thread.sleep(200);
                } catch (Exception e) {
                    break;
                }
            }
            System.out.println("Fin del juego");
        });

        neoThread.start();
        for (Thread t : agentThreads) t.start();
        printer.start();
    }

    // Método para generar una posición única
    private static Position generateUniquePosition(Set<Position> usedPositions) {
        Position pos;
        do {
            pos = new Position(random.nextInt(HEIGHT), random.nextInt(WIDTH));
        } while (usedPositions.contains(pos));
        usedPositions.add(pos);
        return pos;
    }

    // Cambia generateMap para aceptar un arreglo de posiciones de agentes:
    private static void generateMap(Position neoStart, Position[] agentStarts) {
        // Inicializa todo con '-'
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                map[i][j] = '-';
            }
        }

        // Crear muros aleatorios sin ocupar posiciones especiales
        int totalWalls = HEIGHT * WIDTH / 5;
        int walls = 0;
        while (walls < totalWalls) {
            int r = random.nextInt(HEIGHT);
            int c = random.nextInt(WIDTH);
            boolean occupied = (r == neoStart.x && c == neoStart.y) || (r == phonePosition.x && c == phonePosition.y);
            for (Position agentStart : agentStarts) {
                if (r == agentStart.x && c == agentStart.y) occupied = true;
            }
            if (map[r][c] == '-' && !occupied) {
                map[r][c] = '#';
                walls++;
            }
        }

        // Colocar los personajes
        map[neoStart.x][neoStart.y] = 'N';
        for (Position agentStart : agentStarts) {
            map[agentStart.x][agentStart.y] = 'A';
        }
        map[phonePosition.x][phonePosition.y] = 'P';
    }

    public static synchronized void printMap() {
        System.out.println("\n" ); // separador visual
        for (char[] row : map) {
            for (char c : row) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }

    public static synchronized boolean isFree(int x, int y, boolean isNeo) {
        if (x < 0 || y < 0 || x >= HEIGHT || y >= WIDTH) return false;
        if (isNeo) {
            return map[x][y] == '-' || map[x][y] == 'P';
        } else {
            return map[x][y] == '-';
        }
    }

    public static synchronized void moveAgent(Position oldPos, Position newPos) {
        if (isFree(newPos.x, newPos.y, false) || map[newPos.x][newPos.y] == 'N') {
            if (oldPos.x == phonePosition.x && oldPos.y == phonePosition.y) {
                map[oldPos.x][oldPos.y] = 'P';
            } else if (map[oldPos.x][oldPos.y] == 'A') {
                map[oldPos.x][oldPos.y] = '-';
            }
            map[newPos.x][newPos.y] = 'A';
        }
    }

    public static synchronized void moveNeo(Position oldPos, Position newPos) {
        if (isFree(newPos.x, newPos.y,true)) {
            if (map[oldPos.x][oldPos.y] == 'N') {
                map[oldPos.x][oldPos.y] = '-';
            }
            map[newPos.x][newPos.y] = 'N';
        }
    }
}
