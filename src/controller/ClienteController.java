package controller;

import conexion.ConexaoMySQL;

import java.sql.*;
import java.util.Scanner;

public class ClienteController {
    private static final Scanner in = new Scanner(System.in);

    private static String esc(String s) { return s.replace("'", "''"); }

    public static void listar() {
        String sql = "SELECT id_cliente, nome, cpf, telefone, email FROM cliente ORDER BY id_cliente";
        try {
            ConexaoMySQL cx = new ConexaoMySQL();
            try (Connection c = cx.getConnection();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

                System.out.println("\n-- Clientes --");
                while (rs.next()) {
                    System.out.printf("#%d | %s | CPF %s | %s | %s%n",
                            rs.getInt("id_cliente"),
                            rs.getString("nome"),
                            rs.getString("cpf"),
                            rs.getString("telefone"),
                            rs.getString("email"));
                }
            }
            cx.close();
        } catch (Exception e) {
            System.out.println("Erro ao listar clientes: " + e.getMessage());
        }
    }

    public static void inserir() {
        try {
            System.out.print("Nome: ");     String nome = in.nextLine();
            System.out.print("CPF (11): "); String cpf = in.nextLine();
            System.out.print("Telefone: "); String tel = in.nextLine();
            System.out.print("Email: ");    String email = in.nextLine();

            String sql = String.format(
                    "INSERT INTO cliente (nome, cpf, telefone, email) " +
                    "VALUES ('%s','%s','%s','%s')",
                    esc(nome), esc(cpf), esc(tel), esc(email));

            ConexaoMySQL cx = new ConexaoMySQL();
            try (Connection c = cx.getConnection(); Statement st = c.createStatement()) {
                st.executeUpdate(sql);
                System.out.println("Cliente inserido!");
            }
            cx.close();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("CPF já cadastrado.");
        } catch (Exception e) {
            System.out.println("Erro ao inserir cliente: " + e.getMessage());
        }
    }

    public static void atualizar() {
        try {
            listar();
            System.out.print("ID para atualizar: ");
            int id = Integer.parseInt(in.nextLine());

            System.out.print("Novo nome: ");     String nome = in.nextLine();
            System.out.print("Novo CPF (11): "); String cpf = in.nextLine();
            System.out.print("Novo telefone: "); String tel = in.nextLine();
            System.out.print("Novo email: ");    String email = in.nextLine();

            String sql = String.format(
                    "UPDATE cliente SET nome='%s', cpf='%s', telefone='%s', email='%s' " +
                    "WHERE id_cliente=%d",
                    esc(nome), esc(cpf), esc(tel), esc(email), id);

            ConexaoMySQL cx = new ConexaoMySQL();
            try (Connection c = cx.getConnection(); Statement st = c.createStatement()) {
                int n = st.executeUpdate(sql);
                System.out.println(n > 0 ? "Atualizado." : "Não encontrado.");
            }
            cx.close();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("CPF já cadastrado.");
        } catch (Exception e) {
            System.out.println("Erro ao atualizar: " + e.getMessage());
        }
    }

    public static void remover() {
        try {
            listar();
            System.out.print("ID para remover: ");
            int id = Integer.parseInt(in.nextLine());

            if (temVendaCliente(id)) {
                System.out.println("Há venda associada a este cliente. Exclua as vendas primeiro.");
                return;
            }

            String sql = String.format("DELETE FROM cliente WHERE id_cliente=%d", id);

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

    private static boolean temVendaCliente(int idCliente) {
        String sql = String.format("SELECT COUNT(1) FROM venda WHERE id_cliente=%d", idCliente);
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
