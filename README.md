# 🚀 Space Ship Game

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/Java%20Swing-5382A1?style=for-the-badge)
![AWT](https://img.shields.io/badge/AWT%20%2F%20Canvas-333333?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-En%20desarrollo-5C32B7?style=for-the-badge)

**Space Ship Game** es un videojuego arcade 2D desarrollado en **Java**, utilizando **Swing**, **AWT**, `Canvas`, `BufferStrategy` y programación orientada a objetos.

El jugador controla una nave espacial, dispara láseres, esquiva meteoritos y enfrenta enemigos tipo UFO dentro de una ventana gráfica. El juego implementa movimiento con vectores, aceleración, rotación, colisiones, animaciones por frames, generación de oleadas y renderizado mediante doble buffer.

<p align="center">
  <img src="./spaceshooter/PNG/playerShip1_blue.png" width="150px" alt="Space Ship Game"/>
</p>

---

## 📌 Tabla de contenido

- [Descripción](#-descripción)
- [Características principales](#-características-principales)
- [Controles](#-controles)
- [Tecnologías utilizadas](#-tecnologías-utilizadas)
- [Estructura del proyecto](#-estructura-del-proyecto)
- [Arquitectura interna del proyecto](#-arquitectura-interna-del-proyecto)
- [Mecánicas, matemáticas y física del juego](#-mecánicas-matemáticas-y-física-del-juego)
- [Flujo de ejecución del juego](#-flujo-de-ejecución-del-juego)
- [Renderizado gráfico](#-renderizado-gráfico)
- [Descripción técnica por archivo](#-descripción-técnica-por-archivo)
- [Cómo ejecutar el proyecto](#-cómo-ejecutar-el-proyecto)
- [Captura de pantalla](#-captura-de-pantalla)
- [Conceptos aplicados](#-conceptos-aplicados)
- [Detalles técnicos destacables](#-detalles-técnicos-destacables)
- [Mejoras futuras](#-mejoras-futuras)
- [Posibles mejoras técnicas del código](#-posibles-mejoras-técnicas-del-código)
- [Autor](#-autor)
- [Créditos](#-créditos)
- [Estado del proyecto](#-estado-del-proyecto)

---

## 🎮 Descripción

Este proyecto consiste en un juego estilo **space shooter / arcade espacial**, donde el jugador controla una nave dentro de un escenario 2D.

El objetivo principal es sobrevivir a las oleadas de meteoritos y enemigos, disparando proyectiles y evitando colisiones. La nave puede rotar, acelerar, disparar y desplazarse por la pantalla con un comportamiento similar al de los juegos arcade clásicos de naves espaciales.

El juego implementa un ciclo principal de actualización y dibujo mediante un hilo (`Thread`) y una lógica de FPS. Esto permite que los objetos del juego se actualicen y rendericen continuamente.

---

## ✨ Características principales

- Ventana gráfica creada con `JFrame`.
- Renderizado 2D usando `Canvas`, `Graphics` y `BufferStrategy`.
- Sistema de actualización constante mediante `Runnable` y `Thread`.
- Control de nave con teclado.
- Movimiento con aceleración, velocidad y dirección.
- Límite de velocidad máxima para la nave.
- Disparo de láseres con cadencia controlada.
- Generación de meteoritos por oleadas.
- División de meteoritos al ser destruidos.
- Enemigo tipo UFO con movimiento por nodos.
- Disparo enemigo hacia la posición aproximada del jugador.
- Sistema básico de colisiones circulares.
- Animaciones de explosión por frames.
- Carga de sprites e imágenes desde recursos del proyecto.
- Organización modular por paquetes.
- Uso de vectores 2D para movimiento, dirección y distancia.

---

## 🕹️ Controles

| Tecla | Acción |
|------|--------|
| `W` | Acelerar la nave |
| `A` | Girar hacia la izquierda |
| `D` | Girar hacia la derecha |
| `SPACE` | Disparar láser |

---

## 🛠️ Tecnologías utilizadas

- **Java**
- **Java Swing**
- **AWT**
- **Canvas**
- **BufferStrategy**
- **Programación orientada a objetos**
- **Vectores 2D**
- **Sprites 2D**
- **Transformaciones afines**
- **Animaciones por frames**

---

## 📁 Estructura del proyecto

```text
space_ship/
│
├── README.md
│
├── spaceshooter/
│   ├── Backgrounds/
│   ├── Bonus/
│   ├── PNG/
│   │   ├── Effects/
│   │   ├── Enemies/
│   │   ├── Explosions/
│   │   ├── Lasers/
│   │   ├── Meteors/
│   │   ├── player.png
│   │   └── playerShip1_blue.png
│   ├── Spritesheet/
│   ├── Vector/
│   ├── license.txt
│   ├── preview.png
│   └── sample.png
│
└── src/
    ├── gameObjects/
    │   ├── Chronometer.java
    │   ├── Constants.java
    │   ├── GameObject.java
    │   ├── Graphics2D.java
    │   ├── Laser.java
    │   ├── Meteor.java
    │   ├── MovingObject.java
    │   ├── Player.java
    │   ├── Size.java
    │   └── Ufo.java
    │
    ├── graphics/
    │   ├── Animation.java
    │   ├── Assets.java
    │   └── Loader.java
    │
    ├── input/
    │   └── KeyBoard.java
    │
    ├── math/
    │   └── Vector2D.java
    │
    └── project/
        ├── GameState.java
        ├── Main.java
        └── Window.java
```

---

## 🧩 Arquitectura interna del proyecto

El proyecto está organizado bajo una arquitectura sencilla pero funcional para un videojuego 2D. La lógica se divide en paquetes especializados, permitiendo separar la ventana principal, el estado del juego, los objetos, las animaciones, la entrada por teclado, la carga de recursos y las operaciones matemáticas.

La estructura general se puede entender así:

```text
Main
 └── Window
      └── GameState
           ├── Player
           ├── Meteor
           ├── Laser
           ├── Ufo
           └── Animation
```

A nivel de herencia, los objetos principales se organizan así:

```text
GameObject
 └── MovingObject
      ├── Player
      ├── Meteor
      ├── Laser
      └── Ufo
```

La clase `Window` administra la ventana, el ciclo principal del juego y el renderizado.  
La clase `GameState` administra todos los objetos activos del juego.  
La clase `GameObject` define la base común de cualquier entidad dibujable.  
La clase `MovingObject` agrega movimiento, velocidad, ángulo, colisiones y destrucción.  
Las clases `Player`, `Meteor`, `Laser` y `Ufo` representan los objetos principales del juego.  
La clase `Vector2D` permite trabajar con matemáticas vectoriales para simular movimiento 2D.

---

## 🧮 Mecánicas, matemáticas y física del juego

El juego implementa una física 2D básica basada en vectores donde cada objeto móvil tiene una posición, una velocidad, un ángulo y, en algunos casos, aceleración.

---

### Movimiento con vectores

La posición de un objeto se representa con un vector:

```text
position = (x, y)
```

La velocidad también se representa como un vector:

```text
velocity = (vx, vy)
```

En cada actualización del juego, la posición se modifica sumando la velocidad:

```text
position = position + velocity
```

En código, esto se realiza con el método `add()` de `Vector2D`:

```java
position = position.add(velocity);
```

Esto permite que cada objeto se desplace de forma continua en la pantalla.

---

### Clase Vector2D

La clase `Vector2D` es uno de los componentes matemáticos más importantes del proyecto. Esta clase permite representar posiciones, velocidades, direcciones, aceleraciones y distancias.

Algunas operaciones principales son:

```java
public Vector2D add(Vector2D v) {
    return new Vector2D(x + v.getX(), y + v.getY());
}
```

Este método suma dos vectores. Se usa para mover objetos, por ejemplo:

```java
position = position.add(velocity);
```

También existe la resta de vectores:

```java
public Vector2D subtract(Vector2D v) {
    return new Vector2D(x - v.getX(), y - v.getY());
}
```

Esta operación se usa para calcular la distancia entre dos objetos o para obtener un vector que apunte desde un objeto hacia otro.

El método `scale()` multiplica un vector por un valor:

```java
public Vector2D scale(double value) {
    return new Vector2D(x * value, y * value);
}
```

Esto permite aumentar o reducir la magnitud de una velocidad, dirección o aceleración.

---

### Magnitud de un vector

La magnitud de un vector representa su longitud. En el juego se usa para saber qué tan rápido se mueve un objeto o qué distancia hay entre dos puntos.

Matemáticamente:

```text
|v| = √(x² + y²)
```

En código:

```java
public double getMagnitud() {
    return Math.sqrt(x * x + y * y);
}
```

Esta fórmula viene del teorema de Pitágoras, ya que un vector 2D puede interpretarse como la hipotenusa de un triángulo rectángulo.

---

### Normalización de vectores

Normalizar un vector significa convertirlo en un vector de longitud `1`, conservando su dirección.

En código:

```java
public Vector2D normalize() {
    double magnitud = getMagnitud();
    return new Vector2D(x / magnitud, y / magnitud);
}
```

Esto se usa cuando se necesita una dirección pura, sin importar la velocidad original. Por ejemplo, el UFO normaliza el vector que apunta hacia el jugador antes de disparar.

---

### Límite de velocidad

Para evitar que un objeto acelere infinitamente, se usa el método `limit()`:

```java
public Vector2D limit(double value) {
    if (getMagnitud() > value)
        return this.normalize().scale(value);
    return this;
}
```

Este método revisa si la magnitud del vector supera el máximo permitido. Si lo supera, normaliza el vector y luego lo escala al valor máximo.

La nave utiliza este método para no superar su velocidad máxima:

```java
velocity = velocity.limit(maxVel);
```

---

### Dirección mediante seno y coseno

El método `setDirection()` permite cambiar la dirección de un vector usando un ángulo:

```java
public Vector2D setDirection(double angle) {
    double magnitud = getMagnitud();
    return new Vector2D(Math.cos(angle) * magnitud, Math.sin(angle) * magnitud);
}
```

Matemáticamente:

```text
x = cos(ángulo) × magnitud
y = sin(ángulo) × magnitud
```

Esto permite convertir un ángulo en una dirección 2D.

---

### Aceleración de la nave

La nave no se mueve cambiando su posición directamente. Primero se calcula una aceleración en la dirección hacia donde apunta la nave.

Cuando el jugador presiona `W`, la nave acelera en la dirección del vector `heading`:

```java
acceleration = heading.scale(Constants.ACC);
```

Luego esa aceleración se suma a la velocidad:

```java
velocity = velocity.add(acceleration);
```

Finalmente, la velocidad se suma a la posición:

```java
position = position.add(velocity);
```

La lógica se puede resumir así:

```text
aceleración → velocidad → posición
```

Esto produce un movimiento más natural, ya que la nave conserva inercia y no se detiene instantáneamente.

---

### Fricción o desaceleración

Cuando el jugador deja de presionar `W`, el juego aplica una desaceleración en sentido contrario a la velocidad actual:

```java
acceleration = velocity.scale(-1).normalize().scale(Constants.ACC / 2);
```

Esto simula una fricción ligera o pérdida progresiva de velocidad.  
La nave reduce su movimiento poco a poco en lugar de detenerse de golpe.

---

### Rotación de la nave

La nave usa un ángulo para saber hacia dónde apunta.  
Cuando el jugador presiona `A` o `D`, el ángulo disminuye o aumenta:

```java
if (KeyBoard.RIGHT)
    angle += Constants.DELTAANGLE;

if (KeyBoard.LEFT)
    angle -= Constants.DELTAANGLE;
```

Luego, el vector `heading` cambia su dirección según ese ángulo:

```java
heading = heading.setDirection(angle - Math.PI / 2);
```

Esto hace que la nave acelere hacia la dirección visual en la que está apuntando.

---

### Rotación visual con AffineTransform

Aunque la física del movimiento se maneja con vectores, la rotación visual de los sprites se realiza con `AffineTransform`.

Por ejemplo, en la clase `Player`, primero se crea una transformación de traslación:

```java
at = AffineTransform.getTranslateInstance(position.getX(), position.getY());
```

Luego se aplica una rotación sobre el centro de la imagen:

```java
at.rotate(angle, width / 2, height / 2);
```

Finalmente se dibuja la imagen rotada:

```java
g2d.drawImage(texture, at, null);
```

Este mismo principio se usa para dibujar meteoritos, láseres y el UFO con rotación.

---

### Disparo de láseres

Cuando el jugador presiona `SPACE`, la nave crea un nuevo objeto `Laser`.

El láser nace desde el centro de la nave y avanza en la dirección actual del jugador:

```java
new Laser(
    getCenter().add(heading.scale(width)),
    heading,
    Constants.LASER_VEL,
    angle,
    Assets.redLaser,
    gameState
);
```

La velocidad del láser se obtiene escalando su vector de dirección:

```java
this.velocity = velocity.scale(maxVel);
```

Luego, en cada actualización:

```java
position = position.add(velocity);
```

Si el láser sale de los límites de la ventana, se destruye:

```java
if (position.getX() < 0 || position.getX() > Constants.WIDTH 
 || position.getY() < 0 || position.getY() > Constants.HEIGHT) {
    Destroy();
}
```

---

### Control de disparo con Chronometer

Para evitar que el jugador dispare demasiados láseres por segundo, se usa la clase `Chronometer`.

Cuando se dispara, el cronómetro se activa:

```java
fireRate.run(Constants.FIRERATE);
```

Mientras el cronómetro está activo, no se permite otro disparo:

```java
if (KeyBoard.SHOOT && !fireRate.isRunning()) {
    // crear láser
}
```

Esto implementa una mecánica de cadencia de disparo o `fire rate`.

En `Constants.java`, la cadencia está definida así:

```java
public static final int FIRERATE = 100;
```

---

### Colisiones por distancia entre centros

Las colisiones se calculan midiendo la distancia entre los centros de dos objetos.

Cada objeto móvil tiene un centro calculado así:

```java
protected Vector2D getCenter() {
    return new Vector2D(position.getX() + width / 2, position.getY() + height / 2);
}
```

Luego se calcula la distancia entre dos centros:

```java
double distance = m.getCenter().subtract(getCenter()).getMagnitud();
```

Si la distancia es menor que la suma aproximada de los radios de ambos objetos, se considera una colisión:

```java
if (distance < m.width / 2 + width / 2 && movingObjects.contains(this)) {
    objectCollision(m, this);
}
```

Esta técnica equivale a una colisión circular simple.  
Es eficiente y adecuada para objetos como naves, meteoritos, láseres y enemigos.

---

### Destrucción de objetos

Cuando dos objetos colisionan, se genera una explosión y ambos objetos son eliminados de la lista de objetos activos:

```java
gameState.playExplosion(getCenter());
a.Destroy();
b.Destroy();
```

Los meteoritos tienen una lógica especial: cuando se destruyen, pueden dividirse en meteoritos más pequeños.

---

### División de meteoritos

Los meteoritos están organizados por tamaño usando el enum `Size`:

```java
BIG(2, Assets.meds),
MED(2, Assets.smalls),
SMALL(2, Assets.tinies),
TINY(0, null);
```

La progresión de tamaños es:

```text
BIG → MED → SMALL → TINY
```

Cuando un meteorito se destruye, `GameState` ejecuta:

```java
gameState.divideMeteor(this);
```

La clase `GameState` determina el nuevo tamaño y genera meteoritos en direcciones aleatorias:

```java
new Vector2D(0, 1).setDirection(Math.random() * Math.PI * 2)
```

Esto crea una dirección aleatoria entre `0` y `2π` radianes, es decir, cualquier dirección posible en un plano 2D.

---

### Movimiento de meteoritos

Los meteoritos tienen una velocidad inicial aleatoria y se mueven automáticamente:

```java
position = position.add(velocity);
```

Además, rotan constantemente:

```java
angle += Constants.DELTAANGLE / 2;
```

Cuando salen por un borde de la pantalla, aparecen por el lado contrario:

```java
if (position.getX() > Constants.WIDTH)
    position.setX(-width);

if (position.getY() > Constants.HEIGHT)
    position.setY(-height);

if (position.getX() < -height)
    position.setX(Constants.WIDTH);

if (position.getY() < -width)
    position.setY(Constants.HEIGHT);
```

Esta mecánica crea un efecto de pantalla envolvente o `screen wrapping`.

---

### Movimiento del UFO con path following

El enemigo UFO sigue una ruta compuesta por varios nodos.  
La ruta se genera en `GameState` creando puntos aleatorios en diferentes zonas de la pantalla:

```java
ArrayList<Vector2D> path = new ArrayList<Vector2D>();
path.add(new Vector2D(posX, posY));
```

El UFO se mueve hacia el nodo actual.  
Cuando está suficientemente cerca, pasa al siguiente nodo:

```java
if (distanceToNode < Constants.NODE_RADIUS) {
    index++;
}
```

Para moverse hacia un objetivo, usa una fuerza de seguimiento llamada `seekForce()`:

```java
private Vector2D seekForce(Vector2D target) {
    Vector2D desiredVelocity = target.subtract(getCenter());
    desiredVelocity = desiredVelocity.normalize().scale(maxVel);
    return desiredVelocity.subtract(velocity);
}
```

Esta técnica calcula una velocidad deseada hacia el objetivo y luego obtiene la diferencia con la velocidad actual.  
El resultado funciona como una fuerza de corrección para dirigir suavemente al UFO.

---

### Masa del UFO

El UFO no cambia su velocidad de forma instantánea.  
La fuerza de seguimiento se divide entre una constante de masa:

```java
pathFollowing = pathFollowing.scale(1 / Constants.UFO_MASS);
```

Esto simula una aceleración más suave:

```text
aceleración = fuerza / masa
```

Mientras mayor es la masa, más lento responde el UFO al cambio de dirección.

En `Constants.java`, la masa del UFO se define así:

```java
public static final double UFO_MASS = 60.0;
```

---

### Disparo del UFO

El UFO también puede disparar al jugador.  
Primero calcula un vector desde el UFO hacia la nave:

```java
Vector2D toPlayer = gameState.getPlayer().getCenter().subtract(getCenter());
```

Luego normaliza ese vector:

```java
toPlayer = toPlayer.normalize();
```

Después obtiene un ángulo base hacia el jugador y le aplica una variación aleatoria:

```java
double currentAngle = toPlayer.getAngle();
double newAngle = Math.random() * Math.PI - (Math.PI / 2) + currentAngle;
```

Esto hace que el UFO dispare en dirección aproximada al jugador, pero no siempre con precisión perfecta.

---

### Animaciones por frames

Las explosiones se manejan con la clase `Animation`.  
Una animación está formada por un arreglo de imágenes:

```java
private BufferedImage[] frames;
```

Cada cierto tiempo, la animación avanza al siguiente frame:

```java
if (time > velocity) {
    time = 0;
    index++;
}
```

Cuando se llega al final del arreglo de frames, la animación deja de ejecutarse:

```java
if (index >= frames.length)
    running = false;
```

En `GameState`, las animaciones se actualizan y se eliminan cuando terminan:

```java
if (!anim.isRunning())
    explosions.remove(i);
```

Este sistema permite mostrar explosiones temporales sin convertirlas en objetos permanentes del juego.

---

## 🔄 Flujo de ejecución del juego

El flujo general del juego es el siguiente:

```text
Main
 └── crea Window
      └── start()
           └── inicia Thread
                └── run()
                     ├── init()
                     │    ├── carga Assets
                     │    └── crea GameState
                     │
                     └── loop principal
                          ├── update()
                          │    ├── actualiza teclado
                          │    ├── actualiza jugador
                          │    ├── actualiza meteoritos
                          │    ├── actualiza láseres
                          │    ├── actualiza UFO
                          │    └── actualiza explosiones
                          │
                          └── draw()
                               ├── limpia pantalla
                               ├── dibuja objetos
                               ├── dibuja animaciones
                               └── muestra buffer
```

La clase `Window` controla el ciclo principal con un objetivo de 60 FPS:

```java
private final int FPS = 60;
private double TARGETTIME = 1000000000 / FPS;
```

El tiempo se calcula usando nanosegundos:

```java
now = System.nanoTime();
delta += (now - lastTime) / TARGETTIME;
```

Cuando `delta` alcanza o supera `1`, el juego actualiza la lógica y dibuja un nuevo frame:

```java
if (delta >= 1) {
    update();
    draw();
    delta--;
    frames++;
}
```

---

## 🖼️ Renderizado gráfico

El juego utiliza `Canvas` y `BufferStrategy` para dibujar los objetos en pantalla.

Primero se obtiene la estrategia de buffer:

```java
bs = canvas.getBufferStrategy();
```

Si todavía no existe, se crea una estrategia de doble buffer:

```java
canvas.createBufferStrategy(2);
```

Luego se obtiene el contexto gráfico:

```java
g = bs.getDrawGraphics();
```

Se limpia la pantalla:

```java
g.setColor(Color.BLACK);
g.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);
```

Se dibujan los objetos del estado del juego:

```java
gameState.draw(g);
```

Finalmente, se libera el contexto gráfico y se muestra el buffer:

```java
g.dispose();
bs.show();
```

Esta técnica ayuda a que el renderizado sea más fluido y evita parpadeos visuales.

---

## 📦 Descripción técnica por archivo

| Archivo | Función dentro del proyecto |
|--------|------------------------------|
| `Main.java` | Punto de entrada del programa. Crea la ventana principal e inicia el juego. |
| `Window.java` | Administra la ventana, el `Canvas`, el hilo principal, el game loop, el cálculo de FPS y el renderizado con `BufferStrategy`. |
| `GameState.java` | Controla el estado del juego: jugador, meteoritos, UFO, láseres, explosiones, oleadas y actualización de objetos. |
| `GameObject.java` | Clase abstracta base para todos los objetos del juego. Define posición, textura, `update()` y `draw()`. |
| `MovingObject.java` | Extiende `GameObject` y agrega velocidad, ángulo, tamaño, colisiones y destrucción. |
| `Player.java` | Implementa la nave del jugador, su aceleración, rotación, disparo, fricción, límite de velocidad y dibujo con fuego de propulsión. |
| `Laser.java` | Representa los proyectiles. Se mueve en línea recta según su vector de dirección y se destruye al salir de la pantalla. |
| `Meteor.java` | Representa los meteoritos. Se mueven, rotan, reaparecen por los bordes y se dividen al ser destruidos. |
| `Ufo.java` | Representa el enemigo UFO. Sigue una ruta por nodos, calcula fuerza de seguimiento, dispara hacia el jugador y rota visualmente. |
| `Size.java` | Enum que define los tamaños de meteoritos y cuántos fragmentos generan al destruirse. |
| `Chronometer.java` | Temporizador usado para controlar la cadencia de disparo del jugador y del UFO. |
| `Vector2D.java` | Clase matemática para operar con vectores: suma, resta, escala, normalización, magnitud, límite de velocidad y cambio de dirección. |
| `Animation.java` | Controla animaciones por frames, especialmente explosiones. |
| `Assets.java` | Carga y organiza las imágenes del jugador, láseres, meteoritos, explosiones y UFO. |
| `Loader.java` | Carga imágenes desde los recursos del proyecto usando `ImageIO`. |
| `KeyBoard.java` | Detecta teclas presionadas y liberadas mediante `KeyListener`. |
| `Constants.java` | Centraliza valores globales del juego como tamaño de ventana, velocidad, aceleración, fire rate y parámetros del UFO. |

---

## ▶️ Cómo ejecutar el proyecto

### Opción 1: Ejecutar desde IntelliJ IDEA

1. Clona el repositorio:

```bash
git clone https://github.com/SalazarPaulo/space_ship.git
```

2. Abre la carpeta del proyecto en **IntelliJ IDEA**.

3. Verifica que el proyecto tenga configurado un **JDK**.

4. Abre el archivo principal:

```text
src/project/Main.java
```

5. Ejecuta la clase `Main`.

La clase principal del proyecto es:

```java
package src.project;

public class Main {
    public static void main(String x[]) {
        Window obj = new Window();
        obj.start();
    }
}
```

---

### Opción 2: Ejecutar desde terminal en Windows PowerShell

Desde la raíz del proyecto:

```powershell
$files = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $files
java -cp "out;." src.project.Main
```

---

### Opción 3: Ejecutar desde terminal en Linux/macOS

Desde la raíz del proyecto:

```bash
find src -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp "out:." src.project.Main
```

---

## ⚠️ Nota importante

Si aparece un error relacionado con `setVisible(boolean)`, verifica que en `Window.java` la ventana se muestre así:

```java
setVisible(true);
```

También es recomendable iniciar el hilo del juego de esta manera:

```java
public void start() {
    running = true;
    thread = new Thread(this);
    thread.start();
}
```

Esto permite que el ciclo principal del juego se active antes de iniciar el hilo.

---

## 🖼️ Captura de pantalla

<p align="center">
  <img src="https://github.com/SalazarPaulo/SalazarPaulo/blob/main/Assets/SpaceShipImg.png" width="650px" alt="Space Ship Game"/>
</p>

---

## 🧠 Conceptos aplicados

Este proyecto aplica varios conceptos importantes de programación y desarrollo de videojuegos:

- Programación orientada a objetos.
- Herencia mediante `GameObject` y `MovingObject`.
- Polimorfismo con métodos `update()` y `draw()`.
- Game loop con actualización y renderizado.
- Manejo de FPS.
- Doble buffering con `BufferStrategy`.
- Entrada por teclado con `KeyListener`.
- Física 2D básica con vectores.
- Aceleración, velocidad y posición.
- Normalización de vectores.
- Cálculo de magnitud vectorial.
- Rotación con funciones trigonométricas.
- Transformaciones 2D con `AffineTransform`.
- Colisiones circulares por distancia.
- Animaciones por frames.
- Generación procedural básica de oleadas.
- Movimiento enemigo con path following.
- Manejo de recursos gráficos externos.

---

## 🛠️ Detalles técnicos destacables

### Uso de clases abstractas

`GameObject` funciona como una plantilla general para cualquier objeto del juego.  
Obliga a que cada objeto implemente sus propios métodos:

```java
public abstract void update();
public abstract void draw(Graphics g);
```

Esto permite que cada objeto tenga su propia lógica de actualización y dibujo.

---

### Uso de herencia

`MovingObject` hereda de `GameObject` y agrega comportamiento común para los objetos que se mueven:

```java
public abstract class MovingObject extends GameObject
```

Gracias a esto, `Player`, `Meteor`, `Laser` y `Ufo` comparten atributos como:

```text
position
velocity
angle
maxVel
width
height
gameState
```

---

### Uso de polimorfismo

En `GameState`, todos los objetos móviles se guardan en una misma lista:

```java
private ArrayList<MovingObject> movingObjects;
```

Luego se actualizan con:

```java
movingObjects.get(i).update();
```

Cada objeto ejecuta su propia versión de `update()`, dependiendo de si es una nave, un meteorito, un láser o un UFO.

---

### Generación de oleadas

Cuando ya no quedan meteoritos activos, el juego inicia una nueva oleada:

```java
for (i = 0; i < movingObjects.size(); i++)
    if (movingObjects.get(i) instanceof Meteor)
        return;

startWave();
```

Cada nueva oleada aumenta la cantidad de meteoritos:

```java
meteors++;
```

Esto permite que la dificultad aumente progresivamente.

---

### Carga de recursos gráficos

La clase `Assets` centraliza la carga de imágenes del juego:

```java
player = Loader.ImageLoader("/spaceshooter/PNG/player.png");
redLaser = Loader.ImageLoader("/spaceshooter/PNG/Lasers/laserRed01.png");
ufo = Loader.ImageLoader("/spaceshooter/PNG/ufo.png");
```

Los meteoritos se cargan en arreglos según su tamaño:

```java
bigs[i] = Loader.ImageLoader("/spaceshooter/PNG/Meteors/meteorGrey_big" + (i + 1) + ".png");
meds[i] = Loader.ImageLoader("/spaceshooter/PNG/Meteors/meteorGrey_med" + (i + 1) + ".png");
smalls[i] = Loader.ImageLoader("/spaceshooter/PNG/Meteors/meteorGrey_small" + (i + 1) + ".png");
tinies[i] = Loader.ImageLoader("/spaceshooter/PNG/Meteors/meteorGrey_tiny" + (i + 1) + ".png");
```

Esto facilita reutilizar recursos visuales sin cargar imágenes manualmente en cada clase.

---

### Entrada por teclado

La clase `KeyBoard` implementa `KeyListener` y guarda el estado de las teclas en un arreglo booleano:

```java
private boolean[] keys = new boolean[256];
```

Luego actualiza acciones específicas del jugador:

```java
UP = keys[KeyEvent.VK_W];
RIGHT = keys[KeyEvent.VK_D];
LEFT = keys[KeyEvent.VK_A];
SHOOT = keys[KeyEvent.VK_SPACE];
```

De esta manera, el jugador puede controlar la nave de forma continua mientras mantiene presionadas las teclas.

---

## 🚧 Mejoras futuras

Algunas mejoras que se pueden implementar son:

- Agregar pantalla de inicio.
- Agregar pantalla de pausa.
- Agregar sistema de puntaje.
- Agregar vidas o barra de salud.
- Agregar sonidos durante disparos y explosiones.
- Mejorar el sistema de niveles.
- Agregar menú principal.
- Crear un archivo `.jar` ejecutable.
- Optimizar la detección de colisiones.
- Agregar más enemigos.
- Agregar niveles de dificultad.
- Agregar pantalla de Game Over.
- Agregar guardado de puntaje máximo.
- Agregar interfaz visual para mostrar vidas y puntuación.

---

## 🔍 Posibles mejoras técnicas del código

Además de las mejoras jugables, también se pueden mejorar algunos aspectos internos del código:

- Cambiar `setVisible();` por `setVisible(true);` en `Window.java`.
- En `start()`, colocar `running = true` antes de `thread.start()`.
- Evitar normalizar vectores de magnitud cero en `Vector2D.normalize()`.
- Revisar el cálculo de `getAngle()`, ya que `Math.asin()` no distingue todos los cuadrantes como lo haría `Math.atan2(y, x)`.
- Revisar expresiones como `(int)Math.random() * textures.length`, porque el casteo antes de multiplicar puede producir siempre `0`.
- Cambiar el nombre de la clase `Window` a `GameWindow` para evitar confusión con `java.awt.Window`.
- Eliminar o revisar la interfaz `Graphics2D.java` en `gameObjects`, ya que puede confundirse con `java.awt.Graphics2D`.
- Agregar validaciones cuando se cargan imágenes con `Loader.ImageLoader()`.
- Separar la lógica de colisiones en una clase independiente.
- Crear una clase específica para manejar el sistema de oleadas.
- Crear una clase `HUD` para mostrar puntaje, vidas y estado del jugador.

---

## 👨‍💻 Autor

**Paulo Salazar**

- GitHub: [@SalazarPaulo](https://github.com/SalazarPaulo)
- Repositorio: [space_ship](https://github.com/SalazarPaulo/space_ship)

---

## 📄 Créditos

Los recursos gráficos y sonoros utilizados pertenecen al paquete **Space Shooter Redux** de **Kenney**, distribuido bajo licencia **CC0**.

Sitio oficial de Kenney:

```text
https://www.kenney.nl/
```

---

## 📌 Estado del proyecto

Proyecto personal en desarrollo, creado con fines de aprendizaje y práctica de programación en Java, programación orientada a objetos, renderizado 2D, animaciones y mecánicas básicas de videojuegos.
