package hr.fer.utr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Parser {
	static int ulaz;
	static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) throws IOException {
		ulaz = reader.read();
		S();
		if (ulaz != '\n') {
			System.out.println("\nNE");
		} else {
			System.out.println("\nDA");
		}
	}

	public static void S() throws IOException {
		System.out.print("S");

		if (ulaz == 'a') {
			ulaz = reader.read();
			A();
			B();
		} else if (ulaz == 'b') {
			ulaz = reader.read();
			B();
			A();
		} else {
			System.out.println("\nNE");
			System.exit(0);
		}
	}

	public static void A() throws IOException {
		System.out.print("A");

		if (ulaz == 'b') {
			ulaz = reader.read();
			C();
		} else if (ulaz == 'a') {
			ulaz = reader.read();
		} else {
			System.out.println("\nNE");
			System.exit(0);
		}

	}

	public static void B() throws IOException {
		System.out.print("B");

		if (ulaz == 'c') {
			ulaz = reader.read();
			if (ulaz == 'c') {
				ulaz = reader.read();
				S();
				if (ulaz == 'b') {
					ulaz = reader.read();
					if (ulaz == 'c') {
						ulaz = reader.read();
					} else {
						System.out.println("\nNE");
						System.exit(0);
					}
				} else {
					System.out.println("\nNE");
					System.exit(0);
				}
			} else {
				System.out.println("\nNE");
				System.exit(0);
			}
		} // nema else jer se prihvaca i prazan ulaz
	}

	public static void C() throws IOException {
		System.out.print("C");

		A();
		A();

	}

}
