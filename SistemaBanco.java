import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Conta {
    private double saldo;
    private final Lock lock = new ReentrantLock();

    public Conta(double saldoInicial) {
        this.saldo = saldoInicial;
    }

    public double getSaldo() {
        return saldo;
    }

    public void depositar(double valor) {
        lock.lock();
        try {
            saldo += valor;
        } finally {
            lock.unlock();
        }
    }

    public boolean sacar(double valor) {
        lock.lock();
        try {
            if (saldo >= valor) {
                saldo -= valor;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}

class Loja {
    private final Conta conta;

    public Loja(Conta conta) {
        this.conta = conta;
    }

    public synchronized void pagarFuncionarios() {
        double salarioFuncionario = 1400.0;
        if (conta.sacar(salarioFuncionario * 2)) {
            System.out.println("Salário do funcionário: R$" + salarioFuncionario );
        }
    }

    public void receberPagamento(double valor) {
        conta.depositar(valor);
    }

    public Conta getConta() {
        return conta;
    }
}

class Funcionario extends Thread {
    private final Conta contaSalario;
    private final Conta contaInvestimento;

    public Funcionario(Conta contaSalario, Conta contaInvestimento) {
        this.contaSalario = contaSalario;
        this.contaInvestimento = contaInvestimento;
    }

    @Override
    public void run() {
        double salario = 1400.0;
        contaSalario.depositar(salario);
        double valorInvestimento = salario * 0.2;
        contaInvestimento.depositar(valorInvestimento);
    }
}

class Cliente extends Thread {
    private final Loja[] lojas;
    private final Conta conta;
    private final int id;

    public Cliente(int id, Conta conta, Loja[] lojas) {
        this.id = id;
        this.conta = conta;
        this.lojas = lojas;
    }

    @Override
    public void run() {
        while (true) {
            double valorCompra = Math.random() < 0.5 ? 100.0 : 200.0;
            int lojaIndex = (int) (Math.random() * lojas.length);
            Loja loja = lojas[lojaIndex];
            synchronized (loja) {
                if (conta.sacar(valorCompra)) {
                    loja.receberPagamento(valorCompra);
                    System.out.println("Cliente " + id + " realizou compra de R$" + valorCompra);
                } else {
                    break;
                }
            }
        }
    }
}

public class SistemaBanco {
    public static void main(String[] args) {
        Conta bancoConta = new Conta(0);
        Loja loja1 = new Loja(new Conta(0));
        Loja loja2 = new Loja(new Conta(0));
        Loja[] lojas = {loja1, loja2};

        for (int i = 0; i < 4; i++) {
            Funcionario funcionario = new Funcionario(lojas[i / 2].getConta(), new Conta(0));
            funcionario.start();
        }

        Queue<Cliente> filaClientes = new LinkedList<>();

        for (int i = 1; i <= 5; i++) {
            Cliente cliente = new Cliente(i, new Conta(1000), lojas);
            filaClientes.offer(cliente);
        }

        while (!filaClientes.isEmpty()) {
            Cliente cliente = filaClientes.poll();
            cliente.start();
            try {
                cliente.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        loja1.pagarFuncionarios();
        loja2.pagarFuncionarios();

        // Imprimir saldos finais das lojas
        System.out.println("Lucro da loja 1: R$" + loja1.getConta().getSaldo());
        System.out.println("Lucro da loja 2: R$" + loja2.getConta().getSaldo());
    }
}















