package org.hotel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Recepcionista implements Runnable {
    private final Hotel hotel;
    private List<Chave> chaves = new ArrayList<Chave> ();
    private Semaphore semaforo = new Semaphore (1);

    public Recepcionista(Hotel hotel) {
        this.hotel = hotel;
    }
    
    @Override
    public void run() {
        Random random = new Random();
        while (true) {
            synchronized (hotel.getTravaRecepcao()) {
                Hospede hospede = hotel.pegarProximoHospedeDaListaEspera();
                if (hospede != null) {
                	hospede.run();
   
                }
            }

            try {
                Thread.sleep(random.nextInt(5000)); // Tempo de espera aleatório antes da próxima tentativa de alocação
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    // FUNÇÕES RELACIONADAS ÀS CHAVES
    
    // Receber a chave do hospede (simplesmente armazena na lista).
    public void receberChaveDoHospede(Hospede hospede) {
    	try {
    		semaforo.acquire();
    		Chave chaveHospede = hospede.getChave();
    		hospede.setChave(null);

    		chaves.add(chaveHospede);
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	} finally {
    		semaforo.release();
    	}
    }
    
    // Retornar chaves para o hospede
    public Chave retornarChaveParaHospede(Hospede hospede) {
    	try {
    		semaforo.acquire();
    		for (Chave chave : chaves) {
        		if (chave.getQuarto().getNumero() == hospede.getNumeroQuarto()) {
        			
        			this.chaves.remove(chave);
        			hospede.setChave(chave);
        			
        			
        			return chave;
        		}
        	}
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	} finally {
    		semaforo.release();
    	}
    	
    	return null;
    }
    
    public Semaphore getSemaforo() {
		return semaforo;
	}
}
