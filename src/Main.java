import utils.MenuHelper;
import utils.SplashScreen;
import reports.Relatorios;
import controller.ClienteController;
import controller.VeiculoController;
import controller.VendaController;

public class Main {
    public static void main(String[] args) {
        SplashScreen.show();

        boolean loop = true;
        while (loop) {
            switch (MenuHelper.menuPrincipal()) {
                case 1 -> { // Relatórios
                    int op = MenuHelper.submenuRelatorios();
                    if (op == 1) Relatorios.vendasPorMarcaMes();
                    else if (op == 2) Relatorios.vendasDetalhadas();
                    MenuHelper.pause();
                }
                case 2 -> { // Inserir
                    switch (MenuHelper.submenuEntidades("Inserir")) {
                        case 1 -> ClienteController.inserir();
                        case 2 -> VeiculoController.inserir();
                        case 3 -> VendaController.inserir();
                    }
                    MenuHelper.pause();
                }
                case 3 -> { // Remover
                    switch (MenuHelper.submenuEntidades("Remover")) {
                        case 1 -> ClienteController.remover();
                        case 2 -> VeiculoController.remover();
                        case 3 -> VendaController.remover();
                    }
                    MenuHelper.pause();
                }
                case 4 -> { // Atualizar
                    switch (MenuHelper.submenuEntidades("Atualizar")) {
                        case 1 -> ClienteController.atualizar();
                        case 2 -> VeiculoController.atualizar();
                        case 3 -> VendaController.atualizar();
                    }
                    MenuHelper.pause();
                }
                case 5 -> { // Listar
                    switch (MenuHelper.submenuEntidades("Listar")) {
                        case 1 -> ClienteController.listar();
                        case 2 -> VeiculoController.listar();
                        case 3 -> VendaController.listar();
                    }
                    MenuHelper.pause();
                }
                case 6 -> loop = false;
                default -> System.out.println("Opção inválida.");
            }
        }
        System.out.println("Até mais!");
    }
}
