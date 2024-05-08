package org.hotel;

public class Quarto {
    private final int numero;
    
    // Esta variável indica se o quarto está ocupado ou não (quando o hóspede sai temporariamente do quarto, ele não fica ocupado, mas a variável isReserved continuará verdadeira).
    private boolean ocupado;
    private boolean limpo;
    
    // Esta variável indica se o quarto está reservado ou não.
    private boolean isReserved;

    public Quarto(int numero) {
        this.numero = numero;
        this.ocupado = false;
        this.limpo = true;
        this.isReserved = false;
    }

    public synchronized boolean estaDisponivel() {
        return !isReserved && limpo;
    }

    public synchronized void ocupar() {
        ocupado = true;
        limpo = false;
        isReserved = true;
    }

    public synchronized void desocupar() {
        ocupado = false;
        isReserved = false;
    }

    public synchronized void limpar() {
        limpo = true;
    }

    public synchronized boolean foiLimpo() {
        return limpo;
    }
    
    // FUNÇÕES RELACIONADAS AO HÓSPEDE SAIR DO QUARTO TEMPORARIAMENTE
    public synchronized void sair() {
    	this.ocupado = false;
    }
    
    public synchronized void voltarAoQuarto() {
    	this.ocupado = true;
    }
    
    // GETTERS
    
    public boolean getOcupado () {
		return ocupado;
    }

    public int getNumero() {
        return numero;
    }

    public int getCapacidade() {
        return 4; // Supondo que cada quarto tenha capacidade para 4 hóspedes
    }
}
