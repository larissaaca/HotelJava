package org.hotel;

import java.util.Random;

public class Hospede implements Runnable {
    private final Hotel hotel;
    private final String nome;
    private final int tamanhoGrupo;
    private int hospedesSemQuarto;
    private Chave chave;
    private int numeroQuarto;
    
    // variável que indica a quantidade de vezes que o hóspede tentou entrar em um dos quartos.
    private int quantidadeTentativas;

    public Hospede(Hotel hotel, String nome, int tamanhoGrupo) {
        this.hotel = hotel;
        this.nome = nome;
        this.tamanhoGrupo = tamanhoGrupo;
        this.hospedesSemQuarto = tamanhoGrupo;
        this.quantidadeTentativas = 0;
    }

    @Override
    public void run() {
        Random random = new Random();
        Quarto quarto = null; // Inicializa a variável quarto como null
        try {
            Thread.sleep(random.nextInt(5000)); // Tempo de chegada aleatório

            alocarHospedeNoQuarto(random);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void alocarHospedeNoQuarto(Random random) throws InterruptedException {
        // Procura um quarto de hotel disponível
        Quarto quarto = hotel.getQuartoDisponivel();
        
        if (quarto != null) {
            // Ocupa esse quarto
            ocuparQuarto(quarto, random);
            
            // Se os hóspedes sem quarto forem menor do que zero, então todos os hóspedes desse grupo já estão acomodados
            if (this.hospedesSemQuarto <= 0) {
                return;
            } else {
                // Se os hóspedes sem quarto ainda existirem, então procuramos outro quarto para acomodá-los.
                alocarHospedeNoQuarto(random);
            }
        } else {
            // Se não encontrar, coloca o hóspede na fila de espera.

            // Aumenta a quantidade de tentativas que o hospede já tentou.
            this.quantidadeTentativas += 1;
            
            if (quantidadeTentativas >= 2) {
                System.out.println("Hóspede " + this.nome + " não conseguiu encontrar quartos e saiu reclamando");
                this.hotel.getHospedes().remove(this);
                
                return;
            } else {
                hotel.adicionarHospedeNaListaEspera(this);
                System.out.println(nome + " adicionado à lista de espera");
            }
        }
    }
    
    private void ocuparQuarto(Quarto quarto, Random random) throws InterruptedException {
        this.hospedesSemQuarto -= quarto.getCapacidade();
        
        synchronized (hotel.getTravaRecepcao()) {
            quarto.ocupar();
            this.chave = new Chave(this, quarto);
            this.numeroQuarto = quarto.getNumero();
            
            if (this.hospedesSemQuarto > 0) {
                System.out.println(nome + " ocupou o quarto " + quarto.getNumero() + " com um grupo de " + quarto.getCapacidade() + " hóspedes");
            } else {
                System.out.println(nome + " ocupou o quarto " + quarto.getNumero() + " com um grupo de " + this.tamanhoGrupo + " hóspedes");
            }
        }
        
        Thread.sleep(random.nextInt(2000)); // Tempo de permanência aleatório
        
        if (random.nextBoolean()) {
            sairPorUmTempo(random);
        }
        
        Thread.sleep(random.nextInt(2000)); // Tempo de permanência aleatório

        synchronized (hotel.getTravaRecepcao()) {
            quarto.desocupar();
            System.out.println(nome + " desocupou o quarto " + quarto.getNumero());
        }
    }   
    
    // Método que faz o hóspede sair do quarto por um tempo.
    private void sairPorUmTempo(Random random) {
        System.out.println(nome + " saiu do quarto...");
        int index = this.hotel.getQuartos().indexOf(this.chave.getQuarto());
        Quarto quarto = this.hotel.getQuartos().get(index);
        quarto.sair();
        
        Recepcionista recepcionista = selecionarRecepcionista();
        
        recepcionista.receberChaveDoHospede(this);
        
        try {
            Thread.sleep(random.nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        recepcionista.retornarChaveParaHospede(this);
        
        System.out.println(nome + " voltou ao quarto");
        quarto.voltarAoQuarto();
    }
    
    // Seleciona um recepcionista aleatoriamente
    private Recepcionista selecionarRecepcionista() {
        try {
            Random random = new Random();
            int i = random.nextInt(hotel.getRecepcionistas().size());
            return hotel.getRecepcionistas().get(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String getNome() {
        return nome;
    }

    public int getTamanhoGrupo() {
        return tamanhoGrupo;
    }
    
    public void setChave(Chave chave) {
        this.chave = chave;
    }
    
    public Chave getChave() {
        return chave;
    }
    
    public int getNumeroQuarto() {
        return numeroQuarto;
    }
}
