package org.hotel;

public class Main {
    public static void main(String[] args) {
        Hotel hotel = new Hotel();

        // Inicia arrumadeiras
        for (Arrumadeira arrumadeira : hotel.getArrumadeiras()) {
            new Thread(arrumadeira).start();
        }

        // Inicia recepcionistas
        for (Recepcionista recepcionista : hotel.getRecepcionistas()) {
            new Thread(recepcionista).start();
        }

        // Inicia hóspedes
        for (Hospede hospede : hotel.getHospedes()) {
            new Thread(hospede).start();
        }
    }
}
