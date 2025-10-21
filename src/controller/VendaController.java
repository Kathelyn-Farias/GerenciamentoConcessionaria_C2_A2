package controller;

import conexion.ConexaoMySQL;

import java.sql.*;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Scanner;

public class VendaController {
    private static final Scanner in = new Scanner(System.in);

    private static String esc(String s) { return s.replace("'", "''"); }

    public static void listar() {
        String sql = """
            SELECT ve.id_venda, ve.data_venda, ve.valor_final,
                c.id_cliente, c.nome AS cliente,
                v.id_veiculo, v.marca, v.modelo
            FROM venda ve
                JOIN cliente c ON c.id_cliente = ve.id_cliente
                JOIN veiculo v ON v.id_veiculo = ve.id_veiculo
            ORDER BY ve.id_venda
            """;
        try {
            ConexaoMySQL cx = new ConexaoMySQL();
            try (Connection c = cx.getConnection();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

                System.out.println("\n-- Vendas --");
                while (rs.next()) {
                    System.out.printf("#%d | %s | R$ %.2f | Cliente #%d %s | Veículo #%d %s %s%n",
                            rs.getInt("id_venda"),
                            rs.getDate("data_venda"),
                            rs.getFloat("valor_final"),
                            rs.getInt("id_cliente"),
                            rs.getString("cliente"),
                            rs.getInt("id_veiculo"),
                            rs.getString("marca"),
                            rs.getString("modelo"));
                }
            }
            cx.close();
        } catch (Exception e) {
            System.out.println("Erro ao listar vendas: " + e.getMessage());
        }
    }

    public static void inserir() {
        try {
            System.out.println("\nClientes (id | nome):");
            listarClientesIdNome();

            System.out.println("\nVeículos disponíveis (id | marca modelo):");
            listarVeiculosDisponiveis();

            System.out.print("ID do cliente: ");
            int idCliente = Integer.parseInt(in.nextLine());

            System.out.print("ID do veículo: ");
            int idVeiculo = Integer.parseInt(in.nextLine());

            if (!veiculoDisponivel(idVeiculo)) {
                System.out.println("Veículo não está disponível para venda.");
                return;
            }

            System.out.print("Data da venda (AAAA-MM-DD): ");
            String data = in.nextLine(); // validado pelo MySQL como DATE

            System.out.print("Valor final: ");
            float valor = Float.parseFloat(in.nextLine());

            String sql = String.format(Locale.US,
                    "INSERT INTO venda (data_venda, valor_final, id_cliente, id_veiculo) " +
                    "VALUES ('%s', %.2f, %d, %d)",
                    esc(data), valor, idCliente, idVeiculo);

            ConexaoMySQL cx = new ConexaoMySQL();
            try (Connection c = cx.getConnection(); Statement st = c.createStatement()) {
                st.executeUpdate(sql); // trigger vai marcar veiculo.disponivel = false
                System.out.println("Venda registrada!");
            }
            cx.close();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Falha de integridade (FK/UNIQUE). Verifique IDs e se o veículo já foi vendido.");
        } catch (Exception e) {
            System.out.println("Erro ao inserir venda: " + e.getMessage());
        }
    }

    public static void remover() {
        try {
            listar();
            System.out.print("ID da venda para remover: ");
            int id = Integer.parseInt(in.nextLine());

            String sql = String.format("DELETE FROM venda WHERE id_venda=%d", id);

            ConexaoMySQL cx = new ConexaoMySQL();
            try (Connection c = cx.getConnection(); Statement st = c.createStatement()) {
                int n = st.executeUpdate(sql); // trigger libera veiculo.disponivel = true
                System.out.println(n > 0 ? "Venda removida." : "Não encontrada.");
            }
            cx.close();
        } catch (Exception e) {
            System.out.println("Erro ao remover venda: " + e.getMessage());
        }
    }

    public static void atualizar() {
        try {
            listar();
            System.out.print("ID da venda para atualizar: ");
            int id = Integer.parseInt(in.nextLine());

            System.out.print("Nova data (AAAA-MM-DD): ");
            String data = in.nextLine();

            System.out.print("Novo valor final: ");
            float valor = Float.parseFloat(in.nextLine());

            String sql = String.format(Locale.US,
                    "UPDATE venda SET data_venda='%s', valor_final=%.2f WHERE id_venda=%d",
                    esc(data), valor, id);

            ConexaoMySQL cx = new ConexaoMySQL();
            try (Connection c = cx.getConnection(); Statement st = c.createStatement()) {
                int n = st.executeUpdate(sql);
                System.out.println(n > 0 ? "Atualizada." : "Não encontrada.");
            }
            cx.close();
        } catch (Exception e) {
            System.out.println("Erro ao atualizar venda: " + e.getMessage());
        }
    }

    /* ===== Helpers (também só com Statement) ===== */

    private static boolean veiculoDisponivel(int idVeiculo) {
        String sql = String.format("SELECT disponivel FROM veiculo WHERE id_veiculo=%d", idVeiculo);
        try {
            ConexaoMySQL cx = new ConexaoMySQL();
            try (Connection c = cx.getConnection();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
                if (!rs.next()) return false;
                return rs.getBoolean(1);
            } finally { cx.close(); }
        } catch (Exception e) {
            return false;
        }
    }

    private static void listarClientesIdNome() {
        String sql = "SELECT id_cliente, nome FROM cliente ORDER BY id_cliente";
        try {
            ConexaoMySQL cx = new ConexaoMySQL();
            try (Connection c = cx.getConnection();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.printf("#%d | %s%n", rs.getInt(1), rs.getString(2));
                }
            }
            cx.close();
        } catch (Exception e) {
            System.out.println("Erro ao listar clientes: " + e.getMessage());
        }
    }

    private static void listarVeiculosDisponiveis() {
        String sql = "SELECT id_veiculo, marca, modelo FROM veiculo WHERE disponivel = TRUE ORDER BY id_veiculo";
        try {
            ConexaoMySQL cx = new ConexaoMySQL();
            try (Connection c = cx.getConnection();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.printf("#%d | %s %s%n", rs.getInt(1), rs.getString(2), rs.getString(3));
                }
            }
            cx.close();
        } catch (Exception e) {
            System.out.println("Erro ao listar veículos: " + e.getMessage());
        }
    }
}
