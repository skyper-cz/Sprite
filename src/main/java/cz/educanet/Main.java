package cz.educanet;

import java.util.Scanner;

import static cz.educanet.Window.Okno;

public class Main {

    public static int W;
    public static int H;

    public static void main(String[] args) throws Exception {

        Scanner sc1 = new Scanner(System.in);

        System.out.println("Jak velké má být pole?");
        System.out.println("Výška:");
        W = sc1.nextInt();
        System.out.println("Šířka:");
        H = sc1.nextInt();

            Okno();

    }
}
