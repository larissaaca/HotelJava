package org.hotel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Hotel {
    private static final int NUM_QUARTOS = 10;
    private static final int NUM_HOSPEDES = 50;
    private static final int NUM_ARRUMADEIRAS = 10;
    private static final int NUM_RECEPCIONISTAS = 5;

    private final List<Quarto> quartos = new ArrayList<>();
    private final List<Hospede> hospedes = new ArrayList<>();
    private final List<Arrumadeira> arrumadeiras = new ArrayList<>();
    private final List<Recepcionista> recepcionistas = new ArrayList<>();
    private final Queue<Hospede> listaEspera = new LinkedList<>();

    private final Object travaRecepcao = new Object();
    private final Object travaArrumacao = new Object();
    private final Object travaHospede = new Object();

    public Hotel() {
        // Inicializa os quartos
        for (int i = 0; i < NUM_QUARTOS; i++) {
            quartos.add(new Quarto(i + 1));
        }

        // Inicializa arrumadeiras, recepcionistas e hóspedes
        for (int i = 0; i < NUM_ARRUMADEIRAS; i++) {
            arrumadeiras.add(new Arrumadeira(this));
        }

        for (int i = 0; i < NUM_RECEPCIONISTAS; i++) {
            recepcionistas.add(new Recepcionista(this));
        }

        for (int i = 0; i < NUM_HOSPEDES; i++) {
            hospedes.add(new Hospede(this, "Hóspede " + i, 1 + (int) (Math.random() * 12))); // Tamanho do grupo entre 1 e 12
        }
    }

    public Quarto getQuartoDisponivel() {
        synchronized (travaRecepcao) {
            for (Quarto quarto : quartos) {
                if (quarto.estaDisponivel()) {
                    return quarto;
                }
            }
        }
        return null;
    }

    public Quarto getQuartoDisponivelParaGrupo(int tamanhoGrupo) {
        synchronized (travaRecepcao) {
            for (Quarto quarto : quartos) {
                if (quarto.estaDisponivel() && quarto.getCapacidade() >= tamanhoGrupo) {
                    return quarto;
                }
            }
        }
        return null;
    }

    public void adicionarHospedeNaListaEspera(Hospede hospede) {
        synchronized (travaHospede) {
            listaEspera.add(hospede);
        }
    }

    public Hospede pegarProximoHospedeDaListaEspera() {
        synchronized (travaHospede) {
            return listaEspera.poll();
        }
    }

    public Object getTravaRecepcao() {
        return travaRecepcao;
    }

    public Object getTravaArrumacao() {
        return travaArrumacao;
    }

    public List<Quarto> getQuartos() {
        return quartos;
    }

    public List<Hospede> getHospedes() {
        return hospedes;
    }

    public List<Arrumadeira> getArrumadeiras() {
        return arrumadeiras;
    }

    public List<Recepcionista> getRecepcionistas() {
        return recepcionistas;
    }

    public Queue<Hospede> getListaEspera() {
        return listaEspera;
    }

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
