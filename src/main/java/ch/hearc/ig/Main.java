package ch.hearc.ig;
import ch.hearc.ig.apiService.Connexion;
import ch.hearc.ig.apiService.ConnexionManager;
import ch.hearc.ig.apiService.FlowManager;
import org.w3c.dom.ls.LSOutput;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() throws Exception {
        Scanner scanner = new Scanner(System.in);
        Connexion connexion = new Connexion();
        ConnexionManager connexionManager = new ConnexionManager();
        FlowManager flowManager = new FlowManager();
        Set<Integer> quittances = new HashSet<>();


        System.out.println("""
        =================================
        LOGIN
        =================================
        """ );

    System.out.print("USERNAME : ");
    String username = scanner.nextLine();

    System.out.print("PASSWORD : ");
    String password = scanner.nextLine();

    connexion = connexionManager.login(username,password);

        System.out.println(connexion.getToken());

    quittances = flowManager.getValidatedReceipt(connexion);

        System.out.println(quittances);


    }
}
