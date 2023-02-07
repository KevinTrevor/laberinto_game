/**
 *  PROYECTO 4 - LABERINTO
 *  Hecho por: Kevin Rojas - C.I: 29.582.382
 *  
 *  Algunas necesidades para que este juego se ejecute correctamente:
 * 
 *  Necesitamos un paquete llamado: proyecto
 * 
 *  Necesitamos tener un archivo llamado map.txt en la siguiente ruta:
 *  './src/main/java/res/maps/map.txt'
 * 
 *  Esto para que haya posibilidad de crear muchos mapas y que se generen
 *  dinámicamente al ejecutarse el código. Los mapas tienen un tamaño de 19x15; 
 *  es decir, 19 filas y 15 columnas.
 * 
 *  En ese mapa se tiene la siguiente representación:
 *  - 0: Representa un espacio vacío o camino/posición posible. 
 *  - 1: Representa una pared o muro del laberinto.
 *  - 2: Representa el punto de inicio del jugador.
 *  - 3: Representa la meta o el punto final a alcanzar por el jugador.
 *  
 *  A la entrega del archivo .java se le adjunta un mapa de prueba en un archivo
 *  map.txt
 * 
 *  Espero que lo disfrute mucho, profesor Arabia. ^^
 */
package proyecto;

import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Clase InterfazGrafica que implementa la interfaz Runnable para trabajar con hilos. 
 * Se usa para dibujar la interfaz gráfica del juego.
 */
class InterfazGrafica implements Runnable{
    
    // Atributos generales
    public PanelDeJuego panelJuego;
    Thread hiloContador;
    public Font comic_sans_30, comic_sans_40;
    
    // Atributos de condición de perdida y victoria
    public int tiempo = 90;
    public boolean juegoTerminado = false;
    
    /**
     * Método constructor de la clase InterfazGrafica.
     * @param panel
     */
    public InterfazGrafica(PanelDeJuego panel) {
        this.panelJuego = panel;
        comic_sans_30 = new Font("Comic Sans MS", Font.PLAIN, 30);
        comic_sans_40 = new Font("Comic Sans MS", Font.BOLD, 40);
    }
    
    /**
     * Método que dibuja en pantalla dependiendo ciertas condiciones.
     */
    public void paint(Graphics2D g) {
        // CONDICIÓN VICTORIA
        if (juegoTerminado == true) {
            String texto = "¡HAS GANADO!";
            
            // Obtenemos la posición del texto centrado.
            int x = posicionXDeTexto(texto, g);
            int y = panelJuego.alturaPantalla / 2;

            g.setFont(comic_sans_40);
            g.setColor(Color.green);
            g.drawString(texto, x, y);
            
            hiloContador = null;
            panelJuego.hiloJuego = null;
        }
        else {
            // CONDICIÓN DE PERDIDA
            if (tiempo == 0) {
                String texto = "¡HAS PERDIDO!";
                
                // Obtenemos la posición del texto centrado.
                int x = posicionXDeTexto(texto, g);
                int y = panelJuego.alturaPantalla / 2;

                g.setFont(comic_sans_40);
                g.setColor(Color.red);
                g.drawString(texto, x, y);
                
                hiloContador = null;
                panelJuego.hiloJuego = null;
            }
            else {
                g.setFont(comic_sans_30);
                g.setColor(Color.white);
                g.drawString("Tiempo: " + tiempo, 15, 30);
            }
        }
    }
    
    /**
     * Método que determina la posición en el eje X de un texto dependiendo del
     * tamaño del mismo.
     * @param texto
     * @param g
     */
    public int posicionXDeTexto(String texto, Graphics2D g) {
        int sizeText = (int) g.getFontMetrics().getStringBounds(texto, g).getWidth();
        return ((panelJuego.anchuraPantalla / 2) - (sizeText / 2)) - 110;
    }
    
    /**
     * Método que actualiza el tiempo transcurrido en la interfaz gráfica.
     */
    public void update() {
        tiempo -= 1;
    }
    
    /**
     * Método que inicia el hilo de ejecución de la clase InterfazGrafica.
     */
    public void startThread() {
        hiloContador = new Thread(this);
        hiloContador.start();
    }
            
