package utils;

import conexion.ConexaoMySQL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SplashScreen {

    private static final String SISTEMA     = "SISTEMA DE CONCESSIONÁRIA";
    private static final String DISCIPLINA  = "Banco de Dados";
    private static final String PROFESSOR   = "Prof. Howard Roatti";
    private static final String TURMA       = "TURMA: 4HC1";
    private static final String[] INTEGRANTES = {"Enrico Schultz Breda", "Kathelyn V. Rocha Farias"};
    // =======================================

    // --- Consultas de contagem ---
    private final String qryTotalClientes = "SELECT COUNT(1) AS total_clientes FROM cliente";
    private final String qryTotalVeiculos = "SELECT COUNT(1) AS total_veiculos FROM veiculo";
    private final String qryTotalVendas   = "SELECT COUNT(1) AS total_vendas   FROM venda";

    private long runCount(String sql, String alias) {
        try {
            ConexaoMySQL cx = new ConexaoMySQL();
            try (Connection c = cx.getConnection();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
                if (rs.next()) return rs.getLong(alias);
            } finally {
                cx.close();
            }
        } catch (Exception e) {
            System.out.println("[Splash] Erro ao consultar \"" + alias + "\": " + e.getMessage());
        }
        return 0L;
    }

    private long getTotalClientes() { return runCount(qryTotalClientes, "total_clientes"); }
    private long getTotalVeiculos() { return runCount(qryTotalVeiculos, "total_veiculos"); }
    private long getTotalVendas()   { return runCount(qryTotalVendas,   "total_vendas"); }

    /** Retorna a tela formatada */
    public String getUpdatedScreen() {
        String createdBy = String.join(", ", INTEGRANTES);
        return String.format("""
                ########################################################
                #                 %s
                #
                #  TOTAL DE REGISTROS:
                #      1 - CLIENTES:         %5d
                #      2 - VEÍCULOS:         %5d
                #      3 - VENDAS:           %5d
                #
                #  CRIADO POR: %s
                #
                #  PROFESSOR:  %s
                #
                #  DISCIPLINA: %s
                #              %s
                ########################################################
                """,
                SISTEMA,
                getTotalClientes(),
                getTotalVeiculos(),
                getTotalVendas(),
                createdBy,
                PROFESSOR,
                DISCIPLINA,
                TURMA
        );
    }

    /** Imprime a splash. Chame no início do Main. */
    public static void show() {
        System.out.println(new SplashScreen().getUpdatedScreen());
    }
}
