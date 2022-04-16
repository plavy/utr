package hr.fer.utr;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

public class SimPa {
	static ArrayList<ArrayList<String>> nizovi = new ArrayList<ArrayList<String>>();
	static TreeSet<String> prihvatljivaStanja = new TreeSet<String>();
	static String pocetnoStanje;
	static String pocetniZnak;
	static ArrayList<ArrayList<String>> prijelazi = new ArrayList<ArrayList<String>>();

	static ArrayList<String> niz = new ArrayList<String>();
	static String ispis = "";

	static String trenutnoStanje;
	static String trenutniUlaz;
	static String trenutniZnak;
	static String sljedeceStanje;
	static ArrayList<String> stog = new ArrayList<String>();

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

		line = file.nextLine();
		// simboli stoga

		line = file.nextLine();
		for (String prihvatljvo : line.split(","))
			prihvatljivaStanja.add(prihvatljvo);

		line = file.nextLine();
		pocetnoStanje = line;

		line = file.nextLine();
		pocetniZnak = line;

		while (file.hasNextLine()) {
			line = file.nextLine();
			ArrayList<String> niz = new ArrayList<String>();
			String prviDio = line.split("->")[0];
			String drugiDio = line.split("->")[1];
			niz.add(prviDio.split(",")[0]);
			niz.add(prviDio.split(",")[1]);
			niz.add(prviDio.split(",")[2]);
			niz.add(drugiDio.split(",")[0]);
			niz.add(drugiDio.split(",")[1]);
			prijelazi.add(niz);
		}

		for (ArrayList<String> element : nizovi) {
			niz = element;
			trenutnoStanje = pocetnoStanje;
			stog.add(pocetniZnak);
			trenutniZnak = pocetniZnak;
			simuliraj();
			System.out.println(ispis);
			ispis = "";
			stog.clear();
		}

		file.close();

	}

	public static void simuliraj() {
		boolean found = true;
		boolean zadrzi_ulaz = false;
		int preostaoUlaz = niz.size();
		while (true) {
			if (found) {
				ispis += trenutnoStanje + "#";
				if (stog.size() == 0) {
					ispis += "$";
				} else {
					for (int i = stog.size(); i > 0; i--) {
						ispis += stog.get(i - 1);
					}
				}
				ispis += "|";
				if (preostaoUlaz == 0 && prihvatljivaStanja.contains(trenutnoStanje)) {
					ispis += "1";
					break;
				}
			}
			if (zadrzi_ulaz) {
				zadrzi_ulaz = false;
			} else {
				try {
					trenutniUlaz = niz.remove(0);
				} catch (IndexOutOfBoundsException e) {
					if (!found) {
						ispis += "0";
						break;
					}
					trenutniUlaz = "$";
				}
			}
			try {
				trenutniZnak = stog.remove(stog.size() - 1);
			} catch (IndexOutOfBoundsException e) {
				trenutniZnak = "$";
			}
			found = false;
			for (ArrayList<String> prijelaz : prijelazi) {
				if (prijelaz.get(0).equals(trenutnoStanje)) {
					if (prijelaz.get(1).equals(trenutniUlaz) || prijelaz.get(1).equals("$")) {
						if (prijelaz.get(2).equals(trenutniZnak)) {
							found = true;
							if (prijelaz.get(1).equals("$")) {
								zadrzi_ulaz = true;
							} else {
								preostaoUlaz--;
							}
							trenutnoStanje = prijelaz.get(3);
							String dodaj = prijelaz.get(4);
							if (!dodaj.equals("$")) {
								for (int i = dodaj.length(); i > 0; i--) {
									stog.add(String.valueOf(dodaj.charAt(i - 1)));
								}
							}
							break;
						}
					}
				}
			}
			if (!found && preostaoUlaz > 0) {
				ispis += "fail|0";
				break;
			}
		}
	}

}
