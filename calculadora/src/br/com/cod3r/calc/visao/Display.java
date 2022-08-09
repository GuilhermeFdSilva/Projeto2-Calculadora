package br.com.cod3r.calc.visao;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import br.com.cod3r.calc.MemoriaObservador;
import br.com.cod3r.calc.modelo.Memoria;

@SuppressWarnings("serial")
public class Display extends JPanel implements MemoriaObservador {
	private final JLabel label;
	public Display() {
		Memoria.getIstancia().adicionarObservador(this);
		setBackground(new Color(46, 49, 50));
		label = new JLabel(Memoria.getIstancia().getTextoAtual());
		label.setForeground(Color.WHITE);
		label.setFont(new Font("courier", Font.PLAIN, 40));
		setLayout(new FlowLayout(FlowLayout.RIGHT));
		add(label);
	}
	public void valorAlterado(String novoValor) {
		label.setText(novoValor);
	}
}
