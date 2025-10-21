package utils;

import java.util.Locale;
import java.util.Scanner;

public class MenuHelper {
    private static final Scanner in = new Scanner(System.in);

    // ===== Menus =====
    public static int menuPrincipal() {
        System.out.println("\n==== MENU ====");
        System.out.println("1) Relatórios");
        System.out.println("2) Inserir registros");
        System.out.println("3) Remover registros");
        System.out.println("4) Atualizar registros");
        System.out.println("5) Listar registros");
        System.out.println("6) Sair");
        return readInt("Escolha: ", 0, 6);
    }

    public static int submenuEntidades(String titulo) {
        System.out.printf("%n%s → 1) Cliente  2) Veículo  3) Venda  0) Voltar%n", titulo);
        return readInt("Escolha: ", 0, 3);
    }

    public static int submenuRelatorios() {
        System.out.println("\nRelatórios: 1) Vendas por marca/mês  2) Vendas detalhadas  0) Voltar");
        return readInt("Escolha: ", 0, 2);
    }

    // ===== Helpers de entrada/UX =====
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) throw new NumberFormatException();
                return v;
            } catch (NumberFormatException e) {
                System.out.printf("Valor inválido. Digite um inteiro entre %d e %d.%n", min, max);
            }
        }
    }

    public static float readFloat(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim().replace(',', '.'); // aceita vírgula
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Use números (ex.: 1234.56).");
            }
        }
    }

    public static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Campo obrigatório.");
        }
    }

    public static boolean confirm(String pergunta) {
        System.out.print(pergunta + " [s/N]: ");
        String s = in.nextLine().trim().toLowerCase(Locale.ROOT);
        return s.equals("s") || s.equals("sim") || s.equals("y") || s.equals("yes");
    }

    public static void pause() {
        System.out.print("\nPressione Enter para continuar...");
        scanner().nextLine();
    }

    public static Scanner scanner() { return in; } // se quiser acesso direto
}
