package cz.educanet;

import java.util.Scanner;

import static cz.educanet.Window.Okno;

public class Main {

    public static int W;
    public static int H;
    public static float VelikostSnimku;
    public static String cesta = "res/img.png";
    public static float snimky = 6;

    public static void main(String[] args) throws Exception {

        Scanner sc1 = new Scanner(System.in);
        Scanner sc2 = new Scanner(System.in);
        Scanner sc3 = new Scanner(System.in);
        Scanner sc4 = new Scanner(System.in);

        System.out.println("Jak velké má být pole?");
        System.out.println("Výška:");
        H = sc1.nextInt();
        System.out.println("Šířka:");
        W = sc1.nextInt();
        System.out.println("Velikost Snímku");
        VelikostSnimku = sc2.nextFloat();

        System.out.println("Chcete použít custom snímky?");
        boolean custom = sc3.nextBoolean();
        if (custom) {
            System.out.println("Prosim vložte cestu");
            cesta = sc4.next();
            System.out.println("Vložte počet snímků");
            snimky = sc1.nextFloat();
        }
            Okno();

    }
}
