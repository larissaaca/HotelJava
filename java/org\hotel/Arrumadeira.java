package org.hotel;

import java.util.Random;

public class Arrumadeira implements Runnable {
    private final Hotel hotel;

    public Arrumadeira(Hotel hotel) {
        this.hotel = hotel;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (hotel.getTravaArrumacao()) {
                synchronized (hotel.getTravaRecepcao()) {
                    for (Quarto quarto : hotel.getQuartos()) {
                        if (!quarto.getOcupado() && !quarto.foiLimpo()) {
                            quarto.limpar();
                            System.out.println("Arrumadeira limpou o quarto " + quarto.getNumero());
                        }
                    }
                }
            }
        }
    }
}
