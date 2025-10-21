package controller;

import conexion.ConexaoMySQL;

import java.sql.*;
import java.util.Locale;
import java.util.Scanner;

public class VeiculoController {
    private static final Scanner in = new Scanner(System.in);

    private static String esc(String s) { return s.replace("'", "''"); }

    public static void listar() {
        String sql = "SELECT id_veiculo, marca, modelo, cor, ano, preco, disponivel " +
                    "FROM veiculo ORDER BY id_veiculo";
        try {
            ConexaoMySQL cx = new ConexaoMySQL();
            try (Connection c = cx.getConnection();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

                System.out.println("\n-- Veículos --");
                while (rs.next()) {
                    System.out.printf("#%d | %s %s | %s | %d | R$ %.2f | disp=%s%n",
                            rs.getInt("id_veiculo"),
                            rs.getString("marca"),
                            rs.getString("modelo"),
                            rs.getString("cor"),
                            rs.getInt("ano"),
                            rs.getFloat("preco"),
                            rs.getBoolean("disponivel"));
                }
            }
            cx.close();
        } catch (Exception e) {
            System.out.println("Erro ao listar veículos: " + e.getMessage());
        }
    }

    public static void inserir() {
        try {
            System.out.print("Marca: ");  String marca  = in.nextLine();
            System.out.print("Modelo: "); String modelo = in.nextLine();
            System.out.print("Cor: ");    String cor    = in.nextLine();
            System.out.print("Ano: ");    int ano       = Integer.parseInt(in.nextLine());
            System.out.print("Preço: ");  float preco   = Float.parseFloat(in.nextLine());

            String sql = String.format(Locale.US,
                    "INSERT INTO veiculo (marca, modelo, cor, ano, preco, disponivel) " +
                    "VALUES ('%s','%s','%s',%d,%.2f,TRUE)",
                    esc(marca), esc(modelo), esc(cor), ano, preco);

            ConexaoMySQL cx = new ConexaoMySQL();
            try (Connection c = cx.getConnection(); Statement st = c.createStatement()) {
                st.executeUpdate(sql);
                System.out.println("Veículo inserido!");
            }
            cx.close();
        } catch (Exception e) {
            System.out.println("Erro ao inserir: " + e.getMessage());
        }
    }

    public static void remover() {
        try {
            listar();
            System.out.print("ID para remover: ");
            int id = Integer.parseInt(in.nextLine());

            if (temVenda(id)) {
                System.out.println("Há venda para este veículo. Exclua a venda primeiro.");
                return;
            }

            String sql = String.format("DELETE FROM veiculo WHERE id_veiculo=%d", id);

            ConexaoMySQL cx = new ConexaoMySQL();
            try (Connection c = cx.getConnection(); Statement st = c.createStatement()) {
                int n = st.executeUpdate(sql);
                System.out.println(n > 0 ? "Removido." : "Não encontrado.");
            }
            cx.close();
        } catch (Exception e) {
            System.out.println("Erro ao remover: " + e.getMessage());
        }
    }

    public static void atualizar() {
        try {
            listar();
            System.out.print("ID para atualizar: ");
            int id = Integer.parseInt(in.nextLine());

            System.out.print("Nova marca: ");  String marca  = in.nextLine();
            System.out.print("Novo modelo: "); String modelo = in.nextLine();
            System.out.print("Nova cor: ");    String cor    = in.nextLine();
            System.out.print("Novo ano: ");    int ano       = Integer.parseInt(in.nextLine());
            System.out.print("Novo preço: ");  float preco   = Float.parseFloat(in.nextLine());

            String sql = String.format(Locale.US,
                    "UPDATE veiculo SET marca='%s', modelo='%s', cor='%s', ano=%d, preco=%.2f " +
                    "WHERE id_veiculo=%d",
                    esc(marca), esc(modelo), esc(cor), ano, preco, id);

            ConexaoMySQL cx = new ConexaoMySQL();
            try (Connection c = cx.getConnection(); Statement st = c.createStatement()) {
                int n = st.executeUpdate(sql);
                System.out.println(n > 0 ? "Atualizado." : "Não encontrado.");
            }
            cx.close();
        } catch (Exception e) {
            System.out.println("Erro ao atualizar: " + e.getMessage());
        }
    }

    private static boolean temVenda(int idVeiculo) {
        String sql = String.format("SELECT COUNT(1) FROM venda WHERE id_veiculo=%d", idVeiculo);
        try {
            ConexaoMySQL cx = new ConexaoMySQL();
            try (Connection c = cx.getConnection();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
                rs.next();
                return rs.getInt(1) > 0;
            } finally { cx.close(); }
        } catch (Exception e) {
            return true;
        }
    }
}