    @Override
    /**
     * Método que se ejecuta inmediatamente una vez haya comenzado el hilo.
     */
    public void run() {
        while (hiloContador != null) {

            update();
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(PanelDeJuego.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

// MANEJADORES 

/**
 * Clase ManejadorAmbiente que controla el filtro de sombra de la pantalla.
 */
class ManejadorAmbiente {
    PanelDeJuego panelJuego;
    Sombra sombra;
    
    /**
     * Método constructor de la clase ManejadorAmbiente.
     * @param panel
     */
    public ManejadorAmbiente(PanelDeJuego panel) {
        this.panelJuego = panel;
    }
    
    /**
     * Método que configura el tamaño de la sombra en pantalla.
     */
    public void setup() {
        sombra = new Sombra(panelJuego, 50);
    }
    
    /**
     * Método que dibuja la sombra en pantalla.
     * @param g2
     */
    public void paint(Graphics2D g2) {
        sombra.paint(g2);
    }
}

/** 
 * Clase ManejadorColision para manejar las colisiones de las entidades.
 */
class ManejadorColision {
    PanelDeJuego panelJuego;
    
    /**
     * Método constructor de la clase ManejadorColision.
     * @param panelJuego
     */
    public ManejadorColision(PanelDeJuego panelJuego) {
        this.panelJuego = panelJuego;
    }
    
    /**
     * Método que controla las colisiones de una entidad.
     * @param entidad
     */
    public void colisionMundo(Entidad entidad) {
        
        int entityLeftWorldX = entidad.worldX + entidad.solidArea.x;
        int entityRightWorldX = entidad.worldX + entidad.solidArea.x + entidad.solidArea.width;
        int entityTopWorldY = entidad.worldY + entidad.solidArea.y;
        int entityBottomWorldY = entidad.worldY + entidad.solidArea.y + entidad.solidArea.height;
        
        int entityLeftCol = entityLeftWorldX / panelJuego.tileSize;
        int entityRightCol = entityRightWorldX / panelJuego.tileSize;
        int entityTopRow = entityTopWorldY / panelJuego.tileSize;
        int entityBottomRow = entityBottomWorldY / panelJuego.tileSize;
        
        int espacio1, espacio2;
        
        switch(entidad.direccion) {
            case "arriba":
                entityTopRow = (entityTopWorldY - entidad.velocidad) / panelJuego.tileSize;
                espacio1 = panelJuego.laberinto.mapaLaberinto[entityTopRow][entityLeftCol];
                espacio2 = panelJuego.laberinto.mapaLaberinto[entityTopRow][entityRightCol];
                
                switch (entidad.nombre) {
                    case "Jugador":
                        if (espacio1 == 1 || espacio2 == 1) {
                            this.vueltaInicio(entidad);
                        }
                        
                        if (espacio1 == 3 || espacio2 == 3) {
                            panelJuego.interfazGrafica.juegoTerminado = true;
                        }
                        break;
                    case "Pelota":
                        if (
                                espacio1 == 1 || espacio2 == 1 || 
                                espacio1 == 2 || espacio2 == 2 ||
                                espacio1 == 3 || espacio2 == 3) {
                            entidad.direccion = "abajo";
                        }
                        break;
                }
                break;
            case "abajo":
                entityBottomRow = (entityBottomWorldY + entidad.velocidad) / panelJuego.tileSize;
                if (entityBottomRow >= panelJuego.maxFilaMundo) {
                    switch (entidad.nombre) {
                        case "Jugador":
                            this.vueltaInicio(entidad);
                            break;
                        case "Pelota":
                            entidad.direccion = "arriba";
                            break;
                    }  
                }
                else {
                    espacio1 = panelJuego.laberinto.mapaLaberinto[entityBottomRow][entityLeftCol];
                    espacio2 = panelJuego.laberinto.mapaLaberinto[entityBottomRow][entityRightCol];

                    switch (entidad.nombre) {
                    case "Jugador":
                        if (espacio1 == 1 || espacio2 == 1) {
                            this.vueltaInicio(entidad);
                        }
                        
                        if (espacio1 == 3 || espacio2 == 3) {
                            panelJuego.interfazGrafica.juegoTerminado = true;
                        }
                        break;
                    case "Pelota":
                        if (
                                espacio1 == 1 || espacio2 == 1 || 
                                espacio1 == 2 || espacio2 == 2 ||
                                espacio1 == 3 || espacio2 == 3) {
                            entidad.direccion = "arriba";
                        }
                        break;
                    }
                }
                break;
            case "izquierda":
                entityLeftCol = (entityLeftWorldX - entidad.velocidad) / panelJuego.tileSize;
                espacio1 = panelJuego.laberinto.mapaLaberinto[entityTopRow][entityLeftCol];
                espacio2 = panelJuego.laberinto.mapaLaberinto[entityBottomRow][entityLeftCol];
                
                switch (entidad.nombre) {
                    case "Jugador":
                        if (espacio1 == 1 || espacio2 == 1) {
                            this.vueltaInicio(entidad);
                        }
                        
                        if (espacio1 == 3 || espacio2 == 3) {
                            panelJuego.interfazGrafica.juegoTerminado = true;
                        }
                        break;
                    case "Pelota":
                        if (
                                espacio1 == 1 || espacio2 == 1 || 
                                espacio1 == 2 || espacio2 == 2 ||
                                espacio1 == 3 || espacio2 == 3) {
                            entidad.direccion = "derecha";
                        }
                        break;
                }
                break;
            case "derecha":
                entityRightCol = (entityRightWorldX + entidad.velocidad) / panelJuego.tileSize;
                espacio1 = panelJuego.laberinto.mapaLaberinto[entityTopRow][entityRightCol];
                espacio2 = panelJuego.laberinto.mapaLaberinto[entityBottomRow][entityRightCol];
                
                switch (entidad.nombre) {
                    case "Jugador":
                        if (espacio1 == 1 || espacio2 == 1) {
                            this.vueltaInicio(entidad);
                        }
                        
                        if (espacio1 == 3 || espacio2 == 3) {
                            panelJuego.interfazGrafica.juegoTerminado = true;
                        }
                        break;
                    case "Pelota":
                        if (
                                espacio1 == 1 || espacio2 == 1 || 
                                espacio1 == 2 || espacio2 == 2 ||
                                espacio1 == 3 || espacio2 == 3) {
                            entidad.direccion = "izquierda";
                        }
                        break;
                } 
                break;
        }
    }
    
    /**
     * Método que verifica la colisión de un jugador con los enemigos.
     * @param entidad
     * @param objetivos
     */
    public void colisionEntidad(Entidad entidad, Entidad[] objetivos) {
        
        for(int i = 0; i < objetivos.length; i++) {
            
            if (objetivos[i] != null) {
                // Se obtiene la posición del area de colisión de la entidad.
                entidad.solidArea.x = entidad.worldX + entidad.solidArea.x;
                entidad.solidArea.y = entidad.worldY + entidad.solidArea.y;

                // Se obtiene la posición del area de colision del objetivo.
                objetivos[i].solidArea.x = objetivos[i].worldX + objetivos[i].solidArea.x;
                objetivos[i].solidArea.y = objetivos[i].worldY + objetivos[i].solidArea.y;

                switch (entidad.direccion) {
                    case "arriba":
                        entidad.solidArea.y -= entidad.velocidad;
                        if (entidad.solidArea.intersects(objetivos[i].solidArea)) {
                            entidad.estaColisionando = true;
                            vueltaInicio(entidad);
                        }
                        break;
                    case "abajo":
                        entidad.solidArea.y += entidad.velocidad;
                        if (entidad.solidArea.intersects(objetivos[i].solidArea)) {
                            entidad.estaColisionando = true;
                            vueltaInicio(entidad);
                        }
                        break;
                    case "derecha":
                        entidad.solidArea.x += entidad.velocidad;
                        if (entidad.solidArea.intersects(objetivos[i].solidArea)) {
                            entidad.estaColisionando = true;
                            vueltaInicio(entidad);
                        }
                        break;
                    case "izquierda":
                        entidad.solidArea.x -= entidad.velocidad;
                        if (entidad.solidArea.intersects(objetivos[i].solidArea)) {
                            entidad.estaColisionando = true;
                            vueltaInicio(entidad);
                        }
                        break;
                }
                entidad.solidArea.x = entidad.solidAreaXPorDefecto;
                entidad.solidArea.y = entidad.solidAreaYPorDefecto;
                objetivos[i].solidArea.x = objetivos[i].solidAreaXPorDefecto;
                objetivos[i].solidArea.y = objetivos[i].solidAreaYPorDefecto;
            }
        }
    }
    
    /**
     * Método que verifica la colisión de un enemigo con el jugador.
     * @param entidad
     */
    public void colisionJugador(Entidad entidad) {
        // Se obtiene la posición del area de colisión de la entidad.
        entidad.solidArea.x = entidad.worldX + entidad.solidArea.x;
        entidad.solidArea.y = entidad.worldY + entidad.solidArea.y;

        // Se obtiene la posición del area de colision del objetivo.
        panelJuego.jugador.solidArea.x = panelJuego.jugador.worldX + panelJuego.jugador.solidArea.x;
        panelJuego.jugador.solidArea.y = panelJuego.jugador.worldY + panelJuego.jugador.solidArea.y;

        switch (entidad.direccion) {
            case "arriba":
                entidad.solidArea.y -= entidad.velocidad;
                if (entidad.solidArea.intersects(panelJuego.jugador.solidArea)) {
                    entidad.estaColisionando = true;
                    vueltaInicio(panelJuego.jugador);
                }
                break;
            case "abajo":
                entidad.solidArea.y += entidad.velocidad;
                if (entidad.solidArea.intersects(panelJuego.jugador.solidArea)) {
                    entidad.estaColisionando = true;
                    vueltaInicio(panelJuego.jugador);
                }
                break;
            case "derecha":
                entidad.solidArea.x += entidad.velocidad;
                if (entidad.solidArea.intersects(panelJuego.jugador.solidArea)) {
                    entidad.estaColisionando = true;
                    vueltaInicio(panelJuego.jugador);
                }
                break;
            case "izquierda":
                entidad.solidArea.x -= entidad.velocidad;
                if (entidad.solidArea.intersects(panelJuego.jugador.solidArea)) {
                    entidad.estaColisionando = true;
                    vueltaInicio(panelJuego.jugador);
                }
                break;
            }
        entidad.solidArea.x = entidad.solidAreaXPorDefecto;
        entidad.solidArea.y = entidad.solidAreaYPorDefecto;
        panelJuego.jugador.solidArea.x = panelJuego.jugador.solidAreaXPorDefecto;
        panelJuego.jugador.solidArea.y = panelJuego.jugador.solidAreaYPorDefecto;
    }
    
    /**
     * Método que devuelve a una entidad a su punto de inicio.
     * @param entidad
     */
    public void vueltaInicio(Entidad entidad) {
        entidad.estaColisionando = true;
        HashMap<String, Integer> posInicial = panelJuego.posicionJugador;
        panelJuego.jugador.setPosicionInicial(posInicial.get("x"), posInicial.get("y"));
    }
}

/**
 * Clase ManejadorKey para manejar los eventos de pulsación de teclas.
 */
class ManejadorKey implements KeyListener {
    
    // Booleanos que determinan si una tecla está siendo pulsada o no.
    boolean leftPressed, rightPressed, upPressed, downPressed;
    
    @Override
    /**
     * 
     */
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    /**
     * Método que comprueba si una tecla está siendo pulsada.
     * @param e
     */
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        switch (code) {
            case KeyEvent.VK_LEFT:
                leftPressed = true;
                break;
            case KeyEvent.VK_UP:
                upPressed = true;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = true;
                break;
            case KeyEvent.VK_DOWN:
                downPressed = true;
                break;
            default:
                break;
        }
    }

    @Override
    /**
     * Método que comprueba si una tecla dejó de pulsarse.
     * @param e
     */
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        
        switch (code) {
            case KeyEvent.VK_LEFT:
                leftPressed = false;
                break;
            case KeyEvent.VK_UP:
                upPressed = false;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                break;
            case KeyEvent.VK_DOWN:
                downPressed = false;
                break;
            default:
                break;
        }
    }
}

// CLASES PRINCIPALES

/**
 * Clase Sombra usada para crear una filtro de sombra e iluminación central
 * alrededor del jugador.
 */
class Sombra {
    PanelDeJuego panelJuego;
    BufferedImage filtroSombra;
    int posX, posY;
    
    /**
     * Método constructor de la clase Sombra.
     * @param panel
     * @param sizeCirculo
     */
    public Sombra(PanelDeJuego panel, int sizeCirculo) {
        
        panelJuego = panel;
        
        filtroSombra = new BufferedImage(panelJuego.anchuraMundo, 
                panelJuego.alturaMundo, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2 = (Graphics2D) filtroSombra.getGraphics();
        
        // Area de la pantalla que estara con el filtro de sombra
        Area areaPantalla = new Area(new Rectangle2D.Double(0, 0, 
                panelJuego.anchuraPantalla, panelJuego.alturaPantalla));
        
        // Variables de las coordenadas donde se encuentra el circulo de luz
        int centroX = panelJuego.jugador.screenX + (panelJuego.tileSize)/2;
        int centroY = panelJuego.jugador.screenY + (panelJuego.tileSize)/2;
        
        double x = centroX - (sizeCirculo/2);
        double y = centroY - (sizeCirculo/2);
        
        // Forma del circulo de luz
        Shape formaCirculo = new Ellipse2D.Double(x - 10, y - 10, sizeCirculo, sizeCirculo);
        
        // Area del circulo de luz
        Area areaDeLuz = new Area(formaCirculo);
        
        // Colocamos el circulo en el centro del area de la pantalla
        areaPantalla.subtract(areaDeLuz);
        
        // Se le coloca el color de la sombra y lo dibujamos
        g2.setColor(new Color(0,0,0));
        g2.fill(areaPantalla);
        
        g2.dispose();
        
    }
    
    /**
     * Método que dibuja una sombra en pantalla que sigue al jugador.
     * @param g2
     */
    public void paint(Graphics2D g2) {      
        g2.drawImage(filtroSombra, 0, 0, null);
    }
}

/**
 * Clase Entidad usada como base para crear las entidades 
 * (jugador, enemigo, etc) del juego.
 */
class Entidad {
    // Atributos de posición, movimiento, tamaño y pantalla
    PanelDeJuego panelJuego;
    String nombre;
    public int worldX, worldY, solidAreaXPorDefecto, solidAreaYPorDefecto;
    public int velocidad;
    public String direccion;
    public int ancho, alto;
    
    // Atributos de colisiones
    public Rectangle solidArea;
    public boolean estaColisionando = false;
    
    /**
     * Método constructor de la clase Entidad.
     */
    public Entidad(PanelDeJuego panel) {
        panelJuego = panel;
    }
    
    /**
     * Método que actualiza la posición del enemigo y verifica las colisiones.
     */
    public void update() {
        // Control de colisiones
        estaColisionando = false;
        panelJuego.colChecker.colisionMundo(this);
        panelJuego.colChecker.colisionJugador(this);

        // Si no hay colisiones
        if (estaColisionando == false) {
            switch(direccion) {
                case "arriba": 
                    worldY -= velocidad;
                    break;
                case "abajo": 
                    worldY += velocidad;
                    break;
                case "izquierda":
                    worldX -= velocidad;
                    break;
                case "derecha": 
                    worldX += velocidad;
                    break;
            }
        }
    }

    /**
     * Método que asigna una posición en el mapa al enemigo.
     */
    public void setPosicion(int x, int y) {
        worldX = x * panelJuego.tileSize + 10;
        worldY = y * panelJuego.tileSize + 10;
    }
    
    /**
     * Método que dibuja a la entidad en la pantalla.
     */
    public void paint(Graphics2D g2) {
        int screenX = worldX - panelJuego.jugador.worldX + panelJuego.jugador.screenX;
        int screenY = worldY - panelJuego.jugador.worldY + panelJuego.jugador.screenY;
        
        g2.setColor(new Color(242, 66, 54));
        g2.fillOval(screenX, screenY, alto, ancho);
        g2.setColor(Color.black);
        g2.drawOval(screenX, screenY, alto, ancho);
    }
}

/**
 * Clase PelotaEnemigo que sirve para crear entidad que funcionan como enemigos
 * en el juego.
 */
class PelotaEnemigo extends Entidad {
    
    /**
     * Método constructor de la clase PelotaEnemigo.
     */
    public PelotaEnemigo(PanelDeJuego panel) {
        super(panel);
        
        // Area de colisión
        solidArea = new Rectangle();
        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.height = 8;
        solidArea.width = 6;
        solidAreaXPorDefecto = solidArea.x;
        solidAreaYPorDefecto = solidArea.y;
        
        // Otros atributos
        nombre = "Pelota";
        velocidad = 3;
        ancho = 10;
        alto = 10;
                
        // Dirección inicial
        Random rand = new Random();
        int direcciónInicial = rand.nextInt(2);
        switch (direcciónInicial) {
            case 0:
                direccion = "arriba";
                break;
            case 1:
                direccion = "derecha";
                break;
        }    
    }   
}

/**
 * Clase Jugador hija de la clase Entidad que representa al jugador.
 */
class Jugador extends Entidad{

    // Manejador de eventos
    ManejadorKey keyHandler;
    
    // Atributos de la posición de la cámara
    public final int screenX;
    public final int screenY;
    
    /**
     * Método constructor de la clase Jugador.
     * @param panel
     * @param keyH
     */
    public Jugador(PanelDeJuego panel, ManejadorKey keyH) {
        super(panel);
        this.keyHandler = keyH;
        
        // Area de colisión
        solidArea = new Rectangle();
        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.height = 8;
        solidArea.width = 6;
        solidAreaXPorDefecto = solidArea.x;
        solidAreaYPorDefecto = solidArea.y;
        
        // Posición del jugador en la pantalla
        screenX = (panelJuego.anchuraPantalla / 2) - (panelJuego.tileSize / 2);
        screenY = (panelJuego.alturaPantalla / 2) - (panelJuego.tileSize / 2);
        setPorDefecto();
    }
    
    /**
     * Método que dibuja al jugador en el panel de juego.
     * @param g
     */
    @Override
    public void paint(Graphics2D g) {
        int x = screenX;
        int y = screenY;     
           
        g.setColor(new Color(72, 169, 166));
        g.fillOval(x , y, ancho, alto);
        g.setColor(Color.black);
        g.drawOval(x, y, ancho, alto);
    }
    
    /**
     * Método que actualiza la posición y comprueba de las colisiones del jugador.
     */
    @Override
    public void update() {
        if (keyHandler.upPressed == true || keyHandler.leftPressed == true ||
                keyHandler.downPressed == true || keyHandler.rightPressed == true) {
            
            if (keyHandler.upPressed == true) {
                direccion = "arriba";
            }
            else if (keyHandler.leftPressed == true) {
                direccion = "izquierda";
            }
            else if (keyHandler.downPressed == true) {
                direccion = "abajo";
            }
            else if (keyHandler.rightPressed == true) {
                direccion = "derecha";
            }
                     
            // Control de colisiones con el mundo
            estaColisionando = false;
            panelJuego.colChecker.colisionMundo(this);
            
            // Control de colisiones con enemigos
            panelJuego.colChecker.colisionEntidad(this, panelJuego.enemigos);

            // Si no hay colisiones
            if (estaColisionando == false) {
                switch(direccion) {
                    case "arriba": 
                        worldY -= velocidad;
                        break;
                    case "abajo": 
                        worldY += velocidad;
                        break;
                    case "izquierda":
                        worldX -= velocidad;
                        break;
                    case "derecha": 
                        worldX += velocidad;
                        break;
                }
            }
        }
        
    }
    
    /**
     * Método que asigna la posición inicial del jugador en el laberinto.
     * @param posY
     * @param posX
     */
    public void setPosicionInicial(int posX, int posY) {
        this.worldX = posX * panelJuego.tileSize + 10;
        this.worldY = posY * panelJuego.tileSize + 10;
    }
    
    /**
     * Método que asigna valores por defecto a velocidad, alto, ancho y direccion
     * del jugador.
     */
    private void setPorDefecto() {
        this.velocidad = 5;
        this.alto = 10;
        this.ancho = 10;
        this.direccion = "arriba";
        this.nombre = "Jugador";
    }
}

/**
 * Clase Fabricaenemigo que implementa la interfaz Runnable para trabajar con hilos.
 * Es usada para crear enemigos cada cierta cantidad de tiempo.
 */
class FabricaEnemigo implements Runnable{
    PanelDeJuego panelJuego;
    LinkedList<HashMap<String, Integer>> posicionesPosibles;
    Thread hiloFabrica;
    int indice, sizeLista;
    
    /**
     * Método constructor de la clase FabricaEnemigo.
     */
    public FabricaEnemigo(PanelDeJuego panelJuego) {
        this.panelJuego = panelJuego;
        this.posicionesPosibles = new LinkedList();
        obtenerPosicionesPosibles();
        
        // Se establecen los valores del indice de enemigos y el número de posiciones posibles.
        this.indice = 0;
        this.sizeLista = this.posicionesPosibles.size();
    }
    
    /**
     * Método que obtiene todas las posiciones posibles donde pueden aparecer
     * los enemigos.
     */
    private void obtenerPosicionesPosibles() {
        
        int fila, columna;
        for (fila = 0; fila < panelJuego.maxFilaMundo; fila++) {
            for (columna = 0; columna < panelJuego.maxColumnaMundo; columna++) {
                if (panelJuego.laberinto.mapaLaberinto[fila][columna] == 0) {
                    HashMap<String, Integer> posicion = new HashMap();
                    posicion.put("x", columna);
                    posicion.put("y", fila);
                    posicionesPosibles.addLast(posicion);
                }
            }
        }
    }
    
    /**
     * Método que crea un enemigo en una posición posible aleatoria y lo coloca
     * en un arreglo de enemigos.
     */
    public void crearEnemigo() {
        PelotaEnemigo enemigo = new PelotaEnemigo(panelJuego);
        
        // Obtenemos una posición posible aleatoria donde colocar a nuestro enemigo.
        Random rand = new Random();
        
        int indiceAleatorio = rand.nextInt(sizeLista);
        HashMap<String, Integer> posicionAleatoria = posicionesPosibles.get(indiceAleatorio);
        
        // Lo colocamos en la posicion y lo guardamos en el arreglo de enemigos.
        
        enemigo.setPosicion(posicionAleatoria.get("x"), posicionAleatoria.get("y"));
        panelJuego.enemigos[indice] = enemigo;
        
        // Aumentamos el indice de nuestra arreglo de enemigos.
        indice++;
    }
    
    /**
     * Método que comienza el hilo de la clase FabricaEnemigo.
     */
    public void startThread() {
        hiloFabrica = new Thread(this);
        hiloFabrica.start();
    }
    
    
    @Override
    /**
     * Método que se ejecuta inmediatamente una vez haya comenzado el hilo.
     */
    public void run() {
        while (hiloFabrica != null) {

            crearEnemigo();
            
            try {
                Thread.sleep(6000);

            } catch (InterruptedException ex) {
                Logger.getLogger(PanelDeJuego.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

/**
 * Clase Laberinto que representa el mapa (laberinto) del juego.
 */
class Laberinto {
    
    // Atributos esenciales: panel de juego y mapa del laberinto.
    public PanelDeJuego panelJuego;
    public int[][] mapaLaberinto;
    
    /**
     * Método constructor de la clase Laberinto.
     * @param panel
     */
    public Laberinto(PanelDeJuego panel) throws FileNotFoundException {
        panelJuego = panel;
        mapaLaberinto = new int[panelJuego.maxFilaMundo][panelJuego.maxColumnaMundo];
        
        cargarMapa();
    }
    
    private void cargarMapa() throws FileNotFoundException {
        int fila, columna;
        String path = "./src/main/java/res/maps/map.txt";
        Scanner scan = new Scanner(new File(path));
        for (fila = 0; fila < panelJuego.maxFilaMundo; fila++) {
            String line = scan.nextLine();
            String[] numeros = line.split(" ");
            for (columna = 0; columna< panelJuego.maxColumnaMundo; columna++) {
                int numero = Integer.parseInt(numeros[columna]);
                mapaLaberinto[fila][columna] = numero;
            }
        }
    }
    
    /**
     * Método que dibuja el mapa (laberinto) en el panel de juego.
     */
    public void paint(Graphics2D g) {
        int fila, columna;

        for (fila = 0; fila < panelJuego.maxFilaMundo; fila++) {
            for (columna = 0; columna < panelJuego.maxColumnaMundo; columna++) {
                              
                int worldX = columna * panelJuego.tileSize;
                int worldY = fila * panelJuego.tileSize;
                int screenX = worldX - panelJuego.jugador.worldX + panelJuego.jugador.screenX;
                int screenY = worldY - panelJuego.jugador.worldY + panelJuego.jugador.screenY;
                
                switch (mapaLaberinto[fila][columna]) {
                    case 1:
                        g.setColor(new Color(67, 80, 88));
                        g.fillRect(screenX, screenY, 
                                panelJuego.tileSize, panelJuego.tileSize);
                        g.setColor(new Color(0, 0, 0));
                        g.drawRect(screenX, screenY, 
                                panelJuego.tileSize, panelJuego.tileSize);
                        break;
                    case 2:
                        g.setColor(new Color(129, 157, 174));
                        g.fillRect(screenX, screenY, 
                                panelJuego.tileSize, panelJuego.tileSize);
                        g.setColor(new Color(0, 0, 0));
                        g.drawRect(screenX, screenY, 
                                panelJuego.tileSize, panelJuego.tileSize);
                        break;
                    case 3:
                        g.setColor(new Color(220, 247, 99));
                        g.fillRect(screenX, screenY, 
                                panelJuego.tileSize, panelJuego.tileSize);
                        g.setColor(new Color(0, 0, 0));
                        g.drawRect(screenX, screenY, 
                                panelJuego.tileSize, panelJuego.tileSize);
                        break;
                    default:
                        break;
                }
            }
        }
    }
    
    /**
     * Método que encuentra la entrada (posición inicio) del laberinto.
     */
    public HashMap<String, Integer> encontrarEntrada() {
        int fila, columna;
        HashMap<String, Integer> posicion = new HashMap();
        for (fila = 0; fila < panelJuego.maxFilaMundo; fila++) {
            for (columna = 0; columna < panelJuego.maxColumnaMundo; columna++) {
                
                if (this.mapaLaberinto[fila][columna] == 2) {
                    posicion.put("x", columna);
                    posicion.put("y", fila);
                }
            }
        }

        return posicion;
    }
}

/**
 * Clase PanelDeJuego hija de JPanel que implementa la interfaz Runnable
 * para poder trabajar con hilos.
 */
class PanelDeJuego extends JPanel implements Runnable {

    // Configuración de Panel
    final int FPS = 15;
    public final int tileSize = 30;
    public final int maxFilaPantalla = 15;
    public final int maxColumnaPantalla = 12;
    final int anchuraPantalla = tileSize * maxColumnaPantalla;
    final int alturaPantalla = tileSize * maxFilaPantalla;
    
    // Configuración de mundo
    public final int maxFilaMundo = 19;
    public final int maxColumnaMundo = 15;
    public final int anchuraMundo = maxColumnaMundo * tileSize;
    public final int alturaMundo = maxFilaMundo * tileSize;

    // Clases de generación y manejo del mapa
    public Laberinto laberinto;
    public ManejadorKey keyHandler = new ManejadorKey();
    public ManejadorColision colChecker = new ManejadorColision(this);
    public ManejadorAmbiente envHandler;
    public Thread hiloJuego;
    public InterfazGrafica interfazGrafica = new InterfazGrafica(this);
    
    // Entidades
    public Jugador jugador;
    public FabricaEnemigo fabrica;
    public Entidad[] enemigos = new Entidad[20];
    public HashMap<String, Integer> posicionJugador;
        
    /**
     * Método constructor de la clase PanelDeJuego.
     */
    public PanelDeJuego() throws FileNotFoundException {
        this.setPreferredSize(new Dimension(anchuraPantalla, alturaPantalla));
        setBackground(new Color(241, 242, 238));
        this.setDoubleBuffered(true);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
        
        this.jugador = new Jugador(this, keyHandler);
        laberinto = new Laberinto(this);
        fabrica = new FabricaEnemigo(this);
        posicionJugador = laberinto.encontrarEntrada();
        jugador.setPosicionInicial(
                posicionJugador.get("x"), 
                posicionJugador.get("y"));
        
        envHandler = new ManejadorAmbiente(this);
        envHandler.setup();
        
        fabrica.startThread();
        interfazGrafica.startThread();
    }
    
    @Override
    /**
     * Método que dibuja todos los componentes necesarios del panel de juego.
     * @param g
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;
        
        // Pintar laberinto
        laberinto.paint(g2);
        
        // Pintar jugador
        jugador.paint(g2);
        
        // Pintar enemigos
        for (int i = 0; i < enemigos.length; i++) {
            if (enemigos[i] != null) {
                enemigos[i].paint(g2);
            }
        }
        
        // Pintar la sombra y luz alrededor del jugador
        envHandler.paint(g2);
        
        // Pintar interfaz gráfica
        interfazGrafica.paint(g2);
                
        g2.dispose();
    }
    
    /**
     * Método que actualiza los elementos en el panel de juego (jugador y enemigos).
     */
    public void update() {
        // Actualizar jugador
        jugador.update();
        
        // Actualizar enemigos
        for (int i = 0; i < enemigos.length; i++) {
            if (enemigos[i] != null) {
                enemigos[i].update();
            }
        }
    }
    
    /**
     * Método que comienza el hilo de la clase PanelDeJuego.
     */
    public void startThread() {
        hiloJuego = new Thread(this);
        hiloJuego.start();
    }

    @Override
    /**
     * Método que se ejecuta inmediatamente una vez haya comenzado el hilo.
     */
    public void run() {

        double drawInterval = 1000000000 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (hiloJuego != null) {

            update();

            repaint();
            
            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);

                nextDrawTime += drawInterval;
            } catch (InterruptedException ex) {
                Logger.getLogger(PanelDeJuego.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

/**
 * 
 */
public class Main {
     
    /**
     * Método principal que ejecuta todo el programa.
     * @param args
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        JFrame window = new JFrame("Laberinto de la Oscuridad");
        window.setBackground(Color.black);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        PanelDeJuego panelDeJuego = new PanelDeJuego();
        window.add(panelDeJuego);

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);
        
        panelDeJuego.startThread();
    }
}