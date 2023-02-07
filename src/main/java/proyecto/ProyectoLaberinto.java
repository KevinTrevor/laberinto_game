/**
 * Hecho por: Kevin Rojas C.I: 29.582.382
 */
package proyecto;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kevin Rojas
 */
// INTERFACES DEL PROYECTO
interface FabricaEnemigo {

    public Enemigo crearEnemigo();
}

interface Enemigo {

    public void paint(Graphics grafico);
}

// CLASES PRINCIPALES
class CreadorPelota implements FabricaEnemigo {

    @Override
    public Enemigo crearEnemigo() {
        return null;
    }

}

class CreadorLaser implements FabricaEnemigo {

    @Override
    public Enemigo crearEnemigo() {
        return null;
    }

}

class Laser implements Enemigo, Runnable{

    @Override
    public void paint(Graphics grafico) {

    }

    @Override
    public void run() {
        
    }

}

class Pelota implements Enemigo, Runnable {

    @Override
    public void paint(Graphics grafico) {

    }

    @Override
    public void run() {
        
    }

}

class ManejadorEvento implements KeyListener {
    boolean leftPressed, rightPressed, upPressed, downPressed;
    
    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        if (code == KeyEvent.VK_LEFT) { // IZQUIERDA
            leftPressed = true;
        }
        if (code == KeyEvent.VK_UP) { // ARRIBA
            upPressed = true;
        }
        if (code == KeyEvent.VK_RIGHT) { // DERECHA
            rightPressed = true;
        }
        if (code == KeyEvent.VK_DOWN) { // ABAJO
            downPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        
        if (code == KeyEvent.VK_LEFT) { // IZQUIERDA
            leftPressed = false;
        }
        if (code == KeyEvent.VK_UP) { // ARRIBA
            upPressed = false;
        }
        if (code == KeyEvent.VK_RIGHT) { // DERECHA
            rightPressed = false;
        }
        if (code == KeyEvent.VK_DOWN) { // ABAJO
            downPressed = false;
        }
    }

}

class Jugador implements Runnable {

    private int x;
    private int y;
    private final int alto = 30;
    private final int ancho = 30;
    private final int movimiento = 30;

    public void paint(Graphics grafico) {
        grafico.setColor(Color.pink);
        grafico.fillOval(x , y, ancho, alto);
        grafico.setColor(Color.black);
        grafico.drawOval(x, y, ancho, alto);
        
        grafico.dispose();
    }

    public void setPosition(int posX, int posY) {
        this.x = posX * movimiento;
        this.y = posY * movimiento;
    }

    @Override
    public void run() {
        
    }
    
    public void movimientoY(int value) {
        this.y += value;
    }
    
    public void movimientoX(int value) {
        this.x += value;
    }
}

class Laberinto {

    private final int[][] laberinto;
    private int fila = 0;
    private int columna = 0;
    private final int maximoFilas = 19;
    private final int maximoColumnas = 15;
    private final int altoBloque = 30;
    private final int anchoBloque = 30;

    public Laberinto(int[][] laberinto) {
        this.laberinto = laberinto;
    }

    public void paint(Graphics grafico) {
        for (fila = 0; fila < maximoFilas; fila++) {
            for (columna = 0; columna < maximoColumnas; columna++) {
                if (laberinto[fila][columna] == 1) {
                    grafico.setColor(Color.darkGray);
                    grafico.fillRect(
                            columna * 30,
                            fila * 30,
                            anchoBloque,
                            altoBloque
                    );
                    grafico.setColor(Color.black);
                    grafico.drawRect(
                            columna * 30,
                            fila * 30,
                            anchoBloque,
                            altoBloque
                    );
                }
            }
        }
    }

    public HashMap<String, Integer> encontrarEntrada() {
        HashMap<String, Integer> posicion = new HashMap();
        for (fila = 0; fila < this.maximoFilas; fila++) {
            for (columna = 0; columna < this.maximoColumnas; columna++) {
                if (this.laberinto[fila][columna] == 2) {
                    posicion.put("x", columna);
                    posicion.put("y", fila);
                }
            }
        }

        return posicion;
    }
}

class PanelJuego extends Panel implements Runnable {
    
    final int FPS = 15;
    Laberinto laberinto;
    Jugador jugador;
    FabricaEnemigo fabrica;
    Thread hiloDeJuego;
    ManejadorEvento evento = new ManejadorEvento();
    

    public PanelJuego(int[][] laberinto) {
        this.laberinto = new Laberinto(laberinto);
        this.jugador = new Jugador();
        this.addKeyListener(evento);
        setFocusable(true);
        
        HashMap<String, Integer> posicionJugador = this.laberinto.encontrarEntrada();
        
        jugador.setPosition(
                posicionJugador.get("x"),
                posicionJugador.get("y"));
    }
    
    // MÉTODOS RELACIONADOS CON LOS HILOS
    
    public void startGameThread() {
        hiloDeJuego = new Thread(this);
        hiloDeJuego.start();
    }
    
    @Override
    public void run() {
        
        double drawInterval = 1000000000 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;
        
        while (hiloDeJuego != null) {
            
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
                Logger.getLogger(PanelJuego.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    // MÉTODOS DE DIBUJO Y UPDATE DEL JUEGO
    
    @Override
    public void paint(Graphics grafico) {
        this.laberinto.paint(grafico);
        this.jugador.paint(grafico);
    }
    
    public void update() {
        if (evento.upPressed == true) {
            jugador.movimientoY(-30);
        }
        else if (evento.leftPressed == true) {
            jugador.movimientoX(-30);
        }
        else if (evento.downPressed == true) {
            jugador.movimientoY(30);
        }
        else if (evento.rightPressed == true) {
            jugador.movimientoX(30);
        }
    }
    
    

}

public class ProyectoLaberinto {

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        int i, j;
        int maxFila = 19;
        int maxColumna = 15;
        int[][] mapa = new int[maxFila][maxColumna];
        String path = "./src/main/java/res/maps/map01.txt";
        Scanner scan = new Scanner(new File(path));
        for (i = 0; i < maxFila; i++) {
            String line = scan.nextLine();
            System.out.println(line);
            String[] numeros = line.split(" ");
            for (j = 0; j < maxColumna; j++) {
                int numero = Integer.parseInt(numeros[j]);
                mapa[i][j] = numero; 
            }
        }
        
        for (i = 0; i < maxFila; i++) {
            if (i > 0) {
                System.out.println("\n");
            }
           
            for (j = 0; j < maxColumna; j++) {
                System.out.print(mapa[i][j] + " ");
            }
        }
    }
}
