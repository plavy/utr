package hr.fer.utr;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SimEnka {
	static ArrayList<ArrayList<String>> nizovi = new ArrayList<ArrayList<String>>();
	static String pocetno;
	static ArrayList<ArrayList<String>> prijelazi = new ArrayList<ArrayList<String>>();

	static String ispis;
	static ArrayList<String> niz = new ArrayList<String>();
	static ArrayList<String> trenutnaStanja = new ArrayList<String>();
	static ArrayList<String> epsilonStanja = new ArrayList<String>();
	static ArrayList<String> sljedecaStanja = new ArrayList<String>();

	public static void main(String[] args) throws IOException {

//		Scanner file = new Scanner(System.in);
		Scanner file = new Scanner(Paths.get("ulaz.txt"));

		String line = file.nextLine();
		for (String element : line.split("\\|")) {
			ArrayList<String> jedanNiz = new ArrayList<String>();
			for (String el : element.split(",")) {
				jedanNiz.add(el);
			}
			nizovi.add(jedanNiz);
		}
		line = file.nextLine();
		// stanja

		line = file.nextLine();
		// simboli abecede

		file.nextLine();
		// prihvatljiva stanja

		line = file.nextLine();
		pocetno = line;

		while (file.hasNextLine()) {
			line = file.nextLine();
			ArrayList<String> niz = new ArrayList<String>();
			String prviDio = line.split("->")[0];
			String drugiDio = line.split("->")[1];
			niz.add(prviDio.split(",")[0]);
			niz.add(prviDio.split(",")[1]);
			for (String el : drugiDio.split(",")) {
				niz.add(el);
			}
			prijelazi.add(niz);
		}

		for (ArrayList<String> element : nizovi) {
			niz = element;
			trenutnaStanja.add(pocetno);
			generiraj();
			trenutnaStanja.clear();
		}
		
		file.close();
	}

	public static void epsilon(String stanje) {
		for (ArrayList<String> prijelaz : prijelazi) {
			if (stanje.equals(prijelaz.get(0))) {
				if (prijelaz.get(1).equals("$")) {
					for (int i = 2; true; i++) {
						try {
							if (!epsilonStanja.contains(prijelaz.get(i))) {
								epsilonStanja.add(prijelaz.get(i));
								epsilon(prijelaz.get(i));
							}
						} catch (IndexOutOfBoundsException e) {
							break;
						}
					}
				}
			}
		}
	}

	public static void generiraj() {
		while (niz.size() >= 0) {

			// epsilon prosirivanje
			for (String stanje : trenutnaStanja) {
				for (ArrayList<String> prijelaz : prijelazi) {
					if (stanje.equals(prijelaz.get(0))) {
						if (prijelaz.get(1).equals("$")) {
							for (int i = 2; true; i++) {
								try {
									if (!epsilonStanja.contains(prijelaz.get(i))) {
										epsilonStanja.add(prijelaz.get(i));
										epsilon(prijelaz.get(i));
									}
								} catch (IndexOutOfBoundsException e) {
									break;
								}
							}
						}
					}
				}

			}
			trenutnaStanja.addAll(epsilonStanja);
			trenutnaStanja = (ArrayList<String>) trenutnaStanja.stream().distinct().collect(Collectors.toList());
			epsilonStanja.clear();

			// sortiraj i ispisi trenutna stanja
			Collections.sort(trenutnaStanja);
			ispis = "";
			for (String stanje : trenutnaStanja) {
				if (!stanje.equals("#"))
					ispis = ispis + stanje + ",";
			}
			if (ispis.length() == 0)
				System.out.printf("#");
			else {
				System.out.printf("%s", ispis.substring(0, ispis.length() - 1));
			}
			if (niz.size() != 0)
				System.out.printf("|");
			else
				break;

			// uzimanje sljedeceg simbola
			String trenutniSimbol = niz.remove(0);

			// trazenje sljedecih stanja
			for (String stanje : trenutnaStanja) {
				for (ArrayList<String> prijelaz : prijelazi) {
					if (stanje.equals(prijelaz.get(0))) {
						if (trenutniSimbol.equals(prijelaz.get(1))) {
							for (int i = 2; true; i++) {
								try {
									sljedecaStanja.add(prijelaz.get(i));
								} catch (IndexOutOfBoundsException e) {
									break;
								}
							}
						}
					}
				}
			}

			trenutnaStanja = new ArrayList<String>(sljedecaStanja);
			sljedecaStanja.clear();
		}
		System.out.println();
	}

}
