import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

class Hotel {
    private final List<Quarto> quartos;
    private final List<Recepcionista> recepcionistas;
    private final List<Camareira> camareiras;
    private final Semaphore semaforo;
    private final List<Integer> filaEspera;

    public Hotel(int numQuartos, int numRecepcionistas, int numCamareiras) {
        this.quartos = new ArrayList<>();
        this.recepcionistas = new ArrayList<>();
        this.camareiras = new ArrayList<>();
        this.semaforo = new Semaphore(numQuartos, true);
        this.filaEspera = new ArrayList<>();
        for (int i = 0; i < numQuartos; i++) {
            quartos.add(new Quarto(i + 1));
        }
        for (int i = 0; i < numRecepcionistas; i++) {
            recepcionistas.add(new Recepcionista(this, "Recepcionista " + (i + 1)));
        }
        for (int i = 0; i < numCamareiras; i++) {
            camareiras.add(new Camareira(this, "Camareira " + (i + 1)));
        }
    }

    public synchronized boolean checkIn(Hospede hospede) {
        try {
            semaforo.acquire();
            for (Quarto quarto : quartos) {
                if (quarto.isVago()) {
                    quarto.checkIn(hospede);
                    return true;
                }
            }
            filaEspera.add(hospede.getIdHospede());
            return false; 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            semaforo.release();
        }
    }

    public synchronized void checkOut(Quarto quarto) {
        quarto.checkOut();
        filaEspera.remove(0);
        semaforo.release();
    }

    public List<Quarto> getQuartos() {
        return quartos;
    }

    public List<Recepcionista> getRecepcionistas() {
        return recepcionistas;
    }

    public List<Camareira> getCamareiras() {
        return camareiras;
    }

    public List<Integer> getFilaEspera() {
        return filaEspera;
    }
}

class Quarto {
    private final int numero;
    private boolean vago = true;

    public Quarto(int numero) {
        this.numero = numero;
    }

    public synchronized boolean isVago() {
        return vago;
    }

    public synchronized void checkIn(Hospede hospede) {
        vago = false;
        System.out.println("Hóspede " + hospede.getIdHospede() + " fez check-in no quarto " + numero);
    }

    public synchronized void checkOut() {
        vago = true;
        System.out.println("Quarto " + numero + " foi liberado");
    }

    public int getNumero() {
        return numero;
    }
}

class Hospede extends Thread {
    private final Hotel hotel;
    private final int idHospede;
    private int tentativas = 0;

    public Hospede(Hotel hotel, int idHospede) {
        this.hotel = hotel;
        this.idHospede = idHospede;
    }

    public int getIdHospede() {
        return idHospede;
    }

    @Override
    public void run() {
        while (tentativas < 2) {
            if (hotel.checkIn(this)) {
                System.out.println("Hóspede " + idHospede + " fez check-in com sucesso.");
                try {
                    Thread.sleep(2000); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                Quarto quarto = hotel.getQuartos().stream().filter(Quarto::isVago).findFirst().orElse(null);
                if (quarto != null) {
                    hotel.checkOut(quarto); 
                    break;
                } else {
                    System.out.println("Hóspede " + idHospede + " não conseguiu fazer check-out. Não há quartos disponíveis.");
                }
            } else {
                System.out.println("Hóspede " + idHospede + " não conseguiu fazer check-in. Não há quartos disponíveis.");
                tentativas++;
                try {
                    Thread.sleep(5000); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (tentativas == 2) {
            System.out.println("Hóspede " + idHospede + " deixou uma reclamação e foi embora.");
        }
    }
}

class Recepcionista extends Thread {
    private final Hotel hotel;
    private final String nome;
    private int tentativas = 0;

    public Recepcionista(Hotel hotel, String nome) {
        this.hotel = hotel;
        this.nome = nome;
    }

    @Override
    public void run() {
        while (tentativas < 2) {
            synchronized (hotel) {
                List<Integer> filaEspera = hotel.getFilaEspera();
                for (Integer idHospede : filaEspera) {
                    Quarto quarto = hotel.getQuartos().stream().filter(q -> q.isVago()).findFirst().orElse(null);
                    if (quarto != null) {
                        System.out.println(nome + " encaminhou o hóspede " + idHospede + " para o quarto " + quarto.getNumero());
                        filaEspera.remove(idHospede);
                        break;
                    }
                }
            }
            tentativas++;
        }
    }
}


class Camareira extends Thread {
    private final Hotel hotel;
    private final String nome;
    private int tentativas = 0;

    public Camareira(Hotel hotel, String nome) {
        this.hotel = hotel;
        this.nome = nome;
    }

    @Override
    public void run() {
        while (tentativas < 1) {
            synchronized (hotel) {
                for (Quarto quarto : hotel.getQuartos()) {
                    if (!quarto.isVago() && quarto.getNumero() % hotel.getCamareiras().size() == Integer.parseInt(nome.split(" ")[1])) {
                        System.out.println(nome + " limpou o quarto " + quarto.getNumero());
                    }
                }
            }
            tentativas++;
        }
    }
}
public class AT3 {
    public static void main(String[] args) {
        Hotel hotel = new Hotel(10, 5, 10);
        List<Hospede> hospedes = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Hospede hospede = new Hospede(hotel, i + 1);
            hospedes.add(hospede);
            hospede.start();
        }
        for (Recepcionista recepcionista : hotel.getRecepcionistas()) {
            recepcionista.start();
        }
        for (Camareira camareira : hotel.getCamareiras()) {
            camareira.start();
        }
    }
}









