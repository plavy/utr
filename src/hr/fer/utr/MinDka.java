package hr.fer.utr;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class MinDka {
	static TreeSet<String> stanja = new TreeSet<String>();
	static TreeSet<String> simboli = new TreeSet<String>();
	static TreeSet<String> prihvatljiva = new TreeSet<String>();
	static String pocetno;
	static ArrayList<ArrayList<String>> prijelazi = new ArrayList<ArrayList<String>>();

	public static void main(String[] args) throws IOException {

//		Scanner file = new Scanner(System.in);
		Scanner file = new Scanner(Paths.get("ulaz.txt"));

		String line = file.nextLine();
		for (String stanje : line.split(","))
			stanja.add(stanje);

		line = file.nextLine();
		for (String simbol : line.split(","))
			simboli.add(simbol);

		line = file.nextLine();
		for (String prihvatljvo : line.split(","))
			prihvatljiva.add(prihvatljvo);

		line = file.nextLine();
		pocetno = line;

		while (file.hasNextLine()) {
			line = file.nextLine();
			ArrayList<String> prijelaz = new ArrayList<String>();
			prijelaz.add(line.split(",")[0]);
			prijelaz.add(line.split(",")[1].split("->")[0]);
			prijelaz.add(line.split("->")[1]);
			prijelazi.add(prijelaz);
		}

		ukloni_nedohvatljiva();

		minimiziraj();

		ispis();

	}

	private static void ukloni_nedohvatljiva() {
		TreeSet<String> dohvatljiva = new TreeSet<String>();
		TreeSet<String> nova_dohvatljiva;
		dohvatljiva.add(pocetno);

		// prosiri set stanja
		boolean prosireno = true;
		while (prosireno == true) {
			prosireno = false;
			nova_dohvatljiva = new TreeSet<String>(dohvatljiva);
			for (String dohvatljivo : dohvatljiva) {
				for (ArrayList<String> prijelaz : prijelazi) {
					if (prijelaz.get(0).equals(dohvatljivo)) {
						if (nova_dohvatljiva.add(prijelaz.get(2)))
							prosireno = true;
					}
				}
			}
			dohvatljiva = new TreeSet<String>(nova_dohvatljiva);
		}
		stanja = dohvatljiva;

		// ukloni beskorisna prihvatljiva stanja
		TreeSet<String> nova_prihvatljiva = new TreeSet<String>();
		for (String prihvatljivo : prihvatljiva) {
			if (stanja.contains(prihvatljivo)) {
				nova_prihvatljiva.add(prihvatljivo);
			}
		}
		prihvatljiva = nova_prihvatljiva;

		// ukloni beskorisne prijelaze
		ArrayList<ArrayList<String>> novi_prijelazi = new ArrayList<ArrayList<String>>();
		for (ArrayList<String> prijelaz : prijelazi) {
			if (stanja.contains(prijelaz.get(0))) {
				novi_prijelazi.add(prijelaz);
			}
		}
		prijelazi = novi_prijelazi;

	}

	private static String sljedece_stanje(String stanje, String simbol) throws RuntimeException {
		for (ArrayList<String> prijelaz : prijelazi) {
			if (prijelaz.get(0).equals(stanje) && prijelaz.get(1).equals(simbol))
				return prijelaz.get(2);
		}
		throw new RuntimeException("Sljedece stanje nije odredeno!");
	}

	private static void minimiziraj() {
		TreeMap<String, Integer> stanja_grupa = new TreeMap<String, Integer>();

		// svakom stanju pridodaj grupu na temelju prihvatljivosti
		for (String stanje : stanja) {
			stanja_grupa.put(stanje, prihvatljiva.contains(stanje) ? 2 : 1);
		}
		int indeks_grupe = 2;

		// iteriraj kroz stanja i grupe
		boolean nastavi = true;
		while (nastavi) {
			nastavi = false;
			for (String stanje : stanja_grupa.keySet()) {
				boolean stvorena_nova_grupa = false;
				for (Map.Entry<String, Integer> stanje_grupa : stanja_grupa.entrySet()) {
					if (stanje.equals(stanje_grupa.getKey()))
						continue;
					if (!stanja_grupa.get(stanje).equals(stanje_grupa.getValue()))
						continue;
					boolean stvori_novu_grupu = false;
					for (String simbol : simboli) {
						if (!stanja_grupa.get(sljedece_stanje(stanje, simbol))
								.equals(stanja_grupa.get(sljedece_stanje(stanje_grupa.getKey(), simbol)))) {
							stvori_novu_grupu = true;
							break;
						}
					}
					if (stvori_novu_grupu) {
						stanja_grupa.put(stanje_grupa.getKey(), indeks_grupe + 1);
						stvorena_nova_grupa = true;
						nastavi = true;
					}
				}
				if (stvorena_nova_grupa)
					indeks_grupe++;
			}
		}

		// tablica mapiranja istovjetnih stanja
		Map<String, String> mapiranje_stanja = new TreeMap<String, String>();
		for (Map.Entry<String, Integer> entry1 : stanja_grupa.entrySet()) {
			boolean postavio_sam_sebe = false;
			for (Map.Entry<String, Integer> entry2 : stanja_grupa.entrySet()) {
				if (entry1.getValue().equals(entry2.getValue())) {
					if (entry1.getKey().equals(entry2.getKey())) {
						mapiranje_stanja.put(entry2.getKey(), entry1.getKey());
						postavio_sam_sebe = true;
					} else if (postavio_sam_sebe == true) {
						mapiranje_stanja.put(entry2.getKey(), entry1.getKey());
					} else {
						break;
					}
				}
			}
		}

		// mapiranje stanja svugdje

		TreeSet<String> nova_stanja = new TreeSet<String>();
		for (String stanje : stanja) {
			nova_stanja.add(mapiranje_stanja.get(stanje));
		}
		stanja = nova_stanja;

		TreeSet<String> nova_prihvatljiva = new TreeSet<String>();
		for (String stanje : prihvatljiva) {
			nova_prihvatljiva.add(mapiranje_stanja.get(stanje));
		}
		prihvatljiva = nova_prihvatljiva;
		
		pocetno = mapiranje_stanja.get(pocetno);

		ArrayList<ArrayList<String>> novi_prijelazi = new ArrayList<ArrayList<String>>();
		for (ArrayList<String> prijelaz : prijelazi) {
			prijelaz.set(0, mapiranje_stanja.get(prijelaz.get(0)));
			prijelaz.set(2, mapiranje_stanja.get(prijelaz.get(2)));
			boolean postoji = false;
			for (ArrayList<String> novi_prijelaz : novi_prijelazi) {
				if (prijelaz.get(0).equals(novi_prijelaz.get(0)) && prijelaz.get(1).equals(novi_prijelaz.get(1))
						&& prijelaz.get(2).equals(novi_prijelaz.get(2)))
					postoji = true;
			}
			if (!postoji) {
				novi_prijelazi.add(prijelaz);
			}
		}
		prijelazi = novi_prijelazi;

	}

	private static void ispis() {
		String line = "";
		for (String stanje : stanja)
			line += stanje + ",";
		if (line.equals(""))
			line = ",";
		System.out.println(line.substring(0, line.length() - 1));

		line = "";
		for (String simbol : simboli)
			line += simbol + ",";
		if (line.equals(""))
			line = ",";
		System.out.println(line.substring(0, line.length() - 1));

		line = "";
		for (String prihvatljivo : prihvatljiva)
			line += prihvatljivo + ",";
		if (line.equals(""))
			line = ",";
		System.out.println(line.substring(0, line.length() - 1));

		line = pocetno;
		System.out.println(line);

		for (ArrayList<String> prijelaz : prijelazi) {
			line = prijelaz.get(0) + "," + prijelaz.get(1) + "->" + prijelaz.get(2);
			System.out.println(line);
		}

	}

}
