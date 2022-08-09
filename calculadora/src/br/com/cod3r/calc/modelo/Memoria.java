package br.com.cod3r.calc.modelo;

import java.util.ArrayList;
import java.util.List;

import br.com.cod3r.calc.MemoriaObservador;

public class Memoria {
	private Memoria() {
	}
	private enum TipoComando {
		ZERAR, NUMERO, DIV, MULT, SUB, SOMA, IGUAL, SINAL, PORC, VIRGULA;
	};
	private final List<MemoriaObservador> observadores = new ArrayList<>();
	private static final Memoria istancia = new Memoria();
	private TipoComando ultimaOperacao = null;
	private boolean substituir = false;
	private String textoAtual = "";
	private String textoBuffer = "";
	public static Memoria getIstancia() {
		return istancia;
	}
	public String getTextoAtual() {
		return textoAtual.isEmpty() ? "0" : textoAtual;
	}
	public void adicionarObservador(MemoriaObservador observador) {
		observadores.add(observador);
	}
	public void processarComando(String comando) {
		TipoComando tipoComando = detectarTipoComando(comando);
		if(tipoComando == null) {
			return;
		}else if(tipoComando == TipoComando.ZERAR) {
			textoAtual = "";
			textoBuffer = "";
			substituir = false;
			ultimaOperacao = null;
		}else if(tipoComando == TipoComando.SINAL && !textoAtual.contains("-")) {
			textoAtual = "-" + textoAtual;
		}else if(tipoComando == TipoComando.SINAL && textoAtual.contains("-")) {
			textoAtual = textoAtual.substring(1);
		}else if(tipoComando == TipoComando.NUMERO || tipoComando == TipoComando.VIRGULA) {
			textoAtual = substituir ? comando : textoAtual + comando;
			substituir = false;
		}else if(tipoComando == TipoComando.PORC){
			if(!textoBuffer.isEmpty() && !textoAtual.isEmpty() && ultimaOperacao != null) {
				substituir = true;
				textoAtual = obterPorcentagem();
			}else {
				textoAtual = "";
				textoBuffer = "";
				substituir = false;
				ultimaOperacao = null;
			}
		}else {
			substituir = true;
			textoAtual = obterResultadoOperacao();
			textoBuffer =textoAtual;
			ultimaOperacao = tipoComando;
		}
		observadores.forEach(o -> o.valorAlterado(getTextoAtual()));
	}
	private String obterPorcentagem() {
		double numeroBuffer = Double.parseDouble(textoBuffer.replace(",", "."));
		double numeroAtual = Double.parseDouble(textoAtual.replace(",", "."));
		double resultado = (numeroBuffer / 100) * numeroAtual;
		String resultadoString = Double.toString(resultado).replace(".", ",");
		return resultadoString.endsWith(",0") ? resultadoString.replace(",0", "") : resultadoString;
	}
	private String obterResultadoOperacao() {
		if(ultimaOperacao == null || ultimaOperacao == TipoComando.IGUAL) {
			return textoAtual;
		}
		double numeroBuffer = Double.parseDouble(textoBuffer.replace(",", "."));
		double numeroAtual = Double.parseDouble(textoAtual.replace(",", "."));
		double resultado = 0;
		if(ultimaOperacao == TipoComando.SOMA) {
			resultado = numeroBuffer + numeroAtual;
		}else if(ultimaOperacao == TipoComando.SUB) {
			resultado = numeroBuffer - numeroAtual;
		}else if(ultimaOperacao == TipoComando.MULT) {
			resultado = numeroBuffer * numeroAtual;
		}else if(ultimaOperacao == TipoComando.DIV) {
			resultado = numeroBuffer / numeroAtual;
		}
		String resultadoString = Double.toString(resultado).replace(".", ",");
		boolean inteiro = resultadoString.endsWith(",0");
		return inteiro ? resultadoString.replace(",0", "") : resultadoString;
	}
	private TipoComando detectarTipoComando(String comando) {
		if(textoAtual.isEmpty() && comando == "0" || substituir && comando == "0") {
			return null;
		}
		try {
			Integer.parseInt(comando);
			return TipoComando.NUMERO;
		}catch(NumberFormatException e ) {
			if("AC".equals(comando)) {
				return TipoComando.ZERAR;
			}else if("/".equals(comando)) {
				return TipoComando.DIV;
			}else if("*".equals(comando)) {
				return TipoComando.MULT;
			}else if("+".equals(comando)) {
				return TipoComando.SOMA;
			}else if("-".equals(comando)) {
				return TipoComando.SUB;
			}else if("=".equals(comando)) {
				return TipoComando.IGUAL;
			}else if(",".equals(comando) && !textoAtual.contains(",")) {
				return TipoComando.VIRGULA;
			}else if(Character.toString('\u00b1').equals(comando)) {
				return TipoComando.SINAL;
			}else if("%".equals(comando)) {
				return TipoComando.PORC;
			}
		}
		return null;
	}
}
