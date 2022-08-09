package br.com.cod3r.calc;

@FunctionalInterface
public interface MemoriaObservador {
	void valorAlterado(String novoValor);
}
