Juan Sebastian Velandia
8/07/2025
# Concurrencia - Simulación de la Matrix

Este proyecto es una simulación concurrente inspirada en la película "The Matrix", desarrollada en Java utilizando hilos y barreras de sincronización. El objetivo es modelar un escenario donde Neo debe llegar a un teléfono antes de ser atrapado por los agentes, en un tablero con obstáculos y movimientos concurrentes.

## Descripción General

El tablero es una matriz de 10x10 donde:
- **Neo (N)** debe llegar a la posición del teléfono (**P**) para ganar.
- **Agentes (A)** intentan atrapar a Neo antes de que llegue al teléfono.
- Hay muros (**#**) que bloquean el paso y espacios libres (**-**).

Cada entidad (Neo y los agentes) es un hilo independiente que decide y ejecuta su movimiento en turnos sincronizados mediante una barrera (`CyclicBarrier`). Un hilo adicional imprime el estado del tablero en cada turno.

## Estructura del Proyecto

- [`src/main/java/escuelaing/edu/arsw/Concurrencia/MatrixSimulation.java`](src/main/java/escuelaing/edu/arsw/Concurrencia/MatrixSimulation.java): Clase principal que inicializa el tablero, las posiciones y coordina los hilos.
- [`src/main/java/escuelaing/edu/arsw/Concurrencia/threads/Neo.java`](src/main/java/escuelaing/edu/arsw/Concurrencia/threads/Neo.java): Lógica de movimiento de Neo.
- [`src/main/java/escuelaing/edu/arsw/Concurrencia/threads/Agent.java`](src/main/java/escuelaing/edu/arsw/Concurrencia/threads/Agent.java): Lógica de movimiento de los agentes.
- [`src/main/java/escuelaing/edu/arsw/Concurrencia/interfaces/Entity.java`](src/main/java/escuelaing/edu/arsw/Concurrencia/interfaces/Entity.java): Clase abstracta base para entidades móviles, implementa BFS para encontrar caminos.
- [`src/main/java/escuelaing/edu/arsw/Concurrencia/board/Position.java`](src/main/java/escuelaing/edu/arsw/Concurrencia/board/Position.java): Representa una posición en el tablero.

## Funcionamiento

1. **Inicialización**: Se generan posiciones aleatorias para Neo, los agentes y el teléfono, asegurando que no se sobrepongan.
2. **Tablero**: Se colocan muros aleatorios y se inicializan las posiciones de los personajes.
3. **Simulación**:
   - Cada hilo (Neo y agentes) decide su próximo movimiento usando BFS para buscar el camino más corto hacia su objetivo.
   - Todos los hilos esperan en la barrera hasta que todos hayan decidido.
   - Luego, todos ejecutan su movimiento y esperan nuevamente en la barrera.
   - Un hilo "printer" imprime el tablero y verifica condiciones de victoria o derrota.
4. **Finalización**: El juego termina cuando Neo llega al teléfono o es atrapado por un agente.

## Ejecución

Para compilar y ejecutar el proyecto:

```sh
./mvnw clean package
java -cp target/Concurrencia-0.0.1-SNAPSHOT.jar escuelaing.edu.arsw.Concurrencia.MatrixSimulation
```
## Dependencias
- Java 17+
- Maven
- Spring Boot (solo para pruebas, no es necesario para la simulación principal)

## Ejemplo de Salida
- - - - - - - - - -
- # - - - # - - - -
- - N - - - - # - -
- - - # - - - - - -
- - - - - - - - - -
- - - - - - - - - -
- - - - - - - - - -
- - - - - - - - - -
- - - - - - - - - -
- - - - - - - - - P

Neo llegó al teléfono!
Fin del juego