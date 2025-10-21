package reports;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import conexion.ConexaoMySQL;

public class Relatorios {
    public static void vendasPorMarcaMes() { runFromFile("src/sql/rel_total_por_marca_mes.sql"); }
    public static void vendasDetalhadas()  { runFromFile("src/sql/rel_vendas_detalhadas.sql"); }

    private static void runFromFile(String path) {
        try {
            String sql = Files.readString(Paths.get(path));
            ConexaoMySQL cx = new ConexaoMySQL();
            try (Connection c = cx.getConnection();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
                int cols = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    StringBuilder line = new StringBuilder();
                    for (int i = 1; i <= cols; i++) {
                        line.append(rs.getMetaData().getColumnLabel(i)).append("=")
                            .append(rs.getString(i)).append(i < cols ? " | " : "");
                    }
                    System.out.println(line);
                }
            }
            cx.close();
        } catch (Exception e) {
            System.out.println("Erro ao executar relatório: " + e.getMessage());
        }
    }
}
